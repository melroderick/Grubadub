package edu.brown.cs.kdtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.MinMaxPriorityQueue;

public class KDTree<Q extends KDData> {

	private int dims;
	private KDNode root;

	/**
	 * @param treeData A list with all the data for the KDTree.
	 */
	public KDTree(Collection<Q> treeData) {
		// Make copy of treeData so sorts in buildSubtree don't affect clients.
		List<Q> treeDataCopy = new ArrayList<Q>(treeData);
		dims = treeDataCopy.get(0).getDims();
		root = buildSubtree(treeDataCopy, new KDDataComparator(dims));
	}

	private KDTree(KDNode root, int dims) {
	    this.root = root;
	    this.dims = dims;
	}

	/**
	 * @return A KDTree representing the left subtree. Returns null
	 * if the left subtree is null.
	 */
	public KDTree<Q> getLeft() {
	    if (root.getLeft() == null) {
	        return null;
	    } else {
	        return new KDTree<Q>(root.getLeft(), dims);
	    }
	}

	/**
     * @return A KDTree representing the right subtree. Returns null
     * if the right subtree is null.
     */
    public KDTree<Q> getRight() {
        if (root.getRight() == null) {
            return null;
        } else {
            return new KDTree<Q>(root.getRight(), dims);
        }
    }

	private KDNode buildSubtree(List<Q> treeData, KDDataComparator comp) {
		if (treeData.size() == 0) {
			return null;
		} else if (treeData.size() == 1) {
			return new KDNode(treeData.get(0), comp.getCurrDim(), null, null);
		} else {
			Collections.sort(treeData, comp);

			int middle = treeData.size() / 2;
			Q middleData = treeData.get(middle);
			List<Q> leftTreeData = treeData.subList(0, middle);
			List<Q> rightTreeData = treeData.subList(middle + 1, treeData.size());

			// Get currDim so the KDNode will have correct current dimension
			// info, then increment comp to pass down correct dimension info
			// to subtrees.
			int currDim = comp.getCurrDim();
			comp.incrementDim();

			// Used so incrementDim() calls on left recursive sub-calls won't
			// interfere with right recursive sub-calls.
			KDDataComparator compClone = new KDDataComparator(
					comp.getNumDim(), comp.getCurrDim());


			return new KDNode(middleData, currDim,
					buildSubtree(leftTreeData, comp),
					buildSubtree(rightTreeData, compClone));
		}
	}

	private KDNode find(Q o) {
		return root.find(o);
	}

	/**
	 * @param origin The datum from which to find the nearest neighbor
	 * @return The datum nearest to origin in the KDTree.
	 */
	public Q nearestNeighbor(Q origin) {
		KDNode n = find(origin);
		if (n == null) {
			n = buildDummyNode(origin); // See buildDummyNode for why
		}
		if (this.size() == 1) {
			throw new IllegalArgumentException(
					"ERROR: Only 1 datum in data given, "
							+ "so object has no neighbors");
		} else {
			return nnHelper(n, root, root).data;
		}
	}

	// This is very hackish. My nnHelper, kNNHelper, and fwrHelper methods
	// all take nodes as origins. They also have code that tests for equality
	// between nodes, to ensure that if a user is searching for the nearest
	// neighbors of a datum in the tree, it would not get that same datum as
	// output. The currDim field of the KDNode returned here is thus one more
	// than the number of dimensions, which is impossible for a valid KDNode.
	// This will hopefully ensure that the equality test will fail, and there
	// isn't really any reason any future user would want to get currDim of
	// originNode in the helper methods... or at least I hope so.
	private KDNode buildDummyNode(Q origin) {
		return new KDNode(origin, origin.getLocData().length + 1, null, null);
	}


	/**
	 * @param origin The datum from which to find the nearest neighbors
	 * @param k The number of nearest neighbors to find
	 * @return The k nearest datum to origin in the KDTree.
	 */
	public ArrayList<Q> kNearestNeighbor(Q origin, int k) {
		KDNode originNode = find(origin);
		if (originNode == null) {
			originNode = buildDummyNode(origin); // See buildDummyNode for why
		}
		if (k <= 0) {
			throw new IllegalArgumentException(
					"ERROR: k must be an integer greater than zero.");
		}
		if (k >= this.size()) {
			throw new IllegalArgumentException(
					"ERROR: Too many neighbors requested for data given");
		} else {
			ClosestComparator<Q> comp = new ClosestComparator<Q>(origin);
			MinMaxPriorityQueue<Q> queue =
					MinMaxPriorityQueue.orderedBy(comp)
					.maximumSize(k).create();
			MinMaxPriorityQueue<Q> bestGuesses = knnHelper(originNode, queue, root, k);
			ArrayList<Q> kNearestNeighbors =
					castObjects(bestGuesses.toArray());
			// Right now list is unsorted by distance
			Collections.sort(kNearestNeighbors, comp);
			return kNearestNeighbors;
		}
	}

