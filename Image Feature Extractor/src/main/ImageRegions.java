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

package main;

import java.io.File;
import gui.GUI;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import core.ImageAnalyzer;

public class ImageRegions {


	private static final String TITLE = "Properties of image regions";
	private static final String INITIAL_OPEN = "data/text.png";
	private static GUI mainWindow;
	
	public ImageRegions(){
		mainWindow = new GUI();
		mainWindow.setTitle(TITLE);
		
		ImageAnalyzer imgAnalyzer = ImageAnalyzer.getInstance();
		mainWindow.getSourceImage().addChangeListener(imgAnalyzer);
		
		//open default file
		File initialFile = new File(INITIAL_OPEN);
		if(initialFile.canRead()){
			mainWindow.getSourceImage().loadImage(initialFile);
		}

		//create user interface
		mainWindow.create();
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getCrossPlatformLookAndFeelClassName());
				} catch (Exception e) {
				}
				new ImageRegions();
			}
		});
	}
}
