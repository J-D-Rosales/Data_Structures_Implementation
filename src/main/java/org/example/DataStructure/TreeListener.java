package org.example.DataStructure;


import org.example.DataStructure.quadtree.QuadNode;

import javax.swing.tree.TreeNode;

public interface TreeListener {

    void onAdd(int valueAdded, Integer parent, double x, double y);

    void onRemove(int valueRemoved);
    void onChange(int oldValue, int newValue);
    void onsearch(int valueSearched, boolean found);
    void recorrerArbol(QuadNode node);

}
