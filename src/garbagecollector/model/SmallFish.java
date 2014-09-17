package garbagecollector.model;

import java.util.ArrayList;

/**
 * This is the Class for a SmallFish object. A SmallFish object is the smallest
 * of all the objects, meaning it is a child to any BigFish or MediumFish. It
 * can only hold a reference to one other SmallFish object.
 * 
 * @author David Kavanagh
 * 
 */
public class SmallFish extends Fish {

	public boolean smallRef = false;

	/**
	 * Constructor for the SmallFish object
	 */
	public SmallFish() {

		visited = false;
		references = new ArrayList<Fish>(1);
	}

	/**
	 * getSmall() - Getter for the SmallFish object that is referenced to this
	 * SmallFish
	 * 
	 * @return small - The SmallFish object that is referenced to this SmallFish
	 */
	public SmallFish getSmall() {
		return small;
	}

	/**
	 * setSmall() - Setter for the SmallFish object that is referenced to this
	 * SmallFish
	 * 
	 * @param small
	 *            - The SmallFish object that is referenced to this SmallFish
	 */
	public void setSmall(SmallFish small) {
		this.small = small;
	}

	/**
	 * reference() - Takes in a fish as a parameter. A boolean and the instance
	 * of key word are used to ensure that only one of each type of fish can be
	 * added to the references list for this fish object. This also ensures that
	 * at most one other fish can be referenced by this SmallFish at any time. A
	 * SmallFish can only reference one other SmallFish
	 * 
	 * @param fish
	 */
	public void reference(Fish fish) {

		// test to ensure that this fish cannot reference itself
		if (fish.equals(this)) {

			System.out.print("\n A fish object cannot reference itself");
			return;
		}

		if (fish instanceof SmallFish && !smallRef) {

			references.add(fish);
			smallRef = true;
		} 
	}
}
