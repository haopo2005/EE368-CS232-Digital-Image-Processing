package edu.stanford.ee368.flowchargenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by qianyu on 2018/3/10.
 */

public class RealtimeDrawFlowchartView extends SurfaceView implements SurfaceHolder.Callback {


    protected SurfaceHolder sh;
    private int mWidth;
    private int mHeight;
    private Bitmap drawBitmap;
    private Canvas mCanvas;
    private int[] cornerPoints;

    public RealtimeDrawFlowchartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sh = getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
        mWidth = w;
        mHeight = h;

        drawBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ALPHA_8);
        mCanvas = new Canvas(drawBitmap);
    }

    public void surfaceCreated(SurfaceHolder arg0) {

    }

    public void surfaceDestroyed(SurfaceHolder arg0) {

    }


    /**
     * 在预览摄像头上划线
     *
     * @param cornerPoints 得到的4个角点
     */
    public void drawLine(int[] cornerPoints) {

//绘画
        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT);

        Paint paint = new Paint();

//清屏
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        if (cornerPoints == null) {
            Log.d("wcj", "返回值为空");
            sh.unlockCanvasAndPost(canvas);
            return;
        }

        if (cornerPoints.length == 0) {
            Log.d("wcj", "返回值为空");
            sh.unlockCanvasAndPost(canvas);
            return;
        }

        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(56, 210, 212));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7);

        //==============
        //绘图操作
        //===============
        canvas.drawLine(100,100,200,200,paint);

        sh.unlockCanvasAndPost(canvas);
    }

}
