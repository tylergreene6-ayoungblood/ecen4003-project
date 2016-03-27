/****
 * KOps.java
 ****
 * A class providing image kernel operations.
 * This is a pseudo static class (should not be instantiated)
 */

import java.awt.image.*;

public final class KOps {
    private KOps() { // private constructor emulates static class behaviour

    }
    // given a raster, an XY coord, and 2D kernel, return a single pixel (3 element array)
    public static int[] convolve2D(Raster raster, int x, int y, KKernel kernel) {
        int [] newPixel = {0,0,0};
        for (int i = 0; i < kernel.getWidth(); ++i) {
            for (int j = 0; j < kernel.getHeight(); ++j) {
                for (int b = 0; b < 3; ++b) {
                    int xi = x - kernel.getHalfWidth() + i;
                    int yi = y - kernel.getHalfHeight() + j;
                    xi = xi >= raster.getWidth()?raster.getWidth()-1:xi;
                    xi = xi < 0?0:xi;
                    yi = yi >= raster.getHeight()?raster.getHeight()-1:yi;
                    yi = yi < 0?0:yi;
                    //System.out.printf("xi: %d, yi: %d\n", xi, yi);
                    int pixelPart = raster.getSample(xi, yi, b);
                    newPixel[b] += (kernel.get(i,j) * pixelPart)/255;
                }
            }
        }
        return newPixel;
    }
}
