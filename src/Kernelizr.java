/*
 * Kernelizr.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import java.util.concurrent.*;
import java.io.*;
import java.util.ArrayList;

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
     * @param args The command-line args for the main program.
     */
    public static void main(String[] args) {
        // Argument parsing and validation -------------------------------------
        System.out.printf("\nHello, I'm Kernelizr, an image filter.\n");
        // The first argument should be a path to an image file.
        // If it is not, complain. Otherwise, try to open it
        final String searchPath;
        if (args.length >= 1) {
            searchPath = args[0];
        } else {
            searchPath = "../test/datasets/timeseries/mdb_render/";
            System.out.printf("WARN: No timeseries path provided. Using default %s\n", searchPath);
        }
        final String outputPath;
        if (args.length >= 2) {
            outputPath = args[1];
        } else {
            outputPath = "../test/output/";
            System.out.printf("WARN: No output path provided. Using default %s\n", outputPath);
        }
        final String kernelPath;
        if (args.length >= 3) {
            kernelPath = args[2];
        } else {
            kernelPath = "../test/kernels/gaussianblur_7x7x1.json";
            System.out.printf("WARN: No kernel path provided. Using default %s\n", kernelPath);
        }
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
        // The main processing loop. Loop through frame-by-frame, loading and
        // saving as necessary
        ArrayList<BadRaster> srcRasters = new ArrayList<BadRaster>();
        ArrayList<BadRaster> outRasters = new ArrayList<BadRaster>();
        // Preload (kernel depth + 1)/2 frames
        for (int i = 0; i < (baseKernel.getDepth()+1)/2; ++i) {
            BadRaster raster = new BadRaster();
            File file = srcFiles.get(i);
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
        /*

        // Iterate through the files and load into a BadRaster array

        for (File file : srcFiles) {
            BadRaster raster = new BadRaster();
            try {
                System.out.println("Loading " + file.getName());
                raster.loadFromFile(file);
                srcRasters.add(raster);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR: Failed to open source image: " + file.getName());
                System.exit(1);
            }
        }

        // Save the rasters to files
        for (BadRaster raster : srcRasters) {
            System.out.println("Saving " + raster.getFilename());
            try {
                raster.writeToPath(outputPath + raster.getFilename());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        /*
        if (args.length < 2) {
            System.out.println("No kernel path provided. Using default kernel.");
        }
        */
        // Get an array of file handles ----------------------------------------
        */
        return;
        /*
        // Set processing parameters -------------------------------------------
        // blockSize is the size in pixels of each block associated with a task
        final int blockSize = 8;
        // nThreads is the (supposedly optimal) number of threads to run in
        // the thread pool. Determined by number of processors.
        final int nThreads = Runtime.getRuntime().availableProcessors();
        // Path to save the image
        final String destinationPath = "../test/output.png";
        // Path to base kernel
        final String baseKernelPath = "../test/kernels/diamondblur_11x11.json";


        // Load the source image into a raster
        BadRaster srcRaster = new BadRaster();
        try {
            //srcRaster.loadFromPath(args[0])
            //srcRaster.loadFromPath("../test/datasets/image/rgb3x3.png");
            //srcRaster.loadFromPath("../test/datasets/image/hw8_z20_20150807T000000.png");
            srcRaster.loadFromPath("../test/datasets/image/hw8_z2_20150819T060000_640x640.png");
            //srcRaster.loadFromPath("../test/datasets/image/pattern_128x128.png");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: Failed to open source image.");
            System.exit(1);
        }
        System.out.printf("Loaded %s. Dimensions: %dx%d\n",args[0],srcRaster.getWidth(),srcRaster.getHeight());

        // Create a new raster to write the filtered image data to
        BadRaster destRaster = new BadRaster(srcRaster.getBands(),srcRaster.getWidth(),srcRaster.getHeight());
        // Create a task queue and add items
        TaskQueue tasks = new TaskQueue();
        // Divide the source image into blocks and iterate through
        // Each task has an associated Kernel derived from a BaseKernel
        BaseKernel baseKernel = new BaseKernel(baseKernelPath);
        System.out.printf("Using kernel: %s, sum: %f\n",baseKernel.name,KOps.sum(baseKernel.getKernel()));
        int nBlocks = 0;
        int xBlocks = srcRaster.getWidth()/blockSize;
        int yBlocks = srcRaster.getHeight()/blockSize;
        if (srcRaster.getWidth() % blockSize != 0 || srcRaster.getHeight() % blockSize != 0)
            System.out.println("Warning: Image dimensions not a multiple of blocksize. Some pixels may be ignored.");
        for (int i = 0; i < xBlocks; ++i) {
            for (int j = 0; j < yBlocks; ++j) {
                float [] point = {i/((float)xBlocks),j/((float)yBlocks)};
                // Create a task
                KTask task = new KTask(i*blockSize,j*blockSize,blockSize,blockSize);
                task.setInputRaster(srcRaster);
                task.setOutputRaster(destRaster);
                //Kernel kernel = baseKernel.getModulatedKernel(point);
                //task.setKernel(kernel);
                task.setKernel(baseKernel);
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

        // Save the destination raster to the destination filepath
        System.out.printf("Saving image to %s. Dimensions: %dx%d\n",destinationPath,destRaster.getWidth(),destRaster.getHeight());
        try {
            destRaster.writeToPath(destinationPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        */
    }
}
