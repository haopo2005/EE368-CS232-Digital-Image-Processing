package edu.stanford.ee368.flowchargenerator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by qianyu on 2018/3/1.
 */

public class Graph {
    List<FlowchartShape> flowchartShapes;
    Canvas canvas;

    public Graph() {
        flowchartShapes = new ArrayList<>();
        FlowchartShape shape1 = new Rectangle(new Point(300, 200), 200, 100);
        FlowchartShape shape2 = new Rectangle(new Point(600, 200), 200, 100);
        FlowchartShape shape3 = new Rectangle(new Point(900, 200), 200, 100);
        FlowchartShape shape4 = new Rhombus(new Point(600, 350), 200, 100);
        FlowchartShape shape5 = new Rectangle(new Point(300, 500), 200, 100);
        FlowchartShape shape6 = new Rectangle(new Point(900, 500), 200, 100);
        flowchartShapes.add(shape1);
        flowchartShapes.add(shape2);
        flowchartShapes.add(shape3);
        flowchartShapes.add(shape4);
        flowchartShapes.add(shape5);
        flowchartShapes.add(shape6);
        shape1.neighbors.add(shape2);
        shape3.neighbors.add(shape2);
        shape2.neighbors.add(shape4);
        shape4.neighbors.add(shape5);
        shape4.neighbors.add(shape6);
        shape1.edges.add(new Edge(new Point(400, 200), new Point(500, 200)));
        shape3.edges.add(new Edge(new Point(800, 200), new Point(700, 200)));
        shape2.edges.add(new Edge(new Point(600, 250), new Point(600, 300)));
        shape4.edges.add(new Edge(new Point(500, 350), new Point(300, 450)));
        shape4.edges.add(new Edge(new Point(700, 350), new Point(900, 450)));
    }

    public Graph(List<FlowchartShape> shapes, List<Edge> edges) {
        flowchartShapes = new ArrayList<>();
        if (shapes.isEmpty()) {
            return;
        }
        for (Edge edge : edges) {
            Point from = edge.from;
            Point to = edge.to;
            FlowchartShape closestToFrom = getClosestFlowchartShape(shapes, from);
            FlowchartShape closestToTo = getClosestFlowchartShape(shapes, to);
            Point anchorFrom = closestToFrom.getClosestAnchor(from);
            Point anchorTo = closestToTo.getClosestAnchor(to);
            closestToFrom.neighbors.add(closestToTo);
            closestToFrom.edges.add(new Edge(anchorFrom, anchorTo));
        }
        flowchartShapes.addAll(shapes);
    }

    private FlowchartShape getClosestFlowchartShape(List<FlowchartShape> shapes, Point point) {
        double minDist = Double.MAX_VALUE;
        FlowchartShape closestShape = shapes.get(0);
        for (FlowchartShape shape : shapes) {
            double dist = shape.getClosestAnchorDist(point);
            if (dist < minDist) {
                minDist = dist;
                closestShape = shape;
            }
        }
        return closestShape;
    }

    public void draw() {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        drawShapes(paint);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        drawArrows(paint);
    }

