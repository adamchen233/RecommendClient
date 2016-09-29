/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

/**
 *
 * @author xiaohui
 */
import java.util.LinkedList;

public class Queue {

    private LinkedList<Object> queue = new LinkedList<Object>();

    public void enQueue(Object t) {
        queue.addLast(t);
    }

    public Object deQueue() {
        return queue.removeFirst();
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public boolean contians(Object t) {
        return queue.contains(t);
    }
    public int getNum() {
        return queue.size();
    }
}
