/****
 * Kernelizr.java
 ****
 * Processes an image with a multithreaded, dynamic kernel-
 * based filtering algorithm.
 */

package kernelizr;

public class Kernelizr {
    public static void main(String[] args) {
        System.out.println("Hello, I'm Kernelizr, an image filter.");
        for (String s: args) {
            System.out.println(s);
        }
    }
}
