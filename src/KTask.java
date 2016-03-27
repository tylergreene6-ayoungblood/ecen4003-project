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
    private Raster raster;
    private int startX, startY, width, height;
    private KKernel kernel;
    public void setKernel(KKernel k) {
        kernel = k;
    }
    public KKernel getKernel() {
        return kernel;
    }
    public void setRegion(int sX, int sY, int w, int h) {
        startX = sX;
        startY = sY;
        width = w;
        height = h;
    }
    public void setRaster(Raster r) {
        raster = r;
    }
    public Raster getRaster() {
        return raster;
    }
    public int getX1() {
        return startX;
    }
    public int getX2() {
        return startX + width;
    }
    public int getY1() {
        return startY;
    }
    public int getY2() {
        return startY + height;
    }
    String getRegionString() {
        return String.format("[ %d, %d, %d, %d ]", startX, startY, width, height);
    }
}
