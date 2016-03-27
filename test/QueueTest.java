import java.util.concurrent.atomic.AtomicInteger;

/*test class*/
public class QueueTest {

	private static long startTime = System.currentTimeMillis();
	final private ThreadLocal<Integer> THREAD_ID = new ThreadLocal<Integer>() {
		final private AtomicInteger id = new AtomicInteger(0);

		protected Integer initialValue() {
			return id.getAndIncrement();
		}
	};

    /*
    Part should be modified for the project, I just used the TA's old test case
    to make sure everything was running smoothly
    */

	private final static int THREADS = 8;
	private final static int COUNT = 128;
	private final static int PER_THREAD = COUNT / THREADS;

	Thread[] thread = new Thread[THREADS];
	ProjectQueue queue = new ProjectQueue();

	/* Create and start the threads */
	public void testParallel() throws Exception {
		System.out.println("test parallel FineDoublyLinkedList");
		for (int i = 0; i < THREADS; i++) {
			thread[i] = new MyThread(i * PER_THREAD);
		}
		for (int i = 0; i < THREADS; i++) {
			thread[i].start();
		}
		for (int i = 0; i < THREADS; i++) {
			thread[i].join();
		}

	}


	class MyThread extends Thread {
		int threadLocalValue;

		MyThread(int i) {
			threadLocalValue = i;
		}

		public void run() {

            int i = 0;
            while(i < PER_THREAD){  //if push returns true, then push the next value, but if not keep trying to push that value
                if(queue.push(threadLocalValue + i)){  //basically check if validate worked, if not keep trying
                    i++;
                }
            }
            i = 0;
            while(i < PER_THREAD){  //same thing with pop, if pop doesn't work, keep trying
                if(queue.pop() != -1){      //if validate returns false you get a -1
                    i++;
                }
            }
		}
	}

	/* Main method */
	public static void main(String[] args) {
		QueueTest queueTest = new QueueTest();
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
