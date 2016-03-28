/*
 * Pixel.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * A simple Pixel class. A pixel is an array of float data. Typically, a pixel
 * has three bands (for RGB colourspaces), but Pixel can have an arbitrary
 * band count. Pixel also provides convenience methods for RGB[A] pixels when
 * an RGB[A] pixel is ordered 0,1,2[,A].
 */
public class Pixel {
    /** The pixel data, a float array */
    protected float[] p;
    /** The band count */
    protected int bands;
    /**
     * Creates a Pixel with the default 3 bands and sets all data to zero.
     */
    public Pixel() {
        Pixel(3);
    }
    /**
     * Creates a pixel with an arbitrary number of bands and sets all data
     * to zero.
     * @param bands The number of bands
     */
    public Pixel(int bands) {
        p = new float[bands];
        for (int i = 0; i < bands; ++i) p[i] = 0.0f;
    }
    /**
     * Creates a pixel from a float array. The number of bands will be the
     * length of the float array.
     * @param data The float data to convert to a Pixel
     */
    public Pixel(float[] data) {
        p = data;
    }
    /**
     * Get a specified component of a pixel
     * @param band The band to retrieve
     * @return The selected band
     */
    public float get(int band) {
        return p[band];
    }
    /**
     * Set a specified component of a pixel
     * @param band The band to set
     * @param value The value to set
     */
    public void set(int band, float value) {
        p[band] = values;
    }
    /**
     * Set the RGB components of a pixel (assumed to be bands 0,1,2)
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     */
    public void set(float r, float g, float b) {
        p[0] = r;
        p[1] = g;
        p[2] = b;
    }
    /**
     * Set the RGBA components of a pixel (assumed to be bands 0,1,2,3)
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     * @param a The alpha component
     */
    public void set(float r, float g, float b, float a) {
        set(r,g,b);
        p[3] = a;
    }
    /**
     * Get the red component of the pixel (assumed to be band 0)
     * @return The 0th component of the pixel
     */
    public float r() {
        return p[0];
    }
    /**
     * Get the green component of the pixel (assumed to be band 1)
     * @return The 1st component of the pixel
     */
    public float g() {
        return p[1];
    }
    /**
     * Get the blue component of the pixel (assumed to be band 2)
     * @return The 2nd component of the pixel
     */
    public float b() {
        return p[2];
    }
    /**
     * Get the alpha component of the pixel (assumed to be band 3)
     * @return The 3rd component of the pixel
     */
    public float a() {
        return p[3];
    }
    /**
     * Set the red component of the pixel (assumed to be band 0)
     * @param r The red value
     */
    public void r(float r) {
        p[0] = r;
    }
    /**
     * Set the green component of the pixel (assumed to be band 1)
     * @param g The green value
     */
    public void g(float g) {
        p[1] = r;
    }
    /**
     * Set the blue component of the pixel (assumed to be band 2)
     * @param b The blue value
     */
    public void b(float b) {
        p[2] = b;
    }
    /**
     * Set the alpha component of the pixel (assumed to be band 3)
     * @param a The alpha value
     */
    public void a(float a) {
        p[3] = a;
    }
}
