/*
 * TaskQueue.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import java.util.concurrent.locks.*;

/**
 * TaskQueue is a concurrent task queue for managing tasks between concurrent
 * threads.
 */
public class TaskQueue {
    /** Head node */
    private TaskNode head;
    /** Tail node */
    private TaskNode tail;
    /** Queue length */
    private int length;
    /** Create an empty queue */
    public TaskQueue(){
        // Initialize head and tail
        head = new TaskNode();
        tail = new TaskNode();
        // Set empty queue
        head.next = tail;
        tail.next = null;
        length = 0;
    }
    /**
     * Add a task to the the queue.
     * @param task The task to add
     * @return True if add was successful, false otherwise
     */
    public boolean push(KTask task) {
        TaskNode curr;
        TaskNode next;
        curr = head;
        next = head.next;
        TaskNode newNode = new TaskNode(task);
        newNode.task = task;
        curr.lock(); // add only needs one lock
        try {
            if (validatePush(curr, next)) { //make sure curr points to next
                head.next = newNode; //add new node to the front of the queue
                newNode.next = next;
                ++length;
                return true;
            } else { // if not return false
                return false;
            }
        } finally {
            curr.unlock();
        }
    }
    /**
     * Pop a task from the queue.
     * @return The item popped. Return null on error
     */
    public KTask pop() {
        TaskNode pred, curr, next;
        pred = head;
        curr = head.next;
        if (curr == tail) { // if curr = tail then the queue is empty
            return null;
        }
        next = curr.next;
        pred.lock();
        try {
            curr.lock();
            try {
                next.lock(); // lock the three locks and get rid of the middle one
                try {
                    if (validatePop(pred, curr, next)) {
                        pred.next = next; // connect pred to next skipping current
                        --length;
                        return curr.task;
                    } else {
                        return null; // if validate doesn't work return null
                    }
                } finally {
                    next.unlock();
                }
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }
    /**
     * Get the length of the queue
     * @return The length of the queue
     */
    public int length() {
        return length;
    }
    /**
     * Return true if the queue is empty
     * @return True if empty, false otherwise
     */
    public boolean isEmpty() {
        return (length == 0);
    }
    /**
     * Check if our push worked
     * @param curr The current node
     * @param next The next node
     * @return True if push worked, false otherwise
     */
    private boolean validatePush(TaskNode curr, TaskNode next) {
        return (curr.next == next); // does curr point to next
    }
    /**
     * Check if our pop worked
     * @param pred The preceding node
     * @param curr The current node
     * @param next The next node
     * @return True if pop worked, false otherwise
     */
    private boolean validatePop(TaskNode pred, TaskNode curr, TaskNode next){
        return (pred.next == curr && curr.next == next); // check pred->curr->next
    }
    /**
     * Print the contents of the queue
     */
    public void print() {
        int i = 0;
        TaskNode temp;
        temp = head;
        while (temp != null) {
            if (temp == head) {
                System.out.println("[HEAD]");
            } else if (temp == tail) {
                System.out.println("[TAIL]");
            } else {
                System.out.println("  [ " + temp.task.getRegionString() + " ] ");
            }
            temp = temp.next;
            i++;
        }
        i -= 2;
        System.out.println("There are " + i + " items in the queue.");
    }
}

/**
 * TaskNode is a node for TaskQueue.
 */
class TaskNode {
    public KTask task;
    public TaskNode next;
    public Lock nodeLock = new ReentrantLock();
    /**
     * Create an empty TaskNode.
     */
    public TaskNode() {
        next = null;
        this.task = null;
    }
    /**
     * Create an TaskNode from a Task.
     * @param task The task to associate with the node.
     */
    public TaskNode(KTask task) {
        next = null;
        this.task = task;
    }
    /**
     * Lock the node
     */
    public void lock() {
        nodeLock.lock();
    }
    /**
     * Unlock the node
     */
    public void unlock(){
        nodeLock.unlock();
    }
}
