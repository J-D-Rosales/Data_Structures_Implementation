package org.example.DataStructure;

// this hsould use patterns like interface or strategy. but well

public class BinaryTree {

    private BinaryNode root;
    private TreeListener treeListener;

    public BinaryTree() {
        this.root = null;
    }

    public void setTreeListener(TreeListener treeListener) {
        this.treeListener = treeListener;
    }

    public static class BinaryNode {
        private int value;
        private BinaryNode left;
        private BinaryNode right;
        public BinaryNode(int value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return "Nodo: " + value;
        }
    }

    public void insertar(int value)
    {
        this.root = insertarRecursivo(this.root, value,null,0.0,10.0,4.0);
    }

    private BinaryNode insertarRecursivo(BinaryNode actual, int value, BinaryNode parent,  double x, double y, double hSpace) {
        if (actual == null) {
            // NOTIFICAR AL LISTENER
            BinaryNode newNode = new BinaryNode(value);
            if (treeListener != null) {
                Integer parentVal = (parent != null) ? parent.value : null;
                treeListener.onAdd(value, parentVal, x, y);
            }
            return newNode;
        }
        if (value < actual.value) {
            actual.left = insertarRecursivo(actual.left, value,actual,x - hSpace,y-2, hSpace/2);
        } else if (value > actual.value) {
            actual.right = insertarRecursivo(actual.right, value,actual,x + hSpace,y-2, hSpace/2);
        }
        return actual;
    }

    public void search(int value){
        BinaryNode encontrado = searchRecursivo(this.root, value);
        if (treeListener != null) {
            // No encontrado
            treeListener.onsearch(value, encontrado != null);  // Encontrado
        }
    }

    private BinaryNode searchRecursivo(BinaryNode actual, int value) {
        if (actual == null || actual.value == value) {
            return actual;
        }
        if (value < actual.value) {
            return searchRecursivo(actual.left, value);
        }
        return searchRecursivo(actual.right, value);
    }

    public void delete(int value) {
        this.root = deleteRecursivo(this.root, value);
        if (treeListener != null) {
            treeListener.onRemove(value);
        }
    }


    private BinaryNode deleteRecursivo(BinaryNode actual, int value) {
        if (actual == null) {
            return null;
        }

        if (value < actual.value) {
            actual.left = deleteRecursivo(actual.left, value);
        } else if (value > actual.value) {
            actual.right = deleteRecursivo(actual.right, value);
        } else {
            // Nodo encontrado
            if (actual.left == null && actual.right == null) {
                return null;
            }
            if (actual.left == null) {
                return actual.right;
            }
            if (actual.right == null) {
                return actual.left;
            }

            int minVal = encontrarMinValue(actual.right);
            actual.value = minVal;
            actual.right = deleteRecursivo(actual.right, minVal);
        }
        return actual;
    }

    private int encontrarMinValue(BinaryNode actual) {
        if (actual.left == null) {
            return actual.value;
        }
        return encontrarMinValue(actual.left);
    }

}
