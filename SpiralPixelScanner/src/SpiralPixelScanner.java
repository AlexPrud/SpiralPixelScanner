
import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This class scans a selected rectangle, looking for specific pixel RGB values.
 * 
 * @author APR
 */
public class SpiralPixelScanner {
    
    // Settings
    final static int SIZE = 549; /* ODD ONLY */
    final static int GAP = 0;
    final static int HSIZE = SIZE / 2;
    
    // Pixels to locate (examples) RGB values from 0-255
    static int[] pix0 = {255,219,195};
//    static int[] pix1 = {0,255,0};
//    static int[] pix2 = {0,0,255};
    static int[][] pixels = {pix0};

    /**
     * Compare the model (RGB values) to the pixel color
     * 
     * @param pixels desired RGB value
     * @param color color of the scanned pixel
     * @return match found (within tolerance)
     */
    public static boolean compare(int[][] pixels, Color color) {
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        for (int[] pixel : pixels) {
            if (gap(pixel[0], r) && gap(pixel[1], g) && gap(pixel[2], b)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calculates if the red, blue or green value is a match
     * 
     * @param target desired value
     * @param result result value
     * @return match found (within tolerance)
     */
    private static boolean gap(int target, int result) {
        
        
//        int lower = target - GAP;
//        int upper = target + GAP;
//        
//        return result >= lower && result <= upper;
         return target == result;
        
    }
    
    /**
     * iterate over every pixel in a bImg (must be an even square) and
     * determines if a pixel matches the desired pixel
     * 
     * @param bImg Buffered image that has square screenshot
     * @param pixels array of RGB pixels / models
     * @return relative point of the rectangle
     */
    public static Point scan(BufferedImage bImg, int[][] pixels) {
        
        int posX = HSIZE;
        int posY = HSIZE;
        int move = 1;
        Color color;
        boolean stop = false;
        int iteration = 0;
        
        //System.out.println(bImg);
        
        //System.out.println("Comparing at: " + posX + "," + posY);
        if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
            return new Point(posX, posY);
        }
        
        do {
            
            // Left
            if (iteration == HSIZE) move--;
            for (int i = 0; i < move && !stop; i++) {
                posX--;
                //System.out.println("Comparing at: " + posX + "," + posY);
                if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
                    return new Point(posX, posY);
                }
            }

            // Up
            if (iteration == HSIZE) stop = true;
            for (int i = 0; i < move && !stop; i++) {
                posY--;
                //System.out.println("Comparing at: " + posX + "," + posY);
                if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
                    return new Point(posX, posY);
                }
            }

            move++;

            // Right
            for (int i = 0; i < move && !stop; i++) {
                posX++;
                //System.out.println("Comparing at: " + posX + "," + posY);
                if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
                    return new Point(posX, posY);
                }
            }

            // Down
            for (int i = 0; i < move && !stop; i++) {
                posY++;
                //System.out.println("Comparing at: " + posX + "," + posY);
                if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
                    return new Point(posX, posY);
                }
            }

            move++;
            iteration++;

        } while (!stop);
        
        return null;
        
    }

    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        
        int mouseX;
        int mouseY;
        Point mouseP;
        int magicNum;
        BufferedImage bImg;
        Robot robot;
        Rectangle img;
        Point point;
        
        int sleep = 5;
        
        try {
            
            // Countdown
            System.out.println("Starting in " + sleep + "...");
            for (int i = 0; i < sleep; i++) {
                Thread.sleep(1000);
                System.out.println("Starting in " + (sleep - i - 1) + "...");
            }
            System.out.println("Started");
            
            // Prepare the square
            mouseP = MouseInfo.getPointerInfo().getLocation();
            mouseX = (int)mouseP.getX() - HSIZE;
            mouseY = (int)mouseP.getY() - HSIZE;
            robot = new Robot();
            img = new Rectangle(mouseX, mouseY, SIZE, SIZE);
            
            
            // Get image for scanning
            while (true) {
                bImg = robot.createScreenCapture(img);
                point = scan(bImg, pixels);
                if (point != null) {
                    
                    // Actions to be taken if a desired pixel has been located
                    robot.mouseMove(mouseX + (int)point.getX(), mouseY + (int)point.getY() );
                    Thread.sleep(50);
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    Thread.sleep(50);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                }
            }
        } catch (AWTException ex) {
            System.err.println("AWTException");
        } catch (InterruptedException ex) {
            Logger.getLogger(SpiralPixelScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
