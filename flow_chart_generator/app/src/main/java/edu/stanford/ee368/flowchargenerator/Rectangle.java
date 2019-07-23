package edu.stanford.ee368.flowchargenerator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by qianyu on 2018/3/1.
 */

public class Rectangle extends FlowchartShape {

    public Point center;
    public int width;
    public int height;

    private int halfWidth;
    private int halfHeight;


    public Rectangle(Point center, int width, int height) {
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
        canvas.drawRect(center.x - halfWidth, center.y - halfHeight, center.x + halfWidth, center.y + halfHeight, paint);
    }

    @Override
    public void draw(Mat mat, Scalar scalar, int thickness) {
        Imgproc.rectangle(mat, new org.opencv.core.Point(center.x-halfWidth, center.y-halfHeight), new org.opencv.core.Point(center.x+halfWidth, center.y+halfHeight), scalar, thickness);
    }

    @Override
    public Point getCenter() {
        return center;
    }
}
