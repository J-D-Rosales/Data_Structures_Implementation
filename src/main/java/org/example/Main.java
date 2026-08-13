package org.example;

import org.example.DataStructure.GraphStreamVisualizer;
import org.example.DataStructure.TreeListener;
import org.example.DataStructure.heaps.BinomialHeapNode;
import org.example.DataStructure.heaps.LazyBinomialHeap;
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
        LazyBinomialHeap heap1 = new LazyBinomialHeap(1, "Chanchito 1");
        heap1.enqueue(2, "Chanchito 2");
        heap1.enqueue(3, "Chanchito 3");
        heap1.enqueue(4, "Chanchito 4");
        heap1.enqueue(5, "Chanchito 5");
        heap1.enqueue(6, "Chanchito 6");
        heap1.enqueue(7, "Chanchito 7");
        heap1.enqueue(8, "Chanchito 8");
        heap1.enqueue(9, "Chanchito 9");
        heap1.enqueue(10, "Chanchito 10");
        heap1.enqueue(11, "Chanchito 11");
        heap1.enqueue(12, "Chanchito 12");
        heap1.enqueue(13, "Chanchito 13");
        heap1.enqueue(14, "Chanchito 14");
        heap1.enqueue(15, "Chanchito 15");
        heap1.enqueue(16, "Chanchito 16");
        heap1.enqueue(17, "Chanchito 17");
        heap1.enqueue(18, "Chanchito 18");
        heap1.enqueue(19, "Chanchito 19");
        heap1.enqueue(20, "Chanchito 20");

        BinomialHeapNode minimo = heap1.extractMin();

        System.out.println();
        System.out.println("MInimo es:");
        if (minimo != null) {
            minimo.printNode();
        }
        System.out.println();
        System.out.println();

        heap1.printHeapBySlots();
    }
    public static void main(String[] args){
        binomialHeapExample();
    }
}