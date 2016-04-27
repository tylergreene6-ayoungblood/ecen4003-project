/*
 * Kernelizr.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import java.util.concurrent.*;
import java.io.*;
import java.util.ArrayList;
// For profiling
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Processes an image with a multithreaded, dynamic kernel-based image
 * filtering algorithm.
 */
public class Kernelizr {
    /**
     * The main method of Kernelizr. Handles the higher-level process:<br>
     * - load a series of images<br>
     * - load a specified kernel, or use the default<br>
     * - start a thread pool for processing<br>
     * - save the images to a path<br>
     * Command-line argument specification:<br>
     * No arguments are required; arguments that are not passed will be set to
     * program defaults. The first argument is the directory of input images,
     * the second argument is the output path, the third argument is a path
     * to a JSON kernel specification, and the fourth argument allows the user
     * to override the number of processors used (default is number of cores).
     * Finally, the fifth argument allows the user to override the blocksize.
     * @param args The command-line args for the main program.
     */
    public static void main(String[] args) {
        // Start overall timers (total keeps track of entire program execution,
        // convol keeps track of actual convolution time)
        final long totalStartTime = System.currentTimeMillis();
        long convolTime = 0;
        // Argument parsing and validation -------------------------------------
        System.out.printf("\nHello, I'm Kernelizr, an image filter.\n");
        // The first argument should be a path to a directory of images.
        // If it is not passed, warn and use the default.
        final String searchPath;
        if (args.length >= 1) {
            searchPath = args[0];
        } else {
            searchPath = "../test/datasets/timeseries/hw8_timeseries_2015-07-29T145000.DZ/";
            System.out.printf("WARN: No timeseries path provided. Using default %s\n", searchPath);
        }
        // The second argument should be a directory path.
        // If it is not passed, warn and use the default.
        final String outputPath;
        if (args.length >= 2) {
            outputPath = args[1];
        } else {
            outputPath = "../test/output/";
            System.out.printf("WARN: No output path provided. Using default %s\n", outputPath);
        }
        // The third argument should be a kernel file path.
        // If it is not passed, warn and use the default.
        final String kernelPath;
        if (args.length >= 3) {
            kernelPath = args[2];
        } else {
            kernelPath = "../test/kernels/gaussianblur_1x1x7.json";
            System.out.printf("WARN: No kernel path provided. Using default %s\n", kernelPath);
        }
        // Fourth argument allows thread count override
        int nThreadsProposed;
        if (args.length >= 4) {
            try {
                nThreadsProposed = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                nThreadsProposed = 1;
                System.out.printf("WARN: Bad thread count proposed. Using default: %d\n", nThreadsProposed);
            }
            if (nThreadsProposed < 1) nThreadsProposed = 1;
        } else {
            nThreadsProposed = Runtime.getRuntime().availableProcessors();
            System.out.printf("INFO: Detected %d processors, using this value for thread count.\n", nThreadsProposed);
        }
        // Fifth argument allows block size override
        int blockSizeProposed;
        if (args.length >= 5) {
            try {
                blockSizeProposed = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                blockSizeProposed = 32;
                System.out.printf("WARN: Bad thread count proposed. Using default: %d\n", nThreadsProposed);
            }
            if (blockSizeProposed< 1) blockSizeProposed = 1;
        } else {
            blockSizeProposed = 32;
            System.out.printf("INFO: Default value for block size is %d, using this value.\n",blockSizeProposed);
        }
        // Set processing parameters -------------------------------------------
        // blockSize is the size in pixels of each block associated with a task
        final int blockSize = blockSizeProposed;
        // nThreads is the (supposedly optimal) number of threads to run in
        // the thread pool. Determined by number of processors or by argument
        final int nThreads = nThreadsProposed;

        // Search for *.png files in the specified directory and add them to
        // the list of files we care about
        File searchDir = new File(searchPath);
        File[] searchFiles = searchDir.listFiles();
        ArrayList<File> srcFiles = new ArrayList<File>();
        for (int i = 0; i < searchFiles.length; i++) {
            if (searchFiles[i].isFile() && searchFiles[i].getName().endsWith(".png")) {
                srcFiles.add(searchFiles[i]);
            }
        }
        System.out.printf("Found %d files in %s\n",srcFiles.size(),searchPath);
        // Load the base kernel
        BaseKernel baseKernel = new BaseKernel(kernelPath);
        System.out.printf("Using kernel: %s, sum: %f\n",baseKernel.name,KOps.sum(baseKernel.getKernel()));
        //baseKernel.to
        // Keep the input and output images in lists
        ArrayList<BadRaster> srcRasters = new ArrayList<BadRaster>();
        ArrayList<BadRaster> outRasters = new ArrayList<BadRaster>();
        // Preload (kernel depth + 1)/2 frames
        int loadedIndex = -1;
        for (loadedIndex = 0; loadedIndex < (baseKernel.getDepth()+1)/2; ++loadedIndex) {
            BadRaster raster = new BadRaster();
            File file = srcFiles.get(loadedIndex);
            System.out.println("Preloading " + file.getName());
            try {
                raster.loadFromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR: Failed to open source image: " + file.getName());
                System.exit(1);
            }
            srcRasters.add(raster);
        }
        // ##     ##    ###    #### ##    ##    ##        #######   #######  ########
        // ###   ###   ## ##    ##  ###   ##    ##       ##     ## ##     ## ##     ##
        // #### ####  ##   ##   ##  ####  ##    ##       ##     ## ##     ## ##     ##
        // ## ### ## ##     ##  ##  ## ## ##    ##       ##     ## ##     ## ########
        // ##     ## #########  ##  ##  ####    ##       ##     ## ##     ## ##
        // ##     ## ##     ##  ##  ##   ###    ##       ##     ## ##     ## ##
        // ##     ## ##     ## #### ##    ##    ########  #######   #######  ##
        // The main processing loop. Loop through frame-by-frame, loading and
        // saving as necessary
        long totalConvolutions = 0; // for profiling
        for (int frame = 0; frame < srcFiles.size(); ++frame) {
            // get a handle for the source raster
            BadRaster srcRaster = srcRasters.get(frame);
            System.out.printf("Processing frame %d, %s (%dx%d)\n",frame,srcRaster.getFilename(),srcRaster.getWidth(),srcRaster.getHeight());
            // Create a new raster to write the filtered image data to
            BadRaster outRaster = new BadRaster(srcRaster.getBands(),srcRaster.getWidth(),srcRaster.getHeight());
            //outRasters.add(outRaster);

            // Create a task queue and add items
            TaskQueue tasks = new TaskQueue();
            // Divide the source image into blocks and iterate through
            int nBlocks = 0;
            int xBlocks = srcRaster.getWidth()/blockSize;
            int yBlocks = srcRaster.getHeight()/blockSize;
            if (srcRaster.getWidth() % blockSize != 0 || srcRaster.getHeight() % blockSize != 0)
                System.out.println("WARN: Image dimensions not a multiple of blocksize. Some pixels may be ignored.");
            for (int i = 0; i < xBlocks; ++i) {
                for (int j = 0; j < yBlocks; ++j) {
                    float [] point = {i/((float)xBlocks),j/((float)yBlocks)};
                    // Create a task
                    KTask task = new KTask(frame,i*blockSize,j*blockSize,blockSize,blockSize);
                    task.setInputRaster(srcRasters);
                    task.setOutputRaster(outRaster);
                    //Kernel kernel = baseKernel.getModulatedKernel(point);
                    //task.setKernel(kernel);
                    //task.setKernel(baseKernel);
                    // !! Overriding normal kernel behaviour for demo !!
                    // Written under duress, take out later
                    double dist = (Math.pow(i-xBlocks/2,2) + Math.pow(j-yBlocks/2,2))/(xBlocks/2 * yBlocks/2);
                    // System.out.printf("Creating new block at (%d,%d), dist: %f\n",i-xBlocks/2,j-yBlocks/2,dist);
                    Kernel kernel = new Kernel(11,11,11);
                    kernel.setKernel(GaussianKernelFactory.get3DFloat(4*dist+0.1,11));
                    kernel.normalize();
                    task.setKernel(kernel);
                    totalConvolutions += (blockSize*blockSize*baseKernel.getWidth()*baseKernel.getHeight()*baseKernel.getDepth());
                    tasks.push(task);
                    //System.out.println("Pushed task: " + task.getRegionString() + " w/ modulation: [" + point[0] + "," + point[1] + "]");
                    ++nBlocks;

                }
            }
            System.out.printf("Task queue built. Tasks: %d, block size: %d\n", tasks.length(), blockSize);

            // Start the thread pool running and start a timer
            System.out.printf("Starting thread pool with %d threads. Starting timer.\n", nThreads);
            final long startTime = System.currentTimeMillis();
            // executor manages the thread pool
            ExecutorService executor = Executors.newFixedThreadPool(nThreads);
            // Add worker threads to the thread pool queue
            while (!tasks.isEmpty()) {
                Runnable worker = new KWorker(tasks.pop());
                executor.execute(worker);
            }
            // Stop the thread pool and wait for threads to finish
            executor.shutdown();
            while (!executor.isTerminated()) {}
            System.out.println("Task queue empty, thread pool shutdown.");
            final long endTime = System.currentTimeMillis();
            final long elapsedTime = endTime - startTime;
            System.out.printf("Processed %d blocks in %f %s\n",nBlocks,(elapsedTime>1000)?elapsedTime/1000.0f:elapsedTime,(elapsedTime>1000)?"s":"ms");
            convolTime += elapsedTime;
            // Save the destination raster to the destination filepath
            String outputFilePath = outputPath + srcRaster.getFilename();
            System.out.printf("Saving image to %s. Dimensions: %dx%d\n",outputFilePath,outRaster.getWidth(),outRaster.getHeight());
            try {
                outRaster.writeToPath(outputFilePath);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            // Load the next source raster
            if (loadedIndex < srcFiles.size()) {
                BadRaster raster = new BadRaster();
                File file = srcFiles.get(loadedIndex);
                System.out.println("Loading " + file.getName());
                try {
                    raster.loadFromFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR: Failed to open source image: " + file.getName());
                    System.exit(1);
                }
                srcRasters.add(raster);
                ++loadedIndex;
            }
            // Clear unused rasters
            if (frame > (baseKernel.getDepth()+1)/2) {
                System.out.println("Clearing " + srcRasters.get(frame - (baseKernel.getDepth()+1)/2).getFilename());
                srcRasters.set(frame - (baseKernel.getDepth()+1)/2,null);

            }
        }
        // Print a total elapsed time
        final long totalEndTime = System.currentTimeMillis();
        final long totalElapsedTime = totalEndTime - totalStartTime;
        System.out.printf("Done. Processed %d frames in %f %s\n",srcFiles.size(),(totalElapsedTime>1000)?totalElapsedTime/1000.0f:totalElapsedTime,(totalElapsedTime>1000)?"s":"ms");

        // Print profiling information -----------------------------------------
        final BadRaster lastRaster = srcRasters.get(srcRasters.size()-1);
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostname = "ERROR";
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTimeString = sdf.format(new Date());
        System.out.printf("#### START Profiling information #####################################\n");
        System.out.printf("Environment: %s, %s, %s\n",hostname, System.getProperty("os.name"), utcTimeString);
        System.out.printf("Images: %d frames, %dx%d, from %s\n", srcFiles.size(), lastRaster.getWidth(), lastRaster.getHeight(), searchPath);
        System.out.printf("Kernel: %dx%dx%d, name: %s\n", baseKernel.getWidth(), baseKernel.getHeight(), baseKernel.getDepth(), baseKernel.name);
        System.out.printf("Process: %d threads, blocksize %d\n", nThreads, blockSize);
        System.out.printf("Total time: %f s; average frame time: %f ms \n",totalElapsedTime/1000.f,((float)(totalElapsedTime))/srcFiles.size());
        System.out.printf("Convolution time: %f s; average frame time: %f ms \n",convolTime/1000.f,((float)(convolTime))/srcFiles.size());
        System.out.printf("Operations: %d total convolutions, %d convolutions/s\n", totalConvolutions, totalConvolutions/(totalElapsedTime/1000));
        System.out.printf("#### END profiling information #######################################\n");
    }
}
