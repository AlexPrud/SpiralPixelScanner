import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

/**
 *
 * This class scans a selected rectangle, looking for specific pixel RGB values.
 * 
 * @author APR
 */
public class SpiralPixelScanner {
    
    // Settings
    final static int SIZE_OF_RECT = 549; /* ODD ONLY */
    final static int HALF_SIZE_OF_RECT = SIZE_OF_RECT / 2;
    
    // Pixels to locate (examples) RGB values from 0-255
    static int[] pix0 = {255,219,195};
    static int[][] pixels = {pix0};

    /**
     * Iterate over every pixel in a bImg (must be an even square) and
     * determines if a pixel matches the desired pixel
     * 
     * @param bImg Buffered image that has square screenshot
     * @param pixels array of RGB pixels / models
     * @return relative point of the rectangle
     */
    public static Point scan(BufferedImage bImg, int[][] pixels) {
        
        int posX = HALF_SIZE_OF_RECT;
        int posY = HALF_SIZE_OF_RECT;
        int move = 1;
        int iteration = 0;
        boolean stop = false;
        Color color;
        
        
        if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
            return new Point(posX, posY);
        }
        
        do {
            
            // Left
            if (iteration == HALF_SIZE_OF_RECT) move--;
            for (int i = 0; i < move && !stop; i++) {
                posX--;
                if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
                    return new Point(posX, posY);
                }
            }

            // Up
            if (iteration == HALF_SIZE_OF_RECT) stop = true;
            for (int i = 0; i < move && !stop; i++) {
                posY--;
                if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
                    return new Point(posX, posY);
                }
            }

            move++;

            // Right
            for (int i = 0; i < move && !stop; i++) {
                posX++;
                if (compare(pixels, new Color(bImg.getRGB(posX, posY)))) {
                    return new Point(posX, posY);
                }
            }

            // Down
            for (int i = 0; i < move && !stop; i++) {
                posY++;
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
     * Compare the model (RGB values) to the pixel color
     * 
     * @param pixels desired RGB value
     * @param color color of the scanned pixel
     * @return match found
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
     * @return match found
     */
    private static boolean gap(int target, int result) {
        
         return target == result;
    }

    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        
        int mouseX;
        int mouseY;
        int magicNum;
        int sleep = 5;
        Point mouseP;
        Point point;
        BufferedImage bImg;
        Robot robot;
        Rectangle img;
        
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
            mouseX = (int)mouseP.getX() - HALF_SIZE_OF_RECT;
            mouseY = (int)mouseP.getY() - HALF_SIZE_OF_RECT;
            robot = new Robot();
            img = new Rectangle(mouseX, mouseY, SIZE_OF_RECT, SIZE_OF_RECT);
            
            while (true) {
                
                // Get image for scanning
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
            System.out.println("InterruptedException");
        }
    }
}
