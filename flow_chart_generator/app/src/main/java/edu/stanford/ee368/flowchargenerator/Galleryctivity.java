package edu.stanford.ee368.flowchargenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

import edu.stanford.ee368.flowchargenerator.imageproc.Helper;

public class Galleryctivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap grayBitmap, imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleryctivity);
        imageView = (ImageView) findViewById(R.id.imageView);
        OpenCVLoader.initDebug();
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(imageBitmap);
        }
    }

    public void generateFlowchart(View view) {
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Mat rgba = new Mat();
        double size = 0.2;
        int newWidth = (int) (imageBitmap.getWidth() * size);
        int newHeight = (int) (imageBitmap.getHeight() * size);
        Bitmap smaller = Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, true);

//        Mat gray = new Mat();
//        Utils.bitmapToMat(imageBitmap, rgba);
        Utils.bitmapToMat(smaller, rgba);
//        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGB2GRAY);
//        Utils.matToBitmap(gray, grayBitmap);
        Graph graph = Helper.getGraph(rgba, null);
        View flowchartDrawView = new FlowchartDrawView(getApplicationContext(), graph);
        Canvas canvas = new Canvas(smaller);
        flowchartDrawView.draw(canvas);
        imageView.setImageBitmap(smaller);
    }
}
