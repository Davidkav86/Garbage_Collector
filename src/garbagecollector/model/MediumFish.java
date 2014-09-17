package garbagecollector.model;

import java.util.ArrayList;

/**
 * This is the Class for a MediumFish object. A MediumFish object is the parent
 * of a SmallFish object. A MediumFish is the middle sized of the three fish and
 * can hold a reference to two different fish objects. It can have a reference
 * to one other MediumFish and one SmallFish
 * 
 * @author David Kavanagh
 * 
 */
public class MediumFish extends Fish {

	public boolean mediumRef = false;
	public boolean smallRef = false;

	/**
	 * Constructor for the MediumFish object
	 */
	public MediumFish() {

		visited = false;
		references = new ArrayList<Fish>(2);
	}

	/**
	 * getMedium() - Getter for the MediumFish object that is referenced to this
	 * MediumFish
	 * 
	 * @return medium - The MediumFish object that is referenced to this
	 *         MediumFish
	 */
	public MediumFish getMedium() {
		return medium;
	}

	/**
	 * setMedium() - Setter for the MediumFish object that is referenced to this
	 * MediumFish
	 * 
	 * @param medium
	 *            - The MediumFish object that is referenced to this MediumFish
	 */
	public void setMedium(MediumFish medium) {
		this.medium = medium;
	}

	/**
	 * getSmall() - Getter for the SmallFish object that is referenced to this
	 * MediumFish
	 * 
	 * @return small - The SmallFish object that is referenced to this
	 *         MediumFish
	 */
	public SmallFish getSmall() {
		return small;
	}

	/**
	 * setSmall() - Setter for the SmallFish object that is referenced to this
	 * MediumFish
	 * 
	 * @param small
	 *            - The SmallFish object that is referenced to this MediumFish
	 */
	public void setSmall(SmallFish small) {
		this.small = small;
	}

	/**
	 * reference() - Takes in a fish as a parameter. A boolean and the instance
	 * of key word are used to ensure that only one of each type of fish can be
	 * added to the references list for this fish object. This also ensures that
	 * at most two other fish can be referenced by this MediumFish at any time.
	 * A BigFish cannot be referenced by a MediumFish
	 * 
	 * @param fish
	 */
	public void reference(Fish fish) {
		
		// test to ensure that this fish cannot reference itself
		if (fish.equals(this)) {

			System.out.print("\n A fish object cannot reference itself");
			return;
		}

		if (fish instanceof MediumFish && !mediumRef) {

			references.add(fish);
			mediumRef = true;
		} else if (fish instanceof SmallFish && !smallRef) {

			references.add(fish);
			smallRef = true;
		} 
	}
}