	/**
	 * @param origin The datum from which to find data within a given radius
	 * @param radius The radius within which to give results
	 * @return All the data within radius of origin,
	 * as defined by origin's getDistanceFrom method.
	 */
	public ArrayList<Q> findWithinRadius(Q origin, double radius) {
		if (radius < 0) {
			throw new IllegalArgumentException("Radius must be 0 or greater");
		}
		KDNode originNode = find(origin);
		if (originNode == null) {
			originNode = buildDummyNode(origin); // See buildDummyNode for why
		}
		if (this.size() == 1) {
			throw new IllegalArgumentException(
					"ERROR: Only 1 datum in data given, so no possible data "
							+ "within radius");
		} else {
			ArrayList<Q> withinRadius = fwrHelper(originNode, radius, root);
			ClosestComparator<Q> comp = new ClosestComparator<Q>(origin);
			Collections.sort(withinRadius, comp);
			return withinRadius;
		}
	}

	private ArrayList<Q> fwrHelper(
			KDNode originNode, double radius, KDNode curr) {
		ArrayList<Q> withinRadius = new ArrayList<>();

		// If curr is the exact same node as originNode, search both subtrees
		if (curr == originNode) {
			ArrayList<Q> temp = fwrHelper(originNode, radius, curr.getLeft());
			temp.addAll(
					fwrHelper(originNode, radius, curr.getRight()));
			return temp;
		}

		if (curr == null) {
			return new ArrayList<Q>();
		}

		if (curr != originNode &&
				originNode.data.euclidianDist(curr.data) < radius) {
			withinRadius.add(curr.data);
		}

		double currComponentDifference = originNode.getComponent(curr.currDim) -
				curr.getComponent(curr.currDim);

		// So we know where we looked if we have to search the other subtree
		boolean searchedLeft;
		if (currComponentDifference < 0) {
			withinRadius.addAll(fwrHelper(originNode, radius, curr.getLeft()));
			searchedLeft = true;
		} else {
			withinRadius.addAll(fwrHelper(originNode, radius, curr.getRight()));
			searchedLeft = false;
		}

		// Search other subtree if it's possible it has points within radius
		// (when |currComponentDifference| < radius)
		if (Math.abs(currComponentDifference) < radius) {
			if (searchedLeft) {
				withinRadius.addAll(
						fwrHelper(originNode, radius, curr.getRight()));
			} else {
				withinRadius.addAll(
						fwrHelper(originNode, radius, curr.getLeft()));
			}
		}

		return withinRadius;
	}

	private ArrayList<Q> castObjects(Object[] l) {
		ArrayList<Q> newList = new ArrayList<Q>();
		for (Object o : l) {
			newList.add((Q)(o));
		}
		return newList;
	}

	private MinMaxPriorityQueue<Q> knnHelper(KDNode originNode,
			MinMaxPriorityQueue<Q> bestGuesses, KDNode curr, int k) {

		if (curr == null) {
			return bestGuesses;
		}

		// Adds current node's data to bestGuess. This will do nothing
		// if the current node's data is worse the worst datum in bestGuesses.
		if (curr != originNode) {
			bestGuesses.add(curr.data);
		}

		double currComponentDifference =
				originNode.getComponent(curr.currDim) -
				curr.getComponent(curr.currDim);

		// So we know where we looked if we have to search the other subtree
		boolean searchedLeft;
		if (currComponentDifference < 0) {
			bestGuesses = knnHelper(originNode, bestGuesses, curr.getLeft(), k);
			searchedLeft = true;
		} else {
			bestGuesses = knnHelper(originNode, bestGuesses, curr.getRight(), k);
			searchedLeft = false;
		}

		// Search other subtree if bestGuesses doesn't have k elements
		// or if its guesses aren't good enough
		// (when |currComponentDifference| < dist for worst element of bestGuesses)
		boolean bestGuessesIsFull = bestGuesses.size() == k;
		boolean guessesNotGoodEnough =
				Math.abs(currComponentDifference) <
				// Distance between the farthest element in bestGuesses
				bestGuesses.peekLast().euclidianDist(originNode.data);

		if (!bestGuessesIsFull || guessesNotGoodEnough) {
			if (searchedLeft) {
				bestGuesses = knnHelper(
						originNode, bestGuesses, curr.getRight(), k);
			} else {
				bestGuesses = knnHelper(
						originNode, bestGuesses, curr.getLeft(), k);
			}
		}

		return bestGuesses;
	}

	private KDNode closest(KDNode originNode, KDNode A, KDNode B) {
		if (originNode.data.euclidianDist(A.data) <
				originNode.data.euclidianDist(B.data)) {
			return A;
		} else {
			return B;
		}
	}

