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

package gui;

import java.awt.Color;

/**
 * Singleton class that manages which graphic elements are displayed
 *
 */
public class DisplayManager {
	
	//Bounding box draw params
	public static final Color BB_COLOR = Color.GRAY;
	public static final int BB_STROKE_WIDTH = 1;
	
	//centroid draw params
	public static final Color CENTROID_COLOR = Color.RED;
	public static final int CENTROID_STROKE_WIDTH = 1;
	public static final int CENTROID_SIZE = 5;
	
	//contour draw params
	public static final Color OUTER_CONTOUR_COLOR = Color.GREEN;
	public static final int OUTER_CONTOUR_STROKE_WIDTH = 1;
	public static final Color INNER_CONTOUR_COLOR = Color.RED;
	public static final int INNER_CONTOUR_STROKE_WIDTH = 1;
	
	//orientation line params
	public static final Color ORIENTATION_LINE_COLOR = Color.ORANGE;
	public static final int ORIENTATION_LINE_STROKE_WIDTH = 1;
	public static final int ORIENTATION_LINE_LENGTH = 30;
	
	//font params
	public static final Color ID_FONT_COLOR = Color.GRAY;

	private boolean boundingBoxesVisible;
	private boolean centroidVisible;
	private boolean contoursVisible;
	private boolean regionIDsVisible;
	private boolean areaSizeVisible;
	private boolean orientationVisible;
	
	private DisplayManager() {
		
		boundingBoxesVisible = true;
		centroidVisible = true;
		contoursVisible = true;
		regionIDsVisible = false;
		areaSizeVisible = false;
		orientationVisible = false;
	}

	private static class Holder {
		private static final DisplayManager INSTANCE = new DisplayManager();
	}
	
	public static DisplayManager getInstance() {
		return Holder.INSTANCE;
	}
	
	public boolean isBoundingBoxesVisible() {
		return boundingBoxesVisible;
	}
	public void setBoundingBoxesVisible(boolean boundingBoxesVisible) {
		this.boundingBoxesVisible = boundingBoxesVisible;
	}
	public boolean isCentroidVisible() {
		return centroidVisible;
	}
	public void setCentroidVisible(boolean centroidVisible) {
		this.centroidVisible = centroidVisible;
	}

	public boolean isContoursVisible() {
		return contoursVisible;
	}

	public void setContoursVisible(boolean contoursVisible) {
		this.contoursVisible = contoursVisible;
	}

	public boolean isRegionIDsVisible() {
		return regionIDsVisible;
	}

	public void setRegionIDsVisible(boolean regionIDsVisible) {
		this.regionIDsVisible = regionIDsVisible;
	}

	public boolean isAreaSizeVisible() {
		return areaSizeVisible;
	}

	public void setAreaSizeVisible(boolean areaSizeVisible) {
		this.areaSizeVisible = areaSizeVisible;
	}

	public boolean isOrientationVisible() {
		return orientationVisible;
	}

	public void setOrientationVisible(boolean orientationVisible) {
		this.orientationVisible = orientationVisible;
	}
}
