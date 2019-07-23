package edu.stanford.ee368.flowchargenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import org.opencv.core.Mat;

/**
 * Created by qianyu on 2018/3/3.
 */

public class FlowchartDrawView extends View {

    Graph graph;

    public FlowchartDrawView(Context context, Graph graph) {
        super(context);
        this.graph = graph;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        graph.setCanvas(canvas);
        graph.draw();
    }

}
