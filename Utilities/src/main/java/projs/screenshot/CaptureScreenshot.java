package projs.screenshot;

import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class CaptureScreenshot { 
    public static void main(String[] args) 
    { 
        try 
        {
        	File screenshotFile = new File("C:/Users/siddharth.s/screenshots/Shot.jpg");
            Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
            BufferedImage image = new Robot().createScreenCapture(capture); 
            ImageIO.write(image, "jpg", screenshotFile);
            Desktop.getDesktop().open(screenshotFile);
        } 
        catch (Exception ex) { 
            System.out.println(ex); 
        } 
    } 
} 
