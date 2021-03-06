/*
 * BadRaster.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

 import java.io.*;
 import javax.imageio.*;
 import java.awt.image.BufferedImage;

/**
 * A class representing a rectangular array of pixels. Far superior
 * to java.awt.image.Raster. A BadRaster encapsulates a floating point
 * pixel array. A pixel is a floating point array with an arbitrary number
 * of components, as set by {@code bands}. In a typical RGB image, {@code bands}
 * will be 3. For a monochrome image, {@code bands} will be 1.
 * <br>
 * A BadRaster defines values for pixels occupying a rectangular area.
 * The rectangle is bounded by (0,0) and (width,height). By convention,
 * pixel values are scaled to [0,1], but BadRaster does not restrict this
 * internally; pixel values can be any floating point value. BadRaster provides
 * methods to read in image data from a file and write out image data to a file.
 * These methods use java.awt.image.BufferedImage internally.
 * <br>
 * A BadRaster may also be thought of as a cube of floating point data, and
 * manipulated as such. A single value within the cube can be manipulated,
 * as can an entire "stack" (with {@code setPixel} and {@code getPixel}).
 */
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
     * @param bands  The number of bands (typically 3 for RGB)
     * @param width  The width of the image
     * @param height The height of the image
     */
    public BadRaster(int bands, int width, int height) {
        this.bands = (bands > 0) ? bands : 1;
        this.width = (width > 0) ? width : 1;
        this.height = (height > 0) ? height : 1;
        data = new float[bands][width][height];
    }
    /**
     * Load a raster from a filepath.
     * @param path The filepath to load the image from. Should be a path to
     * to an image
     * @throws IOException if the file cannot be loaded
     */
    public void loadFromPath(String path) throws IOException {
        loadFromFile(new File(path));
    }
    /**
     * Load a raster from a File object.
     * @param file The file to load. Should be an image
     * @throws IOException if the file cannot be loaded
     */
    public void loadFromFile(File file) throws IOException {
        // Get a buffered image
        BufferedImage bufferedImage = ImageIO.read(file);
        // Initialize the BadRaster fields
        this.bands = 3; // assume 3 bands (RGB)
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
        data = new float[bands][width][height];
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
    /**
     * Write the raster to the specified filepath.
     * @param path The path to write the image to
     * @throws IOException if the file cannot be written
     */
    public void writeToPath(String path) throws IOException {
        writeToFile(new File(path));
    }
    /**
     * Write the raster to the specified file.
     * @param file The file to write the image to
     * @throws IOException if the file cannot be written
     */
    public void writeToFile(File file) throws IOException {
        // Get a BufferedImage
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        // Write the data into the BufferedImage, pixel-by-pixel
        for (int i = 0; i < bufferedImage.getWidth(); ++i) {
            for (int j = 0; j < bufferedImage.getHeight(); ++j) {
                int pixel = 0;
                pixel |= (int)(data[0][i][j]*255) << 16; // red
                pixel |= (int)(data[1][i][j]*255) << 8; // green
                pixel |= (int)(data[2][i][j]*255); // blue
                bufferedImage.setRGB(j,i, pixel); // transpose
            }
        }
        // Write the image to a file
        ImageIO.write(bufferedImage, "png", file);
    }
    /**
     * Set a pixel of the raster.
     * @param pixel A float array. The length of the array should be the number
     * of bands
     * @param x     The x position of the pixel to set
     * @param y     The y position of the pixel to set
     */
    public void setPixel(float [] pixel, int x, int y) {
        for (int i = 0; i < bands; ++i)
            data[i][x][y] = pixel[i];
    }
    /**
     * Set a pixel component of the raster.
     * @param value The value to set
     * @param x     The x position of the pixel component to set
     * @param y     The y position of the pixel component to set
     * @param b     The band to set
     */
    public void setPixelComponent(float value, int x, int y, int b) {
            data[b][x][y] = value;
    }
    /**
     * Get a pixel of the raster. If the pixel requested is out of range, the
     * returned value will be clamped to an edge pixel.
     * @param x The x position of the pixel to get
     * @param y The y position of the pixel to get
     * @return An n-element float array, where n is the number of bands
     */
    public float [] getPixel(int x, int y) {
        int xi = (x<0)?0:((x>=width)?width-1:x);
        int yi = (y<0)?0:((y>=height)?height-1:y);
        float [] pixel = new float[bands];
        for (int i = 0; i < bands; ++i)
            pixel[i] = data[i][xi][yi];
        return pixel;
    }
    /**
     * Get a pixel component. If the pixel component requested is out of range,
     * the returned value will be clamped to an edge pixel component.
     * @param x The x position of the pixel component to get
     * @param y The y position of the pixel component to get
     * @param b The band of the pixel to get
     * @return The pixel component
     */
    public float getPixelComponent(int x, int y, int b) {
        int xi = (x<0)?0:((x>=width)?width-1:x);
        int yi = (y<0)?0:((y>=height)?height-1:y);
        return data[b][xi][yi];
    }
    /**
     * Get the image width
     * @return The width of the image in pixels
     */
    public int getWidth() {
        return width;
    }
    /**
     * Get the image height
     * @return The height of the image in pixels
     */
    public int getHeight() {
        return height;
    }
    /**
     * Get the number of bands in the raster.
     * @return The number of bands
     */
    public int getBands() {
        return bands;
    }
    /**
     * Return a string representation of a specified raster band, as either
     * the raw float data or scaled to unsigned byte values [0,255]
     * Do not call this on a large raster!
     * @param band The band to display
     * @param toByte If set, print the values as integers instead of floats
     * @return a string representation of the raster band
     */
    public String toString(int band, boolean toByte) {
        String rv = String.format("BadRaster (band %d): (%dx%d)\n", band, width, height);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (toByte)
                    rv += String.format("%3d ",(int)(255*data[band][i][j]));
                else
                    rv += String.format("%5.3f ",255*data[band][i][j]);
            }
            rv += String.format("\n");
        }
        return rv;
    }
    /**
     * Return a string representation of the entire raster, values as ints.
     * Bands are printed as separate sections, ordered RGB.
     * Do not call this on a large raster!
     * @return a string representation of the entire raster
     */
    public String toString() {
        return toString(0,true) + toString(1,true) + toString(2,true);
    }
}
