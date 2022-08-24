package erockz11.comp4093;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;


public class App {

	private static final ToIplImage converter = new ToIplImage();

    public static void main( String[] args ) throws IOException {

    	// Load image
    	BufferedImage src = ImageIO.read(new File("resources/images/img10.jpg"));
    	// Convert BufferedImage to Mat
    	Mat srcMat = Java2DFrameUtils.toMat(src);

    	// Main Frame
    	CanvasFrame mFrame = new CanvasFrame("main");
    	mFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	mFrame.setCanvasScale(0.3);
    	mFrame.setLocation(0, 0);
    	mFrame.showImage(src);

    	// Convert to HSV
    	Mat hsvMat = new Mat();
    	cvtColor(srcMat, hsvMat, opencv_imgproc.COLOR_RGB2HSV);

    	//HSV Frame
    	CanvasFrame hsvFrame = new CanvasFrame("hsv");
    	hsvFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	hsvFrame.setCanvasScale(0.3);
    	hsvFrame.setLocation(1280, 0);
    	hsvFrame.showImage(converter.convert(hsvMat));
    	
    }

}
