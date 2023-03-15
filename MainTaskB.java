import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Danila Shulepin
 */
public class MainTaskB {

    public static void main(String[] args) {


        Scanner scan = new Scanner(System.in);
        int numberOfLines = Integer.parseInt(scan.nextLine());

        BTree<Date, Integer> tree = new BTree<>(20);

        for (int i = 0; i < numberOfLines; i++) {

            String newString = scan.nextLine();
            Date date;
            List<Integer> list;

            //If DEPOSIT
            if (newString.contains("DEPOSIT")) {
                date = new Date(newString.split(" DEPOSIT ")[0]);
                tree.add(date, Integer.parseInt(newString.split(" DEPOSIT ")[1]));
            }
            //If WITHDRAW
            else if (newString.contains("WITHDRAW")) {
                date = new Date(newString.split(" WITHDRAW ")[0]);
                tree.add(date, -1 * Integer.parseInt(newString.split(" WITHDRAW ")[1]));

            }
            //If REPORT
            else {
                newString = newString.split("REPORT FROM ")[1];
                if (!tree.isEmpty()) {
                    Date from = new Date(newString.split(" TO ")[0]);
                    Date to = (new Date(newString.split(" TO ")[1]));
                    list = tree.lookupRange((from), to);
                    int sum = 0;
                    for (Integer x :
                            list) {
                        sum += x;
                    }
                    System.out.println(sum);
                } else {
                    System.out.println(0);
                }
            }
        }
        scan.close();
    }
}

/**
 * Interface for implementing BTree
 *
 * @param <K> - key type
 * @param <V> - value type
 * @author Danila Shulepin
 */
interface RangeMap<K, V> {
    /**
     * Number of items in the BTree
     *
     * @return int number of items
     */
    int size();

    /**
     * Checking if BTree is empty
     *
     * @return true if size == 0, false otherwise
     */
    boolean isEmpty();

    /**
     * Adds item to BTree
     *
     * @param key   - key of an item
     * @param value - value of an item
     */
    void add(K key, V value);

    /**
     * Checking if key is present in BTree
     *
     * @param key - key of an item
     * @return true key presents in BTree, false otherwise
     */
    boolean contains(K key);

    /**
     * Searching value by the key
     *
     * @param key - key of an item
     * @return value if key presents in BTree, null otherwise
     */
    V lookup(K key);

    /**
     * Searching range of values by the key
     *
     * @param from - starting key
     * @param to   - ending key
     * @return list of values with keys in range [from;to]
     */
    List<V> lookupRange(K from, K to);

    /**
     * Removing an item by key
     *
     * @param key - key of removing item
     * @return null, just deleting item from BTree
     */
    Object remove(K key);
}

/**
 * Class represents BTree
 *
 * @author Danila Shulepin
 * @param <K>
 * @param <V>
 */
class BTree<K extends Comparable, V extends Number> implements RangeMap<K, V> {

    /**
     * root - Creating of a root Node of the BTree
     * t - parameter t
     * size - current size of a tree
     */
    public Node<K, V> root;
    public int t;
    int size;

    /**
     * Constructor of a BTree
     *
     * @param t - value of parameter t
     */
    BTree(int t) {
        this.root = null;
        this.t = t;
        size = 0;
    }


    /**
     * Function of size
     *
     * Time complexity: O(1)
     *
     * @return current size of a tree
     */
    @Override
    public int size() {
        return this.size;
    }


    /**
     * Checking if BTree is empty
     *
     * Time complexity: O(1)
     *
     * @return true if there is no items in BTree, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Adding new item to BTree
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param key   - key of an item
     * @param value - value of an item
     */
    @Override
    public void add(K key, V value) {

        //Creating pair from key and value
        Pair<K, V> k = new Pair<>(key, value);

        //Is root null?
        if (root == null) {

            // Creating a new root
            // and inserting a pair k
            root = new Node(t, true);
            root.pairs[0] = k;
            root.currentNumberOfValues = 1;

        } else {
            // Checking if the root is full
            Node r = root;
            if (root.currentNumberOfValues == 2 * t - 1) {

                // Creating a new Node and making old root its child
                Node s = new Node(t, false);
                root = s;
                s.children[0] = r;

                // Calling of split function
                s.split(0, r);
                s.unfullInsert(k);

            } else  // If root is not full, call insertNonFull for root
                r.unfullInsert(k);
        }
        size += 1;
    }

    /**
     * Searching for key inside the tree
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param key - key of an item
     * @return true if there is such a key, false otherwise
     */
    @Override
    public boolean contains(K key) {
        //Invoking search method with root
        Node node = search(root, key);
        //If there exists such a node
        if (node != null) return true;
        return false;
    }

