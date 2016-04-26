/*
 * GaussianKernelFactory.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * Generates Gaussian kernels, per the formulas on
 * https://en.wikipedia.org/wiki/Gaussian_function
 * Provides 1D, 2D, and 3D generation methods.
 * This is a pseudo-static class (should not be instantiated).
 */
public final class GaussianKernelFactory {
    /**
     * Private constructor. Emulates static class behaviour.
     */
    private GaussianKernelFactory() {
        //
    }
    /**
     * Creates a one-dimensional gaussian kernel with a given theta and size.
     * @param theta The theta value for the gaussian distribution
     * @param size The length of the kernel. Should be an odd number &gt; 1.
     * @return An array containing the calculated kernel.
     */
    public static double[] get1D(double theta, int size) {
        double[] g = new double[size];
        for (int i = 0; i < size; ++i) {
            g[i] = getDiscrete1D(theta,i-(size/2));
        }
        return g;
    }
    /**
     * Creates a two-dimensional gaussian kernel with a given theta and size.
     * @param theta The theta value for the gaussian distribution
     * @param size The square size of the kernel. Should be an odd number &gt; 1.
     * @return An array containing the calculated kernel.
     */
    public static double[][] get2D(double theta, int size) {
        double[][] g = new double[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                g[i][j] = getDiscrete2D(theta,i-(size/2),j-(size/2));
            }
        }
        return g;
    }
    /**
     * Creates a three-dimensional gaussian kernel with a given theta and size.
     * @param theta The theta value for the gaussian distribution
     * @param size The cubic size of the kernel. Should be an odd number &gt; 1.
     * @return An array containing the calculated kernel.
     */
    public static double[][][] get3D(double theta, int size) {
        double[][][] g = new double[size][size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                for (int k = 0; k < size; ++k) {
                    g[i][j][k] = getDiscrete3D(theta,i-(size/2),j-(size/2),k-(size/2));
                }
            }
        }
        return g;
    }
    /**
     * Calculate a discrete value of a single point in a 1D distribution.
     * @param theta The theta value for the gaussian distribution
     * @param x The point at which to calculate a discrete value.
     * @return The discrete gaussian value
     */
    public static double getDiscrete1D(double theta, int x) {
        double ts2 = 2 * theta * theta;
        return Math.exp(-(x*x)/ts2)/(ts2*Math.PI);
    }
    /**
     * Calculate a discrete value of a single point in a 2D distribution.
     * @param theta The theta value for the gaussian distribution
     * @param x The x coordinate of the discrete value.
     * @param y The y coordinate of the discrete value.
     * @return The discrete gaussian value
     */
    public static double getDiscrete2D(double theta, int x, int y) {
        double ts2 = 2 * theta * theta;
        return Math.exp(-((x*x)/ts2 + (y*y)/ts2))/(ts2*Math.PI);
    }
    /**
     * Calculate a discrete value of a single point in a 3D distribution.
     * @param theta The theta value for the gaussian distribution
     * @param x The x coordinate of the discrete value.
     * @param y The y coordinate of the discrete value.
     * @param z The z coordinate of the discrete value.
     * @return The discrete gaussian value
     */
    public static double getDiscrete3D(double theta, int x, int y, int z) {
        double ts2 = 2 * theta * theta;
        return Math.exp(-((x*x)/ts2 + (y*y)/ts2 + (z*z)/ts2))/(ts2*Math.PI);
    }
}
