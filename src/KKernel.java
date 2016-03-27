/****
 * KKernel.java
 ****
 * A Kernelizr static kernel object.
 */

//package kernelizr;

//import javax.imageio.*;
//import java.io.*;
//import java.awt.image.*;
//import java.util.concurrent.*;

public class KKernel {
    private byte[][] kernel;
    private int width, height;
    public KKernel(int width, int height) {
        this.width = width;
        this.height = height;
        kernel = new byte[width][height];
    }
    // set a new kernel and normalize it
    public void setKernel(byte[][] newKernel) {
        kernel = newKernel;
        double sum = 0.0;
        for (int i = 0; i < kernel.length; ++i) {
            for (int j = 0; j < kernel[i].length; ++j) {
                sum += kernel[i][j]/255.0;
            }
        }
        for (int i = 0; i < kernel.length; ++i) {
            for (int j = 0; j < kernel[i].length; ++j) {
                kernel[i][j] = (byte)Math.round(kernel[i][j]*(255.0/sum));
            }
        }
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getHalfWidth() {
        return (width-1)/2;
    }
    public int getHalfHeight() {
        return (height-1)/2;
    }
    public int get(int x, int y) {
        return kernel[x][y];
    }
    // print the kernel as either raw byte values or scaled to 0..1.0
    public void printKernel(boolean scaled) {
        for (int i = 0; i < kernel.length; ++i) {
            for (int j = 0; j < kernel[i].length; ++j) {
                if (scaled) {
                    System.out.printf("%3d",kernel[i][j]);
                } else {
                    System.out.printf("%5.4f",kernel[i][j]/255.0);
                }
            }
            System.out.println();
        }
    }
}
