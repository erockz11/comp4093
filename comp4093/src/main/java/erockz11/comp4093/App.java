package erockz11.comp4093;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import static org.bytedeco.javacpp.opencv_core.CV_8UC1;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_core.bitwise_and;
import static org.bytedeco.javacpp.opencv_imgproc.circle;


public class App {

	private static final ToIplImage converter = new ToIplImage();
	
	static int xCoord = 100;
	static int yCoord = 100;

    public static void main( String[] args ) throws IOException {
    	
    	// Load image
    	BufferedImage src = ImageIO.read(new File("resources/images/img8.jpg"));
    	// Convert BufferedImage to Mat
    	Mat rgbMat = Java2DFrameUtils.toMat(src);

    	// Main Frame
    	CanvasFrame mFrame = new CanvasFrame("main");
    	mFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	mFrame.setCanvasScale(0.17);
    	mFrame.setLocation(0, 0);
    	mFrame.showImage(src);

    	// Convert to HSV
    	Mat hsvMat = new Mat();
    	cvtColor(rgbMat, hsvMat, opencv_imgproc.COLOR_RGB2HSV);

    	// HSV Frame
    	CanvasFrame hsvFrame = new CanvasFrame("hsv");
    	hsvFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	hsvFrame.setCanvasScale(0.17);
    	hsvFrame.setLocation(640, 0);
    	hsvFrame.showImage(converter.convert(hsvMat));
    	
    	// Result Frame
    	CanvasFrame resultFrame = new CanvasFrame("result");
    	resultFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	resultFrame.setCanvasScale(0.17);
    	resultFrame.setLocation(1280, 0);

    	// Convert to IplImage
    	IplImage rgbIpl = new IplImage(rgbMat);
    	IplImage hsvIpl = new IplImage(hsvMat);

    	// Split channels
//    	IplImage hueIpl = IplImage.create(hsvIpl.width(), hsvIpl.height(), hsvIpl.depth(), CV_8UC1);
//    	IplImage satIpl = IplImage.create(hsvIpl.width(), hsvIpl.height(), hsvIpl.depth(), CV_8UC1);
//    	IplImage valIpl = IplImage.create(hsvIpl.width(), hsvIpl.height(), hsvIpl.depth(), CV_8UC1);
//    	opencv_core.cvSplit(hsvIpl, hueIpl, satIpl, valIpl, null);

    	// Apply Thresholding
//    	IplImage hueThreshold = IplImage.create(hsvIpl.width(), hsvIpl.height(), hsvIpl.depth(), CV_8UC1);
//    	IplImage satThreshold = IplImage.create(satIpl.width(), satIpl.height(), satIpl.depth(), CV_8UC1);
//    	IplImage valThreshold = IplImage.create(valIpl.width(), valIpl.height(), valIpl.depth(), CV_8UC1);
//    	opencv_core.cvInRangeS(hueIpl, opencv_core.cvScalar(100, 100, 100, 0), opencv_core.cvScalar(200, 200, 200, 0), hueThreshold);
//    	opencv_core.cvInRangeS(satIpl, opencv_core.cvScalar(0, 0, 0, 0), opencv_core.cvScalar(0, 0, 0, 0), satThreshold);
//    	opencv_core.cvInRangeS(valIpl, opencv_core.cvScalar(0, 0, 0, 0), opencv_core.cvScalar(0, 0, 0, 0), valThreshold);

    	IplImage rgbThresholdIpl = IplImage.create(rgbIpl.width(), rgbIpl.height(), rgbIpl.depth(), CV_8UC1);
    	IplImage hsvThresholdIpl = IplImage.create(hsvIpl.width(), hsvIpl.height(), hsvIpl.depth(), CV_8UC1);
    	opencv_core.cvInRangeS(rgbIpl, opencv_core.cvScalar(20, 80, 80, 0), opencv_core.cvScalar(255, 255, 200, 0), rgbThresholdIpl);
    	opencv_core.cvInRangeS(hsvIpl, opencv_core.cvScalar(20, 80, 80, 0), opencv_core.cvScalar(255, 255, 255, 0), hsvThresholdIpl);

    	// Apply smoothing



    	// Combine Images (bitwise AND)
    	Mat combinedThreshold = new Mat();
    	Mat rgbThresholdMat = opencv_core.cvarrToMat(rgbThresholdIpl);
    	Mat hsvThresholdMat = opencv_core.cvarrToMat(hsvThresholdIpl);
    	bitwise_and(rgbThresholdMat, hsvThresholdMat, combinedThreshold);

    	// HSV Channel Frames
//    	CanvasFrame hueFrame = new CanvasFrame("hue");
//    	hueFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//    	hueFrame.setCanvasScale(0.17);
//    	hueFrame.setLocation(0, 500);
//    	hueFrame.showImage(converter.convert(hueIpl));
//
//    	CanvasFrame satFrame = new CanvasFrame("saturation");
//    	satFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//    	satFrame.setCanvasScale(0.17);
//    	satFrame.setLocation(640, 500);
//    	satFrame.showImage(converter.convert(satIpl));
//
//    	CanvasFrame valFrame = new CanvasFrame("value");
//    	valFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//    	valFrame.setCanvasScale(0.17);
//    	valFrame.setLocation(1280, 500);
//    	valFrame.showImage(converter.convert(valIpl));

    	// Threshold Frames
//    	CanvasFrame hueThresholdFrame = new CanvasFrame("hue threshold");
//    	hueThresholdFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//    	hueThresholdFrame.setCanvasScale(0.17);
//    	hueThresholdFrame.setLocation(0, 940);
//    	hueThresholdFrame.showImage(converter.convert(hueThreshold));
//
//    	CanvasFrame satThresholdFrame = new CanvasFrame("saturation threshold");
//    	satThresholdFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//    	satThresholdFrame.setCanvasScale(0.17);
//    	satThresholdFrame.setLocation(640, 940);
//    	satThresholdFrame.showImage(converter.convert(satThreshold));
//
//    	CanvasFrame valThresholdFrame = new CanvasFrame("value threshold");
//    	valThresholdFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//    	valThresholdFrame.setCanvasScale(0.17);
//    	valThresholdFrame.setLocation(1280, 940);
//    	valThresholdFrame.showImage(converter.convert(valThreshold));

    	CanvasFrame rgbThreshFrame = new CanvasFrame("rgb threshold");
    	rgbThreshFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	rgbThreshFrame.setCanvasScale(0.17);
    	rgbThreshFrame.setLocation(0, 500);
    	rgbThreshFrame.showImage(converter.convert(rgbThresholdIpl));

    	CanvasFrame hsvThreshFrame = new CanvasFrame("hsv threshold");
    	hsvThreshFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	hsvThreshFrame.setCanvasScale(0.17);
    	hsvThreshFrame.setLocation(640, 500);
    	hsvThreshFrame.showImage(converter.convert(hsvThresholdIpl));

    	CanvasFrame combinedThreshFrame = new CanvasFrame("combined threshold");
    	combinedThreshFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	combinedThreshFrame.setCanvasScale(0.17);
    	combinedThreshFrame.setLocation(1280, 500);
    	combinedThreshFrame.showImage(converter.convert(combinedThreshold));

    	// Buttons
    	JFrame buttonFrame = new JFrame("buttons");
    	buttonFrame.setSize(300, 300);
    	buttonFrame.setLocation(1920, 150);

    	final JLabel xLabel = new JLabel();
    	xLabel.setHorizontalAlignment(JLabel.CENTER);
    	xLabel.setBounds(0, 0, 250, 100);
    	
    	final JLabel yLabel = new JLabel();
    	yLabel.setHorizontalAlignment(JLabel.CENTER);
    	yLabel.setBounds(0, 20, 250, 100);

    	SpinnerModel xVal = new SpinnerNumberModel(xCoord, 0, src.getWidth(), 10);
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
    	
    	SpinnerModel yVal = new SpinnerNumberModel(yCoord, 0, src.getHeight(), 10);
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
    	
    	// Main Loop
    	while(true) {
    		Mat resultImage = new Mat(rgbMat.clone());
    		drawSourceCircle(resultImage, xCoord, yCoord);
    		resultFrame.showImage(converter.convert(resultImage));
    		
    	}
    	
    }
    
    static void drawSourceCircle(Mat img, int x, int y) {
    	
    	Point point = new Point(x, y);
    	circle(img, point, 100, new Scalar(0, 0, 255, 255), 25, 8, 0);
    }

}
