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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.ImageAnalyzer;
import core.Region;
import core.RegionManager;

public class GUI extends JFrame implements ChangeListener{
	
	private static final long serialVersionUID = 5601258703065991698L;
	private String windowTitle;
	private ImageComponent sourceImage;
	private ImageAnalyzer imgAnalyser;
	
	private JPanel mainPanel;
	private JLabel huMomentValue1;
	private JLabel huMomentValue2;
	private JLabel huMomentValue3;
	private JLabel huMomentValue4;
	
	private int selectedRegionID;
	
	private JTable selectedRegionTable;
	private JTable compareRegionTable;

	public GUI() {
		//create component to display image
		sourceImage = new ImageComponent();
		
		imgAnalyser = ImageAnalyzer.getInstance();
		sourceImage.setViewSectionVisible(false);
		windowTitle = "";
		mainPanel = new JPanel();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// window size and position
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setSize(800, 700);
		setLocation((screenSize.width - getWidth()) / 2,
				(screenSize.height - getHeight()) / 2);
		setLocationRelativeTo(null);
		
		selectedRegionID = -1;
	}
	
	public void create(){
		setTitle(windowTitle);
		setupMenuBar();
		buildMainPanel();
		setupListeners();
		
		setVisible(true);
	}
	
	private void setupMenuBar() {
		JMenuBar menu = new JMenuBar();

		// File menu & entries
		JMenu fileMenu = new JMenu("File");
		menu.add(fileMenu);
		Action fileOpenAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			{
				putValue(Action.NAME, "Open");
				putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
			}

			public void actionPerformed(ActionEvent e) {
				File input = openFile();
				if (input != null) {
					RegionManager.getInstance().clear();
					sourceImage.loadImage(input);
				}
			}
		};
		
		
		Action exitAction = new AbstractAction() {
			
			private static final long serialVersionUID = 1L;
			
			{
				putValue(Action.NAME, "Exit");
				putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
			}
			
			public void actionPerformed(ActionEvent e) {
				 System.exit( 0 ); 
			}
		};
		
		
		fileMenu.add(fileOpenAction);
		fileMenu.add(exitAction);
		
