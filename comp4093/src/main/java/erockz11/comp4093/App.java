package erockz11.comp4093;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class App {

	private static final ToIplImage converter = new ToIplImage();

	static int xCoord = 100;
	static int yCoord = 100;

    public static void main(String[] args) throws IOException {

    	setup();

    	// Load image
    	BufferedImage src = ImageIO.read(new File("resources/images/img14.jpg"));

    	// Convert BufferedImage to Mat
    	Mat srcMat = Java2DFrameUtils.toMat(src);
    	int height = srcMat.rows();
    	int width = srcMat.cols();

    	// Source frame
    	display(srcMat, "source", 0, 0, 0.17);

    	// Convert to HSV
    	Mat hsvMat = new Mat(height, width);
    	cvtColor(srcMat, hsvMat, COLOR_RGB2HSV);

    	// HSV frame
    	display(hsvMat, "hsv", 640, 0, 0.17);

    	// Result frame
    	CanvasFrame resultFrame = new CanvasFrame("result");
    	resultFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	resultFrame.setCanvasScale(0.17);
    	resultFrame.setLocation(1280, 0);

    	// Apply thresholding
    	Mat hsvThresholdMat = new Mat();
    	inRange(hsvMat, new Mat(1, 1, CV_32SC4, new Scalar(20, 25, 100, 0)), new Mat(1, 1, CV_32SC4, new Scalar(86, 255, 255, 0)), hsvThresholdMat);
    	display(hsvThresholdMat, "hsv threshold", 0, 500, 0.17);

    	// Apply smoothing
    	GaussianBlur(hsvThresholdMat, hsvThresholdMat, new Size(5, 5), 0);

    	// Canny edge detection
    	Mat canny = new Mat();
    	Canny(hsvThresholdMat, canny, 30, 90);
    	display(canny, "canny edge detection", 640, 500, 0.17);

    	// Detect objects
    	detectContours(canny, srcMat);

    	// Buttons and labels
    	setupSpinner(width, height);

    	// Main loop
    	while (true) {

    		Mat resultImage = new Mat();
    		srcMat.copyTo(resultImage);
    		drawSourceCircle(resultImage, xCoord, yCoord);
    		resultFrame.showImage(converter.convert(resultImage));

    		// Deallocate memory to prevent memory leaks
    		resultImage.release();
    	}
    }


    private static void setup() {

    	System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");

    }

    private static void display(Mat img, String title, int x, int y, double scale) {

    	CanvasFrame canvas = new CanvasFrame(title);
    	canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	canvas.setCanvasScale(scale);
    	canvas.setLocation(x, y);
    	canvas.showImage(converter.convert(img));

    }

    private static Mat detectContours(Mat src, Mat dest) {

    	MatVector contours = new MatVector();
        findContours(src, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

        long n = contours.size();

        Mat[] contoursPoly = new Mat[(int) n];
//        Rect[] boundRect = new Rect[(int) n];
        Point2f[] centres = new Point2f[(int) n];
        float[][] radius = new float[(int) n][1];

        // Populate arrays
        for (int i = 0; i < n; i++) {
//        	Mat contour = contours.get(i);
//        	Mat points = new Mat();
//        	approxPolyDP(contour, points, arcLength(contour, true) * 0.02, true);
//        	drawContours(dest, new MatVector(points), -1, Scalar.BLUE);

        	contoursPoly[i] = contours.get(i);
        	approxPolyDP(contoursPoly[i], new Mat(), arcLength(contoursPoly[i], true) * 0.02, true);
//        	boundRect[i] = boundingRect(contoursPoly[i]);
        	centres[i] = new Point2f();
        	minEnclosingCircle(contoursPoly[i], centres[i], radius[i]);
        }

        // Draw
        MatVector contoursPolyList = new MatVector();
        for (Mat poly : contoursPoly) {
        	contoursPolyList.put(poly);
        }
        for (int i = 0; i < n; i++) {
        	Scalar colour = new Scalar(0, 0, 255, 0);
        	drawContours(dest, contoursPolyList, -1, colour);
//        	rectangle(dest, boundRect[i], colour);
        	circle(dest, new Point((int) centres[i].x(), (int) centres[i].y()), (int) radius[i][0], colour);
        }

    	return dest;
    }

    private static void setupSpinner(int width, int height) {
    	JFrame buttonFrame = new JFrame("buttons");
    	buttonFrame.setSize(300, 300);
    	buttonFrame.setLocation(1920, 150);

    	final JLabel xLabel = new JLabel();
    	xLabel.setHorizontalAlignment(JLabel.CENTER);
    	xLabel.setBounds(0, 0, 250, 100);

    	final JLabel yLabel = new JLabel();
    	yLabel.setHorizontalAlignment(JLabel.CENTER);
    	yLabel.setBounds(0, 20, 250, 100);

    	SpinnerModel xVal = new SpinnerNumberModel(xCoord, 0, width, 10);
    	final JSpinner xSpin = new JSpinner(xVal);
    	xLabel.setText("x : " + xSpin.getValue());
    	xSpin.setBounds(100, 100, 50, 30);
    	buttonFrame.add(xSpin);
    	buttonFrame.add(xLabel);
    	xSpin.addChangeListener(new ChangeListener() {
    		public void stateChanged(ChangeEvent e) {
    			xLabel.setText("x : " + ((JSpinner)e.getSource()).getValue());
    			xCoord = (Integer) xSpin.getValue();
    			System.out.println("x = " + xCoord);
    		}
    	});

    	SpinnerModel yVal = new SpinnerNumberModel(yCoord, 0, height, 10);
    	final JSpinner ySpin = new JSpinner(yVal);
    	yLabel.setText("y : " + xSpin.getValue());
    	ySpin.setBounds(100, 150, 50, 30);
    	buttonFrame.add(ySpin);
    	buttonFrame.add(yLabel);
    	ySpin.addChangeListener(new ChangeListener() {
    		public void stateChanged(ChangeEvent e) {
    			yLabel.setText("y : " + ((JSpinner)e.getSource()).getValue());
    			yCoord = (Integer) ySpin.getValue();
    			System.out.println("y = " + yCoord);
    		}
    	});

    	buttonFrame.setLayout(null);
    	buttonFrame.setVisible(true);
    }

    private static void drawSourceCircle(Mat img, int x, int y) {

    	Point point = new Point(x, y);
    	circle(img, point, 100, new Scalar(0, 0, 255, 255), 25, 8, 0);

    }
}