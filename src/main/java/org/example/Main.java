package org.example;

import org.example.DataStructure.GraphStreamVisualizer;
import org.example.DataStructure.TreeListener;
import org.example.DataStructure.heaps.*;
import org.example.DataStructure.quadtree.QuadTree;
import org.example.DataStructure.utils.Point2d;


public class Main {

    public static void quadTreeExample() throws InterruptedException {
        System.setProperty("org.graphstream.ui", "swing");

        QuadTree quadTree = new QuadTree();
        TreeListener treeListener = new GraphStreamVisualizer();
        quadTree.setTreeListener(treeListener);
        Thread.sleep(1000);

        Point2d punto1 = new Point2d(10, 10);
        quadTree.insertar(punto1);
        Thread.sleep(1000);

        Point2d punto2 = new Point2d(15, 20);
        quadTree.insertar(punto2);
        Thread.sleep(1000);

        Point2d punto3 = new Point2d(5, 5);
        quadTree.insertar(punto3);
        Thread.sleep(1000);

        Point2d punto4 = new Point2d(15, 9);
        quadTree.insertar(punto4);
        Thread.sleep(1000);

        Point2d punto5 = new Point2d(5, 15);
        quadTree.insertar(punto5);
        Thread.sleep(1000);

        Point2d punto6 = new Point2d(10, 20);
        quadTree.insertar(punto6);
        Thread.sleep(1000);

        Point2d punto7 = new Point2d(20, 10);
        quadTree.insertar(punto7);
        Thread.sleep(1000);

        Point2d punto8 = new Point2d(15, 15);
        quadTree.insertar(punto8);
        Thread.sleep(1000);

        Point2d punto9 = new Point2d(2, 18);
        quadTree.insertar(punto9);
        Thread.sleep(1000);

        Point2d punto10 = new Point2d(15, 14);
        quadTree.insertar(punto10);
        Thread.sleep(1000);

        quadTree.delete(punto2);
        System.out.println("Finished processing.");
    }

    public static void binomialHeapExample() {
        LazyBinomialHeap heap1 = new LazyBinomialHeap(11, "Chanchito 11");
        heap1.enqueue(2, "Chanchito 2");
        heap1.enqueue(3, "Chanchito 3");
        heap1.enqueue(4, "Chanchito 4");
        heap1.enqueue(5, "Chanchito 5");
        heap1.enqueue(6, "Chanchito 6");
        heap1.enqueue(7, "Chanchito 7");
        heap1.enqueue(8, "Chanchito 8");
        heap1.enqueue(9, "Chanchito 9");

        // Antes
        heap1.printHeapBySlots();

        BinomialHeapNode chanchitoAEliminar = heap1.enqueue(10, "Chanchito 10");

        heap1.decreaseKey(chanchitoAEliminar, 1);

        // Despues
        heap1.printHeapBySlots();
    }

    public static void fibonacciHeapExample() {
        FibonacciHeap heap = new FibonacciHeap();
        FibonacciHeapNode example =heap.enqueue(10, "Chanchito 1");
        heap.enqueue(20, "Chanchito 2");
        heap.enqueue(30, "Chanchito 3");
        heap.enqueue(40, "Chanchito 4");
        heap.enqueue(50, "Chanchito 5");

        heap.printHeapBySlots();

        heap.extractMin();

        heap.printHeapBySlots();

        heap.decreaseKey(example,1 );

        heap.printHeapBySlots();
    }

    public static void main(String[] args){
        fibonacciHeapExample();
    }
}