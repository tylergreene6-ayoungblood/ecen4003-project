/*
 * KKernel.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * A non-dynamic kernel, with floating point data. Kernels are small rectangular
 * matrices describing an image filter. The dimensions should be mxn, where
 * both m and n are odd numbers: m,n = 2k+1 for all k.
 */
public class KKernel {
    /** The floating point kernel, a 2D array */
    protected float[][] kernel;
    /** The width of the kernel. */
    protected int width;
    /** The height of the kernel. */
    protected int height;
    /**
     * Creates a KKernel with a default kernel. The default kernel is an
     * identity matrix for image processing (zero everywhere except the center)
     * @param width The width of the kernel
     * @param height The Height of the kernel
     */
    public KKernel(int width, int height) {
        this.width = width;
        this.height = height;
        kernel = new float[width][height];
        // Default "identity" matrix (one in the middle, zero elsewhere)
        for (int i = 0; i < kernel.length; ++i)
            for (int j = 0; j < kernel[i].length; ++j)
                kernel[i][j] = 0.0f;
        kernel[(int)Math.floor(width/2.0f)][(int)Math.floor(height/2.0f)] = 1.0f;
    }
    /**
     * Set new values for the kernel from a floating point array.
     * @param newKernel The floating point data to update the kernel with.
     * The dimensions of newKernel must match the existing kernel size.
     */
    public void setKernel(float[][] newKernel) {
        kernel = newKernel;
    }
    /**
     * Set a specified element of the kernel
     * @param x The x index to modify
     * @param y The y index to modify
     * @param value The value to set
     */
    public void set(int x, int y, float value) {
        kernel[x][y] = value;
    }
    /**
     * Normalize the kernel. Normalize ensures that the sum of the entire
     * kernel is 1.
     */
    public void normalize() {
        double sum = 0.0;
        for (int i = 0; i < kernel.length; ++i)
            for (int j = 0; j < kernel[i].length; ++j)
                sum += kernel[i][j];
        for (int i = 0; i < kernel.length; ++i)
            for (int j = 0; j < kernel[i].length; ++j)
                kernel[i][j] = (float)(kernel[i][j]/sum);
    }
    /**
     * Get the width of the kernel.
     * @return The width of the kernel
     */
    public int getWidth() {
        return width;
    }
    /**
     * Get the height of the kernel.
     * @return The height of the kernel
     */
    public int getHeight() {
        return height;
    }
    /**
     * Get the "half-width" of the kernel, which is (width-1)/2.
     * For sane kernels (which have an odd width), this is the number of pixels
     * on each side of the center.
     * @return The "half-width" of the kernel
     */
    public int getHalfWidth() {
        return (width-1)/2;
    }
    /**
     * Get the "half-height" of the kernel, which is (height-1)/2.
     * For sane kernels (which have an odd height), this is the number of pixels
     * above and below the center
     * @return The "half-height" of the kernel
     */
    public int getHalfHeight() {
        return (height-1)/2;
    }
    /**
     * Get a component of the kernel at the specified position.
     * @param x The x coordinate of the kernel component
     * @param y The y coordinate of the kernel component
     * @return The specified component of the kernel
     */
    public float get(int x, int y) {
        return kernel[x][y];
    }
    /**
     * Return a string representation of the kernel. Values are scaled to [0,1],
     * floating point.
     * @return A string representation of the kernel
     */
    public String toString() {
        String string = "";
        for (int i = 0; i < kernel.length; ++i) {
            for (int j = 0; j < kernel[i].length; ++j) {
                string += String.format("%5.3f, ", kernel[i][j]);
            }
            string += String.format("\n");
        }
        return string;
    }
}
