/*
 * KTask.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * KTask represents a task which is run by Kernelizr worker threads.
 */
public class KTask {
    /** The raster associated with the task. */
    protected BadRaster raster;
    /** The x-coordinate of the origin of region of interest. */
    protected int startX;
    /** The y-coordinate of the origin of the region of interest. */
    protected int startY;
    /** The width of the region of interest. */
    protected int width;
    /** The height of the region of interest. */
    protected int height;
    /** The kernel associated with the task. */
    protected KKernel kernel;
    /**
     * Get a new KTask with a specific region.
     * @param originX The x-coordinate of the origin of the region
     * @param originY The y-coordinate of the origin of the region
     * @param width The width of the region
     * @param height The width of the region
     */
    public KTask(int originX, int originY, int width, int height) {
        this.startX = originX;
        this.startY = originY;
        this.width = width;
        this.height = height;
    }
    /**
     * Set the kernel of the task.
     * @param kernel The kernel to add to the task
     */
    public void setKernel(KKernel kernel) {
        this.kernel = kernel;
    }
    /**
     * Get the kernel from the task.
     * @return The kernel of the task.
     */
    public KKernel getKernel() {
        return kernel;
    }
    /**
     * Set the raster for the task.
     * @param raster The raster to add to the task
     */
    public void setRaster(BadRaster raster) {
        this.raster = raster;
    }
    /**
     * Get the raster from the task.
     * @return The raster of the task.
     */
    public BadRaster getRaster() {
        return raster;
    }
    /**
     * Get the width associated with the task.
     * @return The width of the task region.
     */
    public int getWidth() {
        return width;
    }
    /**
     * Get the height associated with the task.
     * @return The height of the task region.
     */
    public int getHeight() {
        return height;
    }
    /**
     * Set the region of interest for the task
     * @param originX The x coordinate of the origin
     * @param originY The y coordinate of the origin
     * @param width The width of the region of interest
     * @param height The height of the region of interest
     */
    public void setRegion(int originX, int originY, int width, int height) {
        startX = originX;
        startY = originY;
        this.width = width;
        this.height = height;
    }
    /**
     * Return a string describing the region of interest. Formatted as
     * both the origin/size and the corner coordinates.
     * @return The string describing the region
     */
    String getRegionString() {
        return String.format("origin: (%d,%d), size: (%d,%d); [ %d, %d, %d, %d ]",
               startX, startY, width, height,
               startX, startY, startX + width, startY + height);
    }
}
