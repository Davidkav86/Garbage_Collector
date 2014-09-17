package garbagecollector.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class DriverTest {

	Driver driver = new Driver();

	/**
	 * referenceTest() - Test to ensure that the references are being correctly
	 * assigned. Tests to make sure that only one of each type of fish can be
	 * added to each fishes references ArrayList
	 * 
	 */
	@Test
	public void referenceTest() {

		BigFish big = new BigFish();
		MediumFish medium = new MediumFish();
		SmallFish small = new SmallFish();

		driver.reference(big, medium);

		driver.reference(medium, small);

		assertTrue(big.references.get(0) == medium);

		assertFalse(big.references.get(0) == small);

		assertTrue(medium.references.get(0) == small);

		assertFalse(medium.references.get(0) == big);

		// try to add another medium fish to the big fishes references
		driver.reference(big, medium);
		// ensure that the big fishes references did not add a second big fish
		assertTrue(big.references.size() == 1);

	}

	/**
	 * assignRootsTest() - Test to ensure that the roots are correctly assigned
	 * to the roots stack and are assigned in the correct order.
	 * 
	 */
	@Test
	public void assignRootsTest() {

		BigFish big = new BigFish();
		MediumFish medium = new MediumFish();
		SmallFish small = new SmallFish();

		driver.assignRoots(big, null, null);
		// ensure that the fish objects are being added in the correct order
		assertTrue(driver.roots[1] == null);
		assertTrue(driver.roots[2] == null);
		assertFalse(driver.roots[0] == null);

		driver.assignRoots(big, medium, small);

		assertTrue(driver.roots[0] == big);

		assertTrue(driver.roots[1] == medium);

		assertTrue(driver.roots[2] == small);

		assertFalse(driver.roots[0] == small);
	}

	/**
	 * garbageCollectionTest() - Tests the garbage collection method. 9 objects
	 * are passed into the tospace heap. 5 are referenced to each other. Then
	 * the garbage collection method is called and the tospace is tested to
	 * ensure only referenced objects are left
	 * 
	 */
	@Test
	public void garbageCollectionTest() {

		BigFish B1 = new BigFish();
		BigFish B2 = new BigFish();
		BigFish B3 = new BigFish();

		MediumFish M1 = new MediumFish();
		MediumFish M2 = new MediumFish();
		MediumFish M3 = new MediumFish();

		SmallFish S1 = new SmallFish();
		SmallFish S2 = new SmallFish();
		SmallFish S3 = new SmallFish();

		driver.tospace.add(B1);
		driver.tospace.add(B2);
		driver.tospace.add(B3);
		driver.tospace.add(M1);
		driver.tospace.add(M2);
		driver.tospace.add(M3);
		driver.tospace.add(S1);
		driver.tospace.add(S2);
		driver.tospace.add(S3);

		driver.assignRoots(B1, null, null);

		driver.reference(B1, B2);
		driver.reference(B2, M2);
		driver.reference(M2, M3);
		driver.reference(M3, S1);

		assertTrue(driver.tospace.size() == 9);

		driver.garbageCollection();

		// check if tospace is only holding the 5 fish that are referenced
		assertTrue(driver.tospace.size() == 5);
		assertFalse(driver.tospace.size() == 9);

		// make sure that B1 is marked as visited
		assertTrue(B1.visited);
		// make sure that B3 is not
		assertFalse(B3.visited);
		// make sure that M2's references list only holds one object
		assertTrue(M2.references.size() == 1);
		// make sure that S1's reference list does not hold any objects
		assertFalse(S1.references.size() > 0);
		// test to see if the correct objects are now in the tospace list in the
		// correct order
		assertEquals(driver.tospace.get(0), B1);
		assertEquals(driver.tospace.get(4), S1);

		System.out.print("\n-----Heap--------");
		System.out.print("\n Size: " + driver.tospace.size());
		System.out.print("\n-------------");
		for (Fish fish : driver.tospace) {

			System.out.print("\n" + fish + ": visited: " + fish.visited);

		}
		System.out.print("\n-------------");

	}
}
