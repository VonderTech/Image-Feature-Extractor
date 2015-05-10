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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import core.BoundingBox;
import core.Centroid;
import core.Region;
import core.RegionManager;
import core.ImageChangeListener;
import core.Region.RegionParams;


public class ImageComponent extends JComponent{

	private static final long serialVersionUID = -7037485372677445280L;
	
	public final static int BACKGROUND = 0xFFFFFFFF;
	public final static int FOREGROUND = 0xFF000000;
	
	private final static int MIN_WIDTH = 200;
	private final static int MIN_HEIGHT = 200;
	public final static int MIN_SECTION_SIZE = 1;
	
	//image that will never be altered
	private BufferedImage originalImage;
	//image thats displayed
	private BufferedImage displayImage;
	private int imageWidth;
	private int imageHeight;
	private int pixels[];
	
	private int sectionSize;
	private int maxSectionSize;

	private Rectangle viewSection;
	private boolean isViewSectionVisible;
	private DisplayManager displayMgr;
	private RegionManager descriptorMgr;
	
	private Vector<ImageChangeListener> imageChangeListeners;
	
	
	public ImageComponent(){
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		//1px simple  border
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		isViewSectionVisible = false;
		
		imageChangeListeners = new Vector<ImageChangeListener>();
		
		displayMgr = DisplayManager.getInstance();
		descriptorMgr = RegionManager.getInstance();
	}
	
