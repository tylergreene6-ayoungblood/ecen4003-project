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
     * Load a kernel from JSON. GSON parses the JSON file into a JSONKernel
     * object, which is then converted to a regular KKernel
     * @param path The JSON kernel path
     * @return A JSONKernel derived from the JSON kernel
     */
    public static JSONKernel kernelFromJSONPath(String path) {
        Gson gson = new Gson();

        try {

            System.out.println("Reading JSON from a file");

            BufferedReader br = new BufferedReader(new FileReader(path));

            //convert the json string back to object
            JSONKernel kernelObj = gson.fromJson(br, JSONKernel.class);
            System.out.println(kernelObj.type);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSONKernel;
    }
}
