/*
 * Kernelizr.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

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
        final int blockSize = 64;

        // Open the source image
        BadRaster srcRaster = new BadRaster();
        try {
            srcRaster.loadFromPath("../test/datasets/image/rgb3x3.png");
            srcRaster.loadFromPath("../test/datasets/image/103-menger-3840x2160-2_cropped_640x640.png");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Print the source raster data
        //System.out.println(srcRaster.toString());
        // Print the source image dimensions
        System.out.printf("Loaded image. Dimensions: %dx%d\n",srcRaster.getWidth(),srcRaster.getHeight());

        // Create a kernel
        KKernel kernel = new KKernel(3,3);
        kernel.setKernel(new float [][] {{1/16.0f,2/16.0f,1/16.0f},{2/16.0f,4/16.0f,2/16.0f},{1/16.0f,2/16.0f,1/16.0f}});
        kernel.setKernel(new float [][] {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}});

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
        //System.out.println(destRaster.toString());
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