	public void loadImage(final File imagePath){
		try {
			//BufferedImage newImage = ImageIO.read(imagePath);
			originalImage = ImageIO.read(imagePath);
			displayImage = null;
			
			this.imageWidth = originalImage.getWidth();
			this.imageHeight = originalImage.getHeight();
			
			//inform listeners
			Iterator<ImageChangeListener> changeListenerIt = imageChangeListeners.iterator();
			while(changeListenerIt.hasNext()){
				changeListenerIt.next().onImageChange(this);
			}
			
			
			resetViewSection();
			repaint();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Bild konnte nicht geladen werden.", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			originalImage = new BufferedImage(MIN_WIDTH, MIN_HEIGHT,
					BufferedImage.TYPE_INT_ARGB);
			this.imageWidth = MIN_WIDTH;
			this.imageHeight = MIN_HEIGHT;
		}
	}
	
	public BufferedImage getImage(){
		return originalImage;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
//        g2.setComposite(AlphaComposite.getInstance(
//                AlphaComposite.SRC_OVER, 1.0f));
		
		if(displayImage == null){
			g2.drawImage(originalImage, null, 0, 0);
		} else {
			g2.drawImage(displayImage, null, 0, 0);
		}

		
		
		if(isViewSectionVisible){
			g2.drawRect(viewSection.x,viewSection.y, viewSection.width, viewSection.height);
		}
		
	
		//Draw descriptor elements
		Iterator<Integer> descriptorIdIt = descriptorMgr.getRegionIDs();
		
		while(descriptorIdIt.hasNext()){
			Region currentDescriptor = descriptorMgr.getRegion(descriptorIdIt.next());
			
			if(displayMgr.isBoundingBoxesVisible() && currentDescriptor.hasParam(RegionParams.BOUNDING_BOX)){
				g2.setStroke(new BasicStroke(DisplayManager.BB_STROKE_WIDTH));
				g2.setColor(DisplayManager.BB_COLOR);
				BoundingBox bBox = currentDescriptor.getBoundingBox();
				g2.drawRect(bBox.getX(), bBox.getY(), bBox.getWidth(), bBox.getHeight());
				
				if(displayMgr.isRegionIDsVisible()){
					g2.setColor(DisplayManager.ID_FONT_COLOR);
					g2.drawString("id: " + currentDescriptor.getID(), bBox.getX(), bBox.getY() + bBox.getHeight() + 15);
				}
				
				if(displayMgr.isAreaSizeVisible()){
					g2.setColor(DisplayManager.ID_FONT_COLOR);
					g2.drawString("Area: " + currentDescriptor.getArea(), bBox.getX(), bBox.getY() + bBox.getHeight() + 30);
				}
			}
				

			if(displayMgr.isCentroidVisible() && currentDescriptor.hasParam(RegionParams.CENTROID)){
				g2.setStroke(new BasicStroke(DisplayManager.CENTROID_STROKE_WIDTH));
				g2.setColor(DisplayManager.CENTROID_COLOR);
				
				Centroid centroid = currentDescriptor.getCentroid();
				g2.drawOval(centroid.getX(), centroid.getY(), DisplayManager.CENTROID_SIZE, DisplayManager.CENTROID_SIZE);
				
				
			}
			
			if(displayMgr.isOrientationVisible()
					&& currentDescriptor.hasParam(RegionParams.ORIENTATION)
					&& currentDescriptor.hasParam(RegionParams.CENTROID)){
				
				g2.setStroke(new BasicStroke(DisplayManager.ORIENTATION_LINE_STROKE_WIDTH));
				g2.setColor(DisplayManager.ORIENTATION_LINE_COLOR);
				
				Centroid centroid = currentDescriptor.getCentroid();
				double angle = currentDescriptor.getOrientationAngle();
				
				double posX = centroid.getX() + 1;
				double posY = centroid.getY() - Math.tan(angle);
				
				posX -= centroid.getX();
				posY -= centroid.getY();
				
				posX *= DisplayManager.ORIENTATION_LINE_LENGTH;
				posY *= DisplayManager.ORIENTATION_LINE_LENGTH;
				
				g2.drawLine(centroid.getX() , centroid.getY(), centroid.getX()+(int)posX, centroid.getY()+(int)posY);
			}
		}

		g2.dispose();
	}
	
	public Dimension getPreferredSize() {
		if(originalImage != null) 
			return new Dimension(originalImage.getWidth(), originalImage.getHeight());
		else
			return new Dimension(MIN_WIDTH, MIN_HEIGHT);
	}
	
	public int getImageWidth(){
		return this.imageWidth;
	}
	
	public int getImageHeight(){
		return this.imageHeight;
	}
	
	public void setSectionPostion(final Point p){
		
		//check boundaries
		if(p.x >= 0 && p.y >= 0 && p.x <= imageWidth && p.y <= imageHeight){
			viewSection.x = p.x - viewSection.width/2;
			viewSection.y = p.y - viewSection.height/2;
		}
		repaint();
	}
	
	public Rectangle getViewSection(){
		return viewSection;
	}
	
	public int getViewSectionSize(){
		return viewSection.width;
	}
	
	public void setViewSectionSize(int newSize){
		viewSection.width = newSize;
		viewSection.height = newSize;
		repaint();
	}
	
	public int[] getPixels() {
		// get reference to internal pixels array
		if(pixels == null) {
			pixels = new int[imageWidth * imageHeight];
			originalImage.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);
		} else if(pixels.length != imageWidth * imageHeight)
		{
			pixels = new int[imageWidth * imageHeight];
			originalImage.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);
		}
		return pixels;
	}
	
	public void setPixels(int[] pixels) {
		// set pixels with same dimension
		setPixels(pixels, imageWidth, imageHeight);
	}
	
	public void setPixels(int[] pixels, int width, int height) {
		// set pixels with arbitrary dimension
		if(pixels == null || pixels.length != width * height) throw new IndexOutOfBoundsException();
	
		displayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		imageWidth = width;
		imageHeight = height;

		
		displayImage.setRGB(0, 0, width, height, pixels, 0, width);
		
//		if(pixels != null && pixels != this.pixels) {
//			// update internal pixels array
//			System.arraycopy(pixels, 0, this.pixels, 0, java.lang.Math.min(pixels.length, this.pixels.length));
//		}

		this.invalidate();
		this.repaint();
	}

	public boolean isViewSectionVisible() {
		return isViewSectionVisible;
	}

	public void setViewSectionVisible(boolean isViewSectionVisible) {
		this.isViewSectionVisible = isViewSectionVisible;
	}
	
	public int getMaxSectionSize() {
		return maxSectionSize;
	}
	
	private void resetViewSection(){
		if(imageWidth <= imageHeight) {
			this.sectionSize = imageWidth/2;
			this.maxSectionSize = imageWidth;
		} else {
			this.sectionSize = imageHeight/2;
			this.maxSectionSize = imageHeight;
		}
		this.viewSection = new Rectangle(new Point(0,0),new Dimension(sectionSize, sectionSize));
	}
	
	public void addChangeListener(ImageChangeListener listener){
		imageChangeListeners.add(listener);
	}
	
	public void removeChangeListener(ImageChangeListener listener){
		imageChangeListeners.remove(listener);
	}
}
