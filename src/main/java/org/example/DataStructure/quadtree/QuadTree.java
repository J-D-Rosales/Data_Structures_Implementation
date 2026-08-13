package org.example.DataStructure.quadtree;

import jdk.jshell.execution.JdiInitiator;
import org.example.DataStructure.TreeListener;
import org.example.DataStructure.utils.Point2d;

import java.awt.*;
import java.util.*;


public class QuadTree {
    // Solo necesitamos el root
    QuadNode root;
    private TreeListener treeListener;

    public QuadTree(){}
    public QuadTree(Point2d point){
        this.root = new QuadNode(point);
    }

    public void setTreeListener(TreeListener treeListener) {
        this.treeListener = treeListener;
    }

    public void insertar(Point2d punto){
        if (punto != null){
            root = insertarRecursivo(punto, root);
        }
        // LLAMADA PARA EL TREE LISTENER;
        if (treeListener != null){
            treeListener.recorrerArbol(root);
        }
    }

    public QuadNode insertarRecursivo(Point2d punto, QuadNode qn) {
        if (punto == null){
            return qn;
        }

        if (qn == null) {
            return new QuadNode(punto);
        }

        if (punto.x() == qn.point.x() && punto.y() == qn.point.y()) {
            return qn;
        }
        else if (punto.x() >= qn.point.x()) {
            if (punto.y() >= qn.point.y()) {
                qn.NE = insertarRecursivo(punto, qn.NE);
            }
            else {
                qn.SE = insertarRecursivo(punto, qn.SE);
            }
        }
        else {
            if (punto.y() >= qn.point.y()) {
                qn.NW = insertarRecursivo(punto, qn.NW);
            }
            else {
                qn.SW = insertarRecursivo(punto, qn.SW);
            }
        }
        return qn;
    }
    // Falta hacer llamada al arbol y que haga algo
    public void buscar(Point2d punto){
        if (punto == null){
            return;
        }
        if (buscarRecursivo(punto,root) != null){
            System.out.println("Nodo Encontrado");
        }
        else {
            System.out.println("Nodo Inexistente");
        }
    }

    public QuadNode buscarRecursivo(Point2d punto,  QuadNode raiz) {
        if (raiz == null){
            return null;
        }
        if (punto.x() == raiz.point.x() && punto.y() == raiz.point.y()) {
            return raiz;
        }
        else if (punto.x() >= raiz.point.x()) {
            if (punto.y() >= raiz.point.y()) {
                return buscarRecursivo(punto, raiz.NE);
            }
            else {
                return buscarRecursivo(punto, raiz.SE);
            }
        }
        else{
            if (punto.y() >= raiz.point.y()) {
                return buscarRecursivo(punto, raiz.NW);
            }
            else {
                return buscarRecursivo(punto, raiz.SW);
            }
        }
    }

    private QuadNode encontrarCandidatoCuadrante(QuadNode actual, int direccion){
        if (actual == null) return null;

        switch (direccion){
            case 0 -> { while (actual.SW != null) actual = actual.SW; }
            case 1 -> { while (actual.SE != null) actual = actual.SE; }
            case 2 -> { while (actual.NW != null) actual = actual.NW; }
            case 3 -> { while (actual.NE != null) actual = actual.NE; }
        }
        return actual;
    }

    private QuadNode[] aplicarCriterioUnoDelete(QuadNode[] nodos){
        QuadNode[] resultado = new QuadNode[nodos.length];

        if (nodos[0] != null && (nodos[1] == null || nodos[0].point.y() <= nodos[1].point.y())
                && (nodos[2] == null || nodos[0].point.x() <= nodos[2].point.x())) {
            resultado[0] = nodos[0];
        }

        if (nodos[1] != null && (nodos[0] == null || nodos[1].point.y() <= nodos[0].point.y())
                && (nodos[3] == null || nodos[1].point.x() <= nodos[3].point.x())) {
            resultado[1] = nodos[1];
        }

        if (nodos[2] != null && (nodos[3] == null || nodos[2].point.y() <= nodos[3].point.y())
                && (nodos[0] == null || nodos[2].point.x() <= nodos[0].point.x())) {
            resultado[2] = nodos[2];
        }

        if (nodos[3] != null && (nodos[2] == null || nodos[3].point.y() <= nodos[2].point.y())
                && (nodos[1] == null || nodos[3].point.x() <= nodos[1].point.x())) {
            resultado[3] = nodos[3];
        }

        return resultado;
    }

    private Object[] aplicarCriterioDosDelete(QuadNode[] nodos, QuadNode root){
        double minDistance = Double.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < 4; i++){
            if (nodos[i] != null) {
                double dist = Math.abs(nodos[i].point.x() - root.point.x()) +
                        Math.abs(nodos[i].point.y() - root.point.y());
                if (dist < minDistance){
                    minDistance = dist;
                    minIndex = i;
                }
            }
        }

