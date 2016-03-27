/*
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

 import java.io.*;
 import javax.imageio.*;
 import java.awt.image.BufferedImage;

/**
 * A class representing a rectangular array of pixels. Far superior
 * to java.awt.image.Raster. A BadRaster encapsulates a three dimensional
 * dimensional pixel array that describes the particular raster frame.
 * <p>
 * A BadRaster defines values for pixels occupying a rectangular area.
 * The rectangle is bounded by (0,0) and (width,height).
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
     * Return a string representation of a specified raster band.
     * Do not call this on a large raster!
     * @param band The band to display
     */
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
    /**
     * Return a string representation of the entire raster.
     * Bands are printed as separate sections, ordered RGB.
     * Do not call this on a large raster!
     */
    public String toString() {
        return toString(0) + toString(1) + toString(2);
    }
}
