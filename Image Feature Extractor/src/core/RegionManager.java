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

public class RegionManager {
	
	private int regionID;

	private HashMap<Integer, Region> regions;
	
	private RegionManager() {
		regionID = 0;
		
		regions = new HashMap<Integer, Region>();
	}

	private static class Holder {
		private static final RegionManager INSTANCE = new RegionManager();
	}

	public static RegionManager getInstance() {
		return Holder.INSTANCE;
	}
	
	public Region createRegion(){
		Region descriptor = new Region(regionID);
		regions.put(regionID, descriptor);
		
		++regionID;
		return descriptor;
	}
	
	public boolean hasRegion(int id){
		return regions.containsKey(id);
	}
	
	public Region getRegion(int id){
		return regions.get(id);
	}
	
	public Iterator<Integer> getRegionIDs(){
		return regions.keySet().iterator();
	}
	
	public void clear(){
		regionID = 0;
		regions.clear();
	}
}
