/****
 * Kernelizr.java
 ****
 * Processes an image with a multithreaded, dynamic kernel-
 * based filtering algorithm.
 */

//package kernelizr;

import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import java.util.concurrent.*;

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
                kernel.setKernel(new byte[][] {{0,1,0},{1,-4,1},{0,1,0}});
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

    }
}
