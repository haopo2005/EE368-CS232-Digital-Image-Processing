package edu.stanford.ee368.flowchargenerator;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianyu on 2018/3/1.
 */

public abstract class FlowchartShape {
    public List<FlowchartShape> neighbors;
    public List<Edge> edges;
    public List<Point> anchors;

    public FlowchartShape() {
        neighbors = new ArrayList<>();
        edges = new ArrayList<>();
        anchors = new ArrayList<>();
    }

    public abstract void draw(Canvas canvas, Paint paint);
    public abstract void draw(Mat mat, Scalar scalar, int thickness);

    public abstract Point getCenter();

    public Point getClosestAnchor(Point point) {
        double minDist = Double.MAX_VALUE;
        Point res = anchors.get(0);
        for (Point anchor : anchors) {
            double dist = getDistance(point, anchor);
            if (dist < minDist) {
                minDist = dist;
                res = anchor;

            }
        }
        return res;
    }

    public double getClosestAnchorDist(Point point) {
        double minDist = Double.MAX_VALUE;
        for (Point anchor : anchors) {
            double dist = getDistance(point, anchor);
            if (dist < minDist) {
                minDist = dist;
            }
        }
        return minDist;
    }

    private static double getDistance(Point from, Point to) {
        return Math.sqrt(Math.pow(from.y-to.y,2)+Math.pow(from.x-to.x,2));
    }

    public static org.opencv.core.Point pointTranslator(Point point) {
        return new org.opencv.core.Point(point.x, point.y);
    }

}
