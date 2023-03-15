import java.util.Scanner;

/**
 * @author Danila Shulepin
 */
public class MainTaskC {

    public static void main(String[] args)  {
        PriorityQueueFibHeap<Integer, String> heap = new PriorityQueueFibHeap<>();

        Scanner scan = new Scanner(System.in);

        int numberOfLines = Integer.parseInt(scan.nextLine());

        for (int i = 0; i < numberOfLines; i++) {
            String str = scan.nextLine();

            //If ADD operation
            if (str.split(" ")[0].equals("ADD")) {
                heap.insert(Integer.parseInt(str.split(" ")[2]), str.split(" ")[1]);
            }
            //PRINT_MIN operation
            else {
                Object result = heap.extractMin();
                if (result != null)
                    System.out.println(result);
            }
        }
        scan.close();
    }
}

/**
 * Priority Queue interface
 *
 * @author Danila Shulepin
 * @param <K> - key
 * @param <V> - value
 */
interface IPriorityQueue<K, V> {
    void insert(Object item);

    Object findMin();

    Object extractMin();

    void decreaseKey(Object item, K newKey);

    void delete(Object item);

    void union(Object anotherQueue);
}

/**
 * Class Pair represents a pair of (key-value)
 *
 * @param <K> - type of key
 * @param <V> - type of value
 * @author Danila Shulepin
 */
class Pair<K extends Comparable, V extends Comparable> {

    K key;
    V value;

    /**
     * Constructor of a Pair class
     *
     * @param key   - value of a new key
     * @param value - value of a new value
     */
    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

}

/**
 * Class Node
 *
 * @author Danila Shulepin
 * @param <K> - key
 * @param <V> - value
 */
class Node<K extends Comparable<? super K>, V extends Comparable<? super V>> {
    Node leftSibling;
    Node rightSibling;
    Node parent;
    Node child;
    int degree;
    K key;
    V value;
    boolean marked;

    /**
     * Constructor
     *
     * @param key - type K
     * @param value - type V
     */
    Node(K key, V value) {
        this.leftSibling = this;
        this.rightSibling = this;
        this.child = null;
        this.parent = null;
        this.key = key;
        this.value = value;
        this.degree = 0;
        this.marked = false;
    }

    /**
     * Linker of two nodes as child-parent
     *
     * Time complexity: O(1)
     *
     * @param parent - parent node
     */
    public void link(Node parent) {

        leftSibling.rightSibling = rightSibling;
        rightSibling.leftSibling = leftSibling;
        //Make this a child of x
        this.parent = parent;
        if (parent.child == null) {
            parent.child = this;
            rightSibling = this;
            leftSibling = this;
        } else {
            leftSibling = parent.child;
            rightSibling = parent.child.rightSibling;
            parent.child.rightSibling = this;
            rightSibling.leftSibling = this;
        }
        //Increase degree
        parent.degree++;
        //Set mark false
        marked = false;
    }

    /**
     * Cascading cut of nodes
     *
     * Time complexity: O(1)
     *
     * @param node - node from root list
     */
    public void cascadingCut(Node node) {
        Node z = this.parent;
        //If there's a parent
        if (z != null) {
            if (this.marked) {
                //It's marked, cut it from parent
                z.cut(this, node);
                //Cut its parent as well
                z.cascadingCut(node);
            } else {
                //If y is unmarked, set it marked
                this.marked = true;
            }
        }
    }

    /**
     * Cutting x from its place and adding to root list
     *
     * Time complexity: O(1)
     *
     * @param x - cut node
     * @param y - node from root list
     */
    public void cut(Node x, Node y) {
        //Remove x from child list and decrement degree
        x.leftSibling.rightSibling = x.rightSibling;
        x.rightSibling.leftSibling = x.leftSibling;
        this.degree--;
        //Reset child if necessary
        if (this.degree == 0) {
            this.child = null;
        } else if (this.child == x) {
            this.child = x.rightSibling;
        }
        //Add x to root list of heap
        x.rightSibling = y;
        x.leftSibling = y.leftSibling;
        y.leftSibling.rightSibling = x;
        y.leftSibling = x;

        //Set parent of x to null
        x.parent = null;
        //Set mark of x to false
        x.marked = false;
    }
}

/**
 * Class Fibonacci Heap
 *
 * @author Danila Shulepin
 * @param <K> - key
 * @param <V> - value
 */
class PriorityQueueFibHeap<K extends Comparable<? super K>, V extends Comparable<? super V>> implements IPriorityQueue {
    Node minNode;
    int size;

    /**
     * Constructor
     */
    PriorityQueueFibHeap() {
        minNode = null;
        size = 0;
    }


    /**
     * Insert new item to heap
     *
     * Time complexity: O(1)
     *
     * @param item - Pair object
     */
    @Override
    public void insert(Object item) {
        Pair pair = (Pair) item;
        insert((K) pair.key, (V) pair.value);
    }

    /**
     * Inserting new item with key and value
     *
     * Time complexity: O(1)
     *
     * @param key - key
     * @param x - value
     * @return Node
     */
    public Node insert(K key, V x) {
        Node node = new Node(key, x);
        //Add node into root list
        //If min node does not exist yet
        if (minNode != null) {
            //Add it to the left from min node
            node.rightSibling = minNode;
            node.leftSibling = minNode.leftSibling;
            minNode.leftSibling = node;
            node.leftSibling.rightSibling = node;

            //Compare keys and values
            if (minNode.key.compareTo(key) > 0) {
                minNode = node;
            } else if (minNode.key.compareTo(key) == 0) {
                if (minNode.value.compareTo(x) > 0) {
                    minNode = node;
                }
            }
        }
        //Make new node a min one
        else {
            minNode = node;
        }
        size++;
        return node;
    }