    /**
     * Looking for value corresponding to key
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param key - key of an item
     * @return value or null
     */
    @Override
    public V lookup(K key) {
        //Searching for node
        Node node = search(root, key);
        //If node exists find value
        if (node != null) return (V) node.pairs[find(node, key)].getValue();
        return null;
    }

    /**
     * Looking for list of values corresponding keys between form and to
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param from - starting key
     * @param to   - ending key
     * @return list of values
     */
    @Override
    public List<V> lookupRange(K from, K to) {

        //Invoking walking() function
        List<V> list = walking(root, from, to);
        return list;
    }

    /**
     * Removing element corresponding to key
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param key - key of removing item
     * @return
     */
    @Override
    public Object remove(K key) {
        // Searching for appropriate node
        Node x = search(root, key);
        if (x == null) {
            return null;
        }

        // Delete element
        return remove(root, key);
    }


    /**
     * Removing element with key from node x
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param x   - node
     * @param key - key of element
     */
    private Object remove(Node x, K key) {

        //Trying to find position of key inside current node x
        int pos = find(x, key);

        //There exists such an element in the current node x
        if (pos != -1) {
            Pair p = x.pairs[pos];
            // 1. If the key is in node x and x is a leaf, delete the key k from x
            if (x.isLeaf) {
                //Moving all the elements after found one except the last one
                while (pos < x.currentNumberOfValues) {
                    if (pos != 2 * t - 2) {
                        x.pairs[pos] = x.pairs[pos + 1];
                    }
                    pos++;
                }
                x.currentNumberOfValues--;
                return p;
            }
            // 2. If the key is in node x and x is an internal node
            else {
                // 2a. If the child y that precedes k in node x has at least t keys, then find the
                //     predecessor k0 of k in the subtree rooted at y. Recursively delete k0, and
                //     replace k by k0 in x.
                Node predecessorChild = x.children[pos];
                Pair predecessor;

                //Current number of values in node should be not less than t
                if (predecessorChild.currentNumberOfValues >= t) {

                    //Searching for predecessor
                    while (true) {
                        if (predecessorChild.isLeaf) {
                            predecessor = predecessorChild.pairs[predecessorChild.currentNumberOfValues - 1];
                            break;
                        } else {
                            predecessorChild = predecessorChild.children[predecessorChild.currentNumberOfValues];
                        }
                    }
                    //Remove predecessor and placing it in the position of removing value
                    remove(x.children[pos], (K) predecessor.getKey());
                    x.pairs[pos] = predecessor;
                    return p;
                }

                // 2b. If y has fewer than t keys, then, symmetrically, examine the child z that
                //     follows k in node x. If z has at least t keys, then find the successor k0 of k in
                //     the subtree rooted at z. Recursively delete k0, and replace k by k0 in x.
                Node successorChild = x.children[pos + 1];
                Pair successor;

                //Current number of values in node should be not less than t
                if (successorChild.currentNumberOfValues >= t) {

                    //Searching for successor
                    successor = successorChild.pairs[0];
                    if (!successorChild.isLeaf) {
                        successorChild = successorChild.children[0];
                        while (true) {
                            if (successorChild.isLeaf) {
                                successor = successorChild.pairs[0];
                                break;
                            } else {
                                successorChild = successorChild.children[0];
                            }
                        }
                    }
                    //Remove successor and placing it in the position of removing value
                    remove(x.children[pos+1], (K) successor.getKey());
                    x.pairs[pos] = successor;
                    return p;
                }

                // 2c. Otherwise, if both y and z have only t-1 keys, merge k and all of z into y,
                //     so that x loses both k and the pointer to z, and y now contains 2t-1 keys.
                //     Then free z and recursively delete k from y.
                int temp = predecessorChild.currentNumberOfValues + 1;

                //Place removing element as last to the nearest predecessorChild
                predecessorChild.pairs[predecessorChild.currentNumberOfValues++] = x.pairs[pos];

                //Copying elements from successorChild to predecessorChild
                for (int i = 0, j = predecessorChild.currentNumberOfValues; i < successorChild.currentNumberOfValues; i++) {
                    predecessorChild.pairs[j++] = successorChild.pairs[i];
                    predecessorChild.currentNumberOfValues++;
                }

                //Copying children from successorChild to predecessorChild
                for (int i = 0; i < successorChild.currentNumberOfValues + 1; i++) {
                    predecessorChild.children[temp++] = successorChild.children[i];
                }

                //predecessorChild is child of x on the pos position
                x.children[pos] = predecessorChild;
                //Moving all the elements except the last one
                for (int i = pos; i < x.currentNumberOfValues; i++) {
                    if (i != 2 * t - 2) {
                        x.pairs[i] = x.pairs[i + 1];
                    }
                }
                //Moving all the children except the last one
                for (int i = pos + 1; i < x.currentNumberOfValues + 1; i++) {
                    if (i != 2 * t - 1) {
                        x.children[i] = x.children[i + 1];
                    }
                }
                //Decreasing number of elements
                x.currentNumberOfValues--;

                //If x is empty, replace it by its child
                if (x.currentNumberOfValues == 0) {
                    if (x.equals(root)) {
                        root = x.children[0];
                    }
                    x = x.children[0];
                }
                //Remove key element from predecessorChild
                remove(predecessorChild, key);
                return p;
            }
        }

        //Does not exist such a position
        else {
            //3. If the key k is not present in internal node x, determine the root x.ci of the
            //   appropriate subtree that must contain k, if k is in the tree at all. If x.ci has
            //   only t-1 keys, execute step 3a or 3b as necessary to guarantee that we descend
            //   to a node containing at least t keys. Then finish by recursing on the appropriate
            //   child of x.
            for (pos = 0; pos < x.currentNumberOfValues; pos++) {
                if (x.pairs[pos].getKey().compareTo(key) == 1) {
                    break;
                }
            }

            //Take appropriate child
            Node tmp = x.children[pos];


            // If x.ci has not less than t keys
            if (tmp.currentNumberOfValues >= t) {
                Pair p = (Pair) remove(tmp, key);
                return p;
            }

            // 3a. If x.ci has only t-1 keys but has an immediate sibling (left or right) with at least t keys,
            //     give x.ci an extra key by moving a key from x down into x.ci, moving a
            //     key from x.ci’s immediate left or right sibling up into x, and moving the
            //     appropriate child pointer from the sibling into x.ci.
            Node node;
            Pair elementPair;
            // Checking right sibling, if it's not the rightest node
            if (pos != x.currentNumberOfValues && x.children[pos + 1].currentNumberOfValues >= t) {
                //Replace removing value with next child's first element
                elementPair = x.pairs[pos];
                node = x.children[pos + 1];
                x.pairs[pos] = node.pairs[0];
                //Place removing element as the last in prev child
                tmp.pairs[tmp.currentNumberOfValues++] = elementPair;
                //And first child of next child as prev child's new child
                tmp.children[tmp.currentNumberOfValues] = node.children[0];
                //Move elements of next child to the left
                for (int i = 1; i < node.currentNumberOfValues; i++) {
                    node.pairs[i - 1] = node.pairs[i];
                }
                //And children as well
                for (int i = 1; i <= node.currentNumberOfValues; i++) {
                    node.children[i - 1] = node.children[i];
                }
                node.currentNumberOfValues--;

                Pair p = (Pair) remove(tmp, key);
                return p;
            }
            // Checking left sibling, if it's not the leftest node
            else if (pos != 0 && x.children[pos - 1].currentNumberOfValues >= t) {
                //Replace removing value with previous child's last element
                elementPair = x.pairs[pos - 1];
                node = x.children[pos - 1];

                x.pairs[pos - 1] = node.pairs[node.currentNumberOfValues - 1];
                Node child = node.children[node.currentNumberOfValues];
                node.currentNumberOfValues--;

                //Move elements of prev child to the left
                for (int i = tmp.currentNumberOfValues; i > 0; i--) {
                    tmp.pairs[i] = tmp.pairs[i - 1];
                }
                tmp.pairs[0] = elementPair;
                //And children as well
                for (int i = tmp.currentNumberOfValues + 1; i > 0; i--) {
                    tmp.children[i] = tmp.children[i - 1];
                }
                tmp.children[0] = child;
                tmp.currentNumberOfValues++;
                Pair p = (Pair)remove(tmp, key);
                return p;
            }
            // 3b. If x.ci and both of x.ci’s immediate siblings have t-1 keys, merge x.ci
            //     with one sibling, which involves moving a key from x down into the new
            //     merged node to become the median key for that node.
            else {
                Node left;
                Node right;
                // Assigning values of elementPair, left and right nodes
                if (pos != x.currentNumberOfValues) {
                    elementPair = x.pairs[pos];
                    left = x.children[pos];
                    right = x.children[pos + 1];
                } else {
                    elementPair = x.pairs[pos - 1];
                    right = x.children[pos];
                    left = x.children[pos - 1];
                    pos--;
                }

                //Moving elements and children of x node to the left
                for (int i = pos; i < x.currentNumberOfValues - 1; i++) {
                    x.pairs[i] = x.pairs[i + 1];
                }
                for (int i = pos + 1; i < x.currentNumberOfValues; i++) {
                    x.children[i] = x.children[i + 1];
                }
                x.currentNumberOfValues--;
                left.pairs[left.currentNumberOfValues++] = elementPair;

                //Adding elements and children of the right node to the left node
                for (int i = 0, j = left.currentNumberOfValues; i < right.currentNumberOfValues + 1; i++, j++) {
                    if (i < right.currentNumberOfValues) {
                        left.pairs[j] = right.pairs[i];
                    }
                    left.children[j] = right.children[i];
                }
                left.currentNumberOfValues += right.currentNumberOfValues;

                //If x is empty
                if (x.currentNumberOfValues == 0) {
                    if (x == root) {
                        root = x.children[0];
                    }
                    x = x.children[0];
                }
                Pair p = (Pair) remove(left, key);
                return p;
            }
        }
    }


