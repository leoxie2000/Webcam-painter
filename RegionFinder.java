import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;                // how similar a pixel color must be to the target color, to belong to a region
	// suitable value for maxColorDiff depends on your implementation of colorMatch() and how much difference in color you want to allow
	private static final int minRegion = 5;                // how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();            // a region is a list of points
	// so the identified regions are in a list of lists of points


	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood fill regions in the image, similar enough to the targetColor.
	 */
	public void findRegions(Color targetColor) {
		regions.clear();

		//creating new bufferedimage to keep track of the pixels that've been visited
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		//looping through all the pixels
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {

				//picking a color and find all of the pixels that share similar color,putting them into regions.
				Color currentcolor = new Color(image.getRGB(x, y));
				if (colorMatch(currentcolor, targetColor) && visited.getRGB(x, y) == 0) {
					ArrayList<Point> smallregion = new ArrayList<Point>();
					Point matchedPoint = new Point(x, y);
					ArrayList<Point> toVisit = new ArrayList<Point>();
					toVisit.add(matchedPoint);

					//As long as there are points to visit, visit them
					while (toVisit.size() > 0) {
						Point toVisitPoint = toVisit.remove(toVisit.size() - 1);
						smallregion.add(toVisitPoint);
						visited.setRGB(toVisitPoint.x, toVisitPoint.y, 1);

						//looping through all the neigthbors
						for (int cx = Math.max(0,toVisitPoint.x - 1); cx <= Math.min(image.getWidth()-1,toVisitPoint.x + 1); cx++) {
							for (int cy = Math.max(0,toVisitPoint.y - 1); cy <= Math.min(image.getHeight()-1,toVisitPoint.y + 1); cy++) {
								Point neighborPoint = new Point(cx, cy);
								Color neighborColor = new Color(image.getRGB(cx, cy));
								if (colorMatch(neighborColor, targetColor) == true && visited.getRGB(neighborPoint.x, neighborPoint.y) == 0) {
									toVisit.add(neighborPoint);

								}
								visited.setRGB(neighborPoint.x, neighborPoint.y, 1);
							}
						}
					}

					//if meets the minimum size requirement, add it to the list
					if (smallregion.size() >= minRegion) {
						regions.add(smallregion);
					}
				}
				}
			}
		}


		// making sure all red, green , and blue are within small differences
	private static boolean colorMatch(Color c1,Color c2){
		int redDiff = Math.abs(c1.getRed()-c2.getRed());
		int blueDiff = Math.abs(c1.getBlue()-c2.getBlue());
		int greenDiff = Math.abs(c1.getGreen()-c2.getGreen());
		if(redDiff <= maxColorDiff && blueDiff <= maxColorDiff &&greenDiff <= maxColorDiff){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */

	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		if (regions.size()!=0){
			ArrayList<Point> largest = regions.get(0);
			for (int i = 0; i < regions.size();i++){
				if (regions.get(i).size() >= largest.size()){
					largest = regions.get(i);
				}
			}
			return largest;
		}
		return null;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(),image.copyData(null), image.getColorModel().isAlphaPremultiplied(),null);

		//loop through all pointsr in regions
		for(int i = 0; i < regions.size(); i++){
			Color randomC= new Color((int)(Math.random()*16777216));
			for(int j = 0;j < regions.get(i).size(); j++){
				int x = (int)(regions.get(i).get(j).getX());
				int y = (int)(regions.get(i).get(j).getY());
				recoloredImage.setRGB(x, y, randomC.getRGB());
			}

		}
		// Now recolor the regions in it

	}
}

