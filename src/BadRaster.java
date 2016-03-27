/**
 * A class representing a rectangular array of pixels. Far superior
 * to java.awt.image.Raster. A BadRaster encapsulates a three dimensional
 * dimensional pixel array that describes the particular raster frame.
 * <p>
 *
 *
 */

import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;

public class BadRaster {
    /** The byte array of raster image data */
    protected float[][][] data;
    /** The number of bands */
    protected int bands;
    /** The width of the raster (the "x" dimension) */
    protected int width;
    /** The height of the raster (the "y" dimension) */
    protected int height;

    /**
     * Creates a BadRaster and leaves it uninitialized.
     */
    public BadRaster() {
        //
    }
    /**
     * Creates a BadRaster with the given dimensions and sets all the
     * data to zero.
     */
    public BadRaster(int bands, int width, int height) {
        this.bands = (bands > 0) ? bands : 1;
        this.width = (width > 0) ? width : 1;
        this.height = (height > 0) ? height : 1;
        data = new float[bands][width][height];
    }
    /**
     * Load a raster from a filepath
     */
    public void loadFromPath(String path) throws IOException {
        loadFromFile(new File(path));
    }
    /**
     * Load a raster from a File object. This assumes a sane RGB image file.
     */
    public void loadFromFile(File file) throws IOException {
        // Get a buffered image and initialize the BadRaster fields
        BufferedImage bufferedImage = ImageIO.read(file);
        System.out.println("Type: " + bufferedImage.getType());
        this.bands = 3; // assume 3 bands (RGB)
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
        data = new float[bands][width][height];
        /*
        // Get the byte array out of a BufferedImage
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte byteBuffer = (DataBufferByte)raster.getDataBuffer();
        byte[] imageBytes = byteBuffer.getData();
        // Move the byte array into the float array
        for (int i = 0; i < bufferedImage.getWidth(); ++i) {
            for (int j = 0; j < bufferedImage.getHeight(); ++j) {
                data[0][i][j] =
                data[1][i][j] =
                data[2][i][j] =
            }
        }
        */
        // Get the pixel data out of the BufferedImage and convert it to floats
        // RGB data is ordered [0,1,2] and scaled to [0,1]
        for (int i = 0; i < bufferedImage.getWidth(); ++i) {
            for (int j = 0; j < bufferedImage.getHeight(); ++j) {
                int pixel = bufferedImage.getRGB(j,i); // transpose
                data[0][i][j] = ((pixel & 0xff0000) >> 16)/255.0f; // red
                data[1][i][j] = ((pixel & 0xff00) >> 8)/255.0f; // green
                data[2][i][j] = (pixel & 0xff)/255.0f; // blue
            }
        }
    }
    // Write the raster to a file path
    public void writeToPath(String path) throws IOException {
        writeToFile(new File(path));
    }
    // Write the raster to a file
    public void writeToFile(File file) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, height, 5);
        for (int i = 0; i < bufferedImage.getWidth(); ++i) {
            for (int j = 0; j < bufferedImage.getHeight(); ++j) {
                int pixel = 0;
                pixel |= (int)(data[0][i][j]*255) << 16; // red
                pixel |= (int)(data[1][i][j]*255) << 8; // green
                pixel |= (int)(data[2][i][j]*255); // blue
                bufferedImage.setRGB(j,i, pixel); // transpose
            }
        }
        ImageIO.write(bufferedImage, "png", file);
    }
    public String toString(int band) {
        String rv = String.format("BadRaster (band %d): (%dx%d)\n", band, width, height);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                rv += String.format("%5.3f, ",data[band][i][j]);
            }
            rv += String.format("\n");
        }
        return rv;
    }
}
