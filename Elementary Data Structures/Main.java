package com.company;

import javax.naming.SizeLimitExceededException;
import java.io.*;
import java.util.NoSuchElementException;

public class Main {

    public static int flag = 0;

    public static void main(String[] args) throws SizeLimitExceededException, CloneNotSupportedException, IOException, ClassNotFoundException {
        BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
        String line = scan.readLine();
        int numberOfLines = Integer.parseInt(line.split(" ")[0]);
        int capacityOfStack = Integer.parseInt(line.split(" ")[1]);
        DoubleHashSet<String> mySet = new DoubleHashSet<>(numberOfLines);
        BoundedStack<DoubleHashSet> myStack = new BoundedStack<>(capacityOfStack);

        myStack.push(new DoubleHashSet(mySet));

        for (int i = 0; i < numberOfLines; i++) {
//
            line = scan.readLine();
            String line1;
            String line2 = "";
            if (line.contains(" ")) {
                line1 = line.split(" ")[0];
                line2 = line.split(" ")[1];
            } else line1 = line;
            if (line1.equals("NEW")) {
                if (line2.endsWith("/")) {
                    if (mySet.contains(line2.substring(0, line2.length() - 1))
                            || mySet.contains(line2)) {
                        System.out.println("ERROR: cannot execute " + line);
                    } else {
                        mySet.add(line2);
                        //if (flag==0)

                        myStack.push(new DoubleHashSet(mySet));
                    }
                } else {
                    if (mySet.contains(line2 + ("/")) || mySet.contains(line2)) {
                        System.out.println("ERROR: cannot execute " + line);
                    } else {
                        mySet.add(line2);
                        //if (flag==0)

                        myStack.push(new DoubleHashSet(mySet));
                    }
                }

            } else if (line1.equals("REMOVE")) {
                if (mySet.contains(line2)) {
                    mySet.remove(line2);
                    //if (flag==0)

                    myStack.push(new DoubleHashSet(mySet));

                } else {
                    System.out.println("ERROR: cannot execute " + line);
                }
            } else if (line1.equals("LIST")) {
                mySet.print();
            } else if (line1.equals("UNDO")) {
                try {
                    if (line2.equals("")) {
                        //flag = 1;
                        line2 = "1";
                        //flag = 0;
                    }
                    if (myStack.size() - 1 >= Integer.parseInt(line2)) {
                        //flag = 1;
                        for (int j = 0; j < Integer.parseInt(line2); j++) {
                            myStack.pop();

                            mySet = new DoubleHashSet<>(myStack.top());
                        }
                        //flag = 0;
                    } else System.out.println("ERROR: cannot execute " + line);
                } catch (NoSuchElementException ex) {
                    //flag = 0;
                    System.out.println("ERROR: cannot execute " + line);
                }
            }
        }

    }
}


/**
 *  ADT for stack
 *
 * @param <T> Type of stored element
 * @author Danila Shulepin
 * @version 1.0 19.02.2022
 */
interface IBoundedStack<T> {
    void push(T value); // push an element onto the stack

    // remove the oldest element
    // when if stack is full
    T pop(); // remove an element from the top of the stack

    T top(); // look at the element at the top of the stack

    // (without removing it)
    void flush(); // remove all elements from the stack

    boolean isEmpty(); // is the stack empty?

    boolean isFull(); // is the stack full?

    int size(); // number of elements

    int capacity(); // maximum capacity
}

interface ISet<T> {
    void add(T item) throws SizeLimitExceededException; // add item in the set

    void remove(T item); // remove an item from a set

    boolean contains(T item); // check if a item belongs to a set

    int size(); // number of elements in a set

    boolean isEmpty(); // check if the set is empty
}

interface ICircularBoundedQueue<T> {
    void offer(T value);

    T poll();

    T peek();

    void flush();

    boolean isEmpty();

    boolean isFull();

    int size();

    int capacity();
}

/**
 * Implements ArrayCircularBoundedQueue.
 *
 * @param <T> type of stored values
 */
class ArrayCircularBoundedQueue<T> implements ICircularBoundedQueue<T> {
    private static int capacity;
    private int size = 0;

    private int front = 0;
    private int rear = 0;
    private T array[];

    /**
     * Makes new ArrayCircularBoundedQueue with given capacity
     *
     * @param capacity maximum capacity
     */
    ArrayCircularBoundedQueue(int capacity) {
        this.capacity = capacity;
        array = (T[]) (new Object[capacity]);
        for (int i = 0; i < capacity; i++) {
            array[i] = null;
        }
    }

    /**
     * Offers element at the front of the queue.
     *
     * @param value value to be offered
     */
    @Override
    public void offer(T value) {
        if (isEmpty()) {
            front = 0;
            rear = 0;
            array[0] = value;
        } else {
            rear = (rear + 1) % capacity;
            array[rear] = value;
        }
        if (!isFull()) size++;
        else front = (front + 1) % capacity;
    }

    /**
     * Deletes last element from the back.
     *
     * @return deleted element
     */
    @Override
    public T poll() {
        T value = array[front];
        array[front] = null;
        front = (front + 1) % capacity;
        this.size--;
        if (value == null) throw new NoSuchElementException();
        return value;
    }

    /**
     * Returns last element from the back.
     *
     * @return last element
     */
    @Override
    public T peek() {
        return array[front];
    }

