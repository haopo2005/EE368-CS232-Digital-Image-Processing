//package edu.stanford.ee368.flowchargenerator.imageproc;
package edu.stanford.ee368.flowchargenerator.imageproc;

import android.graphics.Bitmap;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PrePro {
	/**
	 * 
	 * @param mat: input image
	 * @return Mat[]:
	 * Mat[0] = rectangles
	 * Mat[1] = diamonds
	 * Mat[2] = arrows
	 */

	static int SMALL_REGION_REMOVAL_THRESHOLD = 100;
	static int OPEN_SMALL_REGION_REMOVAL = 50;
	static int ARROW_OPEN_RADIUS = 10;
	static int HOUGH_THRESHOLD = 50;
	static int HOUGH_MIN_LINE_LENGTH = 30;
	static int HOUGH_MAX_LINE_GAP = 5;
	static int CANNY_THRESHOLD_1 = 100;
	static int CANNY_THRESHOLD_2 = 100;
	static int CANNY_APETURE_SIZE = 3;

	public static String getParamNameByIndex(int index){
		String ParamName = "";
		switch(index)
		{
			case 0: ParamName = "SMALL_REGION_REMOVAL_THRESHOLD"; break;
			case 1: ParamName = "OPEN_SMALL_REGION_REMOVAL"; break;
			case 2: ParamName = "ARROW_OPEN_RADIUS"; break;
			case 3: ParamName = "HOUGH_THRESHOLD"; break;
			case 4: ParamName = "HOUGH_MIN_LINE_LENGTH"; break;
			case 5: ParamName = "HOUGH_MAX_LINE_GAP"; break;
			case 6: ParamName = "CANNY_THRESHOLD_1"; break;
			case 7: ParamName = "CANNY_THRESHOLD_2"; break;
			case 8: ParamName = "CANNY_APETURE_SIZE"; break;
			default: ParamName = "Not Found"; break;
		}
		return ParamName;
	}

	public static int getParamsSize(){
		return 8;
	}

	public static int getParam(int index){
		int threshold = 0;
		switch(index)
		{
			case 0: threshold = getSmallRegionRemovalThreshold(); break;
			case 1: threshold = getOpenSmallRegionRemoval(); break;
			case 2: threshold = getArrowOpenRadius(); break;
			case 3: threshold = getHoughThreshold(); break;
			case 4: threshold = getHoughMinLineLength(); break;
			case 5: threshold = getHoughMaxLineGap(); break;
			case 6: threshold = getCannyThreshold1(); break;
			case 7: threshold = getCannyThreshold2(); break;
			case 8: threshold = getCannyApetureSize(); break;
			default: threshold = 0; break;
		}
		return threshold;
	}

	public static void addParam(int index){
		int threshold = getParam(index);
		switch(index)
		{
			case 0: setSmallRegionRemovalThreshold(threshold+1); break;
			case 1: setOpenSmallRegionRemoval(threshold+1); break;
			case 2: setArrowOpenRadius(threshold+1); break;
			case 3: setHoughThreshold(threshold+1); break;
			case 4: setHoughMinLineLength(threshold+1); break;
			case 5: setHoughMaxLineGap(threshold+1); break;
			case 6: setCannyThreshold1(threshold+1); break;
			case 7: setCannyThreshold2(threshold+1); break;
			case 8: setCannyApetureSize(threshold+1); break;
		}
	}

	public static void minusParam(int index){
		int threshold = getParam(index);
		switch(index)
		{
			case 0: setSmallRegionRemovalThreshold(threshold-1); break;
			case 1: setOpenSmallRegionRemoval(threshold-1); break;
			case 2: setArrowOpenRadius(threshold-1); break;
			case 3: setHoughThreshold(threshold-1); break;
			case 4: setHoughMinLineLength(threshold-1); break;
			case 5: setHoughMaxLineGap(threshold-1); break;
			case 6: setCannyThreshold1(threshold-1); break;
			case 7: setCannyThreshold2(threshold-1); break;
			case 8: setCannyApetureSize(threshold-1); break;
		}
	}

	public static Mat[] prepro(Mat mat){
		try {
			System.out.println("Hello, World!");

			// get mat size
			int rows = mat.rows();
			int cols = mat.cols();

			// rgb2gray, generate gray image
			Mat gray = new Mat(rows,cols,CvType.CV_8UC1);
			Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
			System.out.println("Grayscale Done!");
			saveImage(gray, "gray");

			// binarize, generate new mat
			Mat bina = new Mat(rows, cols, CvType.CV_8UC1);
			Imgproc.adaptiveThreshold(gray, bina, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 65, 40);
//			Imgproc.adaptiveThreshold(gray, bina, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 51, 40);

			System.out.println("Binarization Done!");
			saveImage(bina, "bina");

			// bit inverted
			Core.bitwise_not(bina, bina); 
			System.out.println("BitWiseNot Done!");
			saveImage(bina, "invt");

			// denoise and fill
			Mat denoise = denoiseAndFill(bina, SMALL_REGION_REMOVAL_THRESHOLD);
			saveImage(denoise, "denoise");

			// get edges
			Mat edges = new Mat(rows, cols, CvType.CV_8UC1);
			Imgproc.Canny(denoise, edges, CANNY_THRESHOLD_1, CANNY_THRESHOLD_2, CANNY_APETURE_SIZE, false);
			System.out.println("GetEdge Done!");
			saveImage(edges, "edges");

			// hough transform
			Mat substitute = new Mat();
			Mat lines = new Mat(rows, cols, CvType.CV_8UC1);
			Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180.0, HOUGH_THRESHOLD, HOUGH_MIN_LINE_LENGTH, HOUGH_MAX_LINE_GAP);

			// rotate
			if(lines.rows() > 0) {
				Mat rotated = rotate(denoise, lines);
				// parameter update
				cols = rotated.cols();
				rows = rotated.rows();
				substitute = rotated.clone();
			} else {
				substitute = denoise.clone();
			}
			saveImage(substitute, "subs");

			// find contours
			int smallRegion = 100;
			Mat fill = denoiseAndFill(substitute, smallRegion);
			saveImage(fill, "fill");

			// open to eliminate arrows
			Mat seOpen = seGen(ARROW_OPEN_RADIUS);
			Mat opened = new Mat(rows, cols, CvType.CV_8UC1);
			Imgproc.morphologyEx(fill, opened, Imgproc.MORPH_OPEN, seOpen);
			System.out.println("Open Done!");
			saveImage(opened, "open");
			
			// find contours
			//opened = denoiseAndFill(opened, smallRegion);
			//saveImage(opened, "opened");
			// compute difference to get arrows
			Mat diff = new Mat(rows, cols, CvType.CV_8UC1);
			Core.absdiff(fill, opened, diff);
			System.out.println("Diff Done!");

			// remove small area generated by open
			Mat remv = denoiseAndFill(diff, OPEN_SMALL_REGION_REMOVAL);
			saveImage(remv, "remv");

			// dialate
			Mat arro = dilate(remv, 10);
			saveImage(arro, "dilated");

			// get rectangles and diamonds
			Mat blob = new Mat(rows, cols, CvType.CV_8UC1);
			Core.absdiff(fill, remv, blob);
			System.out.println("Blob Done!");
			saveImage(fill, "fill");

			// find circles
			Mat eroded_blob = erode(blob, 10);
			Mat diff_blob = new Mat(rows, cols, CvType.CV_8UC1); // all blobs
			Core.absdiff(blob, eroded_blob, diff_blob);
			Mat copy_blob = new Mat(rows, cols, CvType.CV_8UC1); // without circles
			diff_blob.copyTo(copy_blob);; 
			saveImage(diff_blob, "diff_blob");

			List<MatOfPoint> blobContours = new ArrayList<>();
			Scalar white = new Scalar(255);
			Scalar black = new Scalar(0);
			Imgproc.findContours(diff_blob, blobContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			for (MatOfPoint contour: blobContours) {
				Mat temp = Mat.zeros(rows, cols, CvType.CV_8UC1);
				ArrayList<MatOfPoint> circContour = new ArrayList<>();
				circContour.add(contour);
				Imgproc.drawContours(temp, circContour, 0, white, 10);
				Mat circles = new Mat();
				Imgproc.HoughCircles(temp, circles, Imgproc.CV_HOUGH_GRADIENT, 2, substitute.rows()/4, 200, 100, 0, 0 );
				if(circles.cols() > 0) {
					System.out.println("aha");
					Imgproc.fillPoly(copy_blob, Arrays.asList(contour), black);
				}
			}
			Mat circ = new Mat(rows, cols, CvType.CV_8UC1);
			Core.absdiff(diff_blob, copy_blob, circ);
			saveImage(copy_blob, "copy_blob");

			// distinguish rectangles and diamonds
			Mat[] rectAndDiam = genRectAndDiam(copy_blob);
			Mat rectangle = rectAndDiam[0];
			Mat diamond = rectAndDiam[1];

			// erode
			Mat eroded_rect = erode(rectangle, 10);
			Mat eroded_diam = erode(diamond, 10);
			saveImage(eroded_rect, "eroded_rect");
			saveImage(eroded_diam, "eroded_diam");

			// get boxes
			Mat rect = new Mat(rows, cols, CvType.CV_8UC1);
			Core.absdiff(rectangle, eroded_rect, rect);
			Mat diam = new Mat(rows, cols, CvType.CV_8UC1);
			Core.absdiff(diamond, eroded_diam, diam);
			System.out.println("Box Done!");

			// type conversion
			Mat[] result = new Mat[4];
			result[0] = rect;
			result[1] = diam;
			result[2] = arro;
			result[3] = circ;
			System.out.println("Conversion Done!");
			return result;


		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		return null;
	}	

	// find majority
	public static double[] findMajority(double[] input, int splits) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		HashMap<Integer, ArrayList<Double>> values = new HashMap<Integer, ArrayList<Double>>();
		double pi = Math.PI;

		// histogram
		int maxCnt = 0;
		int maxPos = 0;
		for(int i = 0; i < input.length; i++) {
			for(int j = 0; j < splits; j++) {
				if(-pi/2 + pi/splits * j <= input[i] && input[i] < -pi/2 + pi/splits * (j + 1)) {
					if(map.containsKey(j)) {
						int temp = map.get(j);
						int now = temp + 1;
						map.put(j, now);
						if(now > maxCnt) {
							maxCnt = now;
							maxPos = j;
						}
						values.get(j).add(input[i]);
					} else {
						map.put(j, 1);
						values.put(j, new ArrayList<Double>());
						values.get(j).add(input[i]);
					}
				}
			}
		}

		// array
		ArrayList<Double> arr = values.get(maxPos);
		double[] ret = new double[arr.size()];
		for(int i = 0; i < arr.size(); i++) {
			ret[i] = arr.get(i);
		} 

		// return sorted array
		Arrays.sort(ret);
		return ret;
	}

	// dilate
	public static Mat dilate(Mat m, int r) {
		Mat seErode = seGen(r);
		Mat dilated = new Mat(m.rows(), m.cols(), CvType.CV_8UC1);
		Imgproc.dilate(m, dilated, seErode);
		System.out.println("Dilate Done!");
		return dilated;
	}

	// rotate
	public static Mat rotate(Mat m, Mat houghLines) {
		int[] nums = new int[houghLines.rows() * 4];
		double[] angles = new double[houghLines.rows()];
		houghLines.get(0, 0, nums);
		for(int i = 0; i < houghLines.rows() ; i++) {
			double x1 = nums[4 * i];
			double y1 = nums[4 * i + 1];
			double x2 = nums[4 * i + 2];
			double y2 = nums[4 * i + 3];
			angles[i] = Math.atan((y2 - y1) / (x2 - x1));
		}
		Arrays.sort(angles);

		double[] firstHist = findMajority(angles, 10);
		double[] secondHist = findMajority(firstHist, 6);

		double angle = secondHist[secondHist.length / 2];
		double degree = Math.toDegrees(angle);

		// image rotate
		int x = m.cols();
		int y = m.rows();
		// 4 points, (0, 0) (x, 0) (0, y) (x, y) to rotate transform (clockwise)
		// (0, 0), (x*cosA, -x*sinA) (y*sinA, y*cosA) (x*cosA+y*sinA, -x*sinA+y*cosA)
		int newWidth = 0;
		int newHeight = 0;
		int xshift = 0;
		int yshift = 0;
		if(angle >= 0) {
			newWidth = (int) Math.round(x * Math.cos(angle) + y * Math.sin(angle));
			newHeight = (int) Math.round(y * Math.cos(angle) + x * Math.sin(angle));
			yshift = (int) Math.round(x * Math.sin(angle));
		} else { // angle < 0
			newWidth = (int) Math.round(x * Math.cos(angle) - y * Math.sin(angle));
			newHeight = (int) Math.round(-x * Math.sin(angle) + y * Math.cos(angle));
			xshift = (int) Math.round(-y * Math.sin(angle));
		}

		// shift image
		Mat shifted = Mat.zeros(y + yshift, x + xshift, CvType.CV_8UC1);
		Mat shiftedSub = shifted.submat(yshift, y + yshift, xshift, x + xshift);
		m.copyTo(shiftedSub);
		System.out.println("Shift Done!");

		// rotate image
		Mat rotMat = Imgproc.getRotationMatrix2D(new org.opencv.core.Point(xshift, yshift), degree, 1.0);
		Mat rotated = new Mat();
		Imgproc.warpAffine(shifted, rotated, rotMat, new Size(newWidth, newHeight), Imgproc.INTER_LINEAR);			
		System.out.println("Rotation Done!");

		return rotated;
	}

	// distinguish rectangle and diamond
	public static Mat[] genRectAndDiam(Mat m) {
		List<MatOfPoint> blobContours = new ArrayList<>();
		Mat rectangle = Mat.zeros(m.size(), CvType.CV_8UC1);
		Mat diamond = Mat.zeros(m.size(),  CvType.CV_8UC1);
		Scalar white = new Scalar(255);
		Imgproc.findContours(m, blobContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		for (MatOfPoint contour: blobContours) {
			double actualArea = Imgproc.contourArea(contour);
			Rect bounding = Imgproc.boundingRect(contour);
			double boundingArea = bounding.width * bounding.height;
			if(actualArea / boundingArea > 0.75) {	// rectangular
				Imgproc.fillPoly(rectangle, Arrays.asList(contour), white);
			} else {	// diamond
				Imgproc.fillPoly(diamond, Arrays.asList(contour), white);
			}
		}
		System.out.println("Rectangle Diamond Done!");
		return new Mat[]{rectangle, diamond};
	}

	// remove small area
	public static Mat denoiseAndFill(Mat m, int thres) {
		List<MatOfPoint> noiseContours = new ArrayList<>();
		Mat denoise = Mat.zeros(m.rows(), m.cols(), CvType.CV_8UC1);
		Scalar white = new Scalar(255);
		Imgproc.findContours(m, noiseContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		for (MatOfPoint contour: noiseContours) {
			if(contour.rows() > thres) {
				Imgproc.fillPoly(denoise, Arrays.asList(contour), white);
			}
		}
		System.out.println("Denoise Done!");
		return denoise;
	}

	// erode
	public static Mat erode(Mat m, int r) {
		Mat seErode = seGen(r);
		Mat eroded = new Mat(m.rows(), m.cols(), CvType.CV_8UC1);
		Imgproc.erode(m, eroded, seErode);
		System.out.println("Erode Done!");
		return eroded;
	}

	// 2D square to 1D array
	public static int[] TwoDim2OneDim(int[][] input, int size) {
		int[] output = new int[size * size];
		for(int r = 0; r < size; r++) {
			for(int c = 0; c < size; c++) {
				output[c + r * size] = input[r][c];
			}
		}
		return output;
	}

	// structuring element generation
	public static Mat seGen(int radius) {
		int diameter = 2 * radius + 1;
		int[][] disk = new int[diameter][diameter];
		for(int x = 0; x < diameter; x++) {
			for(int y = 0; y < diameter; y++) {
				int xpos = x - 10;
				int ypos = y - 10;
				if(xpos*xpos + ypos*ypos <= radius*radius) {
					disk[x][y] = 1;
				} else {
					disk[x][y] = 0;
				}
			}
		}
		int[] diskFlat = TwoDim2OneDim(disk, diameter);
		Mat seOpen = new Mat(diameter, diameter, CvType.CV_32SC1);
		seOpen.put(0, 0, diskFlat);
		seOpen.convertTo(seOpen, CvType.CV_8UC1);
		return seOpen;
	}

	public static int getSmallRegionRemovalThreshold() {
		return SMALL_REGION_REMOVAL_THRESHOLD;
	}

	public static void setSmallRegionRemovalThreshold(int smallRegionRemovalThreshold) {
		SMALL_REGION_REMOVAL_THRESHOLD = smallRegionRemovalThreshold;
	}

	public static int getOpenSmallRegionRemoval() {
		return OPEN_SMALL_REGION_REMOVAL;
	}

	public static void setOpenSmallRegionRemoval(int openSmallRegionRemoval) {
		OPEN_SMALL_REGION_REMOVAL = openSmallRegionRemoval;
	}

	public static int getArrowOpenRadius() {
		return ARROW_OPEN_RADIUS;
	}

	public static void setArrowOpenRadius(int arrowOpenRadius) {
		ARROW_OPEN_RADIUS = arrowOpenRadius;
	}

	public static int getHoughThreshold() {
		return HOUGH_THRESHOLD;
	}

	public static void setHoughThreshold(int houghThreshold) {
		HOUGH_THRESHOLD = houghThreshold;
	}

	public static int getHoughMinLineLength() {
		return HOUGH_MIN_LINE_LENGTH;
	}

	public static void setHoughMinLineLength(int houghMinLineLength) {
		HOUGH_MIN_LINE_LENGTH = houghMinLineLength;
	}

	public static int getHoughMaxLineGap() {
		return HOUGH_MAX_LINE_GAP;
	}

	public static void setHoughMaxLineGap(int houghMaxLineGap) {
		HOUGH_MAX_LINE_GAP = houghMaxLineGap;
	}

	public static int getCannyThreshold1() {
		return CANNY_THRESHOLD_1;
	}

	public static void setCannyThreshold1(int cannyThreshold1) {
		CANNY_THRESHOLD_1 = cannyThreshold1;
	}

	public static int getCannyThreshold2() {
		return CANNY_THRESHOLD_2;
	}

	public static void setCannyThreshold2(int cannyThreshold2) {
		CANNY_THRESHOLD_2 = cannyThreshold2;
	}

	public static int getCannyApetureSize() {
		return CANNY_APETURE_SIZE;
	}

	public static void setCannyApetureSize(int cannyApetureSize) {
		CANNY_APETURE_SIZE = cannyApetureSize;
	}

	public static void saveImage(Mat mat, String fileName) throws IOException {
		// convert to image and write to file
		//byte[] data = new byte[mat.rows() * mat.cols()];
		//mat.get(0, 0, data);
		//Mat image = new Mat(mat.cols(),mat.rows(), CvType.CV_8UC1);
		//image.put(mat.rows(), mat.cols(), data);

		// write image0 to file
		//Imgcodecs.imwrite("~/" + fileName + ".png", image);
		Bitmap resultBitmap = null;
		if (mat != null) {
			resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
			if (resultBitmap != null)
				Utils.matToBitmap(mat, resultBitmap);
		}

		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(), "tupian");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		System.out.print(appDir);
		String picName = fileName + ".jpg";
		File file = new File(appDir, picName);

		try {
			FileOutputStream fos = new FileOutputStream(file);
			resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
