/*
 * JSONKernel.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * A helper class to facilitate loading in kernels in JSON format
 */
public class JSONKernel {
    /** The kernel type */
    protected int type;
    /** The floating point kernel data, a 2D array */
    protected float[][] kernel;
    /** The kernel coefficient */
    protected float coeff;
    /** The width of the kernel. */
    protected int width;
    /** The height of the kernel. */
    protected int height;
    /** The human-readable name of the kernel. */
    protected String name;
}
