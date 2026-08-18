package org.example.DataStructure.heaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FibonacciHeap {
    FibonacciHeapNode head;
    FibonacciHeapNode min;
    int size;

    public FibonacciHeap() {
        this.head = null;
        this.min = null;
        this.size = 0;
    }

    public FibonacciHeapNode enqueue(Integer key,String value) {
        FibonacciHeapNode newNode = new FibonacciHeapNode(key, value);
        if (head == null) {
            this.head = newNode;
            this.min = newNode;
            this.size = 1;
            return newNode;
        }
        FibonacciHeapNode tail = this.head.prev;

        newNode.prev = tail;
        newNode.next = this.head;

        this.head.prev = newNode;
        tail.next = newNode;
        this.size++;

        if (newNode.key < min.key) {
            this.min = newNode;
        }

        return newNode;
    }

    public FibonacciHeapNode extractMin() {
        if (this.head == null) {
            return null;
        }

        FibonacciHeapNode min = this.min;
        subirHijosARoot(min);

        if (min.next == min) {
            this.head = null;
            this.min = null;
        } else {
            min.prev.next = min.next;
            min.next.prev = min.prev;
            if (this.head == min) {
                this.head = min.next;
            }

            // 3. Consolidar el bosque para unir árboles de igual grado y actualizar this.min
            coalesce();
        }

        min.next = null;
        min.prev = null;
        min.parent = null;
        min.child = null;
        min.isMarked = false;

        this.size--;
        return min;
    }

    private void coalesce() {
        if (head == null) return;

        List<FibonacciHeapNode> rootList = new ArrayList<>();
        FibonacciHeapNode curr = this.head;
        do {
            rootList.add(curr);
            curr = curr.next;
        } while (curr != this.head);

        FibonacciHeapNode[] slots = new FibonacciHeapNode[64];

        for (FibonacciHeapNode x : rootList) {
            int d = x.degree;

            while (slots[d] != null) {
                FibonacciHeapNode y = slots[d];
                slots[d] = null;

                if (x.key > y.key) {
                    FibonacciHeapNode temp = x;
                    x = y;
                    y = temp;
                }

                x.linkNode(y);

                d = x.degree;
            }
            slots[d] = x;
        }

        // Unimos los slots
        this.head = null;
        this.min = null;

        for (int i = 0; i < 64; i++) {
            if (slots[i] != null) {
                FibonacciHeapNode node = slots[i];
                node.parent = null;

                if (this.head == null){
                    this.head = node;
                    this.head.next = node;
                    this.head.prev = node;
                    this.min = node;
                }
                else{
                    FibonacciHeapNode tail = this.head.prev;

                    node.prev = tail;
                    node.next = this.head;
                    tail.next = node;
                    this.head.prev = node;

                    if (node.key < this.min.key){
                        this.min = slots[i];
                    }
                }
            }
        }
    }

    public void subirHijosARoot(FibonacciHeapNode node) {
        if (node == null || node.child == null) {
            return;
        }

        FibonacciHeapNode current = node.child;
        int numChildren = node.degree;

        for (int i = 0; i < numChildren; i++) {
            FibonacciHeapNode nextChild = current.next;
            subirAroot(current);
            current = nextChild;
        }
        node.child = null;
        node.degree = 0;
    }

    private void subirAroot(FibonacciHeapNode node){
        if (this.head == null) {
            this.head = node;
            node.next = node;
            node.prev = node;
        } else {
            FibonacciHeapNode tail = this.head.prev;
            node.prev = tail;
            node.next = this.head;
            tail.next = node;
            this.head.prev = node;
        }
        node.parent = null;
        node.isMarked = false;
    }

    public void decreaseKey(FibonacciHeapNode nodo, Integer nuevoKey){
        // verificaciones
        if (nodo == null || nuevoKey == null){
            return;
        }
        if (nuevoKey >= nodo.key){
            return;
        }

        // cambiamos la llave
        nodo.key = nuevoKey;

        // La llave es menor que la de su padre, debemos actualizarla
        FibonacciHeapNode parent = nodo.parent;

        if (parent != null && nuevoKey < parent.key){
            cut(nodo,parent);
            cascadingRoot(parent);
        }

        if (this.min == null || nodo.key < this.min.key) {
            this.min = nodo;
        }

    }

    private void cut(FibonacciHeapNode nodo, FibonacciHeapNode parent){

        // Reestablecesmos la lista
        if (nodo.next != nodo){
            nodo.next.prev = nodo.prev;
            nodo.prev.next = nodo.next;

            if (parent.child == nodo) {
                parent.child = nodo.next;
            }
        }
        else{
            parent.child = null;
        }
        parent.degree--;

        // Aislamiento nodal

        nodo.parent = null;

        if (this.head == null){
            nodo.next = nodo;
            nodo.prev = nodo;
            this.head = nodo;
        }
        else {
            // Unimos a la raiz principa,
            FibonacciHeapNode tail = this.head.prev;
            nodo.prev = tail;
            nodo.next = this.head;
            this.head.prev = nodo;
            tail.next = nodo;
         }

        nodo.isMarked = false;

        }

    private void cascadingRoot(FibonacciHeapNode nodo){
        FibonacciHeapNode parent = nodo.parent;

        while (parent != null) {
            if (!nodo.isMarked) {
                nodo.isMarked = true;
                break; // La cascada se detiene al marcar el primer nodo
            } else {
                cut(nodo, parent); // Cortamos el nodo marcado
                nodo = parent;     // Subimos al abuelo
                parent = nodo.parent;
            }
        }
    }


    // Métodos para imprimir el Fibonacci Heap
    public void printHeapBySlots() {
        if (this.head == null) {
            System.out.println("El Heap está totalmente vacío.");
            return;
        }

        // Mapa para agrupar las raíces por su Grado (Slot)
        Map<Integer, List<FibonacciHeapNode>> slots = new HashMap<>();
        int maxGrade = 0;

        // 1. Recorrer la lista circular de raíces principales
        FibonacciHeapNode curr = this.head;
        do {
            int g = curr.degree;
            if (!slots.containsKey(g)) {
                slots.put(g, new ArrayList<>());
            }
            slots.get(g).add(curr);

            if (g > maxGrade) {
                maxGrade = g;
            }

            curr = curr.next;
        } while (curr != this.head);

        // 2. Imprimir slot por slot desde 0 hasta el grado máximo encontrado
        System.out.println("===== ESTADO DEL FIBONACCI HEAP (POR SLOTS) =====");
        for (int grade = 0; grade <= maxGrade; grade++) {
            System.out.println("Slot " + grade + ":");
            List<FibonacciHeapNode> nodesInSlot = slots.get(grade);

            if (nodesInSlot == null || nodesInSlot.isEmpty()) {
                System.out.println("   (vacío)");
            } else {
                int count = 1;
                for (FibonacciHeapNode node : nodesInSlot) {
                    String markedStr = node.isMarked ? " [M]" : "";
                    String isMinStr = (node == this.min) ? " <--- (MIN)" : "";

                    System.out.println("   " + count + "º nodo -> Key: " + node.key
                            + " | Value: \"" + node.value + "\""
                            + markedStr + isMinStr);

                    // Mostrar subárbol si el nodo tiene hijos
                    if (node.child != null) {
                        printSubtree(node.child, "      ");
                    }
                    count++;
                }
            }
        }
        System.out.println("=================================================");
    }

    private void printSubtree(FibonacciHeapNode childNode, String indent) {
        if (childNode == null) return;

        // Recorrido de lista CIRCULAR de hijos (evita bucles infinitos)
        FibonacciHeapNode currChild = childNode;
        do {
            String markedStr = currChild.isMarked ? " [M]" : "";
            System.out.println(indent + "└── [Hijo Grado " + currChild.degree + "] Key: "
                    + currChild.key + " | Value: \"" + currChild.value + "\"" + markedStr);

            // Llamada recursiva si este hijo también tiene sus propios hijos
            if (currChild.child != null) {
                printSubtree(currChild.child, indent + "    ");
            }

            currChild = currChild.next; // Avanzamos por el anillo de hermanos
        } while (currChild != childNode); // Terminamos al dar la vuelta completa
    }
}