    /**
     * Find min node value
     *
     * Time complexity: O(1)
     *
     * @return value of min node
     */
    @Override
    public Object findMin() {
        return minNode.value;
    }

    /**
     * Taking value of node with minimum key
     *
     * Time complexity: O(log(n))
     *
     * @return value of node with minimum key
     */
    @Override
    public Object extractMin() {
        Node z = minNode;
        if (z == null) {
            return null;
        }
        if (z.child != null) {
            z.child.parent = null;

            Node x = z.child.rightSibling;
            while (x != z.child) {
                x.parent = null;
                x = x.rightSibling;
            }

            //Merge the children into root list
            Node minleft = minNode.leftSibling;
            Node zchildleft = z.child.leftSibling;
            minNode.leftSibling = zchildleft;
            zchildleft.rightSibling = minNode;
            z.child.leftSibling = minleft;
            minleft.rightSibling = z.child;
        }
        //Remove z from root list of heap
        z.leftSibling.rightSibling = z.rightSibling;
        z.rightSibling.leftSibling = z.leftSibling;
        if (z == z.rightSibling) {
            minNode = null;
        } else {
            minNode = z.rightSibling;
            consolidate();
        }
        //Decrement size of heap
        size--;
        return z.value;
    }

    /**
     * Log of base 2
     *
     * Time complexity: O(1)
     *
     * @param n
     * @return
     */
    private int D(int n){
        return (int)(Math.log(n)/ Math.log(2));
    }

    /**
     * Auxiliary operation for extract min
     *
     * Time complexity: O(log(n))
     */
    private void consolidate() {
        //Degree list
        Node[] A = new Node[2*D(size)+1];

        Node start = minNode;
        Node w = minNode;
        do {
            //Go through all nodes in the root list
            Node x = w;
            Node next = w.rightSibling;
            int d = x.degree;
            //Element with such degree exists
            while (A[d] != null) {
                Node y = A[d];
                //Compare keys and swap if it's necessary
                //Compare values as well
                if (x.key.compareTo(y.key) > 0) {
                    Node temp = y;
                    y = x;
                    x = temp;
                } else if (x.key.compareTo(y.key) == 0) {
                    if (x.value.compareTo(y.value) > 0) {
                        Node temp = y;
                        y = x;
                        x = temp;
                    }
                }
                if (y == start) {
                    start = start.rightSibling;
                }
                if (y == next) {
                    next = next.rightSibling;
                }
                //Node y disappears from root list.
                y.link(x);
                A[d] = null;
                d++;
            }
            //Now x is a new node with degree d
            A[d] = x;
            w = next;
        } while (w != start);

        minNode = start;
        //Searching for the node with minimum key
        for (Node a : A) {
            if (a != null && a.key.compareTo(minNode.key) < 0) {
                minNode = a;
            } else if (a != null && a.key.compareTo(minNode.key) == 0) {
                if (a.value.compareTo(minNode.value) < 0)
                    minNode = a;
            }
        }
    }

    /**
     * Change key of the item
     *
     * Time complexity: O(1)
     *
     * @param item - a node
     * @param newKey - new key
     */
    @Override
    public void decreaseKey(Object item, Object newKey) {

        Node node = (Node) item;
        K key = (K) newKey;

        //If only key is lesser
        if (!(node.key.compareTo(key) < 0)) {
            node.key = key;
            Node y = node.parent;
            if (y != null && y.key.compareTo(key) > 0) {
                y.cut(node, minNode);
                y.cascadingCut(minNode);
            }
            if (minNode.key.compareTo(key) > 0) {
                minNode = node;
            }
        }
    }

    /**
     *  Deletion of node
     *
     *  Time complexity: O(log(n))
     *
     * @param item - node
     */
    @Override
    public void delete(Object item) {
        Node node = (Node) item;
        //Decreasing key to the minimum value
        Node y = node.parent;
        if (y != null) {
            y.cut(node, minNode);
            y.cascadingCut(minNode);
        }
        minNode = node;
        //Extracting new min node
        extractMin();
    }

    /**
     * Union of two heaps
     *
     * Time complexity: O(1)
     *
     * @param anotherQueue - fibonacci heap
     */
    @Override
    public void union(Object anotherQueue) {
        //If both heaps are not null, just repoint their min node's
        if (anotherQueue != null) {
            PriorityQueueFibHeap queue = (PriorityQueueFibHeap) anotherQueue;
            if (minNode != null) {
                if (queue.minNode != null) {
                    minNode.rightSibling.leftSibling = queue.minNode.leftSibling;
                    queue.minNode.leftSibling.rightSibling = minNode.rightSibling;

                    minNode.rightSibling = queue.minNode;
                    queue.minNode.leftSibling = minNode;

                    //Choose new min node
                    if (minNode.key.compareTo(queue.minNode.key) > 0) {
                        Node temp = minNode;
                        minNode = queue.minNode;
                        queue.minNode = temp;
                    } else if (minNode.key.compareTo(queue.minNode.key) == 0) {
                        if (minNode.value.compareTo(queue.minNode.value) > 0) {
                            Node temp = minNode;
                            minNode = queue.minNode;
                            queue.minNode = temp;
                        }
                    }
                }
            }
            //If old heap is null
            else {
                minNode = queue.minNode;
                size = queue.size;
            }
        }
    }

}