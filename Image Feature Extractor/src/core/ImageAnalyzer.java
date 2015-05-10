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
import java.util.HashMap;
import java.util.Iterator;

import utils.ColorGenerator;

import core.Region.RegionParams;

import gui.ImageComponent;

public class ImageAnalyzer implements ImageChangeListener {

	private ImageComponent image;
	private RegionManager regionMgr;
	private RegionAnalyzer regionAnalyser;
	private int sourcePixels[];
	private int destPixels[];
	private HashMap<Integer, Region> selectedRegions;
	private int selectionColor;
	
	private double huTresholds[];
	private float orientationTreshold;
	private float eccentricityThreshold;
	
	private ImageAnalyzer(){
		regionMgr = RegionManager.getInstance();
		regionAnalyser = new RegionAnalyzer();
		selectedRegions = new HashMap<Integer, Region>();
		selectionColor = ColorGenerator.getInstance().getRandomColor();
		
		orientationTreshold = 10;
		
		huTresholds = new double[5];
		huTresholds[0] = 0.04	;
		huTresholds[1] = 0.004;
		huTresholds[2] = 0.009;
		huTresholds[3] = 0.004;
		huTresholds[4] = 0.00001;
		
		eccentricityThreshold = 0.05f;
	}
	
	private void analyse(){
		int pixels[] = regionAnalyser.getPixels();
		
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		
		// go through pixels and use flood filler to find regions
		for (int yPos = 0; yPos < imageHeight; ++yPos) {
			for (int xPos = 0; xPos < imageWidth; ++xPos) {
				int pixelIndex = yPos * imageWidth + xPos;
				
				// found new region
				if (pixels[pixelIndex] == ImageComponent.FOREGROUND) {
					regionAnalyser.fill(pixelIndex);
					
					//create descriptor for the region
					Region region = regionMgr.createRegion();
					
					//pixel data
					region.setPixels(regionAnalyser.getRegionPixels());
					
					//Bounding Box
					int posX = regionAnalyser.getLeftBound();
					int posY = regionAnalyser.getUpperBound();
					int bBoxWidth = regionAnalyser.getRightBound()-posX;
					int bBoxHeight = regionAnalyser.getLowerBound()-posY;
					
					BoundingBox bBox = new BoundingBox(posX, posY-1, bBoxWidth, bBoxHeight+1);
					region.setBoundingBox(bBox);
				
					//area
					region.setArea((int)regionAnalyser.getArea());
					
					//centroid
					int centroidXPos = posX + (int)regionAnalyser.getCentroidX();
					int centroidYPos = posY + (int)regionAnalyser.getCentroidY();;
					
					Centroid centroid = new Centroid(centroidXPos, centroidYPos);
					region.setCentroid(centroid);
					
					//orientation
					region.setOrientationAngle(regionAnalyser.getOrientationAngle());
					
					//eccentricity
					region.setEccentricity(regionAnalyser.getEccentricity());
					
					//hu moments					
					region.setHuMoment(1, regionAnalyser.getHuMoment(1));
					region.setHuMoment(2, regionAnalyser.getHuMoment(2));
					region.setHuMoment(3, regionAnalyser.getHuMoment(3));
					region.setHuMoment(4, regionAnalyser.getHuMoment(4));
					region.setHuMoment(5, regionAnalyser.getHuMoment(5));
				}
			}
		}
	}

	public int getSelectedRegion(final int xPos, final int yPos){
		int id=-1;
		
		//check the position against each regions bounding box
		if (xPos >= 0 && yPos >= 0 && xPos < image.getImageWidth()
				&& yPos < image.getImageHeight()) {
			
			Iterator<Integer> regionIDIt = regionMgr.getRegionIDs();
			while(regionIDIt.hasNext()){
				Region currentRegion = regionMgr.getRegion(regionIDIt.next());
				if(currentRegion.hasParam(RegionParams.BOUNDING_BOX)){
					BoundingBox bBox = currentRegion.getBoundingBox();
					//check if position is inside bounding box
					if(xPos >= bBox.getX() && yPos >= bBox.getY() && xPos <= bBox.getX() + bBox.getWidth() && yPos <= bBox.getY() + bBox.getHeight()){
						//selected pixel must be foreground
						int pixels[] = image.getPixels();
						int pixelIndex = yPos * image.getImageWidth() + xPos;
						if(pixels[pixelIndex] == ImageComponent.FOREGROUND){
							return currentRegion.getID();
						}
					}
				}
			}
		}
		return id;
	}
	
	public void deselectAllRegions(){
		Iterator<Integer> selectedRegionsIt = selectedRegions.keySet().iterator();
		
		while(selectedRegionsIt.hasNext()){
			setRegionSelected(selectedRegionsIt.next(), false);
		}
		selectedRegions.clear();
	}
	
	public void setRegionSelected(int id, boolean isSelected){		
		if(regionMgr.hasRegion(id)){
			Region region = regionMgr.getRegion(id);
			int pixels[] = region.getPixels();
			
			if(isSelected){
				for(int i=0;i<pixels.length;++i){
					destPixels[pixels[i]] = selectionColor;
				}
				selectedRegions.put(id, region);
			} else {
				for(int i=0;i<pixels.length;++i){
					destPixels[pixels[i]] = ImageComponent.FOREGROUND;
				}
			}
			image.setPixels(destPixels);
			
		} else {
			System.err.println("No region with id " + id + " found.");
		}
	}

	public void onImageChange(ImageComponent image) {
		this.image = image;
		this.sourcePixels = image.getPixels();
		this.destPixels = java.util.Arrays.copyOf(sourcePixels,sourcePixels.length);
		regionAnalyser.setImage(image);
		analyse();
	}
	
