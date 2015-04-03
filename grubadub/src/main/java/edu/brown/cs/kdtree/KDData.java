package edu.brown.cs.kdtree;

public interface KDData {
	default double euclidianDist(KDData other) {
		assert other.getDims() == getLocData().length;

		double distanceSquared = 0;
		double[] otherLoc = other.getLocData();
		double[] thisLoc = getLocData();
		for (int i = 0; i < thisLoc.length; i++) {
			distanceSquared += Math.pow(otherLoc[i] - thisLoc[i], 2);
		}
		return Math.sqrt(distanceSquared);
	}

	default double getComponent(int dim) {
		return getLocData()[dim];
	}

	public double[] getLocData();
	public int getDims();
}
