/*
The MIT License (MIT)

Copyright (c) 2011 Andre Groeschel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import utils.ColorGenerator;

import gui.ImageComponent;

public class RegionAnalyzer {

	private int regionSize;
	private int upperBound;
	private int leftBound;
	private int lowerBound;
	private int rightBound;

	private ColorGenerator colorGenerator;

	private ImageComponent image;
	private int sourcePixels[];
	private int destPixels[];
	private double area;
	private double centroidX;
	private double centroidY;
	private double mu00;
	
	private LinkedList<Pixel> pixels;

	Queue<Integer> floodFillQueue;

	public RegionAnalyzer() {
		colorGenerator = ColorGenerator.getInstance();
		floodFillQueue = new LinkedList<Integer>();

		regionSize = 0;
		upperBound = 0;
		leftBound = 0;
		lowerBound = 0;
		rightBound = 0;
		area = 0;
		centroidX = 0;
		centroidY = 0;
		mu00 = 0;
		pixels = new LinkedList<Pixel>();

	}

	//resets the pixels to their original state
	public void reset() {	
		if(image != null){
			this.destPixels = java.util.Arrays.copyOf(sourcePixels,sourcePixels.length);
		}
	}

	public void setImage(ImageComponent image) {
		this.image = image;
		this.sourcePixels = image.getPixels();
		this.destPixels = java.util.Arrays.copyOf(sourcePixels,sourcePixels.length);
	}

	public void fill(final int pixelStartIndex) {
		upperBound = 0;
		leftBound = 0;
		lowerBound = 0;
		rightBound = 0;
		area = 0;
		centroidX = 0;
		centroidY = 0;
		mu00 = 0;
		pixels.clear();

		if(image != null){
			int labelColor = colorGenerator.getRandomColor();
			int imageWidth = image.getImageWidth();
			int leftNeighbourIndex = 0;
			int rightNeighbourIndex = 0;
			int upperNeighbourIndex = 0;
			int lowerNeighbourIndex = 0;

			int pixelIndex = pixelStartIndex;

			// enqueue first pixel of region
			floodFillQueue.add(pixelIndex);

			while (!floodFillQueue.isEmpty()) {

				pixelIndex = floodFillQueue.poll();

				leftNeighbourIndex = pixelIndex - 1;
				rightNeighbourIndex = pixelIndex + 1;
				upperNeighbourIndex = pixelIndex - imageWidth;
				lowerNeighbourIndex = pixelIndex + imageWidth;

				// left neighbour
				if (leftNeighbourIndex > 0
						&& destPixels[leftNeighbourIndex] == ImageComponent.FOREGROUND) {
					destPixels[leftNeighbourIndex] = labelColor;					
					floodFillQueue.add(leftNeighbourIndex);
					
					updateHorizonalBounds(leftNeighbourIndex);
					
					int xPos = leftNeighbourIndex % imageWidth;
					int yPos = leftNeighbourIndex / imageWidth;
					
					pixels.add(new Pixel(xPos, yPos));
				}
				// right neighbor
				if (rightNeighbourIndex < destPixels.length
						&& destPixels[rightNeighbourIndex] == ImageComponent.FOREGROUND) {
					destPixels[rightNeighbourIndex] = labelColor;
					floodFillQueue.add(rightNeighbourIndex);
					
					updateHorizonalBounds(rightNeighbourIndex);
					
					int xPos = rightNeighbourIndex % imageWidth;
					int yPos = rightNeighbourIndex / imageWidth;
					
					pixels.add(new Pixel(xPos, yPos));
				}
				// upper neighbor
				if (upperNeighbourIndex > 0
						&& destPixels[upperNeighbourIndex] == ImageComponent.FOREGROUND) {
					destPixels[upperNeighbourIndex] = labelColor;
					floodFillQueue.add(upperNeighbourIndex);
					
					updateVerticalBounds(upperNeighbourIndex);
					
					int xPos = upperNeighbourIndex % imageWidth;
					int yPos = upperNeighbourIndex / imageWidth;
					
					pixels.add(new Pixel(xPos, yPos));
				}
				// lower neighbor
				if (lowerNeighbourIndex < destPixels.length
						&& destPixels[lowerNeighbourIndex] == ImageComponent.FOREGROUND) {
					destPixels[lowerNeighbourIndex] = labelColor;
					floodFillQueue.add(lowerNeighbourIndex);
					
					updateVerticalBounds(lowerNeighbourIndex);

					int xPos = lowerNeighbourIndex % imageWidth;
					int yPos = lowerNeighbourIndex / imageWidth;
					
					pixels.add(new Pixel(xPos, yPos));
				}
			}
			
			//subtract the offset from X and Y positions		
			Iterator<Pixel> pixelIt = pixels.iterator();
			while(pixelIt.hasNext()){
				Pixel currentPixel = pixelIt.next();
				currentPixel.x -= leftBound - 1;
				currentPixel.y -= upperBound - 1;
			}
			
			//calculate area of region, centroid position and mu00
			area = getMoment(0, 0);
			centroidX = (getMoment(1, 0) / area);
			centroidY = (getMoment(0, 1) / area);
			mu00 = getCentralMoment(0, 0);
		}
	}
	
	private void updateVerticalBounds(int pixelIndex){
		int imageWidth = image.getImageWidth();
		//get y position of neighbor
		int yPos = pixelIndex / imageWidth;
		
		if(upperBound == 0 || yPos < upperBound){
			upperBound = yPos;
		}
		
		if(yPos > lowerBound){
			lowerBound = yPos;
		}
	}
	
	private void updateHorizonalBounds(int pixelIndex){
		int imageWidth = image.getImageWidth();
		
		//get x position of neighbor
		int xPos = pixelIndex % imageWidth;
		
		if(leftBound == 0 || xPos < leftBound){
			leftBound = xPos;
		}
	
		if(xPos > rightBound){
			rightBound = xPos;
		}
	}
	
	public double getMoment(int p, int q){
		double moment = 0;
		
		Iterator<Pixel> pixelIt = pixels.iterator();
		while(pixelIt.hasNext()){
			Pixel currentPixel = pixelIt.next();			
			moment += Math.pow(currentPixel.x, p) *  Math.pow(currentPixel.y, q);
		}
		return moment;
	}
	
	public double getCentralMoment(int p, int q){
		double centralMoment = 0;
		
		Iterator<Pixel> pixelIt = pixels.iterator();
		while(pixelIt.hasNext()){
			Pixel currentPixel = pixelIt.next();			
			centralMoment += Math.pow(currentPixel.x - centroidX, p) *  Math.pow(currentPixel.y - centroidY, q);
		}
		
		return centralMoment;
	}
	
	public double getNormalCentralMoment(int p, int q){
		double norm = Math.pow(mu00, (double)((p + q)/(double)2) + 1);
		return getCentralMoment(p, q) / norm;
	}
	
	public double getHuMoment(int num){
		double result = 0;
		switch(num){
		case 1:
			result = getNormalCentralMoment(2, 0) + getNormalCentralMoment(0, 2);
			break;
		case 2:
			result = Math.pow(getNormalCentralMoment(2, 0) - getNormalCentralMoment(0, 2), 2)
			+ 4*Math.pow(getNormalCentralMoment(1, 1), 2);
			break;
		case 3:
			result = Math.pow(getNormalCentralMoment(3, 0) - (3*getNormalCentralMoment(1, 2)), 2)
			+ Math.pow((3*getNormalCentralMoment(2, 1)) - getNormalCentralMoment(0, 3), 2);
			break;
		case 4:
			result = Math.pow(getNormalCentralMoment(3, 0) + getNormalCentralMoment(1, 2), 2)
			+ Math.pow(getNormalCentralMoment(2, 1) + getNormalCentralMoment(0, 3), 2);
			break;
		case 5:
			double m30 = getNormalCentralMoment(3, 0);
			double m03 = getNormalCentralMoment(0, 3);
			double m12 = getNormalCentralMoment(1, 2);
			double m21 = getNormalCentralMoment(2, 1);
			result = ((m30 - 3*m12) * (m30+m12) * (Math.pow(m30 + m12, 2) - 3*Math.pow(m21 + m03, 2)))
			+ ((3*m21 - m03)*(m21+m03)*(3*Math.pow(m30+m12, 2) - Math.pow(m21+m03, 2)));
			break;
		}
		
		return result;
	}
	
	public double getOrientationAngle(){
		double angle = 0.5 * Math.atan2(2 * getNormalCentralMoment(1, 1), getNormalCentralMoment(2, 0) - getNormalCentralMoment(0, 2));
		
		return Math.toDegrees(angle);
	}
	
	public double getEccentricity(){
		double m20 = getNormalCentralMoment(2, 0);
		double m02 = getNormalCentralMoment(0, 2);
		double eccentricity = (Math.pow(m20 - m02, 2)
		+ 4*(Math.pow(getNormalCentralMoment(1,1), 2))) / Math.pow(m20 + m02,2);
		return eccentricity;
	}
	
	public int[] getPixels() {
		return destPixels;
	}

	public int getRegionSize() {
		return regionSize;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public int getLeftBound() {
		return leftBound;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public int getRightBound() {
		return rightBound;
	}
	
	public double getArea() {
		return area;
	}

	public double getCentroidX() {
		return centroidX;
	}

	public double getCentroidY() {
		return centroidY;
	}
	
	public int[] getRegionPixels(){
		int regionPixels[] = new int[this.pixels.size()];
		
		Iterator<Pixel> pixelIt = this.pixels.iterator();
		for(int i=0;pixelIt.hasNext();++i){
			Pixel currentPixel = pixelIt.next();
			regionPixels[i] = (currentPixel.y + upperBound-1) * this.image.getImageWidth() + currentPixel.x + leftBound-1;
		}
		return regionPixels;
	}
}