    /**
     * Searching for node containing key
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param x   - node
     * @param key - key
     * @return node which contains such a key, or null
     */
    private Node search(Node x, K key) {
        int i = 0;
        //Walking through all the elements inside the node x searching for key
        if (x == null)
            return x;
        for (i = 0; i < x.currentNumberOfValues; i++) {
            if (x.pairs[i].getKey().compareTo(key) == 1) {
                break;
            }
            if (x.pairs[i].getKey().compareTo(key) == 0) {
                return x;
            }
        }
        if (x.isLeaf) {
            return null;
        } else {
            return search(x.children[i], key);
        }
    }

    /**
     * Searching for such a key inside this node
     *
     * Time complexity: O(t)
     * t - parameter t
     *
     * @param node - this node
     * @param k    - key
     * @return index of element inside node, or -1 if there is no such a key
     */
    public int find(Node node, K k) {
        // Comparing all elements inside node
        for (int i = 0; i < node.currentNumberOfValues; i++) {
            if (node.pairs[i].getKey().compareTo(k) == 0) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Searching for elements with keys between from and to
     *
     * Time complexity: O(t*log(n)+k)
     * k - range of keys
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param node - node in which we search
     * @param from - the least key
     * @param to   - the largest key
     * @return list of values for elements with key between from and to
     */
    private List<V> walking(Node node, K from, K to) {

        List<V> finalList = new ArrayList<>();

        //Searching for mostly corresponding indexes
        int i = 0, j = 0;
        while (node != null && i < node.currentNumberOfValues && j < node.currentNumberOfValues) {
            if (node.pairs[i] != null && (node.pairs[i].getKey().compareTo(from) < 0)) {
                i++;
                j++;
            } else if (node.pairs[j] != null && (node.pairs[j].getKey().compareTo(to) <= 0))
                j++;
            else break;
        }

        //For each element between i and j
        for (int k = i; k <= j; k++) {

            //If node is a leaf
            if (node != null && node.isLeaf) {

                //Add value to the list if its key is between from and to
                if (k < node.currentNumberOfValues && node.pairs[k] != null && node.pairs[k].getKey().compareTo(from) >= 0 && node.pairs[k].getKey().compareTo(to) <= 0) {
                    finalList.add((V) node.pairs[k].getValue());
                }
            } else {
                if (node != null) {
                    //Invoking walking() function for children of current node
                    List<V> newList = walking(node.children[k], from, to);
                    for (V element :
                            newList) {
                        finalList.add(element);
                    }
                    //Add value to the list if its key is between from and to
                    if (k < node.currentNumberOfValues && node.pairs[k] != null && node.pairs[k].getKey().compareTo(from) >= 0 && node.pairs[k].getKey().compareTo(to) <= 0) {
                        finalList.add((V) node.pairs[k].getValue());
                    }
                }
            }
        }
        return finalList;
    }
}

/**
 * Class represents one Node of a BTree
 *
 * @param <K> - type of keys
 * @param <V> - type of values
 * @author Danila Shulepin
 */
class Node<K extends Comparable, V extends Number> {

    // pairs - Each node consists of array of pairs(key-value)
    // t - Parameter t
    // children - Array of each child node
    // currentNumberOfValues - Current number of pairs in this Node
    // isLeaf - Is this Node a leaf?
    Pair<K, V>[] pairs;
    int t;
    Node[] children;
    int currentNumberOfValues;
    boolean isLeaf;

    /**
     * Constructor of a Node
     *
     * @param t        - value of parameter t
     * @param nodeType - will this Node be a leaf?
     */
    Node(int t, boolean nodeType) {

        //Number of elements in Node maximally equals to 2*t-1
        //Number of children in Node maximally equals to 2*t
        this.pairs = new Pair[2 * t - 1];
        this.children = new Node[2 * t];
        currentNumberOfValues = 0;
        this.t = t;
        this.isLeaf = nodeType;
    }


    /**
     * If the Node is not full - insert new value k in appropriate position
     *
     * Time complexity: O(t*log(n))
     * t - parameter t
     * log(n) = log_t(n)
     * n - number of elements
     *
     * @param k - (key-value) pair to add
     */
    void unfullInsert(Pair k) {

        //Starting from the end of the array
        int i = currentNumberOfValues - 1;

        //If the Node is a leaf - just insert the new element k in corresponding position
        if (isLeaf) {

            //Moving pairs of the Node to find index of the position for k
            while (i >= 0 && k.getKey().compareTo(pairs[i].getKey()) < 0) {

                pairs[i + 1] = pairs[i];
                i--;

            }

            //Insert element k in the position (i+1)
            pairs[i + 1] = k;
            currentNumberOfValues = currentNumberOfValues + 1;
        }
        // Node is not a leaf, it has children
        else {
            //Searching for appropriate child to store the value
            while (i >= 0 && k.getKey().compareTo(pairs[i].getKey()) < 0)
                i--;

            // What if found child is full?
            i++;
            if (children[i] != null && children[i].currentNumberOfValues == 2 * t - 1) {
                // If the child is full, then split it
                split(i, children[i]);

                // After split, the middle key of the Node goes up to the parent and
                // array is splitted into two arrays of (t-1). One of the two
                // is going to store the value k
                if (k.getKey().compareTo(pairs[i].getKey()) > 0)
                    i++;
            }
            //Call function recursively
            if (children[i] != null)
                children[i].unfullInsert(k);
        }
    }

    /**
     * Split child function. Splits into 2 sub-arrays.
     *
     * Time complexity: O(t)
     * t - parameter t
     *
     * @param i - index of middle element
     * @param y - node
     */
    void split(int i, Node y) {

        //New Node z to store last (t-1) pairs of the current Node y
        Node z = new Node(y.t, y.isLeaf);
        z.currentNumberOfValues = t - 1;


        //Copying last (t-1) keys of y to z
        for (int j = 0; j < t - 1; j++) {
            z.pairs[j] = y.pairs[j + t];
        }

        // Copying last t children of y to z if y is not a leaf
        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
            }
        }

        // New number of values in y
        y.currentNumberOfValues = t - 1;

        // Current Node will have z as a new child
        // Making space for it
        for (int j = currentNumberOfValues; j >= i + 1; j--)
            children[j + 1] = children[j];

        // z becomes a new child of a current Node
        children[i + 1] = z;

        // Finding the location for middle value of y that goes up to the current Node
        for (int j = currentNumberOfValues - 1; j >= i; j--)
            pairs[j + 1] = pairs[j];

        // Copying middle value to the current Node
        pairs[i] = (Pair) y.pairs[t - 1];

        // Increment number of values in the current Node
        currentNumberOfValues = currentNumberOfValues + 1;
    }
}

