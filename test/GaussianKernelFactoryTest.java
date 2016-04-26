/*
 * TaskQueueTest.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 *
 * Compile and run with
 *    javac -d . -cp ../src GaussianKernelFactoryTest.java && java -cp . GaussianKernelFactoryTest
 */

/**
 * A test for GaussianKernelFactory
 */
public class GaussianKernelFactoryTest {
    /* Main method */
    public static void main(String[] args) {
        // Discrete tests
        for (int i = -3; i < 4; ++i) {
            System.out.printf("%.8f ",GaussianKernelFactory.getDiscrete1D(0.5,i));
        }
        System.out.printf("\n\n");
        for (int i = -3; i < 4; ++i) {
            for (int j = -3; j < 4; ++j) {
                System.out.printf("%.8f ",GaussianKernelFactory.getDiscrete2D(0.5,i,j));
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");
        for (int i = -2; i < 3; ++i) {
            for (int j = -2; j < 3; ++j) {
                for (int k = -2; k < 3; ++k) {
                    System.out.printf("%.6f ",GaussianKernelFactory.getDiscrete3D(0.5,i,j,k));
                }
                System.out.printf("\n");
            }
            System.out.printf("\n");
        }
        // Array tests
        double[] g1d = GaussianKernelFactory.get1D(0.5,5);
        for (int i = 0; i < g1d.length; ++i) {
            System.out.printf("%.8f ",g1d[i]);
        }
        System.out.printf("\n");
        double[][] g2d = GaussianKernelFactory.get2D(0.5,5);
        for (int i = 0; i < g2d.length; ++i) {
            for (int j = 0; j < g2d.length; ++j) {
                System.out.printf("%.8f ",g2d[i][j]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");
        double[][][] g3d = GaussianKernelFactory.get3D(0.5,5);
        for (int i = 0; i < g3d.length; ++i) {
            for (int j = 0; j < g3d.length; ++j) {
                for (int k = 0; k < g3d.length; ++k) {
                    System.out.printf("%.8f ",g3d[i][j][k]);
                }
                System.out.printf("\n");
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");
    }
}
