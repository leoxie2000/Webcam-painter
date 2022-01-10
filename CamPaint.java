import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece
	private ArrayList<Point> largestRegion;
	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();

		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {

		// enter different display modes based on the key pressed
		finder.setImage(image);
		if (displayMode == 'p') g.drawImage(painting,0,0,null);
		else if (displayMode == 'r') {

			finder.recolorImage();
			g.drawImage(finder.getRecoloredImage(),0,0,null);
		}
		else if (displayMode == 'w') g.drawImage(image,0,0,null);
	}

	/**
	 * Webcam method, here finding regions and updating painting.
	 */
	@Override
	public void processImage() {

		if (targetColor != null) {
			finder.findRegions(targetColor);
			largestRegion = finder.largestRegion();
			for (Point p : largestRegion) {
				painting.setRGB(p.x, p.y, paintColor.getRGB());
			}
		}
	}
	/**
	 * Overrides the DrawingGUI method to set targetColor.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) { // to be safe, make sure webcam is grabbing an image
			//Once you press r, you can pass the mousepress location's color to search for similar colors
			if (displayMode == 'r') {
				targetColor = new Color(image.getRGB(x, y));
			}
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
