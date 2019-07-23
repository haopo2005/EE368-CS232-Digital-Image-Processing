package edu.stanford.ee368.flowchargenerator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianyu on 2018/3/1.
 */

public class Rhombus extends FlowchartShape {

    public Point center;
    public int width;
    public int height;

    private int halfWidth;
    private int halfHeight;

    public Rhombus(Point center, int width, int height) {
        super();
        this.center = center;
        this.width = width;
        this.height = height;
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;
        anchors.add(new Point(center.x, center.y-halfHeight));
        anchors.add(new Point(center.x, center.y+halfHeight));
        anchors.add(new Point(center.x-halfWidth, center.y));
        anchors.add(new Point(center.x+halfWidth, center.y));
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Path path = new Path();
        path.moveTo(center.x, center.y + halfHeight); // Top
        path.lineTo(center.x - halfWidth, center.y); // Left
        path.lineTo(center.x, center.y - halfHeight); // Bottom
        path.lineTo(center.x + halfWidth, center.y); // Right
        path.lineTo(center.x, center.y + halfHeight); // Back to Top
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    public void draw(Mat mat, Scalar scalar, int thickness) {
        Imgproc.line(mat, new org.opencv.core.Point(center.x,center.y-halfHeight), new org.opencv.core.Point(center.x-halfWidth,center.y), scalar, thickness);
        Imgproc.line(mat, new org.opencv.core.Point(center.x-halfWidth,center.y), new org.opencv.core.Point(center.x,center.y+halfHeight), scalar, thickness);
        Imgproc.line(mat, new org.opencv.core.Point(center.x,center.y+halfHeight), new org.opencv.core.Point(center.x+halfWidth,center.y), scalar, thickness);
        Imgproc.line(mat, new org.opencv.core.Point(center.x+halfWidth,center.y), new org.opencv.core.Point(center.x,center.y-halfHeight), scalar, thickness);
    }

    @Override
    public Point getCenter() {
        return center;
    }
}
