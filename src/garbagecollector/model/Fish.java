package garbagecollector.model;

import java.util.ArrayList;

/**
 * This class is a Fish class. All other fish objects implement this class. It
 * acts a parent class and allows each of the different fish objects to be
 * placed into the same data structures
 * 
 * @author David Kavanagh
 * 
 */
public class Fish {
	
	public BigFish big;
	public MediumFish medium;
	public SmallFish small;
	
	public boolean visited;
	
	public ArrayList<Fish> references;
	
	/**
	 * Constructor for Fish class
	 */
	public Fish(){
		visited = false;
	}

	public BigFish getBig() {
		return big;
	}


	public void setBig(BigFish big) {
		this.big = big;
	}


	public MediumFish getMedium() {
		return medium;
	}


	public void setMedium(MediumFish medium) {
		this.medium = medium;
	}


	public SmallFish getSmall() {
		return small;
	}


	public void setSmall(SmallFish small) {
		this.small = small;
	}


	public boolean isVisited() {
		return visited;
	}


	public void setVisited(boolean visited) {
		this.visited = visited;
	}


	public ArrayList<Fish> getReferences() {
		return references;
	}


	public void setReferences(ArrayList<Fish> references) {
		this.references = references;
	}	

}
