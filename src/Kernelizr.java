/*
 * Kernelizr.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import java.util.concurrent.*;

/**
 * Processes an image with a multithreaded, dynamic kernel-based image
 * filtering algorithm.
 */
public class Kernelizr {
    /**
     * The main method of Kernelizr. Handles the higher-level process:<br>
     * - loads an image file<br>
     * - kicks off the image processing<br>
     * - saves the image to a file<br>
     * @param args The command-line args for the main program.
     */
    public static void main(String[] args) {
        System.out.println("\nHello, I'm Kernelizr, an image filter.");
        // The first argument should be a path to an image file.
        // If it is not, complain. Otherwise, try to open it
        if (args.length < 1) {
            System.out.println("No image path provided.");
            System.out.println("Usage:");
            System.out.println("    java Kernelizr image_path [kernel_path]");
            System.out.println("    ");
            return;
        }
        if (args.length < 2) {
            System.out.println("No kernel path provided. Using default kernel.");
        }
        // Set processing parameters -------------------------------------------
        // blockSize is the size in pixels of each block associated with a task
        final int blockSize = 8;
        // nThreads is the (supposedly optimal) number of threads to run in
        // the thread pool. Determined by number of processors.
        final int nThreads = Runtime.getRuntime().availableProcessors();
        // Path to save the image
        final String destinationPath = "../test/output.png";
        // Path to base kernel
        final String baseKernelPath = "../test/kernels/gaussianblur_11x11.json";


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
        // Each task has an associated KKernel derived from a BaseKernel
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
                KKernel kernel = baseKernel.getModulatedKernel(point);
                task.setKernel(kernel);
                tasks.push(task);
                System.out.println("Pushed task: " + task.getRegionString() + " w/ modulation: [" + point[0] + "," + point[1] + "]");
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
    }
}