/**
 * Class Pair represents a pair of (key-value)
 *
 * @param <K> - type of key
 * @param <V> - type of value
 * @author Danila Shulepin
 */
class Pair<K extends Comparable, V extends Number> {

    private K key;
    private V value;

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

    /**
     * Get the value of a key variable
     *
     * Time complexity: O(1)
     *
     * @return key
     */
    public K getKey() {
        return key;
    }

    /**
     * Get the value of a value variable
     *
     * Time complexity: O(1)
     *
     * @return value
     */
    public V getValue() {
        return value;
    }

}

/**
 * Class represents date in year, month and day
 *
 * @author Danila Shulepin
 */
class Date implements Comparable {
    int year;
    int month;
    int day;

    Date(String date) {
        String temp = date;
        year = Integer.parseInt(temp.split("-")[0]);
        month = Integer.parseInt(temp.split("-")[1]);
        day = Integer.parseInt(temp.split("-")[2]);
    }

    /**
     * Comparator for two dates
     *
     * Time complexity: O(1)
     *
     * @param object - second date
     * @return 1 if date1 < date 2, 0 if date1 = date 2, -1 otherwise
     */
    @Override
    public int compareTo(Object object) {
        if (((Date) object).year > this.year) return -1;
        if (((Date) object).year < this.year) return 1;
        if (((Date) object).month > this.month) return -1;
        if (((Date) object).month < this.month) return 1;
        if (((Date) object).day > this.day) return -1;
        if (((Date) object).day < this.day) return 1;
        return 0;
    }
}