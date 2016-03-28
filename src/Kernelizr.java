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
     * The main method of Kernelizr. Handles the higher-level process:
     * - loads an image file
     * - kicks off the image processing
     * - saves the image to a file
     * @param args The command-line args for the main program.
     */
    public static void main(String[] args) {
        System.out.println("\nHello, I'm Kernelizr, an image filter.");
        // Print arguments
        for (String s: args) {
            System.out.println(s);
        }
        // The first argument should be a path to an image file.
        // If it is not, complain. Otherwise, try to open it
        if (args.length < 1) {
            System.out.println("No file path provided. Exiting.");
            return;
        }
        // Set processing parameters
        final int blockSize = 32;

        // Open the source image
        BadRaster srcRaster = new BadRaster();
        try {
            srcRaster.loadFromPath("../test/datasets/image/rgb3x3.png");
            srcRaster.loadFromPath("../test/datasets/image/103-menger-3840x2160-2_cropped_640x640.png");
            srcRaster.loadFromPath("../test/datasets/image/hw8_z2_20150819T060000_640x640.png");
            srcRaster.loadFromPath("../test/datasets/image/pattern_128x128.png");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Print the source image dimensions
        System.out.printf("Loaded image. Dimensions: %dx%d\n",srcRaster.getWidth(),srcRaster.getHeight());

        // Create a new raster
        BadRaster destRaster = new BadRaster(srcRaster.getBands(),srcRaster.getWidth(),srcRaster.getHeight());

        // Create a task queue and add items
        TaskQueue tasks = new TaskQueue();
        // Divide the source image into blocks and iterate through
        for (int i = 0; i < srcRaster.getWidth()/blockSize; ++i) {
            for (int j = 0; j < srcRaster.getHeight()/blockSize; ++j) {
                // Create a task
                KTask task = new KTask(i*blockSize,j*blockSize,blockSize,blockSize);
                task.setInputRaster(srcRaster);
                task.setOutputRaster(destRaster);
                KKernel kernel = new KKernel(3,3);
                kernel.setKernel(new float [][] {{0.0625f,0.125f,0.0625f},{0.125f,0.25f,0.125f},{0.0625f,0.125f,0.0625f}});
                task.setKernel(kernel);
                tasks.push(task);
            }
        }
        System.out.printf("Task queue built. Tasks: %d\n", tasks.length());

        ExecutorService executor = Executors.newFixedThreadPool(4);
        while (!tasks.isEmpty()) {
            Runnable worker = new KWorker(tasks.pop());
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        System.out.println("Task queue empty, thread pool shutdown");

        // Print the new image dimensions
        System.out.printf("Saving image. Dimensions: %dx%d\n",destRaster.getWidth(),destRaster.getHeight());

        // Save the destination raster to a file
        try {
            destRaster.writeToPath("../test/rgb3x3_filtered.png");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
