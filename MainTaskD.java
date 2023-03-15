import java.util.ArrayList;

import java.util.Scanner;

/**
 * @author Danila Shulepin
 */
public class MainTaskD {

    /**
     * MSF Prim's algorithm
     *
     * Time complexity: O(|V|*lg|V|+|E|) - Cormen ch.23.2 p.636
     *
     * @param adjacency  - adjacency matrix of graph
     * @param vertexList - vertex list of graph
     * @return string of edges for minimal spanning forest
     */
    public static String Prim(ArrayList<ArrayList<Edge>> adjacency, ArrayList<Vertex> vertexList) {

        //Initializing priority queue, boolean array of visited vertices, and array of priority queue nodes
        PriorityQueueFibHeap<Double, Vertex> queue = new PriorityQueueFibHeap<>();
        boolean[] visit = new boolean[vertexList.size()];
        Node[] arr = new Node[vertexList.size()];

        //Starter vertex
        Vertex start = vertexList.get(0);

        //Assigning current minimum edge of each vertex with maximum possible value, and parent with null
        //Filling queue with current minimum edge as key and vertex as value
        int j = 0;
        for (Vertex x :
                vertexList) {
            x.transverse = (Double.MAX_VALUE);
            x.parent = null;
            arr[j++] = queue.insert((double) (x.transverse), x);
        }

        //Let current minimum edge of starter vertex be 0
        start.transverse = 0.0;

        //While queue is not empty
        while (queue.size != 0) {

            //Take vertex with the minimum key
            Vertex u = (Vertex) queue.extractMin();
            //Now this vertex is visited
            visit[u.index] = true;
            //List of edges from adjacency matrix
            ArrayList<Edge> a = adjacency.get(u.index);

            //For each non-null edge
            for (Edge x :
                    a) {
                if (x != null) {

                    //Take adjacent vertex
                    Vertex y = x.start.equals(u) ? x.end : x.start;

                    //If this vertex is unvisited, and it's current minimum edge larger than weight of current edge
                    if (!visit[y.index] && (double) (x.weight) < (double) (arr[y.index]).key) {

                        //Assign parent of y as u and decrease it's key to weight of current edge
                        ((Vertex) (arr[y.index].value)).parent = u;
                        queue.decreaseKey(arr[y.index], (x.weight));
                    }
                }
            }
        }

        //For each vertex from vertex list minimal spanning forest string
        // will consist of parent:vertex for non-null parents
        StringBuilder builder = new StringBuilder("");
        for (Vertex vert :
                vertexList) {
            if (vert.parent != null)
                builder.append(vert.parent.key.toString() + ":" + vert.key.toString() + " ");
        }
        return builder.toString();

    }

    public static void main(String[] args) {
        //Initializing a graph
        DynamicGraph<String, Double> graph = new DynamicGraph<>();

        Scanner scan = new Scanner(System.in);
        int numberOfLines = Integer.parseInt(scan.nextLine());

        for (int i = 0; i < numberOfLines; i++) {
            String str = scan.nextLine();
            String arr[] = str.split(" ");

            //If it's ADD operation
            if (arr[0].equals("ADD")) {
                //Insert vertex and put its value
                Vertex v = (Vertex) graph.insertVertex(arr[1]);
                v.setValue((double) Integer.parseInt(arr[2]));
            }
            //If it's CONNECT operation
            else if (arr[0].equals("CONNECT")) {
                //Find two vertices corresponding to keys
                Vertex v1 = graph.find(arr[1]);
                Vertex v2 = graph.find(arr[2]);

                //Calculate weight of an edge
                double value = Integer.parseInt(arr[3]);
                value = value / ((double) v1.value + (double) v2.value);

                //Insert edge
                graph.insertEdge(v1, v2, value);
            }
            //If it's PRINT_MIN operation
            else {
                //Invoke MSF Prim's algorithm
                System.out.println(Prim(graph.adjacencyMatrix, graph.vertexList));
            }
        }
        scan.close();

    }
}

/**
 * Graph interface
 *
 * @author Danila Shulepin
 * @param <V> - vertex type
 * @param <E> - edge type
 */
interface IGraph<V, E> {

    Object insertVertex(V v);

    Object insertEdge(Object from, Object to, E w);

    void removeVertex(Object v);

    void removeEdge(Object e);

    boolean areAdjacent(Object v, Object u);

    int degree(Object v);
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
class Node<K extends Comparable, V extends Comparable> {
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
     * Time complexity depends on number of marked elements before this node. Expected: O(1)
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
class PriorityQueueFibHeap<K extends Comparable, V extends Comparable> implements IPriorityQueue {
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

/**
 * Class represents edge
 *
 * @param <E> - type of edges weight
 * @author Danila Shulepin
 */
class Edge<E> {
    //From and To vertices, and weight
    Vertex start;
    Vertex end;
    E weight;

    /**
     * Constructor
     *
     * @param start
     * @param end
     * @param weight
     */
    Edge(Vertex start, Vertex end, E weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

}

/**
 * @param <V> - type of vertex
 * @param <E> - type of weight
 * @author Danila Shulepin
 */
class Vertex<V, E> implements Comparable {
    E transverse;
    V key;
    E value;
    int index;
    Vertex parent;