    public void draw(Mat mat) {
        Scalar scalar = new Scalar(0,255,0);
        int thickthess = 2;
        double tipLength = 0.3;
        drawShapes(mat, scalar, thickthess);
        drawArrows(mat, scalar, thickthess, tipLength);
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    private void drawShapes(Paint paint) {
        for (FlowchartShape shape : flowchartShapes) {
            shape.draw(canvas, paint);
        }
    }

    private void drawShapes(Mat mat, Scalar scalar, int thickness) {
        for (FlowchartShape shape : flowchartShapes) {
            shape.draw(mat, scalar, thickness);
        }
    }

    private void drawArrows(Paint paint) {
        Map<FlowchartShape, Integer> indegree = new HashMap<>();
        for (FlowchartShape shape : flowchartShapes) {
            indegree.put(shape, 0);
        }
        for (FlowchartShape from : flowchartShapes) {
            for (FlowchartShape to : from.neighbors) {
                indegree.put(to, indegree.get(to) + 1);
            }
        }
        Queue<FlowchartShape> queue = new LinkedList<>();
        for (FlowchartShape shape : indegree.keySet()) {
            int indeg = indegree.get(shape);
            if (indeg == 0) {
                queue.add(shape);
            }
        }
        while (!queue.isEmpty()) {
            FlowchartShape from = queue.poll();
            for (int i = 0; i < from.neighbors.size(); i++) {
                FlowchartShape to = from.neighbors.get(i);
                Edge edge = from.edges.get(i);
                drawArrow(edge, paint);
                int indeg = indegree.get(to) - 1;
                indegree.put(to, indeg);
                if (indeg == 0) {
                    queue.add(to);
                }
            }
        }
    }

    private void drawArrows(Mat mat, Scalar scalar, int thickness, double tipLength) {
        Map<FlowchartShape, Integer> indegree = new HashMap<>();
        for (FlowchartShape shape : flowchartShapes) {
            indegree.put(shape, 0);
        }
        for (FlowchartShape from : flowchartShapes) {
            for (FlowchartShape to : from.neighbors) {
                indegree.put(to, indegree.get(to) + 1);
            }
        }
        Queue<FlowchartShape> queue = new LinkedList<>();
        for (FlowchartShape shape : indegree.keySet()) {
            int indeg = indegree.get(shape);
            if (indeg == 0) {
                queue.add(shape);
            }
        }
        while (!queue.isEmpty()) {
            FlowchartShape from = queue.poll();
            for (int i = 0; i < from.neighbors.size(); i++) {
                FlowchartShape to = from.neighbors.get(i);
                Edge edge = from.edges.get(i);
                drawArrow(edge, mat, scalar, thickness, tipLength);
                int indeg = indegree.get(to) - 1;
                indegree.put(to, indeg);
                if (indeg == 0) {
                    queue.add(to);
                }
            }
        }
    }

    private void drawArrow(Edge edge, Paint paint) {
        Point from = edge.from;
        Point to = edge.to;
        drawArrowLine(from, to, paint);
//        if (from.x == to.x || from.y == to.y) {
//            drawArrowLine(from, to, paint);
//            drawArrowHead(from, to, paint);
//        } else {
//            drawArrowLine(from, new Point(to.x, from.y), paint);
//            drawArrowLine(new Point(to.x, from.y), to, paint);
//            drawArrowHead(new Point(to.x, from.y), to, paint);
//        }
    }

    private void drawArrow(Edge edge, Mat mat, Scalar scalar, int thickness, double tipLength) {
        Point from = edge.from;
        Point to = edge.to;
        drawArrowLine(from, to, mat, scalar, thickness, tipLength);
//        if (from.x == to.x || from.y == to.y) {
//            drawArrowLine(from, to, mat, scalar, thickness, tipLength);
//        } else {
//            drawLine(from, new Point(to.x, from.y), mat, scalar, thickness);
//            drawArrowLine(new Point(to.x, from.y), to, mat, scalar, thickness, tipLength);
//        }
    }

    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    private void drawArrowHead(Point from, Point to, Paint paint) {
        double H = 30;
        double L = 20;
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H);
        double arraow_len = Math.sqrt(L * L + H * H);
        double[] arrXY_1 = rotateVec(to.x - from.x, to.y - from.y, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(to.x - from.x, to.y - from.y, -awrad, true, arraow_len);
        double x_3 = to.x - arrXY_1[0];
        double y_3 = to.y - arrXY_1[1];
        double x_4 = to.x - arrXY_2[0];
        double y_4 = to.y - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        Path triangle = new Path();
        triangle.moveTo(to.x, to.y);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();

        canvas.drawPath(triangle,paint);
    }

    private void drawArrowLine(Point from, Point to, Paint paint) {
        canvas.drawLine(from.x, from.y, to.x, to.y, paint);
    }

    private void drawArrowLine(Point from, Point to, Mat mat, Scalar scalar, int thickness, double tipLength) {
        Imgproc.arrowedLine(mat, FlowchartShape.pointTranslator(from), FlowchartShape.pointTranslator(to), scalar, thickness, 8, 0, tipLength);
    }

    private void drawLine(Point from, Point to, Mat mat, Scalar scalar, int thickness) {
        Imgproc.line(mat, FlowchartShape.pointTranslator(from), FlowchartShape.pointTranslator(to), scalar, thickness);
    }

}
