package org.example.DataStructure;

import org.example.DataStructure.quadtree.QuadNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GraphStreamVisualizer implements TreeListener {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> resetTask;
    private Node lastSearchedNode = null;

    private final Graph graph;

    public GraphStreamVisualizer() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graph = new SingleGraph("Binary Tree Visualizer");

        String css =
                "graph { padding: 50px; fill-color: #f8f9fa; }" +
                        "node { size: 35px; fill-color: #2ecc71; text-color: white; text-size: 14px; text-alignment: center; text-offset: 0px, -2px;}" +
                        "edge { fill-color: #bdc3c7; size: 2px; arrow-size: 8px, 4px; }";

        this.graph.setAttribute("ui.stylesheet", css);
        this.graph.display(false); // Manual layout engine (sin física) :(
    }

    @Override
    public void onAdd(int valueAdded, Integer parent, double x, double y) {
        String id = String.valueOf(valueAdded);
        if (graph.getNode(id) == null) {

            Node nodeAdd = graph.addNode(id);

            nodeAdd.setAttribute("ui.label", id);
            nodeAdd.setAttribute("xy",x,y);

            if (parent != null) {
                String parentId = String.valueOf(parent);
                graph.addEdge(parentId + "->" + id, parentId, id, true);
            }
        }

    }

    @Override
    public void onRemove(int valueRemoved) {
        String id = String.valueOf(valueRemoved);
        if (graph.getNode(id) != null) {
            graph.removeNode(id);
        }
    }

    @Override
    public void onChange(int oldValue, int newValue) {
        String idOld = String.valueOf(oldValue);
        String idNew = String.valueOf(newValue);

        Node oldNode = graph.getNode(idOld);

        if (oldNode != null) {
            // Buscar el nodo viejo y elminarlo
            // guardar las corrdenadas
            // colocar el nodo nuevo en las mismas coordenadas
            Object[] xy = (Object[]) oldNode.getAttribute("xy");

            String idPadre =  oldNode.enteringEdges()
                    .map(e -> e.getSourceNode().getId())
                    .findFirst()
                    .orElse(null);
            String[] hijosIds = oldNode.leavingEdges()
                    .map(e -> e.getTargetNode().getId())
                    .toArray(String[]::new);

            graph.removeNode(idOld);

            Node newNode = graph.addNode(idNew);
            newNode.setAttribute("ui.label", idNew);
            if (xy != null) {
                newNode.setAttribute("xy", xy[0], xy[1]);
            }
            if (idPadre != null) {
                graph.addEdge(idPadre + "->" + idNew, idPadre, idNew, true);
            }

            for (String hijoId : hijosIds) {
                graph.addEdge(idNew + "->" + hijoId, idNew, hijoId, true);
            }
        }
    }

    @Override
    public void onsearch(int valueSearched, boolean found) {
        String idSearch = String.valueOf(valueSearched);
        if (found) {
            Node nodeSearch = graph.getNode(idSearch);
            if (nodeSearch != null) {

                // PASO A: ¿Hay una cuenta regresiva de color en marcha?
                if (resetTask != null && !resetTask.isDone()) {
                    // La destruimos para que NO apague el color antes de tiempo
                    resetTask.cancel(true);
                    if (lastSearchedNode != null){
                        lastSearchedNode.setAttribute("ui.style", "fill-color: #2ecc71; size: 35px;");
                    }
                }
                this.lastSearchedNode = nodeSearch;
                // PASO B: Aplicamos el color de resaltado visual
                nodeSearch.setAttribute("ui.style", "fill-color: #e74c3c; size: 45px;");
                // PASO C: Programamos el apagado dentro de 1500 milisegundos (1.5s)
                resetTask = scheduler.schedule(() -> {
                    // Esta lambda se ejecutará cuando el tiempo expire
                    nodeSearch.setAttribute("ui.style", "fill-color: #2ecc71; size: 35px;");
                    this.lastSearchedNode = null;
                }, 1500, TimeUnit.MILLISECONDS);
            }
        } else {
            // Manejo de parpadeo para cuando NO se encuentra el nodo
            System.out.println("NO se encontro");
            graph.setAttribute("ui.stylesheet", "graph {fill-color: red;}");
            scheduler.schedule(() -> graph.setAttribute("ui.stylesheet","graph {fill-color: #f8f9fa;}"), 1000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void recorrerArbol(QuadNode root) {
        // 1. Limpiamos completamente el lienzo
        graph.clear();

        // (Opcional) Re-aplicamos el estilo CSS global tras el clear
        graph.setAttribute("ui.stylesheet",
                "node { fill-color: #3498db; size: 25px; text-size: 14px; text-alignment: at-right; } " +
                        "edge { fill-color: #7f8c8d; text-size: 12px; }"
        );


        if (root != null) {
            dibujarNodoRecursivo(root);
        }
    }

    private void dibujarNodoRecursivo(QuadNode actual) {
        if (actual == null) return;

        // ID único basado en sus coordenadas X e Y
        String idPadre = actual.point.x() + "," + actual.point.y();

        // Crear el nodo en GraphStream
        Node gNode = graph.addNode(idPadre);
        gNode.setAttribute("ui.label", "(" + actual.point.x() + ", " + actual.point.y() + ")");

        // Asignamos las coordenadas reales en el plano 2D
        gNode.setAttribute("x", actual.point.x());
        gNode.setAttribute("y", actual.point.y());

        // Conectamos a sus 4 cuadrantes
        conectarYRecorrer(actual.NE, "NE", idPadre);
        conectarYRecorrer(actual.NW, "NW", idPadre);
        conectarYRecorrer(actual.SE, "SE", idPadre);
        conectarYRecorrer(actual.SW, "SW", idPadre);
    }

    private void conectarYRecorrer(QuadNode hijo, String cuadrante, String idPadre) {
        if (hijo == null) return;

        // Dibujamos primero al hijo
        dibujarNodoRecursivo(hijo);

        String idHijo = hijo.point.x() + "," + hijo.point.y();
        String idArista = idPadre + "->" + idHijo;

        // Creamos la arista dirigida etiquetada con el cuadrante
        Edge arista = graph.addEdge(idArista, idPadre, idHijo, true);
        arista.setAttribute("ui.label", cuadrante);
    }

}
