package org.example.DataStructure.heaps;

public class BinomialHeapNode {
    String value;
    Integer key;
    int grade; // how much child it has (grade)

    // Links to their other nodes.
    BinomialHeapNode parent;
    BinomialHeapNode sibling;
    BinomialHeapNode child;

    // link for the root
    BinomialHeapNode next;
    BinomialHeapNode prev;


    public BinomialHeapNode(Integer key, String value) {
        this.value = value;
        this.key = key;
        this.grade = 0;

        this.parent = null;
        this.sibling = null;
        this.child = null;

        this.next = this;
        this.prev = this;
    }



    // Convierte a 'node' en hijo de 'this'
    // IMPORTANTE: Se asume que 'this.key <= node.key'
    public void linkNode(BinomialHeapNode node) {
        if (node == null) return;
        node.parent = this;
        node.sibling = this.child;
        this.child = node;
        this.grade++;
        // Reseteamos sus punteros de lista circular para evitar punteros basura
        node.next = node;
        node.prev = node;
    }

    public void printNode(){
        System.out.print("Nodo: Key= " + this.key + " Value= " + this.value + " Grade= " + this.grade);
    }
}
