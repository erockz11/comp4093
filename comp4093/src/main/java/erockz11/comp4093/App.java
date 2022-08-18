package erockz11.comp4093;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.CanvasFrame;


public class App {
	
    public static void main( String[] args ) throws IOException {
        
    	BufferedImage img = ImageIO.read(new File("C:\\Users\\Scott\\Documents\\GitHub\\COMP4093\\comp4039\\comp4093\\resources\\images\\img14.jpg"));    	
    	
    	CanvasFrame mframe = new CanvasFrame("main");
    	mframe.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    	mframe.setCanvasScale(0.5);
    	mframe.setLocation(0, 0);
    	mframe.showImage(img);
    	
    }
    
}
