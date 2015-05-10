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

package utils;

import java.awt.Color;

public class ColorGenerator {
	
	private float saturation;
	private float brightness;
	
	private ColorGenerator(){
		this.saturation = 0.5f;
		this.brightness = 0.5f;
	}
	
	private static class Holder {
		private static final ColorGenerator INSTANCE = new ColorGenerator();
	}
	
	public static ColorGenerator getInstance() {
		return Holder.INSTANCE;
	}
	
	public int getRandomColor(){
		float h = (float) Math.random();
		float s = (float) (saturation * Math.random() + 1 - saturation);
		float b = (float) (brightness * Math.random() + 1 - brightness);
		return Color.HSBtoRGB(h, s, b);
	}
}
