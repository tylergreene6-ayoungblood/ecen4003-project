/*
 * KTask.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * KTask represents a task which is run by Kernelizr worker threads.
 */
public class KTask {
    /** The input raster associated with the task. */
    protected BadRaster inRaster;
    /** The output raster associated with the task. */
    protected BadRaster outRaster;
    /** The x-coordinate of the origin of region of interest. */
    protected int startX;
    /** The y-coordinate of the origin of the region of interest. */
    protected int startY;
    /** The width of the region of interest. */
    protected int width;
    /** The height of the region of interest. */
    protected int height;
    /** The kernel associated with the task. */
    protected Kernel kernel;
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
    public void setKernel(Kernel kernel) {
        this.kernel = kernel;
    }
    /**
     * Get the kernel from the task.
     * @return The kernel of the task.
     */
    public Kernel getKernel() {
        return kernel;
    }
    /**
     * Set the input (source) raster for the task.
     * @param raster The input raster to add to the task
     */
    public void setInputRaster(BadRaster raster) {
        this.inRaster = raster;
    }
    /**
     * Get the input (source ) raster from the task.
     * @return The input raster of the task.
     */
    public BadRaster getInputRaster() {
        return inRaster;
    }
    /**
     * Set the output (destination) raster for the task.
     * @param raster The output raster to add to the task
     */
    public void setOutputRaster(BadRaster raster) {
        this.outRaster = raster;
    }
    /**
     * Get the output (destination) raster from the task.
     * @return The output raster of the task.
     */
    public BadRaster getOutputRaster() {
        return outRaster;
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
     * Get the x coordinate of the origin for the task.
     * @return The x coordinate of the origin.
     */
    public int getOriginX() {
        return startX;
    }
    /**
     * Get the y coordinate of the origin for the task.
     * @return The y coordinate of the origin.
     */
    public int getOriginY() {
        return startY;
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
