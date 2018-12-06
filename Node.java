/**
 * Standard Cons-cells
 *
 * @author Stefan Kahrs
 * @author Aaron Powell
 * @version 1
 * 
 */

/**
 * type parameter T
 * Below is an explanation for the fancy stuff
 * attached to that type parameter.
 * For the assessment you do not need to know this,
 * but if you are curious: read on!
 * 
 * Because we want to be able to compare the elements
 * of the list with one another, we require that
 * class T implements the Comparable interface.
 * That interface has itself a type parameter, which
 * gives you what these values can be compared to.
 * The reason this is (in the most general case) not just T
 * itself is the following scenario:
 * class X implements the interface,
 * so we can compare Xs with Xs, then we define a subclass Y of X,
 * so it inherits the compareTo method from X,
 * but Ys are now compared with Xs.
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Node<T extends Comparable<? super T>> {

    protected T head;
    protected Node<T> tail;

    public Node(T h, Node<T> t) {
        head = h;
        tail = t;
    }

    public String toString() {
        if (tail == null)
            return "[" + head + "]";
        return "[" + head + tail.tailString();
    }

    /**
     * This method fails between 7000-10000 list size with a StackOverflowError due
     * to the recursion going too deep. Maybe fixed by converting to string builder?
     * 
     * @return String
     */
    private String tailString() {
        String initialPart = "," + head;
        if (tail == null)
            return initialPart + "]";
        return initialPart + tail.tailString();
    }

    public int length() {
        int result = 1;
        for (Node<T> n = tail; n != null; n = n.tail) {
            result++;
        }
        return result;
    }

    /**
     * // this method should create a queue (of linked lists), split the original
     * (this) list into its sorted non-empty sublists; place those sublists in the
     * queue and return it
     * 
     * @return segmentQueue of of Nodes
     */
    public Queue<Node<T>> queueSortedSegments() {

        Queue<Node<T>> segmentQueue = new LinkedList<>();
        // Skips the qSS if the list is already sorted.
        if (this.isSorted()) {
            segmentQueue.add(this);
            return segmentQueue;
        }
        // Loops through each node, if the head is greater than the tail it splits and
        // adds to a new queue.
        Node<T> split;
        for (Node<T> i = this; i.tail != null; i = i.tail) {
            if (i.head.compareTo(i.tail.head) >= 0) {
                split = i.tail;
                i.tail = null;
                segmentQueue.add(this);
                segmentQueue.addAll(split.queueSortedSegments());
                break;
            }
        }
        return segmentQueue;
    }

    /**
     * Loops through each node comparing whether the head is greater than the tail,
     * note that it is not => as this would result in lists such as 1,3,3,4 being
     * 'not sorted'. Using && sorted constraint to jump out of the for-loop if
     * sorted variable is already false, saving time and being more efficient.
     * 
     * @return boolean
     */
    public boolean isSorted() {
        boolean sorted = true;
        for (Node<T> i = this; i.tail != null && sorted; i = i.tail) {
            if (i.head.compareTo(i.tail.head) > 0) {
                sorted = false;
            }
        }
        return sorted;
    }

    /**
     * Assertions ensure this method is not called unless lists are already sorted
     * and are not null. Comparing each node head of the lists and storing them
     * respectively, and then attaching it to the end of the previously stored node.
     * 
     * @param another of type Node<T>
     * @return Node<T>
     */
    public Node<T> merge(Node<T> another) {

        assert isSorted();
        assert another == null || another.isSorted();

        Node<T> merge = new Node<>(null, null);
        Node<T> stored = merge;

        Node<T> thisNode = this;

        while (true) {
            if (another.head.compareTo(thisNode.head) >= 0) {
                stored.tail = new Node<T>(thisNode.head, null);

                if (thisNode.tail == null) {

                    stored.tail.tail = another;
                    break;
                }

                thisNode = thisNode.tail;
            } else {
                stored.tail = new Node<T>(another.head, null);

                if (another.tail == null) {

                    stored.tail.tail = thisNode;
                    break;
                }

                another = another.tail;
            }

            stored = stored.tail;
        }
        return merge.tail;
    }

    /**
     * Until the size of the queue is 1 (1 node left), this method will recusively
     * poll the pairs, merge pairs, and add them back into the queue. Returning the
     * last node of the queue as its element (Node<T>).
     * 
     * @return Node<T>
     */
    public Node<T> mergeSort() {
        // this method should sort the list in the following way:
        // split the list up into sorted segments and place these into a queue
        // poll pairs of lists from the queue, merge them, and put their merge
        // back into the queue
        // if there is only one list left in the queue that should be returned
        Queue<Node<T>> queue = queueSortedSegments();

        // Ensure method ends up with a queue of size 1, recursively removing merging
        // and adding back.
        while (queue.size() != 1) {
            queue.add(queue.remove().merge(queue.remove()));
        }
        Node<T> merged = queue.remove();
        return merged; // keep compiler happy
    }

    static public Node<Integer> randomList(int n) {
        // for testing purposes we want some random lists to be sorted
        // the list is n elements long
        // the elements of the random list are numbers between 0 and n-1
        Random r = new Random();
        Node<Integer> result = null;
        int k = n;
        while (k > 0) {
            result = new Node<Integer>(r.nextInt(n), result);
            k--;
        }
        return result;
    }

    /**
     * A test method use to test isSorted and during creation of merge method
     * 
     * @param n
     * @return Node<Integer>
     */
    static public Node<Integer> orderedList(int n) {
        // for testing purposes we want some random lists to be sorted
        // the list is n elements long
        // the elements of the random list are numbers between 0 and n-1
        Node<Integer> result = null;
        int k = n;
        int i = 10;
        while (k > 0) {
            result = new Node<Integer>(i, result);
            i--;
            k--;
        }
        return result;
    }

    /**
     * Prints readable well structured information on what is happening to the
     * random list and other information requested. Using ternary operators to
     * sophisticatedly return a boolean value in form of a string.
     * 
     * @param n
     */
    static public void test(int n) {
        // this method should do the following:
        // 1. create a random linked list of length n
        // 2. output it
        // 3. report whether the 'isSorted' method thinks the list is sorted or not
        // 4. sort the list using mergeSort
        // 5. output the sorted list
        // 6. report whether the 'isSorted' method thinks that list is sorted or not
        Node<Integer> randomList = randomList(n);
        System.out.println("Randomly generated list of length " + n + ": " + randomList);
        System.out.println();
        System.out.print("isSorted method thinks that the list: ");
        System.out.print(randomList);
        String sorted1 = (randomList.isSorted()) ? " is sorted!" : " is NOT sorted!";
        System.out.println(sorted1);
        System.out.println("Merge Sorting...");
        System.out.println();
        Node<Integer> sortedList = randomList.mergeSort();
        System.out.print("MergeSorted List:");
        System.out.println(sortedList);
        System.out.println();
        System.out.print("isSorted method thinks that the list: ");
        System.out.print(sortedList);
        String sorted2 = (sortedList.isSorted()) ? " is sorted!" : " is NOT sorted!";
        System.out.println(sorted2);

    }

}