package garbagecollector.model;

import java.util.ArrayList;
import java.util.Stack;

/**
 * This class is the driver class for the garbage collection assignment. This
 * class controls the creation, referencing and reference removal of the
 * objects. A semi-space garbage collection algorithm is implemented inside this
 * class. In order for that algorithm to run, there are two different ArrayLists
 * named tospace and fromspace. These two ArrayLists represent the two regions
 * of memory that the algorithm uses. The tospace array is the heap in memory
 * that the objects would be placed on. The root Array represents the stack used
 * in memory to hold any statically allocated memory created when a program
 * begins.
 * 
 * @author David Kavanagh
 * 
 */
public class Driver {

	public ArrayList<Fish> tospace = new ArrayList<Fish>();
	public ArrayList<Fish> fromspace = new ArrayList<Fish>();
	public ArrayList<Integer> referenced = new ArrayList<>();
	public ArrayList<Integer> unreferenced = new ArrayList<>();

	public Fish[] roots = new Fish[3];

	private static BigFish rootBig = null;
	private static MediumFish rootMedium = null;
	private static SmallFish rootSmall = null;

	/**
	 * Constructor for the Driver class. Three fish are placed onto the stack.
	 * These three fish will be the root objects that all references for the
	 * program will come from
	 * 
	 */
	public Driver() {

		roots[0] = rootBig;
		roots[1] = rootMedium;
		roots[2] = rootSmall;

	}

	/**
	 * allocateBigFish() - Creates a BigFish object and places it into the
	 * tospace ArrayList
	 */
	public void allocateBigFish() {

		BigFish big = new BigFish();
		tospace.add(big);
	}

	/**
	 * allocateMeduimFish() - Creates a MediumFish object and places it into the
	 * tospace ArrayList
	 */
	public void allocateMeduimFish() {

		MediumFish medium = new MediumFish();
		tospace.add(medium);
	}

	/**
	 * allocateSmallFish() - Creates a SmallFish object and places it into the
	 * tospace ArrayList
	 */
	public void allocateSmallFish() {

		SmallFish small = new SmallFish();
		tospace.add(small);

	}

	/**
	 * reference() - Assigns a reference from one fish object to another. Both
	 * fish to be referenced are passed in as parameters. The fish that the
	 * reference is coming from has its own reference method called. The fish
	 * that the reference is going to is passed into that method.
	 * 
	 * 
	 */
	public boolean reference(Fish referenceFrom, Fish referenceTo) {

		// if referenceFrom is a BigFish
		if (referenceFrom instanceof BigFish) {
			// make a call to its reference method and pass in the referenceTo
			// fish
			((BigFish) referenceFrom).reference(referenceTo);
		} else if (referenceFrom instanceof MediumFish) {

			((MediumFish) referenceFrom).reference(referenceTo);
		} else if (referenceFrom instanceof SmallFish) {

			((SmallFish) referenceFrom).reference(referenceTo);
		}

		System.out.print("\n Reference from: " + tospace.indexOf(referenceFrom)
				+ " -- Reference to : " + tospace.indexOf(referenceTo));
		return true;

	}

	/**
	 * assignRoots() - Assigns the roots for the tree structure. There can be at
	 * most three roots. One of each type of fish. If no root has been picked
	 * for any of the fish objects, null will be passed in as a parameter. If a
	 * null is passed in as any of the parameters, there will be nothing
	 * assigned for root
	 * 
	 */
	public void assignRoots(Fish bigRoot, Fish mediumRoot, Fish smallRoot) {

		// if the parameter is an object
		if (bigRoot != null) {
			// sets the element at the specified index to the object
			roots[0] = bigRoot;
			System.out.print("\n Big Root: " + tospace.indexOf(bigRoot));
		}
		if (mediumRoot != null) {

			roots[1] = mediumRoot;
			System.out.print("\n Med Root: " + tospace.indexOf(mediumRoot));
		}
		if (smallRoot != null) {

			roots[2] = smallRoot;
			System.out.print("\n Small Root: " + tospace.indexOf(smallRoot));
		}

	}

	/**
	 * GarbageCollection() - Clears any unreferenced fish objects from the heap
	 * ( tospace ). It does this by searching through the roots Array and
	 * passing any root objects into the depthFirstSearch() method which
	 * performs a search through the tree of referenced objects and mark them as
	 * such. Once that is done, any object marked as referenced is placed into
	 * the fromspace list. The tospace heap is cleared and the fromspace is
	 * copied into the tospace list and the fromspace is cleared. This leaves
	 * only referenced objects in the tospace heap
	 * 
	 */
	public void garbageCollection() {

		// pass any root fish object into the depthFirstSearch() method.
		// test to ensure that a null object will not be passed is as it is not
		// necessary to have three root objects
		for (Fish root : roots) {

			if (root != null) {
				depthFirstSearch(root);
			}
		}

		// any referenced objects are passed into the fromspace list
		for (Fish reference : tospace) {

			if (reference.visited) {
				// add the index of any referenced fish into a list. This list
				// is used in the GUI
				referenced.add(tospace.indexOf(reference));
				// add the referenced fish to the fromspace list
				fromspace.add(reference);
			} else {

				// add the index of any unreferenced fish into a list. This list
				// is used in the GUI
				unreferenced.add(tospace.indexOf(reference));
			}
		}


		 //clear the tospace
		 tospace.clear();

	}

	/**
	 * depthFirstSearch() - A root fish object is taken in as a parameter. That
	 * root fish is marked as visited. The root objects references list is then
	 * searched. If a fish is found, they are passed back into the method which
	 * will recursively call itself until all referenced fish are marked as
	 * visited
	 * 
	 * @param root
	 */
	public void depthFirstSearch(Fish root) {
		// mark the fish object as visited
		root.visited = true;

		// search through the root fishes references ArrayList, mark any fish
		// that are referenced to the root as visited. Then recursively call
		// this method passing in the referenced fish.
		for (Fish reference : root.references) {
			// reference.visited = true;

			depthFirstSearch(reference);
		}

	}

}
