/*
 * TaskQueueTest.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import java.util.concurrent.atomic.*;

/**
 * A test for TaskQueue
 */
public class TaskQueueTest {
    private static long startTime = System.currentTimeMillis();
    final private ThreadLocal<Integer> THREAD_ID = new ThreadLocal<Integer>() {
        final private AtomicInteger id = new AtomicInteger(0);
        protected Integer initialValue() {
            return id.getAndIncrement();
        }
    };

    private final static int THREADS = 8;
    private final static int COUNT = 128;
    private final static int PER_THREAD = COUNT / THREADS;

    Thread[] thread = new Thread[THREADS];
    TaskQueue queue = new TaskQueue();

    public void testParallel() throws Exception {
        System.out.println("test parallel FineDoublyLinkedList");
        for (int i = 0; i < THREADS; i++) {
            thread[i] = new TestThread(i * PER_THREAD);
        }
        for (int i = 0; i < THREADS; i++) {
            thread[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            thread[i].join();
        }
    }

    class TestThread extends Thread {
        int threadLocalValue;
        TestThread(int i) {
            threadLocalValue = i;
        }
        public void run() {
            int i = 0;
            while (i < PER_THREAD) { // if push returns true, then push the next value, but if not keep trying to push that value
                int ii = threadLocalValue + i;
                KTask task = new KTask(ii,ii,ii,ii);
                if (queue.push(task)) { // basically check if validate worked, if not keep trying
                    i++;
                }
            }
            i = 0;
            while (i < PER_THREAD) { // same thing with pop, if pop doesn't work, keep trying
                KTask result = queue.pop();
                if (result.getWidth() != -1) { // if validate returns false you get a -1
                    i++;
                }
            }
        }
    }

    /* Main method */
    public static void main(String[] args) {
        TaskQueueTest queueTest = new TaskQueueTest();
        try {
            queueTest.testParallel();
            queueTest.queue.print(); // print the queue
        } catch (Exception e) {
        }
        long endTime = System.currentTimeMillis();
        /* Print the total execution time of the program */
        System.out.println("It took " + (endTime - startTime) + " milliseconds");
    }

}