    /**
     * Constructor
     *
     * @param key
     * @param index
     */
    Vertex(V key, int index) {
        this.key = key;
        this.index = index;
    }

    /**
     * Set value to value variable
     *
     * @param value - E type variable
     */
    void setValue(E value) {
        this.value = value;
    }

    //Should be comparable
    @Override
    public int compareTo(Object o) {
        Vertex v = (Vertex) o;
        if (index > v.index) return 1;
        if (index == v.index) return 0;
        return -1;
    }


}


/**
 * Class represents Dynamic Graph
 *
 * @param <V> - type of vertex
 * @param <E> - type of edge
 * @author Danila Shulepin
 */
class DynamicGraph<V extends Comparable<? super V>, E extends Comparable<? super E>> implements IGraph {

    //Adjacency matrix, vertex list, and index of vertex
    int index;
    ArrayList<ArrayList<Edge>> adjacencyMatrix;
    ArrayList<Vertex> vertexList;

    /**
     * Constructor
     */
    DynamicGraph() {
        index = 0;
        adjacencyMatrix = new ArrayList<>();
        vertexList = new ArrayList<>();
    }

    /**
     * Inserting a vertex to the graph
     *
     * Time complexity: O(1)
     *
     * @param o - key of type V
     * @return object of type Vertex
     */
    @Override
    public Object insertVertex(Object o) {

        // Initializing vertex and adding it to the vertex list, incrementing parameter index
        Vertex<V, E> vertex = new Vertex((V) o, index++);
        vertexList.add(vertex);

        // Creating new row in adjacency matrix corresponding to new vertex
        adjacencyMatrix.add(new ArrayList<>());
        return vertex;
    }

    /**
     * Inserting a new edge between two vertices
     *
     * Time complexity: O(|V|)
     *
     * @param from - first vertex
     * @param to - second vertex
     * @param w - weight
     * @return new edge
     */
    @Override
    public Object insertEdge(Object from, Object to, Object w) {

        // Creating a new Edge
        Vertex fromVertex = (Vertex) from;
        Vertex toVertex = (Vertex) to;
        Edge<E> edge = new Edge(fromVertex, toVertex, w);

        if (adjacencyMatrix.get(fromVertex.index).size() < toVertex.index + 1) {
            int i = adjacencyMatrix.get(fromVertex.index).size();
            while (adjacencyMatrix.get(fromVertex.index).size() < toVertex.index) {
                adjacencyMatrix.get(fromVertex.index).add(null);
                i++;
            }
            adjacencyMatrix.get(fromVertex.index).add(edge);
        } else {
            adjacencyMatrix.get(fromVertex.index).set(toVertex.index, edge);
        }

        if (adjacencyMatrix.get(toVertex.index).size() < fromVertex.index + 1) {
            int i = adjacencyMatrix.get(toVertex.index).size();
            while (adjacencyMatrix.get(toVertex.index).size() < fromVertex.index) {
                adjacencyMatrix.get(toVertex.index).add(null);
                i++;
            }
            adjacencyMatrix.get(toVertex.index).add(edge);
        } else {
            adjacencyMatrix.get(toVertex.index).set(fromVertex.index, edge);
        }
        return edge;
    }

    /**
     * Removing vertex from graph
     *
     * Time complexity: O(|V|^2)
     *
     * @param v - vertex
     */
    @Override
    public void removeVertex(Object v) {

        for (int i = ((Vertex)v).index+1;i<vertexList.size();i++){
            vertexList.get(i).index--;
        }
        vertexList.remove(v);

        int ind = ((Vertex) v).index;

        adjacencyMatrix.remove(ind);
        for (ArrayList x :
                adjacencyMatrix) {
            if (x.size()>ind) x.remove(ind);
        }
    }

    /**
     * Removing an edge from graph
     *
     * Time complexity: O(1)
     *
     * @param e - edge
     */
    @Override
    public void removeEdge(Object e) {
        Vertex fromVertex = ((Edge) e).start;
        Vertex toVertex = ((Edge) e).end;

        int n = fromVertex.index;
        int m = toVertex.index;

        adjacencyMatrix.get(n).set(m, null);
        adjacencyMatrix.get(m).set(n, null);
    }

    /**
     * Checking if there exists an edge between two vertices
     *
     * Time complexity: O(1)
     *
     * @param v - first vertex
     * @param u - second vertex
     * @return true, if they are adjacent
     */
    @Override
    public boolean areAdjacent(Object v, Object u) {
        return adjacencyMatrix.get(((Vertex) v).index).get(((Vertex) u).index) != null;
    }

    /**
     * Searching for number of adjacent edges
     *
     * Time complexity: O(|V|)
     *
     * @param v - vertex
     * @return number of adjacent edges
     */
    @Override
    public int degree(Object v) {
        int ind = ((Vertex) v).index;
        int count = 0;

        for (Object o :
                adjacencyMatrix.get(ind)) {
            if (o != null) {
                count += 1;
            }
        }
        return count;
    }

    /**
     *	Searching for vertex by a key
     *
     * Time complexity: O(|V|)
     *
     * @param key - a key
     * @return vertex
     */
    Vertex find(V key) {
        for (Vertex v :
                vertexList) {
            if (v.key.equals(key)) return v;
        }
        return null;
    }


}