    /**
     * Flushes all elements.
     */
    @Override
    public void flush() {
        for (int i = 0; i < capacity; i++) {
            array[i] = null;
        }
        front = 0;
        rear = 0;
        size = 0;
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty and false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks if the queue is full.
     *
     * @return true if the queue is full and false otherwise
     */
    @Override
    public boolean isFull() {
        return size == capacity;
    }

    /**
     * Returns current size of the queue.
     *
     * @return current size of the queue
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Return max capacity of the queue.
     * @return max capacity of the queue
     */
    @Override
    public int capacity() {
        return capacity;
    }
}

/**
 * Implements BoundedStack.
 * @param <T> type of stored elements
 * @author Danila Shulepin
 * @version 1.0
 */
class BoundedStack<T> implements IBoundedStack<T> {
    private ArrayCircularBoundedQueue queueEven;
    private ArrayCircularBoundedQueue queueOdd;
    private int count = 0;
    private int capacity;

    /**
     * Makes a new stack with given capacity.
     * @param capacity maximum capacity of the stack
     */
    BoundedStack(int capacity) {
        queueEven = new ArrayCircularBoundedQueue(capacity);
        queueOdd = new ArrayCircularBoundedQueue(capacity);
    }

    /**
     * Pushes element at the top of the stack.
     * @param value the value of pushed element
     */
    @Override
    public void push(T value) {
        if (this.count % 2 == 0) {
            queueEven.offer(value);
        } else {
            queueOdd.offer(value);
        }
    }

    /**
     * Deletes element from the top of the stack.
     * @return deleted value
     */
    @Override
    public T pop() {
        int size;
        if (this.count % 2 == 0) {
            size = queueEven.size();
            queueOdd.flush();
            for (int i = 0; i < size - 1; i++) {
                queueOdd.offer(queueEven.poll());
            }
            this.count++;
            return (T) queueEven.poll();
        } else {
            size = queueOdd.size();
            queueEven.flush();
            for (int i = 0; i < size - 1; i++) {
                queueEven.offer(queueOdd.poll());
            }
            this.count++;
            return (T) queueOdd.poll();
        }
    }

    /**
     * Shows the element from the top of the stack.
     * @return the element from the top of the stack
     */
    @Override
    public T top() {
        int size;
        if (this.count % 2 == 0) {
            size = queueEven.size();
            queueOdd.flush();
            for (int i = 0; i < size - 1; i++) {
                queueOdd.offer(queueEven.poll());
            }
            this.count++;
            T value = (T) queueEven.poll();
            queueOdd.offer(value);
            queueEven.flush();
            return value;

        } else {
            size = queueOdd.size();
            queueEven.flush();
            for (int i = 0; i < size - 1; i++) {
                queueEven.offer(queueOdd.poll());
            }
            this.count++;
            T value = (T) queueOdd.poll();
            queueEven.offer(value);
            queueOdd.flush();
            return value;
        }
    }

    /**
     * Flushes all elements from the stack.
     */
    @Override
    public void flush() {
        queueOdd.flush();
        queueEven.flush();
    }

    /**
     * Checks if the stack is empty and false otherwise.
     * @return true if the stack is empty and false otherwise
     */
    @Override
    public boolean isEmpty() {
        return (queueEven.size() + queueOdd.size()) == 0;
    }

    /**
     * Checks if the stack is full and false otherwise.
     * @return true if the stack is full and false otherwise
     */
    @Override
    public boolean isFull() {
        return (queueEven.size() + queueOdd.size()) == capacity();
    }

    /**
     *
     * @return
     */
    @Override
    public int size() {
        return queueEven.size() + queueOdd.size();
    }

    @Override
    public int capacity() {
        return queueEven.capacity();
    }
}

class DoubleHashSet<T> implements ISet<T>, Serializable {
    private T[] array;
    private boolean[] deleted;
    private int capacity;
    private int index = -1;
    private int size = 0;

    DoubleHashSet(int value) {
        capacity = value;
        array = (T[]) (new Object[capacity]);
        deleted = new boolean[capacity];
    }

    DoubleHashSet(DoubleHashSet<T> set) {
        array = (T[]) set.array.clone();
        deleted = set.deleted.clone();
        capacity = set.capacity;
        index = set.index;
        size = set.size;
    }

    @Override
    public void add(T item) throws SizeLimitExceededException {
        if (isFull()) throw new SizeLimitExceededException();
        if (!contains(item)) {
            array[index] = item;
            deleted[index] = true;
            size++;
        }
    }

    @Override
    public void remove(T item) {
        if (contains(item)) {
            array[index] = null;
            deleted[index] = true;
            size--;
        }
    }

    @Override
    public boolean contains(T item) {
        int hash1 = Math.abs(item.hashCode()) % capacity;
        int hash2 = hashCode2(item);
        index = -1;
        if (isEmpty()) {
            index = hash1;
            return false;
        }
        for (int i = 0; i < capacity; i++) {
            if (array[hash1] == null && !deleted[hash1]) {
                index = hash1;
                return false;
            } else {
                if (array[hash1] == null) ;
                else if (array[hash1].equals(item)) {
                    index = hash1;
                    return true;
                }
            }
            hash1 = (hash1 + hash2) % capacity;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private boolean isFull() {
        return size == capacity;
    }

    private int hashCode2(T item) {
        int hash = 0;
        byte hashArray[] = item.toString().getBytes();
        for (byte x :
                hashArray) {
            hash += (x);
        }
        return hash;
        //return 1;
    }

    public void print() {
        for (int i = 0; i < capacity; i++) {
            if (array[i] != null)
                System.out.print(array[i] + " ");
        }
        System.out.println();
    }

}

