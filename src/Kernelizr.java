/*
 * Kernelizr.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * Processes an image with a multithreaded, dynamic kernel-based image
 * filtering algorithm.
 */

//import javax.imageio.*;
//import java.io.*;
//import java.awt.image.*;
//import java.util.concurrent.*;

public class Kernelizr {
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
        final int blockSize = 64;

        // Open the source image
        BadRaster srcRaster = new BadRaster();
        try {
            srcRaster.loadFromPath("../test/datasets/image/rgb3x3.png");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Print the source raster data
        System.out.println(srcRaster.toString());

        // Create a kernel
        KKernel kernel = new KKernel(3,3);
        System.out.println(kernel.toString());

        // Create a new raster
        BadRaster destRaster = new BadRaster(srcRaster.getBands(),srcRaster.getWidth(),srcRaster.getHeight());

        // Naive single-threaded image filtering (for testing)
        for (int i = 0; i < srcRaster.getWidth(); ++i) {
            for (int j = 0; j < srcRaster.getHeight(); ++j) {
                float [] pixel = KOps.convolve2D(srcRaster, i, j, kernel);
                //float [] pixel = {srcRaster.getPixelComponent(i,j,0),srcRaster.getPixelComponent(i,j,1),srcRaster.getPixelComponent(i,j,2)};
                destRaster.setPixel(pixel, i, j);
            }
        }

        // Print the destination raster data
        System.out.println(destRaster.toString());

        // Save the destination raster to a file
        try {
            destRaster.writeToPath("../test/rgb3x3_filtered.png");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return;
        /*
        BufferedImage bImage = null;
        try {
            bImage = ImageIO.read(new File(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Image dimensions: " + bImage.getWidth() + "x" + bImage.getHeight() + ", type: " + bImage.getType());
        // Assume we have an RGB image. Convert the BufferedImage to a byte array
        WritableRaster imageRaster = bImage.getRaster();
        DataBufferByte imageByteBuffer = (DataBufferByte)bImage.getData().getDataBuffer();
        byte[] imageBytes = imageByteBuffer.getData();
        System.out.println("Image byte length: " + imageBytes.length);
        // Build a task queue
        ConcurrentLinkedQueue<KTask> taskQ = new ConcurrentLinkedQueue<KTask>();
        for (int i = 0; i < bImage.getWidth()/blockSize; ++i) {
            for (int j = 0; j < bImage.getHeight()/blockSize; ++j) {
                // Get the kernel for this block
                // Default to a 3x3 edge detection
                KKernel kernel = new KKernel(3,3);
                kernel.setKernel(new byte[][] {{0,63,0},{63,0,63},{0,63,0}});
                KTask task = new KTask();
                task.setKernel(kernel);
                task.setRegion(i*blockSize,j*blockSize,blockSize,blockSize);
                task.setRaster(imageRaster);
                taskQ.add(task);
            }
        }
        // Print the task queue
        int index = 0;
        for (KTask t: taskQ) {
            System.out.println(String.format("Block %5d: %s", index, t.getRegionString()));
            ++index;
        }
        // Single-threaded task queue processing
        WritableRaster filteredImageRaster = bImage.getRaster();
        while (!taskQ.isEmpty()) {
            KTask task = taskQ.poll();
            for (int i = task.getX1(); i < task.getX2(); ++i) {
                for (int j = task.getY1(); j < task.getY2(); ++j) {
                    filteredImageRaster.setPixel(i,j,KOps.convolve2D(task.getRaster(),i,j,task.getKernel()));
                }
            }
        }
        // Save image
        BufferedImage filteredImage = new BufferedImage(filteredImageRaster.getWidth(), filteredImageRaster.getHeight(), bImage.getType());
        filteredImage.setData(filteredImageRaster);
        try {
            ImageIO.write(filteredImage, "png", new File("../test/out.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        */
    }
}
