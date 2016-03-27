import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProjectQueue{
    private Node head;
    private Node tail;

    public ProjectQueue(){
        head = new Node();      //make head and tail and connect them to one another in the beginning
        tail = new Node();
        head.next = tail;
        tail.next = null;
    }

    public boolean push(int item) {
        Node curr;
        Node next;
        curr = head;
        next = head.next;
        Node newNode = new Node();
        newNode.data = item;
        curr.lock(); //add only needs one lock
        try {
            if (validatePush(curr, next)) {     //make sure curr points to next
                head.next = newNode;        //add new node to the front of the queue
                newNode.next = next;
                return true;
            } else {            //if not return false
                return false;
            }
        } finally {
            curr.unlock();
        }
    }

    public int pop() {
        Node pred;
        Node curr;
        Node next;
        pred = head;
        curr = head.next;
        if(curr == tail){      //if curr = tail then we reached the end of the list
            return 0;
        }
        next = curr.next;
        pred.lock();
        try{
            curr.lock();
            try {
                next.lock();    //lock the three locks and get rid of the middle one
                try {
                    if (validatePop(pred, curr, next)) {
                        pred.next = next;   //connect pred to next skipping current
                        return curr.data;   //curr prints its data
                    } else {
                        return -1;      //if validate doesn't work return -1
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

    private boolean validatePush(Node curr, Node next) {
        return (curr.next == next);     //does curr point to next
    }

    private boolean validatePop(Node pred, Node curr, Node next){
        return (pred.next == curr && curr.next == next);    //does pred->curr->next
    }

    public void printList(){    //just a simple print list method
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
