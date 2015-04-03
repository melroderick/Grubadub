package edu.brown.cs.kdtree;

import java.util.Comparator;

public class KDDataComparator implements Comparator<KDData> {

    private final int numDim;
    private int currDim;

    /**
     * @param numDim
     *            The number of dimensions the KDData has The current dimension
     *            to compare on is initialized to 0
     */
    public KDDataComparator(int numDim) {
        this(numDim, 0);
    }

    /**
     * @param numDim
     *            The number of dimensions the KDData has
     * @param currDim
     *            The current dimension to compare on
     */
    public KDDataComparator(int numDim, int currDim) {
        this.numDim = numDim;
        this.currDim = currDim;
    }

    /**
     * Increments currDim by 1. If currDim = numDim - 1, currDim goes to 0
     *
     */
    public void incrementDim() {
        currDim = (currDim + 1) % numDim;
    }

    /**
     * @return The current dimension the comparator is comparing on
     */
    public int getCurrDim() {
        return currDim;
    }

    /**
     * @return The total number of dimensions the KDData being compared has
     */
    public int getNumDim() {
        return numDim;
    }

    @Override
    public int compare(KDData kd1, KDData kd2) {
        double temp = kd1.getLocData()[currDim] - kd2.getLocData()[currDim];
        if (temp < 0) {
            return -1;
        } else if (temp == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}
