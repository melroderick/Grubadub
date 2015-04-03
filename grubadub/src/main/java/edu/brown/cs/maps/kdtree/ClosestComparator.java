package edu.brown.cs.maps.kdtree;

import java.util.Comparator;

public class ClosestComparator<Q extends KDData> implements Comparator {

	KDData origin;
	
	/** Constructs a comparator that finds the KDData closest to origin
	 * @param origin The datum from which to find the closest other datum
	 */
	public ClosestComparator (Q origin) {
		this.origin = origin;
	}
	
	@Override
	public int compare(Object o1, Object o2) {
		Q d1 = (Q)(o1);
		Q d2 = (Q)(o2);
		
		double temp = origin.euclidianDist(d1) - origin.euclidianDist(d2);
		if (temp < 0) { return -1; }
		else if (temp == 0) { return 0; }
		else { return 1; }
	}

}
