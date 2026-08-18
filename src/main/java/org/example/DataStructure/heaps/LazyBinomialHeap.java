package org.example.DataStructure.heaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LazyBinomialHeap {

    private BinomialHeapNode head;
    private BinomialHeapNode min;
    private int size;

    // Inicialización Necesita mínimo un nodo para existir
    public LazyBinomialHeap(Integer key,String value) {
        BinomialHeapNode binomialHeapNode = new BinomialHeapNode(key, value);
        head = binomialHeapNode;
        min = binomialHeapNode;
        size = 1;
    }

    public BinomialHeapNode enqueue(Integer key,String value) {
        BinomialHeapNode newNode = new BinomialHeapNode(key, value);
        BinomialHeapNode tail = this.head.prev;

        // Nueva cola
        tail.next = newNode;
        newNode.prev = tail;
        //Unimos nueva cola
        newNode.next = head;
        this.head.prev = newNode;

        this.size++;
        // Verificamos si es un mínimo global
        if (newNode.key < min.key) {
            this.min = newNode;
        }
        return newNode;
    }

    public void meld(LazyBinomialHeap heap) {
        BinomialHeapNode node1head = this.head;
        BinomialHeapNode node1tail = this.head.prev;
        BinomialHeapNode node2head = heap.head;
        BinomialHeapNode node2tail = heap.head.prev;

        // conectamos cola de n1 con cabeza de n2
        node1tail.next = node2head;
        node2head.prev = node1tail;

        // conecatmos cabeza de n1 con cola de n2
        node1head.prev = node2tail;
        node2tail.next = node1head;

        if (heap.min.key < this.min.key) {
            this.min = heap.min;
        }

        this.size += heap.size;

        heap.head = null;
        heap.min = null;
        heap.size = 0;
    }


    // Extract Min
    //________________________________________________________________________________________________
    public BinomialHeapNode extractMin(){
        if (this.head == null) {
            return null;
        }
        // Extraer el minimo y suvir sus hijos a la lista del root
        BinomialHeapNode min = this.min;
        subirHijosARoot(min); // el padre se desancla tambien aquí
        if (this.head != null) {
            coalesce();
        } else {
            this.min = null;
        }
        return min;
    }

    private void subirHijosARoot(BinomialHeapNode node) {
        // Para cada hijo quitamos su puntero al padre y lo subimos a la lista.
        BinomialHeapNode current = node.child;

        while (current != null) {
            BinomialHeapNode siblingNode = current.sibling;
            current.parent = null;
            current.sibling = null;
            subirAroot(current);
            current = siblingNode;
        }

        node.child = null;

        if (node.next == node) {
            // Era el único nodo raíz en el heap
            this.head = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;

            // Si el mínimo era justamente la cabeza, movemos 'head'
            if (this.head == node) {
                this.head = node.next;
            }
        }

        // Aislamos los punteros de raíz del nodo eliminado
        node.next = node;
        node.prev = node;
    }

    private void subirAroot(BinomialHeapNode node){
        if (this.head == null) {
            this.head = node;
            node.next = node;
            node.prev = node;
        } else {
            BinomialHeapNode tail = this.head.prev;
            tail.next = node;
            node.prev = tail;
            node.next = this.head;
            this.head.prev = node;
        }
    }

    private void coalesce(){
        // Creamos los slots (temporales)
        BinomialHeapNode[] slots = new BinomialHeapNode[64]; // suficiente para muchas aplicaciones

        if (this.head == null){
            return;
        }
        // Recorremos cada elemento del heap e insertamos en el slot, si hubiera 2, se lo unen con link child y se lo pasa al siguiente.
        BinomialHeapNode tail = this.head.prev;
        tail.next = null;

        BinomialHeapNode current = this.head;

        while (current != null) {
            // Guardamos el Siguiente ANTES de modificar 'current'
            BinomialHeapNode nextNode = current.next;

            // Desconectamos 'current' para procesarlo de forma aislada
            current.next = current;
            current.prev = current;

            // Insertamos en los slots (se fusionará si hay colisión)
            insertarNodoEnSlot(slots, current);

            current = nextNode;
        }
        // tenemos todos los nodos que necesitamos en los slots. Solo falta unirlos todos
        this.head = null;
        this.min = null;
        for (int i = 0; i < 64; i++) {
            if (slots[i] != null) {
                BinomialHeapNode temp = slots[i];
                if (this.head == null){
                    this.head = temp;
                    this.head.next = this.head;
                    this.head.prev = this.head;
                    this.min = temp;
                }
                else{
                    this.head.prev.next = temp;
                    temp.prev = this.head.prev;
                    temp.next = this.head;
                    this.head.prev = temp;
                    if (temp.key < this.min.key){
                        this.min = temp;
                    }
                }
            }
        }

    }
    private void insertarNodoEnSlot(BinomialHeapNode[] slots, BinomialHeapNode current){

        while (slots[current.grade] != null) {
            BinomialHeapNode temp = slots[current.grade];
            slots[current.grade] = null; // Vaciamos el slot ocupado

            // El nodo con menor clave gana y se queda como raíz del nuevo árbol
            if (current.key <= temp.key) {
                current.linkNode(temp);
            } else {
                temp.linkNode(current);
                current = temp; // El nuevo árbol resultante pasa a ser 'temp'
            }
        }

        // Al encontrar una casilla libre (null), colocamos el árbol consolidado
        slots[current.grade] = current;

    }
    //________________________________________________________________________________________________

    public void decreaseKey(BinomialHeapNode node, Integer key){
        // 1. Verificaciones
        if (node == null || key == null) {
            throw new IllegalArgumentException("El nodo no puede ser nulo. ni la llave tampoco");
        }
        if (key >= node.key) {
            throw new IllegalArgumentException("La nueva clave debe ser menor que la actual.");
        }
        // 2. Actualizar la clave del nodo
        node.key = key;

        // 3. Hacer el fixeado hacia arriba
        BinomialHeapNode temp = node;

        while (temp.parent != null && temp.key < temp.parent.key) {
            // Swampeamos
            // valor y clave
            int tempValue = temp.parent.key;
            String tempValue2 = temp.parent.value;

            temp.parent.key = temp.key;
            temp.parent.value = temp.value;

            temp.key = tempValue;
            temp.value = tempValue2;

            temp = temp.parent;


        }
        // El padre puede ser nulo, entonces estamos en una raiz

        if (this.min == null || temp.key < this.min.key ){
            this.min = temp;
        }

    }

    // Métodos para impirmir
    public void printHeapBySlots() {
        if (this.head == null) {
            System.out.println("El Heap está totalmente vacío.");
            return;
        }

        // Mapa para agrupar las raíces por su Grado (Slot)
        Map<Integer, List<BinomialHeapNode>> slots = new HashMap<>();
        int maxGrade = 0;

        // 1. Recorrer la lista circular de raíces una sola vez
        BinomialHeapNode curr = this.head;
        do {
            int g = curr.grade;
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
        System.out.println("===== ESTADO DEL LAZY BINOMIAL HEAP (POR SLOTS) =====");
        for (int grade = 0; grade <= maxGrade; grade++) {
            System.out.println("Slot " + grade + ":");
            List<BinomialHeapNode> nodesInSlot = slots.get(grade);

            if (nodesInSlot == null || nodesInSlot.isEmpty()) {
                System.out.println("   (vacío)");
            } else {
                int count = 1;
                for (BinomialHeapNode node : nodesInSlot) {
                    System.out.println("   " + count + "º nodo -> Key: " + node.key + " | Value: \"" + node.value + "\"");

                    // Mostrar si tiene hijos (subárbol) de forma indentada
                    if (node.child != null) {
                        printSubtree(node.child, "      ");
                    }
                    count++;
                }
            }
        }
        System.out.println("====================================================");
    }
    private void printSubtree(BinomialHeapNode childNode, String indent) {
        BinomialHeapNode currChild = childNode;
        while (currChild != null) {
            System.out.println(indent + "└── [Hijo Grado " + currChild.grade + "] Key: " + currChild.key + " | Value: \"" + currChild.value + "\"");
            if (currChild.child != null) {
                printSubtree(currChild.child, indent + "    ");
            }
            currChild = currChild.sibling; // Recorrer hermanos
        }
    }

}
