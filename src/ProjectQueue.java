/*
 * ProjectQueue.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

import java.util.concurrent.locks.*;

/**
 * ProjectQueue is a concurrent task queue for managing tasks between concurrent
 * threads.
 */
public class ProjectQueue {
    /** Head node */
    private Node head;
    /** Tail node */
    private Node tail;
    /** Create an empty queue */
    public ProjectQueue(){
        // Initialize head and tail
        head = new Node();
        tail = new Node();
        // Set empty queue
        head.next = tail;
        tail.next = null;
    }
    /**
     * Add a task to the the queue.
     * @param item The item to add
     * @return True if add was successful, false otherwise
     */
    public boolean push(int item) {
        Node curr;
        Node next;
        curr = head;
        next = head.next;
        Node newNode = new Node();
        newNode.data = item;
        curr.lock(); // add only needs one lock
        try {
            if (validatePush(curr, next)) { //make sure curr points to next
                head.next = newNode; //add new node to the front of the queue
                newNode.next = next;
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
     * @return The item popped
     */
    public int pop() {
        Node pred;
        Node curr;
        Node next;
        pred = head;
        curr = head.next;
        if (curr == tail) { // if curr = tail then we reached the end of the list
            return 0;
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
                        return curr.data;
                    } else {
                        return -1; // if validate doesn't work return -1
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
     * Check if our push worked
     * @param curr The current node
     * @param next The next node
     * @return True if push worked, false otherwise
     */
    private boolean validatePush(Node curr, Node next) {
        return (curr.next == next); // does curr point to next
    }
    /**
     * Check if our pop worked
     * @param pred The preceding node
     * @param curr The current node
     * @param next The next node
     * @return True if pop worked, false otherwise
     */
    private boolean validatePop(Node pred, Node curr, Node next){
        return (pred.next == curr && curr.next == next); // check pred->curr->next
    }
    /**
     * Print the contents of the queue
     */
    public void print() {
        int i = 0;
        Node temp;
        temp = head;
        while(temp != null){
            if(temp == head){
                System.out.println("[HEAD]");
            } else if(temp == tail){
                System.out.println("[TAIL]");
            } else {
                System.out.println("  [ " + temp.data + " ] ");
            }
            temp = temp.next;
            i++;
        }
        i -= 2;
        System.out.println("There are " + i + " items in the Queue");
        return;
    }
}

class Node{     //constructer
    public int data;
    public Node next;
    public Lock nodeLock = new ReentrantLock();

    public Node(){
        next = null;
        data = 0;
    }

    public void lock() {
        nodeLock.lock();
    }

    public void unlock(){
        nodeLock.unlock();
    }

}
