package erockz11.comp4093;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class App {

	private static final ToIplImage converter = new ToIplImage();

	static int xCoord = 50;
	static int yCoord = 50;
	static float[][] circles;	// circles[i][0] = centre x value, circles[i][1] = centre y value, circles[i][2] = radius
	static int order[];
	static boolean showPath = false;

    public static void main(String[] args) throws IOException {

    	setup();

    	// Load image
    	BufferedImage src = ImageIO.read(new File("resources/images/cropped/img19.jpg"));

//    	// Performance Test
//    	Mat test = new Mat(1, 1, CV_8UC1);
//    	display(test, "test window", 2500, 1400, 1);

    	// Convert BufferedImage to Mat
    	Mat srcMat = Java2DFrameUtils.toMat(src);
    	int height = srcMat.rows();
    	int width = srcMat.cols();

    	// Source frame
//    	display(srcMat, "source", 0, 0, 0.25);

    	// Convert to HSV
    	Mat hsvMat = new Mat(height, width);
    	cvtColor(srcMat, hsvMat, COLOR_RGB2HSV);

    	// HSV frame
//    	display(hsvMat, "hsv", 853, 0, 0.25);

    	// Result frame
    	CanvasFrame resultFrame = new CanvasFrame("result");
    	resultFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	resultFrame.setCanvasScale(0.8);
    	resultFrame.setLocation(853, 1000);

    	// Split channels
    	MatVector hsvChannels = new MatVector();
    	split(hsvMat, hsvChannels);
//    	display(hsvChannels.get(0), "hue", 0, 300, 0.25);
//    	display(hsvChannels.get(1), "sat", 853, 300, 0.25);
//    	display(hsvChannels.get(2), "val", 1706, 300, 0.25);

    	// Apply thresholding
//    	Mat hsvThreshold = new Mat();			// Binarise HSV
//    	inRange(hsvMat, new Mat(1, 1, CV_32SC4, new Scalar(20, 25, 100, 0)), new Mat(1, 1, CV_32SC4, new Scalar(86, 255, 255, 0)), hsvThreshold);
//    	display(hsvThreshold, "hsv threshold", 853, 380, 0.3);

//    	Mat rgbThreshold = new Mat();			// Binarise RGB
//    	inRange(srcMat, new Mat(1, 1, CV_32SC4, new Scalar(85, 140, 10, 0)), new Mat(1, 1, CV_32SC4, new Scalar(190, 210, 220, 0)), rgbThreshold);
//    	display(rgbThreshold, "rgb threshold", 0, 380, 0.25);

    	Mat hueThreshold = new Mat();			// Binarise HSV channels
    	Mat satThreshold = new Mat();
    	Mat valThreshold = new Mat();
    	inRange(hsvChannels.get(0), new Mat(1, 1, CV_32SC4, new Scalar(40, 0, 0, 0)), new Mat(1, 1, CV_32SC4, new Scalar(60, 0, 0, 0)), hueThreshold);
    	inRange(hsvChannels.get(1), new Mat(1, 1, CV_32SC4, new Scalar(100, 0, 0, 0)), new Mat(1, 1, CV_32SC4, new Scalar(240, 0, 0, 0)), satThreshold);
    	inRange(hsvChannels.get(2), new Mat(1, 1, CV_32SC4, new Scalar(100, 0, 0, 0)), new Mat(1, 1, CV_32SC4, new Scalar(255, 0, 0, 0)), valThreshold);
//    	display(hueThreshold, "hue threshold", 0, 500, 0.25);
//    	display(satThreshold, "sat threshold", 853, 500, 0.25);
//    	display(valThreshold, "val threshold", 1706, 500, 0.25);

    	// Bitwise AND
    	Mat hsvL = new Mat();			// Combine HSV channel thresholds
    	Mat hsvR = new Mat();
    	Mat hsvAND = new Mat();
    	bitwise_and(hueThreshold, satThreshold, hsvL);
    	bitwise_and(satThreshold, valThreshold, hsvR);
    	bitwise_and(hsvL, hsvR, hsvAND);
//    	display(hsvAND, "combined hsv threshold", 0, 1000, 0.25);

//    	Mat combinedThreshold = new Mat();		// Combine RGB and HSV channel thresholds
//    	bitwise_and(rgbThreshold, hsvAND, combinedThreshold);
//    	display(combinedThreshold, "rgb AND hsv", 1706, 380, 0.25);

    	// Canny edge detection
//    	Mat canny = new Mat();
//    	Canny(combinedThreshold, canny, 30, 90);
//    	Canny(hsvThreshold, canny, 30, 90);
//    	Canny(hsvAND, canny, 30, 90);
//    	display(canny, "canny edge detection", 1920, 500, 0.4);

    	// Detect objects
//    	detectContours(canny, srcMat);						// Canny Edge Image
//    	detectContours(rgbThreshold, srcMat);				// RGB Image
//    	detectContours(hsvThreshold, srcMat);				// HSV Image
    	detectContours(hsvAND, srcMat);						// Combined HSV channels
//    	detectContours(combinedThreshold, srcMat);			// Combined RGB and HSV channels

    	// Buttons and labels
    	setupButtons(width, height);

    	// Main loop
    	while (true) {

    		Mat resultImage = new Mat();
    		srcMat.copyTo(resultImage);
    		drawSourceCircle(resultImage, xCoord, yCoord);
    		drawPath(resultImage, order, showPath);
    		resultFrame.showImage(converter.convert(resultImage));

    		// Deallocate memory to prevent memory leaks
    		resultImage.release();

    	}
    }


    private static void setup() {

    	// Remove limit on memory usage
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
        Point2f[] centres = new Point2f[(int) n];
        float[][] radius = new float[(int) n][1];

        // Get the centres and radii of minimum enclosing circles of contours in the image
        for (int i = 0; i < n; i++) {

        	contoursPoly[i] = contours.get(i);
        	approxPolyDP(contoursPoly[i], new Mat(), arcLength(contoursPoly[i], true) * 0.02, true);
        	centres[i] = new Point2f();
        	minEnclosingCircle(contoursPoly[i], centres[i], radius[i]);

        }

        // Map the centres and radii or minimum enclosing circles of contours in the image to a single 2D array
        float circles2D[][] = new float[(int) n][3];
        int count = 0;

        for (int i = 0; i < n; i++) {

        	// Cull circles based on radius and/or screen position
        	if (radius[i][0] > 8.0) {

        		circles2D[i][0] = centres[i].x();
        		circles2D[i][1] = centres[i].y();
        		circles2D[i][2] = radius[i][0];
        		count++;

        	}

        }

        // Remove empty elements
        circles = new float[count][3];
        int idx = 0;

        for (int i = 0; i < n; i++) {

        	if (circles2D[i][0] != 0.0 || circles2D[i][1] != 0.0 || circles2D[i][2] != 0.0) {

        		circles[idx][0] = circles2D[i][0];
        		circles[idx][1] = circles2D[i][1];
        		circles[idx][2] = circles2D[i][2];
        		idx++;

        	}

        }

        // Draw
        Scalar colour = new Scalar(0, 0, 255, 0);
        int circleCounter = 1;
        for (int i = 0; i < circles.length; i++) {
        	String s = String.valueOf(circleCounter);
        	circle(dest, new Point((int) circles[i][0], (int) circles[i][1]), (int) circles[i][2], colour, 2, 8, 0);
        	putText(dest, s, new Point((int) circles[i][0], (int) circles[i][1]), FONT_HERSHEY_PLAIN, 5.0, colour, 4, 8, false);
        	circleCounter++;
        }

        // Debug
//        System.out.println("'circles2D' array:");
//        for (int i = 0; i < n; i++) {
//        	System.out.println("x: " + circles2D[i][0] + "   \t" + "y: " + circles2D[i][1] + "\t" + "radius: " + circles2D[i][2]);
//        }
//        System.out.println("count: " + count);
//        System.out.println("'circles' array:");
//        for (int i = 0; i < idx; i++) {
//        	System.out.println("x: " + circles[i][0] + "   \t" + "y: " + circles[i][1] + "\t" + "radius: " + circles[i][2]);
//        }
//        System.out.println("index: " + idx);
//        System.out.println("circles size: " + circles.length);

        System.out.println("Number of circles: " + circles.length);

    	return dest;

    }

    private static void setupButtons(int width, int height) {

    	JFrame buttonFrame = new JFrame("User Input");
    	buttonFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	buttonFrame.setSize(300, 400);
    	buttonFrame.setLocation(2000, 0);

    	// Set up spinners
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

    	// Set up text box and button
    	final JLabel pathLabel = new JLabel();
    	pathLabel.setText("Laser Path: ");
    	pathLabel.setBounds(20,165,250,100);
    	buttonFrame.add(pathLabel);

    	final JTextField path = new JTextField("e.g. 1,2,3,4");
    	path.setBounds(100, 200, 100, 30);
    	buttonFrame.add(path);

    	JButton pathButton = new JButton("Show Path");
    	pathButton.setBounds(100,250,100,30);
    	buttonFrame.add(pathButton);
    	pathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setOrder(path.getText());
				showPath = !showPath;
				System.out.println("Path: " + path.getText());
				System.out.println("showPath: " + showPath);

			}
    	});

    	buttonFrame.setLayout(null);
    	buttonFrame.setVisible(true);

    }

    private static void drawSourceCircle(Mat img, int x, int y) {

    	Point point = new Point(x, y);
    	circle(img, point, 50, new Scalar(255, 0, 255, 0), 10, 8, 0);
    	putText(img, "source", new Point(x + 60, y), FONT_HERSHEY_PLAIN, 2.0, new Scalar(255, 0, 255, 255), 4, 8, false);
    	point.deallocate();

    }

    private static void drawPath(Mat img, int[] arr, boolean state) {

    	if (state == true) {

    		int temp[] = new int[arr.length];

    		// Digits in order -1 in order to match array index
        	for (int i = 0; i < arr.length; i++) {
        		temp[i] = arr[i] - 1;
        	}

        	// Draw
        	Scalar colour = new Scalar(0, 255, 0, 0);
        	Point previous = new Point(xCoord, yCoord);

        	for(int i = 0; i < temp.length; i++) {
        		line(img, previous, new Point((int) circles[temp[i]][0], (int) circles[temp[i]][1]), colour, 3, 8, 0);
        		previous = new Point((int) circles[temp[i]][0], (int) circles[temp[i]][1]);
        	}

        	previous.deallocate();

    	}


    }

    private static void setOrder(String path) {

    	// Parse string separated by commas into array
    	String[] arr = path.split(",", -1);
    	int size = arr.length;
    	order = new int[size];

    	for(int i = 0; i < arr.length; i++) {
    		order[i] = Integer.parseInt(arr[i]);
    	}

    }

}