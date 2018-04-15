package com.procharvester.util;

public class CircularBuffer<T> {

    private T[] buffer;

    private int tail;

    private int head;


    public CircularBuffer(int n) {
        buffer = (T[]) new Object[n];
        tail = 0;
        head = 0;
    }

    public void add(T toAdd) {
        buffer[head++] = toAdd;
        if (head == tail) {
            tail++;
            tail = tail % buffer.length;
        }
        head = head % buffer.length;
    }

    public T get() {
        T t = null;
        int adjTail = tail > head ? tail - buffer.length : tail;
        if (adjTail < head) {
            t = (T) buffer[tail++];
            tail = tail % buffer.length;
        } else {
            return null;
        }
        return t;
    }

    public String toString() {
        return "CircularBuffer(size=" + buffer.length + ", head=" + head + ", tail=" + tail + ")";
    }
}