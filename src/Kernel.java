/*
 * Kernel.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * A non-dynamic, three-dimensional kernel, with floating point data.
 * A kernel is a 3D convolution matrix that describes a filtering algorithm
 * on a 3D dataset. The dimensions should be mxnxp, where m, n, and p
 * are odd numbers: m,n,p = 2k+1 for all k. The major dimension is the Z
 * dimension; that is, the data is stored as [Z][X][Y] (depth, width, height)
 * and input data arrays are expected in this format.
 */
public class Kernel {
    /** The floating point kernel, a 3D array */
    protected float[][][] kernel;
    /** The width (X dimension) of the kernel. */
    protected int width;
    /** The height (Y dimension) of the kernel. */
    protected int height;
    /** The depth (Z dimension) of the kernel. */
    protected int depth;
    /**
     * An empty no-arg constructor. Necessary for subclasses to do their thing.
     */
    public Kernel() {
        //
    }
    /**
     * Creates a Kernel with a default kernel. The default kernel is an
     * identity matrix for image processing (zero everywhere except the center)
     * @param width The width of the kernel
     * @param height The height of the kernel
     * @param depth The depth of the kernel
     */
    public Kernel(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        kernel = new float[depth][width][height]; // [Z][X][Y]
        // Default "identity" matrix (one in the middle, zero elsewhere)
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                for (int k = 0; k < depth; ++k)
                    kernel[i][j][k] = 0.0f;
        kernel[(int)Math.floor(width/2.0f)][(int)Math.floor(height/2.0f)][(int)Math.floor(depth/2.0f)] = 1.0f;
    }
    /**
     * Set new values for the kernel from a 3D array.
     * @param newKernel The new kernel data to update the kernel with.
     * The dimensions of newKernel must match the existing kernel size.
     */
    public void setKernel(float[][][] newKernel) {
        kernel = newKernel;
    }
    /**
     * Get the raw kernel data as a 3D array.
     @return The internal kernel array
     */
    public float[][][] getKernel() {
        return kernel;
    }
    /**
     * Set a specified element of the kernel
     * @param x The x index to modify
     * @param y The y index to modify
     * @param z The z index to modify
     * @param value The value to set
     */
    public void set(int x, int y, int z, float value) {
        kernel[z][x][y] = value;
    }
    /**
     * Get a component of the kernel at the specified position.
     * @param x The X coordinate of the kernel component
     * @param y The Y coordinate of the kernel component
     * @param z The Z coordinate of the kernel component
     * @return The specified component of the kernel
     */
    public float get(int x, int y, int z) {
        return kernel[z][x][y];
    }
    /**
     * Normalize the kernel. Normalize ensures that the sum of the entire
     * kernel is 1, which is nice when doing certain image filtering operations
     * such as blurring or sharpening (normalized kernels ensure the overall
     * brightness of the image stays mostly constant).
     */
    public void normalize() {
        double sum = KOps.sum(kernel);
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                for (int k = 0; k < depth; ++j)
                    kernel[i][j][k] = (float)(kernel[i][j][k]/sum);
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
     * Get the depth of the kernel.
     * @return The depth of the kernel
     */
    public int getDepth() {
        return depth;
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
     * Get the "half-depth" of the kernel, which is (depth-1)/2.
     * For sane kernels (which have an odd depth), this is the number of pixels
     * above and below the center
     * @return The "half-depth" of the kernel
     */
    public int getHalfDepth() {
        return (depth-1)/2;
    }
    /**
     * Return a string representation of the kernel, using the raw float values.
     * @return A string representation of the kernel
     */
    public String toString() {
        return toString(false);
    }
    /**
     * Return a string representation of the kernel, as either raw float values
     * or values scaled to byte range ([-1,1] corresponds to [-127,127])
     * @param asBytes If true, scale the kernel values to signed byte range
     * @return A string representation of the kernel
     */
    public String toString(boolean asBytes) {
        String string = "";
        for (int k = 0; k < depth; ++k) {
            for (int i = 0; i < width; ++i) {
                for (int j = 0; j < height; ++j) {
                    if (asBytes) {
                        string += String.format("%4d,", (byte)(kernel[k][i][j]*127));
                    } else {
                        string += String.format("%5.3f, ", kernel[k][i][j]);
                    }
                }
                string += String.format("\n");
            }
            string += String.format("\n");
        }
        return string;
    }
}
