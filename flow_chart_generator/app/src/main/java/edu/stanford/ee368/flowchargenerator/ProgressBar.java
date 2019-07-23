package edu.stanford.ee368.flowchargenerator;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * Created by qianyu on 2018/3/13.
 */

public class ProgressBar {

    double ratio;
    Scalar IN_PROGRESS_COLOR = new Scalar(0,0,255);
    Scalar OUT_PROGRESS_COLOR = new Scalar(255,0,0);

    double leftX, leftY, rightX, rightY;

    public ProgressBar(int leftX, int leftY, int rightX, int rightY) {
        ratio = 0;
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
    }

    public void draw(Mat mat) {
        int THICK = 5;
        drawLeft(mat, THICK);
        drawRight(mat, THICK);
    }

    private void drawLeft(Mat mat, int thick) {
        int rightTemp = (int) (leftX+(rightX-leftX)*ratio);
        Imgproc.rectangle(mat, new Point(leftX, leftY), new Point(rightTemp, rightY), IN_PROGRESS_COLOR, thick);
    }

    private void drawRight(Mat mat, int thick) {
        int leftTemp = (int) (leftX+(rightX-leftX)*ratio);
        Imgproc.rectangle(mat, new Point(leftTemp, leftY), new Point(rightX, rightY), OUT_PROGRESS_COLOR, thick);
    }

    public void reset() {
        ratio = 0;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

}
