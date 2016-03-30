/*
 * KOps.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import com.google.gson.Gson;
import java.io.*;
//import java.util.List;
import java.util.ArrayList;

/**
 * KOps provides primitive image kernel operations. This is a pseudo-static
 * class (should not be instantiated).
 */
public final class KOps {
    /**
     * Private constructor. Emulates static class behaviour.
     */
    private KOps() {
        //
    }
    /**
     * Convolves a given pixel with a kernel.
     * @param rasters A raster array.
     * @param x The X coordinate to center around.
     * @param y The Y coordinate to center around.
     * @param z The Z coordinate to center around.
     * @param kernel The kernel to convolve the source raster with.
     * @return A n-band float vector representing a pixel, where n is
     * the number of bands.
     */
    public static Pixel convolve3D(ArrayList<BadRaster> rasters, int x, int y, int z, Kernel kernel) {
        int hw = kernel.getHalfWidth();
        int hh = kernel.getHalfHeight();
        int hd = kernel.getHalfDepth();
        Pixel pixel = new Pixel(rasters.get(0).getBands());
        // iterate through bands
        for (int b = 0; b < rasters.get(0).getBands(); ++b) {
            // iterate through kernel
            for (int k = 0; k < kernel.getDepth(); ++k) {
                for (int i = 0; i < kernel.getWidth(); ++i) {
                    for (int j = 0; j < kernel.getHeight(); ++j) {
                        int xi = x - hw + i;
                        int yi = y - hh + j;
                        int zi = z - hd + k;
                        if (zi < 0) zi = 0;
                        if (zi >= rasters.size()) zi = rasters.size()-1;
                        //System.out.printf("i, j, k: %d, %d, %d; xi, yi, zi: %d, %d, %d\n", i, j, k, xi, yi, zi);
                        pixel.add(b, kernel.get(i, j, k) * rasters.get(zi).getPixelComponent(xi,yi,b));
                    }
                }
            }
        }
        return pixel;
    }
    /**
     * Returns a six-element boolean array indicate which side of the
     * passed array are all less (absolute value) than a threshold. Ordered
     * [top,right,bottom,left,front,back].
     * @param kernel A kernel in raw floating point format
     * @param threshold The threshold to compare against.
     * @return A boolean array indicate which sides are all below the threshold.
     */
    public static boolean[] sideCheck(float[][][] kernel, float threshold) {
        boolean [] edges = new boolean[6];
        int w = kernel.length;
        int h = kernel[0].length;
        int d = kernel[0][0].length;
        float t = threshold;
        System.out.println("KOps.sideCheck() unimplemented!");
        edges[0] = false;
        edges[1] = false;
        edges[2] = false;
        edges[3] = false;
        edges[4] = false;
        edges[5] = false;
        return edges;
    }
    /**
     * Returns the sum of a 2D kernel. Utility method.
     * @param kernel A two-dimensional array to be summed
     * @return The sum of the array
     */
    public static float sum(float[][] kernel) {
        float sum = 0.0f;
        for (int i = 0; i < kernel.length; ++i)
            for (int j = 0; j < kernel[0].length; ++j)
                sum += kernel[i][j];
        return sum;
    }
    /**
     * Returns the sum of a 3D kernel. Utility method.
     * @param kernel A three-dimensional array to be summed
     * @return The sum of the array
     */
    public static double sum(float[][][] kernel) {
        double sum = 0.0f;
        for (int i = 0; i < kernel.length; ++i)
            for (int j = 0; j < kernel[0].length; ++j)
                for (int k = 0; k < kernel[0][0].length; ++k)
                    sum += kernel[i][j][k];
        return sum;
    }
    /**
     * Print a float array nicely.
     * @param data A two-dimensional array to be printed
     */
    public static void print(float[][] data) {
        for (int i = 0; i < data.length; ++i) {
            System.out.printf("[ ");
            for (int j = 0; j < data[0].length; ++j) {
                System.out.printf("%5.3f%s ", data[i][j], (j < data[0].length)?",":"");
            }
            System.out.printf("]\n");
        }
    }
    /**
     * Load a kernel from JSON. GSON parses the JSON file into a JSONKernel
     * object, which is then converted to a regular Kernel
     * @param path The JSON kernel path
     * @return A JSONKernel derived from the JSON kernel
     */
    public static JSONKernel kernelFromJSONPath(String path) {
        Gson gson = new Gson();
        try {
            // Get a buffered reader for file
            BufferedReader br = new BufferedReader(new FileReader(path));
            // convert the JSON file to JSONKernel
            JSONKernel jKernel = gson.fromJson(br, JSONKernel.class);
            return jKernel;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
