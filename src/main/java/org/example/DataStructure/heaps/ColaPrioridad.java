package org.example.DataStructure.heaps;


import java.util.ArrayList;
import java.util.List;

public class ColaPrioridad {

    public ColaPrioridad(){
        this.cola = new ArrayList<>();
    }

    public static class Elemento {
        int clave;
        String valor;

        public Elemento(int clave, String valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private final List<Elemento> cola;

    public int parent(int i){
        return (i-1)/2;
    }

    public int leftchild(int i){
        return 2*i+1;
    }
    public int rightchild(int i){
        return 2*i+2;
    }
    public int size(){
        return cola.size();
    }

    public void insertar(int clave){
        cola.add(new Elemento(clave, "valor"));
        int ultimoIndice = cola.size() - 1;
        heapifyUp(ultimoIndice);
    }

    private void heapifyUp(int index){
        while (index > 0){
            int parentIndex = parent(index);

            if (cola.get(index).clave > cola.get(parentIndex).clave){
                cambiarElementos(index,parentIndex);
                index = parentIndex;
            }
            else{
                break;
            }
        }
    }
    // Cambia elemeento A -> B, sus valores
    private void cambiarElementos(int index, int parent){
        Elemento aux = cola.get(index);
        cola.set(index, cola.get(parent));
        cola.set(parent, aux);
    }

    public Elemento extractMax(){
        if (cola.isEmpty()) return null;

        // Extraer el elemento con mayor clave
        Elemento max = cola.getFirst();

        if (cola.size() == 1) {
            cola.removeLast();
            return max;
        }

        // Setemaos la raiz como el ultimo elemento y eleiminamos el ultimo elemento
        cola.set(0,cola.removeLast());


        // Hacemos el hepifydown al elemento primero
        heapifyDown(0);
        return max;
    }

    private void heapifyDown(int index){
        int size = cola.size();

        while (leftchild(index) < cola.size()){
            int left = leftchild(index);
            int right = rightchild(index);
            int mayor = left;
            // si el elemento es mayor a todos
            if (right < size && cola.get(right).clave > cola.get(left).clave) {
                mayor = right;
            }
            if (cola.get(index).clave >= cola.get(mayor).clave) {
                break;
            }
            cambiarElementos(index, mayor);
            index = mayor;
        }
    }


    public void imprimirHeap() {
        if (cola.isEmpty()) {
            System.out.println("El Heap está totalmente vacío.");
            return;
        }

        System.out.println("\n================ ESTADO DEL BINARY HEAP ================");

        // 1. Mostrar como Arreglo / Lista plana
        System.out.println("--- VISTA EN ARREGLO (Índices y Nodos) ---");
        for (int i = 0; i < cola.size(); i++) {
            Elemento e = cola.get(i);
            System.out.println("[" + i + "] -> Key: " + e.clave + " | Value: \"" + e.valor + "\"");
        }

        // 2. Mostrar como Árbol Visual
        System.out.println("\n--- VISTA EN ÁRBOL JERÁRQUICO ---");
        imprimirArbol(0, "", true);

        System.out.println("========================================================\n");
    }

    private void imprimirArbol(int index, String prefix, boolean isTail) {
        if (index < cola.size()) {
            Elemento nodo = cola.get(index);

            // Formato estético con caracteres de árbol
            System.out.println(prefix + (isTail ? "└── " : "├── ") + "[" + index + "] Key: " + nodo.clave);

            int left = leftchild(index);
            int right = rightchild(index);

            // Si tiene al menos un hijo, recurrimos
            boolean tieneHijoIzquierdo = left < cola.size();
            boolean tieneHijoDerecho = right < cola.size();

            if (tieneHijoIzquierdo || tieneHijoDerecho) {
                String newPrefix = prefix + (isTail ? "    " : "│   ");

                // Imprimir hijo izquierdo
                if (tieneHijoIzquierdo) {
                    imprimirArbol(left, newPrefix, !tieneHijoDerecho);
                }

                // Imprimir hijo derecho
                if (tieneHijoDerecho) {
                    imprimirArbol(right, newPrefix, true);
                }
            }
        }
    }

}