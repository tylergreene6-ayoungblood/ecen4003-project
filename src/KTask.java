/****
 * KTask.java
 ****
 * A Kernelizr task object.
 */

//package kernelizr;

//import javax.imageio.*;
//import java.io.*;
import java.awt.image.*;
//import kernelizr.*;

public class KTask {
    WritableRaster imageRaster;
    int startX, startY, width, height;
    KKernel kernel;
    void setKernel(KKernel k) {
        kernel = k;
    }
    void setRegion(int sX, int sY, int w, int h) {
        startX = sX;
        startY = sY;
        width = w;
        height = h;
    }
    void setImageRaster(WritableRaster iR) {
        imageRaster = iR;
    }
    String getRegionString() {
        return String.format("[ %d, %d, %d, %d ]", startX, startY, width, height);
    }
}