        if (minIndex == -1) return null; // No había ningún candidato válido
        return new Object[] { nodos[minIndex], minIndex };
    }

    private Object[] encontrarCandidato(QuadNode root){
        QuadNode[] candidates = new QuadNode[4]; // [0] -> NE, [1] -> NW, [2] -> SE, [3] -> SW
        candidates[0] = encontrarCandidatoCuadrante(root.NE,0);
        candidates[1] = encontrarCandidatoCuadrante(root.NW,1);
        candidates[2] = encontrarCandidatoCuadrante(root.SE,2);
        candidates[3] = encontrarCandidatoCuadrante(root.SW,3);

        QuadNode[] filtrados = aplicarCriterioUnoDelete(candidates); // Devolvera null en el array si el candidato no cumplio

        int cantValidos = 0;
        int indexUnico = -1;

        for (int i = 0; i < 4; i++) {
            if (filtrados[i] != null) {
                cantValidos++;
                indexUnico = i;
            }
        }

        if (cantValidos == 1){
            return new Object[]{ filtrados[indexUnico], indexUnico};
        }

        return aplicarCriterioDosDelete(candidates, root);
    }

    private boolean checkOutsideRegionDelete(QuadNode node, Point2d puntoA, Point2d puntoB){
        if (node == null) return true;

        double x = node.point.x();
        double y = node.point.y();

        boolean xValido = (x < puntoA.x() && x <= puntoB.x()) || (x > puntoA.x() && x > puntoB.x());
        boolean yValido = (y < puntoA.y() && y < puntoB.y()) || (y > puntoA.y() && y > puntoB.y());

        return xValido && yValido;
    }
    public void insertarSubarbol(QuadNode subarbol, QuadNode destinoRoot) {
        if (subarbol == null) {
            return;
        }

        insertarRecursivo(subarbol.point, destinoRoot);

        insertarSubarbol(subarbol.NE, destinoRoot);
        insertarSubarbol(subarbol.NW, destinoRoot);
        insertarSubarbol(subarbol.SE, destinoRoot);
        insertarSubarbol(subarbol.SW, destinoRoot);
    }

    public void InsertarPuntosRoot(QuadNode tree, Point2d puntoCandidato){
        QuadNode nodoDestino = buscarRecursivo(puntoCandidato, this.root);
        insertarSubarbol(tree, nodoDestino);
    }

    public QuadNode adjRoot(QuadNode qn, Point2d puntoA, Point2d puntoB, int cuadranteNodo, int cuadranteCandidato){
        if (qn == null) return null;

        if (checkOutsideRegionDelete(qn, puntoA, puntoB)){
            switch (cuadranteCandidato){
                case 0 -> {
                    if (cuadranteNodo == 1) {
                        qn.SE = adjRoot(qn.SE, puntoA, puntoB, 2, cuadranteCandidato);
                        qn.SW = adjRoot(qn.SW, puntoA, puntoB, 3, cuadranteCandidato);
                    } else {
                        qn.NE = adjRoot(qn.NE, puntoA, puntoB, 0, cuadranteCandidato);
                        qn.SE = adjRoot(qn.SE, puntoA, puntoB, 2, cuadranteCandidato);
                    }
                }
                case 1 -> {
                    if (cuadranteNodo == 0) {
                        qn.SE = adjRoot(qn.SE, puntoA, puntoB, 2, cuadranteCandidato);
                        qn.SW = adjRoot(qn.SW, puntoA, puntoB, 3, cuadranteCandidato);
                    } else {
                        qn.NE = adjRoot(qn.NE, puntoA, puntoB, 0, cuadranteCandidato);
                        qn.SE = adjRoot(qn.SE, puntoA, puntoB, 2, cuadranteCandidato);
                    }
                }
                case 2 -> {
                    if (cuadranteNodo == 0) {
                        qn.NW = adjRoot(qn.NW, puntoA, puntoB, 1, cuadranteCandidato);
                        qn.SW = adjRoot(qn.SW, puntoA, puntoB, 3, cuadranteCandidato);
                    } else {
                        qn.NE = adjRoot(qn.NE, puntoA, puntoB, 0, cuadranteCandidato);
                        qn.NW = adjRoot(qn.NW, puntoA, puntoB, 1, cuadranteCandidato);
                    }
                }
                case 3 -> {
                    if (cuadranteNodo == 1) {
                        qn.NE = adjRoot(qn.NE, puntoA, puntoB, 0, cuadranteCandidato);
                        qn.SE = adjRoot(qn.SE, puntoA, puntoB, 2, cuadranteCandidato);
                    } else {
                        qn.NE = adjRoot(qn.NE, puntoA, puntoB, 0, cuadranteCandidato);
                        qn.NW = adjRoot(qn.NW, puntoA, puntoB, 1, cuadranteCandidato);
                    }
                }
            }
            return qn;
        } else {
            InsertarPuntosRoot(qn, puntoB);

            return null;
        }
    }

    public QuadNode eliminarRecursivo(Point2d punto, QuadNode qn) {
        if (qn == null) return null;
        if (punto.x() == qn.point.x() && punto.y() == qn.point.y()) {
            if (qn.NE == null && qn.NW == null && qn.SE == null && qn.SW == null) {
                return null;
            }

            deleteNodoInterno(qn);
            return qn;
        }

        boolean enNorte = punto.y() >= qn.point.y();
        boolean enEste  = punto.x() >= qn.point.x();

        if (enNorte && enEste) {
            qn.NE = eliminarRecursivo(punto, qn.NE);
        } else if (enNorte && !enEste) {
            qn.NW = eliminarRecursivo(punto, qn.NW);
        } else if (!enNorte && enEste) {
            qn.SE = eliminarRecursivo(punto, qn.SE);
        } else {
            qn.SW = eliminarRecursivo(punto, qn.SW);
        }
        return qn;
    }

    public int opositeCuadrante(int cuadrante){
        return switch (cuadrante) {
            case 0 -> 3;
            case 1 -> 2;
            case 2 -> 1;
            case 3 -> 0;
            default -> -1;
        };
    }

    public void newRoot(QuadNode nodoActual, Point2d puntoA, Point2d puntoB, int cuadrante){
        if (nodoActual == null) return;
        llamadaAdjRoot(nodoActual, puntoA, puntoB, opositeCuadrante(cuadrante));

        switch (cuadrante) {
            case 0 -> newRoot(nodoActual.NE, puntoA, puntoB, 0);
            case 1 -> newRoot(nodoActual.NW, puntoA, puntoB, 1);
            case 2 -> newRoot(nodoActual.SE, puntoA, puntoB, 2);
            case 3 -> newRoot(nodoActual.SW, puntoA, puntoB, 3);
        }
    }

    private void llamadaAdjRoot(QuadNode nodo, Point2d puntoA, Point2d puntoB, int cuadrante) {
        if (nodo == null) return;
        switch (cuadrante){
            case 0, 3 -> {
                nodo.NW = adjRoot(nodo.NW, puntoA, puntoB, 1, cuadrante);
                nodo.SE = adjRoot(nodo.SE, puntoA, puntoB, 2, cuadrante);
            }
            case 1, 2 -> {
                nodo.NE = adjRoot(nodo.NE, puntoA, puntoB, 0, cuadrante);
                nodo.SW = adjRoot(nodo.SW, puntoA, puntoB, 3, cuadrante);
            }
        }
    }

    public void deleteNodoInterno(QuadNode nodoEliminar){

        // [0] -> NE, [1] -> NW, [2] -> SE, [3] -> SW
        Object[] candidato = encontrarCandidato(nodoEliminar); // Object (Node, Cuadrante)
        QuadNode replacement = (QuadNode) candidato[0];
        int cuadranteRe = (int) candidato[1];

        Point2d puntoA = nodoEliminar.point;
        Point2d puntoB = replacement.point;

        // Cambiamos el root original por el replacement, eliminando el nodo B
        switch (cuadranteRe) {
            case 0 -> nodoEliminar.NE = eliminarRecursivo(puntoB, nodoEliminar.NE);
            case 1 -> nodoEliminar.NW = eliminarRecursivo(puntoB, nodoEliminar.NW);
            case 2 -> nodoEliminar.SE = eliminarRecursivo(puntoB, nodoEliminar.SE);
            case 3 -> nodoEliminar.SW = eliminarRecursivo(puntoB, nodoEliminar.SW);
        }
        nodoEliminar.point = new Point2d(puntoB.x(), puntoB.y());


        llamadaAdjRoot(nodoEliminar, puntoA, puntoB, cuadranteRe);

        // Hecho el adjRoot, solo falta el NewRoot.
        switch (cuadranteRe) {
            case 0 -> newRoot(nodoEliminar.NE, puntoA, puntoB, 0);
            case 1 -> newRoot(nodoEliminar.NW, puntoA, puntoB, 1);
            case 2 -> newRoot(nodoEliminar.SE, puntoA, puntoB, 2);
            case 3 -> newRoot(nodoEliminar.SW, puntoA, puntoB, 3);
        };
    }


    public void delete(Point2d punto){
        this.root = eliminarRecursivo(punto, root);
        // llamda al listener
        treeListener.recorrerArbol(root);
    }
}
