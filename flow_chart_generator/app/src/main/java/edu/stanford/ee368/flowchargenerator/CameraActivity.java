package edu.stanford.ee368.flowchargenerator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.stanford.ee368.flowchargenerator.imageproc.Helper;
import edu.stanford.ee368.flowchargenerator.imageproc.PrePro;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;

    Mat mat1;
    BaseLoaderCallback baseLoaderCallback;
    Graph mGraph;

    boolean isProcessing;

    ProgressBar progressBar;
    int CURRENT_EDIT_PARAM_INDEX;
    TextView paramIndex;
    TextView paramValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        isProcessing = false;
        progressBar = new ProgressBar(100,10,900,11);
        CURRENT_EDIT_PARAM_INDEX = 0;
        paramIndex = findViewById(R.id.param_index);
        paramValue = findViewById(R.id.param_value);
        String param_name = PrePro.getParamNameByIndex(CURRENT_EDIT_PARAM_INDEX);
        String para_value = ""+PrePro.getParam(CURRENT_EDIT_PARAM_INDEX);
        String str="<font color='#FF0000'>"+param_name+": </font>";
        String str2="<font color='#FF0000'>"+para_value+"</font>";
        paramIndex.setText(Html.fromHtml(str));
        paramValue.setText(Html.fromHtml(str2));

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        mGraph = new Graph();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat1 = inputFrame.rgba();
        if (!isProcessing) {
            new DrawAsyncTask().execute(mat1);
        } else {
            mGraph.draw(mat1);
        }
        progressBar.draw(mat1);
        return mat1;
    }

    public void changeParam(View view) {
        CURRENT_EDIT_PARAM_INDEX++;
        if (CURRENT_EDIT_PARAM_INDEX == PrePro.getParamsSize()) {
            CURRENT_EDIT_PARAM_INDEX = 0;
        }
        //paramIndex.setText(PrePro.getParamNameByIndex(CURRENT_EDIT_PARAM_INDEX)+ "]: ");
        //paramValue.setText("" + PrePro.getParam(CURRENT_EDIT_PARAM_INDEX));
        String param_name = PrePro.getParamNameByIndex(CURRENT_EDIT_PARAM_INDEX);
        String para_value = ""+PrePro.getParam(CURRENT_EDIT_PARAM_INDEX);
        String str="<font color='#FF0000'>"+param_name+": </font>";
        String str2="<font color='#FF0000'>"+para_value+"</font>";
        paramIndex.setText(Html.fromHtml(str));
        paramValue.setText(Html.fromHtml(str2));
    }

    public void addParam(View view) {
        PrePro.addParam(CURRENT_EDIT_PARAM_INDEX);
        String para_value = ""+PrePro.getParam(CURRENT_EDIT_PARAM_INDEX);
        String str2="<font color='#FF0000'>"+para_value+"</font>";
        paramValue.setText(Html.fromHtml(str2));
    }

    public void minusParam(View view) {
        PrePro.minusParam(CURRENT_EDIT_PARAM_INDEX);
        String para_value = ""+PrePro.getParam(CURRENT_EDIT_PARAM_INDEX);
        String str2="<font color='#FF0000'>"+para_value+"</font>";
        paramValue.setText(Html.fromHtml(str2));
    }

    private class DrawAsyncTask extends AsyncTask<Mat, Void, Graph> {

        DrawAsyncTask() {
            isProcessing = true;
        }

        @Override
        protected Graph doInBackground(Mat... mats) {
            Mat mat = mats[0];
            return Helper.getGraph(mat, progressBar);
        }

        @Override
        protected void onPostExecute(Graph graph) {
            super.onPostExecute(graph);
            System.out.println("Executed");
            mGraph = graph;
            isProcessing = false;
            mGraph.draw(mat1);
            System.out.println("Finish drawing");
        }
    }

    private class DrawThread extends Thread {
        Mat mat;
        public DrawThread(Mat mat) {
            this.mat = mat;
        }

        @Override
        public void run() {
            super.run();
            Graph graph = Helper.getGraph(mat, progressBar);
            graph.draw(mat);
        }
    }

    @Override
    public void onCameraViewStopped() {
        mat1.release();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat1 = new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "Problem in OpenCV", Toast.LENGTH_SHORT).show();
        } else {
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }

        //获取相机拍摄读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //版本判断
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }
}
