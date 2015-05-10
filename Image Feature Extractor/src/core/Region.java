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

public class Region {
	
	public static enum RegionParams {
		PERIMETER, AREA, COMPACTNESS, BOUNDING_BOX, CENTROID, ORIENTATION, ECCENTRICITY
	}
	private int id;
	private float perimeter;
	private float area;
	private float compactness;
	private BoundingBox boundingBox;
	private Centroid centroid;
	private double orientationAngle;
	private int pixels[];
	private double huMoments[];
	private double eccentricity;
	
	public Region(int id){
		this.id = id;
		perimeter = 0.0f;
		area = 0.0f;
		compactness = 0.0f;
		orientationAngle = 0;
		huMoments = new double[5];
	}
	
	public boolean hasParam(RegionParams type){
		switch(type){
		case PERIMETER:
			return perimeter != 0.0f;
		case AREA:
			return area != 0.0f;
		case COMPACTNESS:
			return compactness != 0.0f;
		case BOUNDING_BOX:
			return boundingBox != null;
		case CENTROID:
			return centroid != null;
		case ORIENTATION:
			return orientationAngle != 0;
		case ECCENTRICITY:
			return eccentricity != 0;
		default:
				return false;
		}
	}

	public float getPerimeter() {
		return perimeter;
	}

	public void setPerimeter(float perimeter) {
		this.perimeter = perimeter;
	}

	public float getArea() {
		return area;
	}

	public void setArea(float area) {
		this.area = area;
	}

	public float getCompactness() {
		return compactness;
	}

	public void setCompactness(float compactness) {
		this.compactness = compactness;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public Centroid getCentroid() {
		return centroid;
	}

	public void setCentroid(Centroid centroid) {
		this.centroid = centroid;
	}
	
	public int getID(){
		return id;
	}

	public double getOrientationAngle() {
		return orientationAngle;
	}

	public void setOrientationAngle(double orientationAngle) {
		this.orientationAngle = orientationAngle;
	}

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public double getHuMoment(int num) {
		num -=1;
		if(num >= 0 && num < huMoments.length)
		{
			return huMoments[num];
		}
		
		return 0;
	}

	public void setHuMoment(int num, double value) {
		num -=1;
		if(num >= 0 && num < huMoments.length){
			this.huMoments[num] = value;
		}
	}

	public double getEccentricity() {
		return eccentricity;
	}

	public void setEccentricity(double eccentricity) {
		this.eccentricity = eccentricity;
	}
}