	private static class Holder {
		private static final ImageAnalyzer INSTANCE = new ImageAnalyzer();
	}
	
	public static ImageAnalyzer getInstance() {
		return Holder.INSTANCE;
	}
	
	public void selectSimilarRegions(int regionID){			
		boolean isOrientationSimilar;
		boolean isEccentricitySimilar;
		boolean isHuMomentSimilar[] = new boolean[5];

		if(regionMgr.hasRegion(regionID)){
			Region regionToCompare = regionMgr.getRegion(regionID);
			
			//go through all other regions and compare orientation, eccentricity and Hu moments
			Iterator<Integer> regionIDIt = regionMgr.getRegionIDs();
			while(regionIDIt.hasNext()){
				isOrientationSimilar = false;
				isEccentricitySimilar = false;
				isHuMomentSimilar[0] = false;
				isHuMomentSimilar[1] = false;
				isHuMomentSimilar[2] = false;
				isHuMomentSimilar[3] = false;
				isHuMomentSimilar[4] = false;
			
				
				Region currentRegion = regionMgr.getRegion(regionIDIt.next());
				if(currentRegion.getID() != regionToCompare.getID()){
					//orientation
					if(currentRegion.getOrientationAngle() < (regionToCompare.getOrientationAngle() + orientationTreshold)
							&& currentRegion.getOrientationAngle() > (regionToCompare.getOrientationAngle() - orientationTreshold)){
						isOrientationSimilar = true;
					}
					
					//eccentricity
					if(currentRegion.getEccentricity() < (regionToCompare.getEccentricity() + eccentricityThreshold)
							&& currentRegion.getEccentricity() > (regionToCompare.getEccentricity() - eccentricityThreshold)){
						isEccentricitySimilar = true;
					}
					
					//Hu moment #1
					if(currentRegion.getHuMoment(1) < (regionToCompare.getHuMoment(1) + huTresholds[0])
							&& currentRegion.getHuMoment(1) > (regionToCompare.getHuMoment(1) - huTresholds[0])){
						//setRegionSelected(currentRegion.getID(), true);
						isHuMomentSimilar[0] = true;
					}
					
					//Hu moment #2
					if(currentRegion.getHuMoment(2) < (regionToCompare.getHuMoment(2) + huTresholds[1])
							&& currentRegion.getHuMoment(2) > (regionToCompare.getHuMoment(2) - huTresholds[1])){
						//setRegionSelected(currentRegion.getID(), true);
						isHuMomentSimilar[1] = true;
					}
					
					//Hu moment #3
					if(currentRegion.getHuMoment(3) < (regionToCompare.getHuMoment(3) + huTresholds[2])
							&& currentRegion.getHuMoment(3) > (regionToCompare.getHuMoment(3) - huTresholds[2])){
						//setRegionSelected(currentRegion.getID(), true);
						isHuMomentSimilar[2] = true;
					}
					
					//Hu moment #4
					if(currentRegion.getHuMoment(4) < (regionToCompare.getHuMoment(4) + huTresholds[3])
							&& currentRegion.getHuMoment(4) > (regionToCompare.getHuMoment(4) - huTresholds[3])){
						//setRegionSelected(currentRegion.getID(), true);
						isHuMomentSimilar[3] = true;
					}
					//Hu moment #5
					if(currentRegion.getHuMoment(5) < (regionToCompare.getHuMoment(5) + huTresholds[4])
							&& currentRegion.getHuMoment(5) > (regionToCompare.getHuMoment(5) - huTresholds[4])){
						//setRegionSelected(currentRegion.getID(), true);
						isHuMomentSimilar[4] = true;
					}
				}
				
				if(isOrientationSimilar
						&& isEccentricitySimilar
						&& isHuMomentSimilar[0]
						&& isHuMomentSimilar[1]
						&& isHuMomentSimilar[2]
						&& isHuMomentSimilar[3]
						&& isHuMomentSimilar[4]
						                     )
				{
					setRegionSelected(currentRegion.getID(), true);
				}
			}
		} else {
			System.err.println("No region with id " + regionID + " found.");
		}
		
		
	}
	
	public void printRegionData(int regionID){
		if(regionMgr.hasRegion(regionID)){
			System.out.println("Values of region #" + regionID);
			
			Region region = regionMgr.getRegion(regionID);
			
			if(region.hasParam(RegionParams.AREA)){
				System.out.println("Area: " + region.getArea());
			}
			if(region.hasParam(RegionParams.COMPACTNESS)){
				System.out.println("Compactness: " + region.getCompactness());
			}
			if(region.hasParam(RegionParams.ORIENTATION)){
				System.out.println("Orientation: " + region.getOrientationAngle());
			}
			if(region.hasParam(RegionParams.ECCENTRICITY)){
				System.out.println("Eccentricity: " + region.getEccentricity());
			}
			
			System.out.println("Hu moment #1: " + region.getHuMoment(1));
			System.out.println("Hu moment #2: " + region.getHuMoment(2));
			System.out.println("Hu moment #3: " + region.getHuMoment(3));
			System.out.println("Hu moment #4: " + region.getHuMoment(4));
			System.out.println("Hu moment #5: " + region.getHuMoment(5));

		}
	}
	
	public void setHuTreshold(int num, float value){
		num -= 1;
		if(num >= 0 && num < huTresholds.length){
			huTresholds[num] = value;
		}
	}
	
	public double getHuTreshold(int num){
		num -= 1;
		if(num >= 0 && num < huTresholds.length){
			return huTresholds[num];
		}
		
		return 0;
	}
}
