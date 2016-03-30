/*
 * KWorker.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import java.util.concurrent.atomic.*;

/**
 * A worker thread for Kernelizr concurrent image filtering. Takes a task
 * and runs it.
 */
public class KWorker implements Runnable {
    /** Task for the thread to run */
    private KTask task;
    /** Thread ID */
    private int tid;
    /** Shared thread ID counter */
    private static AtomicInteger tidCounter = new AtomicInteger(0);
    /**
     * Initialize a KWorker with a given task.
     * @param task The task to be run by this thread.
     */
    public KWorker(KTask task) {
        this.task = task;
        tid = tidCounter.getAndIncrement();
    }
    /**
     * Executes the task.
     */
    @Override
    public void run() {
        //System.out.println("Processing task: " + task.getRegionString() + " on thread: " + tid);
        for (int i = 0; i < task.getWidth(); ++i) {
            for (int j = 0; j < task.getHeight(); ++j) {
                // convolve and get the pixel value for this coordinate
                //Pixel pixel = task.getInputRaster().getPixel(task.getOriginX() + i, task.getOriginY() + j);
                try { // fail fast and exit
                    Pixel pixel = KOps.convolve3D(task.getInputRasters(), task.getOriginX() + i, task.getOriginY() + j, task.getFrame(), task.getKernel());
                    // write the pixel value back to the destination raster
                    task.getOutputRaster().setPixel(pixel, task.getOriginX() + i, task.getOriginY() + j);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        //System.out.println("Done with task: " + task.getRegionString() + " on thread: " + tid);
    }
}
