package edu.stanford.ee368.flowchargenerator.imageproc;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.ee368.flowchargenerator.Edge;
import edu.stanford.ee368.flowchargenerator.FlowchartShape;
import edu.stanford.ee368.flowchargenerator.Graph;
import edu.stanford.ee368.flowchargenerator.Point;
import edu.stanford.ee368.flowchargenerator.ProgressBar;
import edu.stanford.ee368.flowchargenerator.Rectangle;
import edu.stanford.ee368.flowchargenerator.Rhombus;

public class Helper {
	
	public static List<Edge> BuildArrows(int r, int[][] center, int[][] tails) {
		
		//Edge edges[];
		//edges = new Edge[r];
		List<Edge> edges;
		edges = new ArrayList<>();
		
		for(int i=0; i<r; ++i) {
			//new Edge(new Point(400, 200), new Point(500, 200))
			Edge e = new Edge(new Point(tails[i][0], tails[i][1]), new Point(center[i][0], center[i][1]));
			edges.add(e);
		}
		return edges;
	}
	
	public static List<FlowchartShape> BuildRectangles(int r, int[][] anchors, int[][] center, int[][][] table) {
		
		//FlowchartShape rect[];
		//rect = new FlowchartShape[r];
		List<FlowchartShape> rect;
		rect = new ArrayList<>();
		
		int width;
		int height;
		int anchor1;
		int anchor2;
		int anchor3;
		int anchor4;
		
		//anchors1 upper right
		//anchors2 bottom left
		//anchors3 upper left
		//anchors4 bottom right
		
		for(int i=0; i<r; ++i) {
					
			anchor1 = anchors[i][0];
			anchor2 = anchors[i][1];
			anchor3 = anchors[i][2];
			anchor4 = anchors[i][3];
			
			width = (int) (Math.pow(table[i][0][anchor1] - table[i][0][anchor3],2) + Math.pow(table[i][1][anchor1] - table[i][1][anchor3],2));
			width = (int) Math.sqrt(width);
			
			height = (int) (Math.pow(table[i][0][anchor2] - table[i][0][anchor3],2) + Math.pow(table[i][1][anchor2] - table[i][1][anchor3],2));
			height = (int) Math.sqrt(height);
					
			FlowchartShape shape = new Rectangle(new Point(center[i][0], center[i][1]), width, height);
			rect.add(shape);
		}
		
		return rect;
		
	}

	public static int[][] Anchor(int r, int height, int width, int[][][] table, int[] len) {

		int anchors[][];
		anchors = new int[r][4];

		int anchor1 = 0;
		int anchor2 = 0;
		int anchor3 = 0;
		int anchor4 = 0;

		for(int i=0; i<r; ++i) {

			int maxx = 0;
			int maxy = 0;
			int minx = height+1;
			int miny = width+1;
			int minxy = height + width;
			int maxxy = 0;
			int compx, compy;

			// Upper left
			for(int j=0; j<len[i] ; ++j)
			{
				compx = table[i][0][j];
				compy = table[i][1][j];

				if(compx + compy <= minxy) {
					minxy = compx + compy;
					anchor3 = j;
				}
			}

			for(int j=0; j<len[i] ; ++j)
			{
				compy = table[i][1][j];
				compx = table[i][0][j];

				if(compy+compx >= maxxy) {
					maxxy = compx + compy;
					anchor4 = j;
				}
			}

			// Upper right
			int y_ref = table[i][1][anchor3];
			for(int j=0; j<len[i] ; ++j)
			{
				compx = table[i][0][j];
				compy = table[i][1][j];

				if(compx > maxx && compy == y_ref ) {
					maxx = compx;
					anchor1 = j;
				}
			}

			y_ref = table[i][1][anchor4];
			for(int j=0; j<len[i] ; ++j)
			{
				compy = table[i][1][j];

				if(compy > maxy && compy == y_ref) {
					maxy = compy;
					anchor2 = j;
				}
			}

			//anchor3 = 1;

			System.out.println("Anchor1 " + table[i][0][anchor1] + " " + table[i][1][anchor1]);
			System.out.println("Anchor2 " + table[i][0][anchor2] + " " + table[i][1][anchor2]);
			System.out.println("Anchor3 " + table[i][0][anchor3] + " " + table[i][1][anchor3]);
			System.out.println("Anchor4 " + table[i][0][anchor4] + " " + table[i][1][anchor4]);

			anchors[i][0] = anchor1;
			anchors[i][1] = anchor2;
			anchors[i][2] = anchor3;
			anchors[i][3] = anchor4;
		}

		return anchors;

	}

	public static int[][] findCenter(int[][][] table, int r, int[] len) {
		
		int center[][];
		center = new int[r][2];
		for(int i=0; i<r; ++i) {
			int xc = 0;
			int yc = 0;
			for(int j=0; j<len[i] ; ++j)
			{
				xc = xc + table[i][0][j];
				yc = yc + table[i][1][j];
			}
			xc = xc / len[i];
			yc = yc / len[i];
			center[i][0] = xc;
			center[i][1] = yc;
			
			System.out.println("X Center " + center[i][0]);
			System.out.println("Y Center " + center[i][1]);
		}
		
		return center;
	}


