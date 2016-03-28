/*
 * KOps.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import com.google.gson.Gson;
import java.io.*;
import java.util.List;

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
     * @param raster The source raster.
     * @param x The x coordinate to center around.
     * @param y The y coordinate to center around.
     * @param kernel The kernel to convolve the source raster with.
     * @return A n-band float vector representing a pixel, where n is
     * the number of bands.
     */
    public static float[] convolve2D(BadRaster raster, int x, int y, KKernel kernel) {
        int hw = kernel.getHalfWidth();
        int hh = kernel.getHalfHeight();
        float [] newPixel = new float[raster.getBands()];
        // iterate through bands
        for (int b = 0; b < raster.getBands(); ++b) {
            // iterate through kernel
            for (int i = 0; i < kernel.getWidth(); ++i) {
                for (int j = 0; j < kernel.getHeight(); ++j) {
                    int xi = x - hw + i;
                    int yi = y - hh + j;
                    newPixel[b] += kernel.get(i, j) * raster.getPixelComponent(xi,yi,b);
                }
            }
        }
        return newPixel;
    }
    /**
     * Returns a four-element boolean array indicate which edges of the
     * passed array are less than a threshold. Ordered [top,right,bottom,left]
     * @param kernel A kernel in raw floating point format
     * @param threshold The threshold to compare against.
     * @return A boolean array indicate which sides are all below the threshold.
     */
    public static boolean[] edgeCheck(float[][] kernel, float threshold) {
        boolean [] edges = new boolean[4];
        int width = kernel.length;
        int height = kernel[0].length;
        // Check top edge (j = 0)
        edges[0] = true;
        for (int i = 0; i < width; ++i) if (kernel[i][0] > threshold) edges[0] = false;
        // check bottom edge (j = height-1)
        edges[2] = true;
        for (int i = 0; i < width; ++i) if (kernel[i][height-1] > threshold) edges[2] = false;
        // check right edge (i = width-1)
        edges[1] = true;
        for (int j = 0; j < height; ++j) if (kernel[width-1][j] > threshold) edges[1] = false;
        // check left edge (i = 0)
        edges[3] = true;
        for (int j = 0; j < height; ++j) if (kernel[0][j] > threshold) edges[3] = false;
        return edges;
    }
    /**
     * Returns the sum of a kernel. Utility method.
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
     * Load a kernel from JSON. GSON parses the JSON file into a JSONKernel
     * object, which is then converted to a regular KKernel
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