	private KDNode nnHelper(
			KDNode originNode, KDNode bestGuess, KDNode curr) {
		// Adapted from pseudocode from
		// http://web.stanford.edu/class/cs106l/handouts/assignment-3-kdtree.pdf

		// This will occur if trying to find the nearest neighbor of the root
		// of the KDTree, as nearestNeighbor sets the initial best guess to
		// root. Problem is solved by setting bestGuess on recursive calls
		// to the roots of the left and right subtrees.
		if (bestGuess == originNode) {
			return closest(originNode,
					nnHelper(originNode, curr.getLeft(), curr.getLeft()),
					nnHelper(originNode, curr.getRight(), curr.getRight()));
		}
		if (curr == null) {
			return bestGuess;
		}

		if (curr != originNode) {
			bestGuess = closest(originNode, bestGuess, curr);
		}

		double currComponentDifference = originNode.getComponent(curr.currDim) -
				curr.getComponent(curr.currDim);

		// So we know where we looked if we have to search the other subtree
		boolean searchedLeft;
		if (currComponentDifference < 0) {
			bestGuess = nnHelper(originNode, bestGuess, curr.getLeft());
			searchedLeft = true;
		} else {
			bestGuess = nnHelper(originNode, bestGuess, curr.getRight());
			searchedLeft = false;
		}

		// Search other subtree if bestGuess isn't good enough
		// (when |currComponentDifference| < distance for bestGuess)
		if (Math.abs(currComponentDifference) <
				bestGuess.data.euclidianDist(originNode.data)) {
			if (searchedLeft) {
				bestGuess = nnHelper(originNode, bestGuess, curr.getRight());
			} else {
				bestGuess = nnHelper(originNode, bestGuess, curr.getLeft());
			}
		}
		return bestGuess;
	}

	/**
	 * @return The number of nodes in the KDTree.
	 */
	public int size() {
		return root.size();
	}

	/**
	 * @return The maximum depth of the KDTree.
	 */
	public int depth() {
		return root.depth();
	}

	@Override
  public String toString() {
		return root.toString();
	}

	private class KDNode {

		public Q data;
		public int currDim;
		public KDNode left;
		public KDNode right;

		public KDNode(Q data, int currDim, KDNode left, KDNode right) {
			this.data = data;
			this.currDim = currDim;
			this.left = left;
			this.right = right;
		}

		private double getComponent(int dim) {
			return data.getComponent(dim);
		}

		/**
		 * @return The left subnode.
		 * Returns null if this has no children on the left
		 */
		public KDNode getLeft() {
			return left;
		}

		/**
		 * @return The right subnode.
		 * Returns null if this has no children on the right
		 */
		public KDNode getRight() {
			return right;
		}

		/**
		 * @param o The data being searched for
		 * @return The node holding Q in the tree, or null if Q can't be found.
		 */
		public KDNode find(Q o) {
			if (data.equals(o)) {
				return this;
			}
			int comparison = new KDDataComparator(data.getDims(), currDim).compare(data, o);
			if (comparison < 0) {
				if (right == null) {
					return null;
				} else {
					return right.find(o);
				}
			} else if (comparison > 0) {
				if (left == null) {
					return null;
				} else {
					return left.find(o);
				}
			} else {
				// comparison equals 0, but that should have been caught in
				// the the ".equals" if branch, so should ever reach here.
				assert(false);
			}

			// So compiler doesn't complain
			return null;
		}

		/**
		 * @return The number of datum stored in the node
		 */
		public int size() {
			if (left == null && right == null) {
				return 1;
			} else if (left != null && right == null) {
				return 1 + left.size();
			} else if (left == null && right != null) {
				return 1 + right.size();
			} else {
				return 1 + left.size() + right.size();
			}
		}

		/**
		 * @return The depth of the node
		 */
		public int depth() {
			if (left == null && right == null) {
				return 1;
			} else if (left != null && right == null) {
				return 1 + left.depth();
			} else if (left == null && right != null) {
				return 1 + right.depth();
			} else {
				return 1 + Math.max(left.depth(), right.depth());
			}
		}

		@Override
		public String toString() {
			String NESTING = "\n  ";
			String NULL_REP = "--";

			if (left == null && right == null) {
				return NULL_REP + NESTING + data.toString() + "\n" + NULL_REP;
			} else if (left == null && right != null) {
				return NULL_REP + NESTING + data.toString() + "\n" + right.data.toString();
			} else if (left != null && right == null) {
				return left.data.toString() + NESTING + data.toString() + "\n" + NULL_REP;
			} else {
				return left.toString() + NESTING + data.toString() + "\n" + right.toString();
			}
		}


	}
}
