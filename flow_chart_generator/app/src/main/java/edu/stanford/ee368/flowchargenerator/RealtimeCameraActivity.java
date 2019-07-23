package edu.stanford.ee368.flowchargenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class RealtimeCameraActivity extends Activity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback, Camera.PreviewCallback{

    public static final String FILE_PATH = "filePath";
    private Camera mCamera;
    private SurfaceView mPreview;
    private Button ibSnapPhoto, ibCancelPhoto;
    private String pictureFilePath;
    private RealtimeDrawFlowchartView realtimeDrawFlowchartView;

    /**
     * 外部调用接口
     * @param context
     * @param filePath 拍摄照片的存储地址
     */
    public static void callMe(Context context, String filePath){
        Intent intent = new Intent(context, RealtimeCameraActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_camera);

//mPrevie为摄像机预览图层
        mPreview = (SurfaceView) findViewById(R.id.surface_view);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        ibSnapPhoto = (Button) findViewById(R.id.snap_photo);
        ibCancelPhoto = (Button) findViewById(R.id.cancel_photo);
        ibSnapPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSnapPhoto();
            }
        });
        ibCancelPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelPhoto();
            }
        });

        pictureFilePath = getIntent().getStringExtra(FILE_PATH);
        realtimeDrawFlowchartView = (RealtimeDrawFlowchartView) findViewById(R.id.realtime_draw_flowchart_view);

//开启Camera
        mCamera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    /**
     * 点击按钮拍照
     */
    private void onSnapPhoto(){
//拍照
        mCamera.takePicture(this, null, null, this);
    }

    /**
     * 点击按钮取消
     */
    private void onCancelPhoto(){
        mCamera.stopPreview();
        finish();
    }

    @Override
    public void onShutter() {
        Toast.makeText(RealtimeCameraActivity.this, "click!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            savePictureToFile(data, pictureFilePath);
        } catch (IOException e) {
            Log.d("wcj", "originalImage保存失败！");
            e.printStackTrace();
        }

//重新启动预览
        mCamera.startPreview();
    }

    //以下为Surface三种回调方法
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//当surfaceView建立时，在上面绑定预览显示界面
        try{
            mCamera.setPreviewDisplay(mPreview.getHolder());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selectedSize  =getBestSupportPreviewSize(sizes, getScreenSize());

//设定摄像机预览界面尺寸
        params.setPreviewSize(selectedSize.width, selectedSize.height);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    /**
     * 保存照片到存储器
     *
     * @param data
     * @param _file
     * @throws IOException
     */
    public static void savePictureToFile(byte[] data, String _file) throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
// String _filePath_file.replace(File.separatorChar +
            // file.getName(), "");
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(data);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 寻找最大的预览图片尺寸(与屏幕分辨率适配）
     *
     * @param previewSizes 所有支持的预览图片大小
     * @return
     */
    public static Camera.Size getBestSupportPreviewSize(List<Camera.Size> previewSizes, Camera.Size screenSize) {
        double screenRatio = screenSize.width * 1.0 / screenSize.height;
        Camera.Size maxSize = previewSizes.get(0);
        for (Camera.Size size : previewSizes) {
            double sizeRatio = size.width * 1.0 / size.height;
            if (size.width < 2000 && sizeRatio > screenRatio - 0.1 && sizeRatio < screenRatio + 0.1)
                maxSize = (size.width > maxSize.width) ? size : maxSize;
        }
        return maxSize;
    }

    private Camera.Size getScreenSize() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 宽度（PX）
        int height = metric.heightPixels;  // 高度（PX）

        return mCamera.new Size(height, width);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//把data转换为bitmap
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width,
                    size.height, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height),
                        100, stream);
                Bitmap inputImage = rotateBitmap(BitmapFactory.decodeByteArray(
                        stream.toByteArray(), 0, stream.size()), 90);
                stream.close();

//=======================
                //inputBitmap为获取到的Bitmap，这里对其进行后续处理
                //=======================
                realtimeDrawFlowchartView.drawLine(new int[]{100,100});

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param bmp    要旋转的图片
     * @param degree 图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
     * @return 旋转好的图片
     */
    public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
//此处bitmap默认为RGBA_8888
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }
}
