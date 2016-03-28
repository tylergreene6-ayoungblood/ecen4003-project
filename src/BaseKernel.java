/*
 * BaseKernel.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * A dynamic, with floating point data. Kernels are small rectangular
 * matrices describing an image filter. The dimensions should be mxn, where
 * both m and n are odd numbers: m,n = 2k+1 for all k.
 * BaseKernel describes an image kernel as a function of space. By specifying
 * a point, BaseKernel can generate a static kernel (KKernel)
 */
public class BaseKernel extends KKernel {
    /**
     * Creates a BaseKernel from a JSONKernel.
     * @param jKernel The JSONKernel to copy from
     */
    public BaseKernel(JSONKernel jKernel) {
        this.width = jKernel.width;
        this.height = jKernel.height;
        for (int i = 0; i < this.width; ++i)
            for (int j = 0; j < this.height; ++j)
                kernel[i][j] = jKernel.kernel[i][j] * jKernel.coeff;
    }
    /**
     * Creates a BaseKernel from a path to a kernel in JSON format.
     * @param jKernelPath The path of the JSON file to read
     */
    public BaseKernel(String jKernelPath) {
        this(KOps.kernelFromJSONPath(jKernelPath));
    }
    /**
     * Return a scaled kernel as a KKernel, trimming dimensions if necessary.
     * @param scale The factor by which to scale
     * @return A sacled and trimmed kernel
     */
    public KKernel getScaledKernel(float scale) {
        float [][] tempKernel = new float[width][height];
        float thresh = 1/256.0f;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                // scale and clip values
                float v = kernel[i][j] * scale;
                tempKernel[i][j] = (v>thresh)?v:0.0f;
            }
        }
        // perimeter check
        boolean trimHorizontal = true; // do we need to trim the top and bottom?
        for (int i = 0; i < width; ++i) if (tempKernel[i][0] > 0.0f) trimHorizontal = false;
        for (int i = 0; i < width; ++i) if (tempKernel[i][height-1] > 0.0f) trimHorizontal = false;
        boolean trimVertical = true; // do we need to trim the sides?
        for (int j = 0; j < height; ++j) if (tempKernel[0][j] > 0.0f) trimVertical = false;
        for (int j = 0; j < height; ++j) if (tempKernel[width-1][j] > 0.0f) trimVertical = false;
        KKernel newKernel = new KKernel(width - (trimVertical?2:0), height - (trimHorizontal?2:0));
        for (int i = (trimVertical?1:0); i < width - (trimVertical?1:0); ++i) {
            for (int j = (trimHorizontal?1:0); j < height - (trimHorizontal?1:0); ++j) {
                newKernel.set(i - (trimVertical?1:0), j - (trimHorizontal?1:0), tempKernel[i][j]);
            }
        }
        if (newKernel.getWidth() != width || newKernel.getHeight() != height) {
            System.out.printf("Kernel trimmed. Original size: %dx%d; new size: %dx%d\n",width,height,newKernel.getWidth(),newKernel.getHeight());
        }
        return newKernel;
    }
}