	public static int[][][] BuildTable(int r, int[] labelTest, int mm, int width, int height) {

		int idx;
		int current[];
		current = new int[r];

		int table[][][];
		table = new int[r][2][mm];

		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				idx = labelTest[y*width+x]-1;
				//System.out.print(idx);
				if(idx==-1)
					continue;
				//System.out.println(current[idx]);
				//System.out.println(idx);
				table[idx][0][current[idx]] = x;
				table[idx][1][current[idx]] = y;
				//System.out.println(idx);
				//System.out.println(table[idx][1][current[idx]]);
				current[idx] = current[idx]+1;

			}
			//System.out.println();
		}

		return table;
	}



	public static List<FlowchartShape> BuildRectangles(int r, int[][] anchors, int[][] center) {

		List<FlowchartShape> rect;
		rect = new ArrayList<>();

		int width;
		int height;

		for(int i=0; i<r; ++i) {

			width = anchors[i][0];
			height = anchors[i][1];

			FlowchartShape shape = new Rectangle(new Point(center[i][0], center[i][1]), width, height);
			rect.add(shape);
		}
		return rect;
	}

	public static List<FlowchartShape> BuildDiamonds(int r, int[][] anchors, int[][] center) {

		List<FlowchartShape> rect;
		rect = new ArrayList<>();

		int width;
		int height;

		for(int i=0; i<r; ++i) {

			width = anchors[i][0];
			height = anchors[i][1];

			FlowchartShape shape = new Rhombus(new Point(center[i][0], center[i][1]), width, height);
			rect.add(shape);
		}
		return rect;
	}

	public static Graph getGraph(Mat img, ProgressBar progressBar) {

	    setProgressBarRation(progressBar, 0);

		int width = img.width();
		int height = img.height();


		Mat[] pre = new Mat[2];
		setProgressBarRation(progressBar, 0.2);
		pre = PrePro.prepro(img);

		setProgressBarRation(progressBar, 0.5);

		// TEST OUTPUT FROM PREPROCESS
		Mat matRectangle = pre[0];
		Mat matDiamond = pre[1];
		Mat matArrow = pre[2];

		System.out.println("Row " + matRectangle.rows());
		System.out.println("Col " + matRectangle.cols());

		System.out.println("Height " + height);
		System.out.println("Width " + width);

		int row = matRectangle.rows();
		int col = matRectangle.cols();

		int regions_rect, regions_diamond, regions_arrow;

		Mat labels_rect = new Mat();
		Mat labels_diamond = new Mat();
		Mat labels_arrow = new Mat();

		Mat stats_rect = new Mat();
		Mat stats_diamond = new Mat();
		Mat stats_arrow = new Mat();

		Mat centroids_rect = new Mat();
		Mat centroids_diamond = new Mat();
		Mat centroids_arrow = new Mat();

		regions_rect = Imgproc.connectedComponentsWithStats(matRectangle, labels_rect, stats_rect, centroids_rect);
		setProgressBarRation(progressBar, 0.6);

		regions_diamond = Imgproc.connectedComponentsWithStats(matDiamond, labels_diamond, stats_diamond, centroids_diamond);
		setProgressBarRation(progressBar, 0.6);

		regions_arrow = Imgproc.connectedComponentsWithStats(matArrow, labels_arrow, stats_arrow, centroids_arrow);
		setProgressBarRation(progressBar, 0.7);

		System.out.println("Regions: " + regions_rect);
		System.out.println("Regions: " + regions_diamond);
		System.out.println("Regions: " + regions_arrow);


		labels_rect.convertTo(labels_rect, CvType.CV_32SC1);
		int[] label_rect = new int[row * col];
		labels_rect.get(0, 0, label_rect);

		labels_diamond.convertTo(labels_diamond, CvType.CV_32SC1);
		int[] label_diamond = new int[row * col];
		labels_diamond.get(0, 0, label_diamond);

		labels_arrow.convertTo(labels_arrow, CvType.CV_32SC1);
		int[] label_arrow = new int[row * col];
		labels_arrow.get(0, 0, label_arrow);

		centroids_rect.convertTo(centroids_rect, CvType.CV_32SC1);
		int[] centroid_rect = new int[row * col];
		centroids_rect.get(0, 0, centroid_rect);

		centroids_diamond.convertTo(centroids_diamond, CvType.CV_32SC1);
		int[] centroid_diamond = new int[row * col];
		centroids_diamond.get(0, 0, centroid_diamond);

		centroids_arrow.convertTo(centroids_arrow, CvType.CV_32SC1);
		int[] centroid_arrow = new int[row * col];
		centroids_arrow.get(0, 0, centroid_arrow);

		stats_rect.convertTo(stats_rect, CvType.CV_32SC1);
		int[] stat_rect = new int[row * col];
		stats_rect.get(0, 0, stat_rect);

		stats_diamond.convertTo(stats_diamond, CvType.CV_32SC1);
		int[] stat_diamond = new int[row * col];
		stats_diamond.get(0, 0, stat_diamond);

		stats_arrow.convertTo(stats_arrow, CvType.CV_32SC1);
		int[] stat_arrow = new int[row * col];
		stats_arrow.get(0, 0, stat_arrow);


		int r_rect= regions_rect - 1;
		int r_diamond = regions_diamond - 1;
		int r_arrow= regions_arrow - 1;

		// find center
		int[][] center_rect = new int[r_rect][2];
		int[][] center_diamond = new int[r_diamond][2];
		int[][] center_arrow = new int[r_arrow][2];
		int index;
		for(int i=1; i<=r_rect; ++i) {
			index = i*2;
			center_rect[i-1][0] = centroid_rect[index];
			center_rect[i-1][1] = centroid_rect[index+1];
		}
		setProgressBarRation(progressBar, 0.7);
		for(int i=1; i<=r_diamond; ++i) {
			index = i*2;
			center_diamond[i-1][0] = centroid_diamond[index];
			center_diamond[i-1][1] = centroid_diamond[index+1];
		}
		setProgressBarRation(progressBar, 0.7);
		for(int i=1; i<=r_arrow; ++i) {
			index = i*2;
			center_arrow[i-1][0] = centroid_arrow[index];
			center_arrow[i-1][1] = centroid_arrow[index+1];
		}
		setProgressBarRation(progressBar, 0.8);

		// ONLY PUT RECTANGLES HERE
		int anchors_rect[][];
		anchors_rect = new int[r_rect][2]; // height, width
		for(int i=0; i<r_rect; ++i) {
			index = 5*(i+1);
			anchors_rect[i][0] = stat_rect[index+2];
			anchors_rect[i][1] = stat_rect[index+3];
		}
		setProgressBarRation(progressBar,0.9);

		int anchors_diamond[][];
		anchors_diamond = new int[r_diamond][2]; // height, width
		for(int i=0; i<r_diamond; ++i) {
			index = 5*(i+1);
			anchors_diamond[i][0] = stat_diamond[index+2];
			anchors_diamond[i][1] = stat_diamond[index+3];
		}
		setProgressBarRation(progressBar,0.9);

		// ONLY PUT ARROWS HERE
		int tails[][];
		tails = new int[r_arrow][2];
		int heads[][];
		heads = new int[r_arrow][2];

		int h,w;
		int box_X, box_Y;

		for(int i=0; i<r_arrow; ++i) {
			index = 5*(i+1);
			w = stat_arrow[index + 2];
			h = stat_arrow[index + 3];

			box_X = (int) (stat_arrow[index] + 0.5 *w);
			box_Y = (int) (stat_arrow[index+1] + 0.5 *h);

			if(center_arrow[i][0] < box_X)
				if(center_arrow[i][1] < box_Y) {
					heads[i][0] = stat_arrow[index];
					heads[i][1] = stat_arrow[index+1];
					tails[i][0] = stat_arrow[index] + w;
					tails[i][1] = stat_arrow[index+1] + h;
				}
				else {
					heads[i][0] = stat_arrow[index];
					heads[i][1] = stat_arrow[index+1]+h;
					tails[i][0] = stat_arrow[index] + w;
					tails[i][1] = stat_arrow[index+1];
				}
			else {
				if(center_arrow[i][1] < box_Y) {
					heads[i][0] = stat_arrow[index]+w;
					heads[i][1] = stat_arrow[index+1];
					tails[i][0] = stat_arrow[index];
					tails[i][1] = stat_arrow[index+1]+h;
				}
				else {
					heads[i][0] = stat_arrow[index]+w;
					heads[i][1] = stat_arrow[index+1]+h;
					tails[i][0] = stat_arrow[index];
					tails[i][1] = stat_arrow[index+1];
				}

			}
		}

		setProgressBarRation(progressBar, 0.9);

		// ---BUILD RECTANGLES---
		// Compute halfwidth and halfheight to build the Flowchartshape Rectangles
		// They need Centroid, halfwidth and halfheight
		List<FlowchartShape> rect = Helper.BuildRectangles(r_rect, anchors_rect, center_rect);
		List<FlowchartShape> diamond = Helper.BuildDiamonds(r_diamond, anchors_diamond, center_diamond);
		List<FlowchartShape> shapes = new ArrayList<>();
		shapes.addAll(rect);
		shapes.addAll(diamond);

		// ---BUILD EDGES---
		// Using tail and Centroid as end point
		List<Edge> edges = Helper.BuildArrows(r_arrow, heads, tails);

		// ---BUILD GRAPH---
		Graph graph = new Graph(shapes, edges);
		setProgressBarRation(progressBar, 1.0);
		return graph;

	}

	private static void setProgressBarRation(ProgressBar progressBar, double ratio) {
		if (progressBar == null) return;
		progressBar.setRatio(ratio);
	}

}