		setJMenuBar(menu);
	}
	
	private void buildMainPanel(){
		
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(true);
		mainPanel.setFocusable(true);
		
		JPanel sliderPanel1 = new JPanel(new GridLayout(2,1));
		JLabel huMomentLabel1 = new JLabel("Hu Moment #1");
		JSlider huTresholdSlider1 = new JSlider(1,1000,(int)(imgAnalyser.getHuTreshold(1)*10000));
		huTresholdSlider1.setName("hu1");
		huTresholdSlider1.addChangeListener(this);
		huMomentValue1 = new JLabel(String.valueOf(huTresholdSlider1.getValue()/(float)(huTresholdSlider1.getMaximum()*10)));
		sliderPanel1.add(huTresholdSlider1);
		sliderPanel1.add(huMomentValue1);
		
		JPanel sliderPanel2 = new JPanel(new GridLayout(2,1));
		JLabel huMomentLabel2 = new JLabel("Hu Moment #2");
		JSlider huTresholdSlider2 = new JSlider(1,1000,(int)(imgAnalyser.getHuTreshold(2)*10000));
		huTresholdSlider2.setName("hu2");
		huTresholdSlider2.addChangeListener(this);
		huMomentValue2 = new JLabel(String.valueOf(huTresholdSlider2.getValue()/(float)(huTresholdSlider2.getMaximum()*10)));
		sliderPanel2.add(huTresholdSlider2);
		sliderPanel2.add(huMomentValue2);
		
		JPanel sliderPanel3 = new JPanel(new GridLayout(2,1));
		JLabel huMomentLabel3 = new JLabel("Hu Moment #3");
		JSlider huTresholdSlider3 = new JSlider(1,1000,(int)(imgAnalyser.getHuTreshold(3)*10000));
		huTresholdSlider3.setName("hu3");
		huTresholdSlider3.addChangeListener(this);
		huMomentValue3 = new JLabel(String.valueOf(huTresholdSlider3.getValue()/(float)(huTresholdSlider3.getMaximum()*10)));
		sliderPanel3.add(huTresholdSlider3);
		sliderPanel3.add(huMomentValue3);
		
		JPanel sliderPanel4 = new JPanel(new GridLayout(2,1));
		JLabel huMomentLabel4 = new JLabel("Hu Moment #4");
		JSlider huTresholdSlider4 = new JSlider(1,1000,(int)(imgAnalyser.getHuTreshold(4)*10000));
		huTresholdSlider4.setName("hu4");
		huTresholdSlider4.addChangeListener(this);
		huMomentValue4 = new JLabel(String.valueOf(huTresholdSlider4.getValue()/(float)(huTresholdSlider4.getMaximum()*10)));
		sliderPanel4.add(huTresholdSlider4);
		sliderPanel4.add(huMomentValue4);

		
		
		JPanel menuPanel = new JPanel();
		menuPanel.setSize(200, 300);
		menuPanel.setLayout(new GridLayout(2,4,1,1));
		
		menuPanel.add(huMomentLabel1);
		menuPanel.add(sliderPanel1);
		menuPanel.add(huMomentLabel2);
		menuPanel.add(sliderPanel2);
		menuPanel.add(huMomentLabel3);
		menuPanel.add(sliderPanel3);
		menuPanel.add(huMomentLabel4);
		menuPanel.add(sliderPanel4);
		
		JPanel regionDataPanel = new JPanel(new GridLayout(1, 2));
		JPanel selectedRegionPanel = new JPanel();
		selectedRegionPanel.setLayout(new BoxLayout(selectedRegionPanel, BoxLayout.Y_AXIS));
		JPanel compareRegionPanel = new JPanel();
		compareRegionPanel.setLayout(new BoxLayout(compareRegionPanel, BoxLayout.Y_AXIS));
		
		String tableHeader[] = {"Attribute", "Value"};
		
		String tableValues[][] = {
				{"Orientation", ""},
				{"Eccentricity", ""},
				{"Hu moment #1", ""},
				{"Hu moment #2", ""},
				{"Hu moment #3", ""},
				{"Hu moment #4", ""},
				{"Hu moment #5", ""}
				};
		
		selectedRegionTable = new JTable(tableValues,tableHeader);
		compareRegionTable = new JTable(tableValues,tableHeader);
		
		selectedRegionTable.setShowGrid(false);
		compareRegionTable.setShowGrid(false);
		
		selectedRegionPanel.add(new JLabel("Selected region"));
		selectedRegionPanel.add(selectedRegionTable);
		
		compareRegionPanel.add(new JLabel("Compare region (select with right mouse)"));
		compareRegionPanel.add(compareRegionTable);

		regionDataPanel.add(selectedRegionPanel);
		regionDataPanel.add(compareRegionPanel);
		
		mainPanel.add(menuPanel);		
		mainPanel.add(sourceImage);
		mainPanel.add(regionDataPanel);
		
		setContentPane(mainPanel);
	}
	

	private void setupListeners(){	
		//setup mouse behavior
		MouseInputAdapter mouseAdapter = new MouseInputAdapter() {
			public void mousePressed(MouseEvent e) {
				selectedRegionID = imgAnalyser.getSelectedRegion(e.getX(), e.getY());
				
				if(e.getButton() == MouseEvent.BUTTON1){
					if(selectedRegionID != -1){
						imgAnalyser.deselectAllRegions();
						imgAnalyser.setRegionSelected(selectedRegionID, true);
						imgAnalyser.selectSimilarRegions(selectedRegionID);
						
						Region region = RegionManager.getInstance().getRegion(selectedRegionID);
						
						selectedRegionTable.setValueAt(String.valueOf(region.getOrientationAngle()), 0, 1);
						selectedRegionTable.setValueAt(String.valueOf(region.getEccentricity()), 1, 1);
						selectedRegionTable.setValueAt(String.valueOf(region.getHuMoment(1)), 2, 1);
						selectedRegionTable.setValueAt(String.valueOf(region.getHuMoment(2)), 3, 1);
						selectedRegionTable.setValueAt(String.valueOf(region.getHuMoment(3)), 4, 1);
						selectedRegionTable.setValueAt(String.valueOf(region.getHuMoment(4)), 5, 1);
						selectedRegionTable.setValueAt(String.valueOf(region.getHuMoment(5)), 6, 1);
					}
				}
				
				if(e.getButton() == MouseEvent.BUTTON3){
					if(selectedRegionID != -1){						
						Region region = RegionManager.getInstance().getRegion(selectedRegionID);
						
						compareRegionTable.setValueAt(String.valueOf(region.getOrientationAngle()), 0, 1);
						compareRegionTable.setValueAt(String.valueOf(region.getEccentricity()), 1, 1);
						compareRegionTable.setValueAt(String.valueOf(region.getHuMoment(1)), 2, 1);
						compareRegionTable.setValueAt(String.valueOf(region.getHuMoment(2)), 3, 1);
						compareRegionTable.setValueAt(String.valueOf(region.getHuMoment(3)), 4, 1);
						compareRegionTable.setValueAt(String.valueOf(region.getHuMoment(4)), 5, 1);
						compareRegionTable.setValueAt(String.valueOf(region.getHuMoment(5)), 6, 1);
					}
				}
			}
		};
		
		sourceImage.addMouseListener(mouseAdapter);
	}
	
	private File openFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Images (*.jpg, *.png, *.gif, *.bmp)", "jpg", "png", "gif", "bmp");
		chooser.setFileFilter(filter);
		int ret = chooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
		
	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public ImageComponent getSourceImage() {
		return sourceImage;
	}


	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		if(slider.getName() == "hu1"){
			huMomentValue1.setText(String.valueOf(slider.getValue()/(float)(slider.getMaximum()*10)));
			imgAnalyser.setHuTreshold(1, slider.getValue()/(float)(slider.getMaximum()*10));
		}
		if(slider.getName() == "hu2"){
			huMomentValue2.setText(String.valueOf(slider.getValue()/(float)(slider.getMaximum()*10)));
			imgAnalyser.setHuTreshold(2, slider.getValue()/(float)(slider.getMaximum()*10));
		}
		if(slider.getName() == "hu3"){
			huMomentValue3.setText(String.valueOf(slider.getValue()/(float)(slider.getMaximum()*10)));
			imgAnalyser.setHuTreshold(3, slider.getValue()/(float)(slider.getMaximum()*10));
		}
		if(slider.getName() == "hu4"){
			huMomentValue4.setText(String.valueOf(slider.getValue()/(float)(slider.getMaximum()*10)));
			imgAnalyser.setHuTreshold(4, slider.getValue()/(float)(slider.getMaximum()*10));
		}
		
		if(selectedRegionID != -1){
			imgAnalyser.deselectAllRegions();
			imgAnalyser.setRegionSelected(selectedRegionID, true);
			imgAnalyser.selectSimilarRegions(selectedRegionID);
		}
		
	}
}
