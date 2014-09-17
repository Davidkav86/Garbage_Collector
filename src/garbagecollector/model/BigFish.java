package garbagecollector.model;

import java.util.ArrayList;

/**
 * This is the Class for a BigFish object. A BigFish object is the parent of the
 * other two objects in this application. A BigFish is the largest of the three
 * fish and can hold a reference to three different fish objects. It can have a
 * reference to one other BigFish, one MediumFish and one SmallFish
 * 
 * @author David Kavanagh
 * 
 */
public class BigFish extends Fish {

	public boolean bigRef = false;
	public boolean mediumRef = false;
	public boolean smallRef = false;

	/**
	 * Constructor for a BigFish object
	 */
	public BigFish() {

		visited = false;
		references = new ArrayList<Fish>(3);
	}

	/**
	 * getBig() - Getter for the BigFish object that is referenced to this
	 * BigFish
	 * 
	 * @return big - The BigFish object that is referenced to this BigFish
	 */
	public BigFish getBig() {
		return big;
	}

	/**
	 * setBig() - Setter for the BigFish object that is referenced to this
	 * BigFish
	 * 
	 * @param big
	 *            - The BigFish object that is referenced to this BigFish
	 */
	public void setBig(BigFish big) {
		this.big = big;
	}

	/**
	 * getMedium() - Getter for the MediumFish object that is referenced to this
	 * BigFish
	 * 
	 * @return medium - The MediumFish object that is referenced to this BigFish
	 */
	public MediumFish getMedium() {
		return medium;
	}

	/**
	 * setMedium() - Setter for the MediumFish object that is referenced to this
	 * BigFish
	 * 
	 * @param medium
	 *            - The MediumFish object that is referenced to this BigFish
	 */
	public void setMedium(MediumFish medium) {
		this.medium = medium;
	}

	/**
	 * getSmall() - Getter for the SmallFish object that is referenced to this
	 * BigFish
	 * 
	 * @return small - The SmallFish object that is referenced to this BigFish
	 */
	public SmallFish getSmall() {
		return small;
	}

	/**
	 * setSmall() - Setter for the SmallFish object that is referenced to this
	 * BigFish
	 * 
	 * @param small
	 *            - The SmallFish object that is referenced to this BigFish
	 */
	public void setSmall(SmallFish small) {
		this.small = small;
	}

	/**
	 * reference() - Takes in a fish as a parameter. A boolean and the instance
	 * of key word are used to ensure that only one of each type of fish can be
	 * added to the references list for this fish object. This also ensures that
	 * at most three other fish can be referenced by this BigFish at any time
	 * 
	 * @param fish
	 */
	public void reference(Fish fish) {

		// test to ensure that this fish cannot reference itself
		if (fish.equals(this)) {
			
			System.out.print("\n A fish object cannot reference itself");
			return;
		}
		
		if (fish instanceof BigFish && !bigRef) {

			references.add(fish);
			bigRef = true;
		} else if (fish instanceof MediumFish && !mediumRef) {

			references.add(fish);
			mediumRef = true;
		} else if (fish instanceof SmallFish && !smallRef) {

			references.add(fish);
			smallRef = true;
		}
	}

}
