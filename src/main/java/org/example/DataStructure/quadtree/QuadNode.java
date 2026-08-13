package org.example.DataStructure.quadtree;

import org.example.DataStructure.utils.Point2d;

public class QuadNode {
    public Point2d point;
    public QuadNode NE;
    public QuadNode SE;
    public QuadNode NW;
    public QuadNode SW;

    QuadNode(Point2d point) {
        this.point = point;
        this.NE = null;
        this.SE = null;
        this.NW = null;
        this.SW = null;
    }
}
