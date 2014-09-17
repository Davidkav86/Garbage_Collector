package garbagecollector;

import garbagecollector.model.BigFish;
import garbagecollector.model.Driver;
import garbagecollector.model.Fish;
import garbagecollector.model.MediumFish;
import garbagecollector.model.SmallFish;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * GarbageCollectorController Class - This class is the controller for the
 * garbage collection simulator GUI.
 * 
 * @author David Kavanagh
 */
public class GarbageCollectorController implements Initializable {

	@FXML
	private Button allocateBigFish;
	@FXML
	private Button allocateMediumFish;
	@FXML
	private Button allocateSmallFish;
	@FXML
	private Button nextPhase;
	@FXML
	private Button stepThrough;
	@FXML
	private Pane tospace = new Pane();
	@FXML
	private Pane fromspace = new Pane();
	@FXML
	private Pane handlePool = new Pane();
	@FXML
	private AnchorPane anchor = new AnchorPane();
	@FXML
	private Pane bigPane = new Pane();
	@FXML
	private Pane mediumPane = new Pane();
	@FXML
	private Pane smallPane = new Pane();
	@FXML
	private Pane referencePool = new Pane();
	@FXML
	private Pane coverPane = new Pane();
	@FXML
	private Text textArea = new Text();
	@FXML
	private Pane bigLocalVariable = new Pane();
	@FXML
	private Pane mediumLocalVariable = new Pane();
	@FXML
	private Pane smallLocalVariable = new Pane();
	@FXML
	private Circle drag = new Circle();

	private int createLinksCounter = 0;
	private int tospaceCounter = 0;
	private int fromspaceCounter = 0;
	private int handleCounter = 0;
	private int referencePoolCounter = 0;
	private int phase = 0;
	private int garbageCollectionSteps = 0;

	private double xTo = 0;
	private double yTo = 0;
	private double xFrom = 0;
	private double yFrom = 0;

	private Node object;

	private boolean populateLists = true;
	private boolean bigLocal = true;
	private boolean mediumLocal = true;
	private boolean smallLocal = true;

	private List<Node> tospaceChildren;

	private List<Node> fromspaceChildren;

	private List<Node> handlePoolChildren;

	private List<Pane> tospaceReferences = new ArrayList<Pane>();

	private List<Node> referencePoolObjects = new ArrayList<Node>();

	private List<Line> lines = new ArrayList<Line>();

	private List<ArrayList<Pane>> tospaceMemory = new ArrayList<ArrayList<Pane>>();

	private ArrayList<Integer> indexes = new ArrayList<>();

	private Pane[] localVariables = new Pane[3];

	private Driver driver = new Driver();

	/**
	 * handleAllocateBigFish() - Handles the event when the "Big Fish" button is
	 * pressed by the user
	 * 
	 * @param event
	 */
	@FXML
	private void handleAllocateBigFish(ActionEvent event) {

		// populate the necessary lists for the running of the program.
		// a boolean is used to ensure this is only carried out once when the
		// first button is pushed
		if (populateLists) {

			populateLists();
		}

		// allocate a big fish in the driver
		driver.allocateBigFish();
		// assign the space in the tospace memory location
		assignTospaceLocation(1);
		// add a big object to the reference pool
		addObjectToReferencePool(1);

	}

	/**
	 * handleAllocateMediumFish() - Handles the event when the "Medium Fish"
	 * button is pressed by the user
	 * 
	 * @param event
	 */
	@FXML
	private void handleAllocateMediumFish(ActionEvent event) {

		if (populateLists) {

			populateLists();
		}
		driver.allocateMeduimFish();

		assignTospaceLocation(2);

		addObjectToReferencePool(2);
	}

	/**
	 * handleAllocateSmallFish() - Handles the event when the "Small Fish"
	 * button is pressed by the user
	 * 
	 * @param event
	 */
	@FXML
	private void handleAllocateSmallFish(ActionEvent event) {

		if (populateLists) {

			populateLists();
		}
		driver.allocateSmallFish();

		assignTospaceLocation(3);

		addObjectToReferencePool(3);
	}

	/**
	 * handleStepThrough() - Handles the event when the "Step" button is pressed
	 * by the user
	 * 
	 * @param event
	 */
	@FXML
	private void handleStepThrough(ActionEvent event) {

		if (garbageCollectionSteps == 0) {

			stepOne();
			return;
		}

		if (garbageCollectionSteps == 1) {

			stepTwo();
			return;
		}

		if (garbageCollectionSteps == 2) {

			stepThree();
			return;
		}
		if (garbageCollectionSteps == 3) {

			populateFromspace();
			setText();
			stepThrough.setDisable(true);
			return;
		}

	}

	/**
	 * handleNextPhase() - Handles the action to take place when the
	 * "Next Phase" button is pressed by the user. A counter is used to tell
	 * which phase of the program the user is currently in
	 * 
	 * @param event
	 */
	@FXML
	private void handleNextPhase(ActionEvent event) {

		// phase 2
		if (phase == 0) {

			// disable the buttons that allow the user to allocate fish to the
			// memory areas
			allocateBigFish.setDisable(true);
			allocateMediumFish.setDisable(true);
			allocateSmallFish.setDisable(true);

			// pass both lists through the methods that will assign them event
			// listeners. This allows for the drag and drop feature implemented
			// in the program
			for (Node n : referencePoolObjects) {
				setUpDragAndDrop(n);
			}
			for (Pane p : localVariables) {
				localVariableDropSetup(p);
			}

			setText();
			phase++;
			System.out.print("\nPhase" + phase);
			return;
		}
		// for the third phase
		if (phase == 1) {

			coverPane.toFront();
			stepThrough.setVisible(true);
			stepThrough.toFront();
			setText();
			phase++;
			return;
		}

	}

	/**
	 * stepOne() - The first step of the garbage collection phase. This method
	 * calls the garbageCollection method in the driver, then gets the indexes
	 * from any referenced objects in the driver and sets the reference pool
	 * objects at those indexes to black. It also marks those objects as
	 * referenced by setting their id to the CSS class "referenced"
	 * 
	 */
	private void stepOne() {

		driver.garbageCollection();
		// get the indexes of the referenced objects from the driver
		indexes.addAll(driver.referenced);
		// any referenced object. Turn them black and set their id to
		// referenced
		for (int index : indexes) {
			((Shape) referencePoolObjects.get(index)).setFill(Color.BLACK);
			((Shape) referencePoolObjects.get(index)).setId("referenced");
		}
		garbageCollectionSteps++;

	}

	/**
	 * stepTwo() - Searches through the list of referencePoolObjects. Any object
	 * that is not marked as referenced by the stepOne method is made invisible
	 * to the user. Any lines that are coming from unreferenced objects are also
	 * made invisible to the user.
	 * 
	 */
	private void stepTwo() {
		// go through the list of reference pool objects
		for (Node n : referencePoolObjects) {

			double xCoord = 0;
			double yCoord = 0;

			// if an object is not marked as referenced, make it invisible
			if (!(n.getId() == "referenced")) {
				n.setVisible(false);
				xCoord = n.getLayoutX();
				yCoord = n.getLayoutY();
			}

			// any lines coming from unreferenced objects set invisible
			for (Line line : lines) {
				if (line.getStartX() == xCoord && line.getStartY() == yCoord) {
					line.setVisible(false);
				}
			}

		}

		deletePoolObjects();
		garbageCollectionSteps++;
	}

	/**
	 * stepThree() -
	 * 
	 */
	private void stepThree() {
		for (Node n : referencePoolObjects) {

			// if it is a referenced object
			if (n.getId() == "referenced") {
				// cast the node as a circle
				Circle circle = (Circle) n;

				if (circle.getRadius() == 35) {
					circle.setFill(Color.DODGERBLUE);
				}
				if (circle.getRadius() == 30) {
					circle.setFill(Color.RED);
				}
				if (circle.getRadius() == 20) {
					circle.setFill(Color.LIMEGREEN);
				}
			}
		}
		garbageCollectionSteps++;
	}

	/**
	 * deletePoolObjects() - Called in the stepTwo method. An ArrayList of the
	 * indexes of unreferenced objects is populated by passing in the
	 * unreferenced list from the driver. These indexes are then used to set the
	 * CSS of the appropriate panes in the handle pool and tospace back to its
	 * original colour. The reference lines at those indexes are also made
	 * invisible to the user
	 */
	private void deletePoolObjects() {

		// a list for the indexes of unreferenced objects
		ArrayList<Integer> unreferenced = driver.unreferenced;

		for (Node n : referencePoolObjects) {
			// get the index of the object from the list
			int objectIndex = referencePoolObjects.indexOf(n);
			// iterate through the list of indexes of referenced objects
			for (int index : unreferenced) {
				// if the object is not referenced
				if (objectIndex == index) {
					System.out.print("\nIndex: " + index);
					// set the CSS id back to being a pool object
					handlePoolChildren.get(index).setId("poolObjects");
					// set the reference lines to invisible
					tospaceReferences.get(index).setVisible(false);
					// iterate through the tospaceMemory list
					for (ArrayList<Pane> panes : tospaceMemory) {
						// if the list is held at the same index as an
						// unreferenced object
						if (tospaceMemory.indexOf(panes) == index) {
							// set all the id of the panes held in that list
							// back to "poolObjects"
							for (Pane pane : panes) {
								pane.setId("poolObjects");
							}
						}
					}

				}
			}
		}
	}

	/**
	 * populateFromspace() - Populates the memory space in fromspace. This is
	 * done by iterating over the tospace memory space. When a space in tospace
	 * in found to be a certain colour, that colour is added to the fromspace. A
	 * counter is used to ensure there are no gaps left in the fromspace area.
	 * Once that operation is complete, the entire tospace is set to its
	 * original colour and the reference lines from the tospace are set to
	 * invisible
	 * 
	 */
	private void populateFromspace() {

		int fromspaceCounter = 0;

		for (int i = 0; i < tospaceChildren.size(); i++) {
			// if the the pane in the tospace is coloured
			if (tospaceChildren.get(i).getId() != "poolObjects") {
				// set the colour in the fromspace to the same as the tospace
				fromspaceChildren.get(fromspaceCounter).setId(
						tospaceChildren.get(i).getId());
				fromspaceCounter++;
			}
		}

		// set all the tospace reference lines to invisible
		for (Pane pane : tospaceReferences) {
			pane.setVisible(false);
		}

		// set CSS id of all the panes in the tospace back to "poolObjects"
		for (int i = 0; i < tospace.getChildren().size(); i++) {

			tospace.getChildren().get(i).setId("poolObjects");
		}

		// assign the reference lines between the handle pool and the fromspace
		for (int i = 0; i < handlePoolChildren.size(); i++) {
			setFromspaceReferenceLines(i);
		}
	}

	/**
	 * addObjectToReferencePool() - Adds the objects to the reference pool. Each
	 * of the objects is added as the allocate fish methods are called. A Circle
	 * object is created and placed onto the reference pool in the GUI. The
	 * radius and colour of the circle and dependent on which size of fish is
	 * being added to memory,
	 * 
	 * @param type
	 */
	private void addObjectToReferencePool(int type) {

		if (tospaceCounter >= 15) {
			return;
		}

		double radius = 0;
		double XLayout = 0;
		double YLayout = 0;

		if (referencePoolCounter == 0) {
			XLayout = 387;
			YLayout = 46;
		} else if (referencePoolCounter == 1) {
			XLayout = 392;
			YLayout = 299;
		} else if (referencePoolCounter == 2) {
			XLayout = 301;
			YLayout = 164;
		} else if (referencePoolCounter == 3) {
			XLayout = 520;
			YLayout = 227;
		} else if (referencePoolCounter == 4) {
			XLayout = 440;
			YLayout = 202;
		} else if (referencePoolCounter == 5) {
			XLayout = 219;
			YLayout = 294;
		} else if (referencePoolCounter == 6) {
			XLayout = 743;
			YLayout = 297;
		} else if (referencePoolCounter == 7) {
			XLayout = 612;
			YLayout = 49;
		} else if (referencePoolCounter == 8) {
			XLayout = 193;
			YLayout = 45;
		} else if (referencePoolCounter == 9) {
			XLayout = 696;
			YLayout = 159;
		} else if (referencePoolCounter == 10) {
			XLayout = 803;
			YLayout = 204;
		} else if (referencePoolCounter == 11) {
			XLayout = 589;
			YLayout = 134;
		} else if (referencePoolCounter == 12) {
			XLayout = 472;
			YLayout = 307;
		} else if (referencePoolCounter == 13) {
			XLayout = 182;
			YLayout = 183;
		} else if (referencePoolCounter == 14) {
			XLayout = 817;
			YLayout = 38;
		}

		// create the circle
		Circle object = new Circle();

		// pass the circle into the children of the referencePool so it can be
		// seen in the UI
		referencePool.getChildren().add(object);

		// set the radius and the colour of the object before it is placed on
		// the referencePool
		if (type == 1) {
			radius = 35;
			object.setFill(Color.DODGERBLUE);
		} else if (type == 2) {
			radius = 30;
			object.setFill(Color.RED);

		} else if (type == 3) {

			radius = 20;
			object.setFill(Color.LIMEGREEN);
		}

		object.setRadius(radius);
		object.setLayoutX(XLayout);
		object.setLayoutY(YLayout);
		object.setStroke(Color.BLACK);
		object.setStrokeWidth(3);
		object.setVisible(true);
		object.toFront();

		referencePoolObjects.add(object);

		referencePoolCounter++;

	}

	/**
	 * assignTospaceLocation() - Assigns the space in the pane that is
	 * representing the tospace memory location. The amount of panes used and
	 * the colour of the panes is dictated by the type of fish that has been
	 * selected for allocation
	 * 
	 * @param allocate
	 *            - an integer representing the type of fish being added to the
	 *            tospace
	 */
	private void assignTospaceLocation(int allocate) {

		ArrayList<Pane> panes = new ArrayList<Pane>();

		switch (allocate) {

		case 1:
			int count1 = tospaceCounter;

			// a big fish will take up 3 panes of memory space
			for (int i = count1; i < (count1 + 3); i++) {

				// ensure that the tospaceChildren list does not go out of
				// bounds
				if (tospaceCounter < tospaceChildren.size()) {
					// set the pane to the style big fish style class
					tospaceChildren.get(i).setId("bigFish");
					// add the pane to the panes list. This is used to remove
					// allocated memory in the garbage collection phase
					Pane pane = (Pane) tospaceChildren.get(i);
					panes.add(pane);
				} else {
					break;
				}

				tospaceCounter++;
			}
			// tell the handle pool that a bigFish was added to the tospace
			assignHandleLocation(1);
			// set the coordinates and style class for the reference lines
			// between tospace and handle pool
			setReferenceLinesCoordinates("bigFish");
			// add the list of panes to the tospaceMemory list
			tospaceMemory.add(panes);
			break;
		case 2:
			int count2 = tospaceCounter;
			for (int i = count2; i < (count2 + 2); i++) {

				if (tospaceCounter < tospaceChildren.size()) {

					tospaceChildren.get(i).setId("mediumFish");

					Pane pane = (Pane) tospaceChildren.get(i);
					panes.add(pane);
				} else {
					break;
				}

				tospaceCounter++;
			}
			assignHandleLocation(2);

			setReferenceLinesCoordinates("mediumFish");

			tospaceMemory.add(panes);
			break;
		case 3:
			int count3 = tospaceCounter;
			for (int i = count3; i < (count3 + 1); i++) {

				if (tospaceCounter < tospaceChildren.size()) {
					tospaceChildren.get(i).setId("smallFish");

					Pane pane = (Pane) tospaceChildren.get(i);
					panes.add(pane);
				} else {
					break;
				}

				tospaceCounter++;
			}

			assignHandleLocation(3);

			setReferenceLinesCoordinates("smallFish");

			tospaceMemory.add(panes);
			break;
		}
	}

	/**
	 * setReferenceLines() - Creates the lines that connects the tospace memory
	 * area to the handle pool. These lines are created when the user decides
	 * which size fish will be placed into memory. A pane is created is placed
	 * on the AnchorPane according to x and y coordinates and the angle of
	 * rotation. The style class is also set accordingly
	 * 
	 * @param xLayout
	 *            - The x layout of the pane
	 * @param yLayout
	 *            - The y layout of the pane
	 * @param style
	 *            - The style class of the pane
	 * @param rotation
	 *            - The rotation of the pane
	 * @param type
	 *            - An int representing the type of fish
	 */
	private void setReferenceLines(double xLayout, double yLayout,
			String style, double rotation, int prefSizeX, int prefSizeY) {

		Pane pane = new Pane();

		anchor.getChildren().add(pane);

		pane.setVisible(true);
		pane.toFront();
		pane.setRotate(rotation);
		pane.setPrefSize(prefSizeX, prefSizeY);
		pane.setLayoutX(xLayout);
		pane.setLayoutY(yLayout);
		pane.setId(style);

		tospaceReferences.add(pane);

	}

	/**
	 * assignHandleLocation() - Assigns the memory space in the handle pool. The
	 * panes are coloured appropriately depending on which of the fish objects
	 * has been added to the.
	 * 
	 * @param allocate
	 */
	private void assignHandleLocation(int allocate) {

		// Test to ensure that only 15 objects can be placed in the reference
		// pool
		if (tospaceCounter > 15) {
			return;
		}

		switch (allocate) {
		case 1:
			handlePoolChildren.get(handleCounter).setId("bigFish");
			handleCounter++;
			break;

		case 2:
			handlePoolChildren.get(handleCounter).setId("mediumFish");
			handleCounter++;
			break;

		case 3:
			handlePoolChildren.get(handleCounter).setId("smallFish");
			handleCounter++;
			break;
		}

	}

	/**
	 * assignLinks() - Passes the two objects selected by the user into the
	 * assignLocalVariables method and/ or the assignReferences method. If
	 * either of those methods returns true, this method will also return true.
	 * This method is called from the createLinks method. If this method returns
	 * true, lines will be drawn between the two selected objects on the
	 * reference pool
	 * 
	 * @param to
	 *            - The object that has been dragged.
	 * @param From
	 *            - The object that the to Node has been dropped on
	 * @return boolean
	 */
	private boolean assignLinks(Node to, Node from) {

		// if the user is assigning local variables
		if (assignLocalVariables(to, from)) {
			return true;
		}

		// the user is assigning references
		if (assignReferences(to, from)) {
			return true;
		}

		return false;
	}

	/**
	 * assignReferences() - Assigns the references from one object to another.
	 * Two Node objects are passed in as parameters. The Node that the reference
	 * is going from is passed into the test radius method to distinguish what
	 * size of object it is. The index for that object in the
	 * referencePoolObjects list is retrieved and used to get the equivalent
	 * fish object in the drivers tospace list. A reference is then made with
	 * "to" Node, if it meets the proper requirements
	 * 
	 * 
	 * @param to
	 *            - The object that the reference is going to
	 * @param from
	 *            - The object that the reference is coming from
	 * @return boolean
	 */
	private boolean assignReferences(Node to, Node from) {

		//
		// -------- Big Fish ------------
		//
		// if the object on the reference pool is representing a big fish
		if (testRadius(from) == 35) {

			// find the index of that object in the referencePoolObjects
			// list
			int indexFrom = referencePoolObjects.indexOf(from);

			// get the equivalent object from the tospace list in the driver
			BigFish referenceFrom = (BigFish) driver.tospace.get(indexFrom);

			if (testRadius(to) == 35) {

				// get the index of the object that it wishes to reference
				int indexTo = referencePoolObjects.indexOf(to);

				// get the equivelent fish from the tospace in the driver
				BigFish referenceTo = (BigFish) driver.tospace.get(indexTo);

				// if this fish already holds a reference to a fish of this size
				// finish the method by returning false
				if (referenceFrom.bigRef) {
					return false;
				} else {

					// set the references in the driver
					if (driver.reference(referenceFrom, referenceTo)) {

						return true;
					} else {
						return false;
					}
				}

			}
			if (testRadius(to) == 30) {

				int indexTo = referencePoolObjects.indexOf(to);

				MediumFish referenceTo = (MediumFish) driver.tospace
						.get(indexTo);

				if (referenceFrom.mediumRef) {
					return false;
				} else {

					// set the references in the driver
					if (driver.reference(referenceFrom, referenceTo)) {

						return true;
					} else {
						return false;
					}
				}

			}
			if (testRadius(to) == 20) {

				int indexTo = referencePoolObjects.indexOf(to);

				SmallFish referenceTo = (SmallFish) driver.tospace.get(indexTo);

				if (referenceFrom.smallRef) {
					return false;
				} else {

					// set the references in the driver
					if (driver.reference(referenceFrom, referenceTo)) {

						return true;
					} else {
						return false;
					}
				}
			}

			return false;
		}
		//
		// -------- Medium Fish ------------
		//
		if (testRadius(from) == 30) {

			int indexFrom = referencePoolObjects.indexOf(from);

			MediumFish referenceFrom = (MediumFish) driver.tospace
					.get(indexFrom);

			// if the user tries to reference a big object from a medium object,
			// do nothing
			if (testRadius(to) == 35) {
				return false;
			}

			if (testRadius(to) == 30) {

				int indexTo = referencePoolObjects.indexOf(to);

				MediumFish referenceTo = (MediumFish) driver.tospace
						.get(indexTo);

				if (referenceFrom.mediumRef) {
					return false;
				} else {

					// set the references in the driver
					if (driver.reference(referenceFrom, referenceTo)) {

						return true;
					} else {
						return false;
					}
				}

			}
			if (testRadius(to) == 20) {

				int indexTo = referencePoolObjects.indexOf(to);

				SmallFish referenceTo = (SmallFish) driver.tospace.get(indexTo);

				if (referenceFrom.smallRef) {
					return false;
				} else {

					// set the references in the driver
					if (driver.reference(referenceFrom, referenceTo)) {

						return true;
					} else {
						return false;
					}
				}
			}

			return false;
		}
		//
		// -------- Small Fish ------------
		//
		if (testRadius(from) == 20) {

			int indexFrom = referencePoolObjects.indexOf(from);

			SmallFish referenceFrom = (SmallFish) driver.tospace.get(indexFrom);

			// if the user tries to reference a big object from a small object,
			// do nothing
			if (testRadius(to) == 35) {
				return false;
			}
			// if the user tries to reference a medium object from a small
			// object,
			// do nothing
			if (testRadius(to) == 30) {
				return false;
			}
			if (testRadius(to) == 20) {

				int indexTo = referencePoolObjects.indexOf(to);

				SmallFish referenceTo = (SmallFish) driver.tospace.get(indexTo);

				if (referenceFrom.smallRef) {
					return false;
				} else {

					// set the references in the driver
					if (driver.reference(referenceFrom, referenceTo)) {

						return true;
					} else {
						return false;
					}
				}
			}

			return false;
		}

		return false;
	}

	/**
	 * assignLocalVariables() - Assigns the local variables. An object is
	 * dragged over to a local variable fish on the left of the screen. This
	 * method will assign the objects that have been dragged as local variables
	 * in the driver and ensure that the line is drawn between the local
	 * variable and the object to show that it is now referenced by a local
	 * variable
	 * 
	 * @param to
	 *            - The object that is looking to be referenced by a local
	 *            variable
	 * @param from
	 *            - The local variable
	 * @return boolean
	 */
	private boolean assignLocalVariables(Node to, Node from) {

		// if the user has dragged a pool object to a local variable
		if (from.equals(bigLocalVariable)) {
			// use the testRadius to distinguish between the different
			// object
			// and use a boolean to ensure that a local variable can only be
			// assigned once.
			if (testRadius(to) == 35 && bigLocal) {

				// find the index of that object in the referencePoolObjects
				// list
				int index = referencePoolObjects.indexOf(to);

				// get the equivalent object from the tospace list in the driver
				Fish fish = driver.tospace.get(index);

				// set the reference in the driver
				driver.assignRoots(fish, null, null);

				// set the coordinates that the line should be drawn from
				xFrom = 109;
				yFrom = 97;

				// set bigLocal to false to ensure a local variable is only
				// assigned once
				bigLocal = false;
				return true;
			}
			return false;
		}
		if (from.equals(mediumLocalVariable)) {
			if (testRadius(to) == 30 && mediumLocal) {

				int index = referencePoolObjects.indexOf(to);

				Fish fish = driver.tospace.get(index);

				driver.assignRoots(null, fish, null);

				System.out.print("\n Medium index: "
						+ referencePoolObjects.indexOf(to) + "\n");

				xFrom = 103;
				yFrom = 178;

				mediumLocal = false;
				return true;
			}
			return false;
		}
		if (from.equals(smallLocalVariable)) {
			if (testRadius(to) == 20 && smallLocal) {

				int index = referencePoolObjects.indexOf(to);

				Fish fish = driver.tospace.get(index);

				driver.assignRoots(null, null, fish);

				System.out.print("\n Small index: "
						+ referencePoolObjects.indexOf(to) + "\n");

				xFrom = 97;
				yFrom = 249;

				smallLocal = false;
				return true;
			}
			return false;
		}

		return false;
	}

	/**
	 * testRadius() - Returns the radius of the circle that is passed in as a
	 * node. It does this by casting the node as a circle and getting its
	 * radius. this method is important for the program to distinguish between
	 * the circles of different sizes as the circles represent the different
	 * size fish on the reference pool
	 * 
	 * @param n
	 *            - A Node object that will be Circle
	 * @return radius - The radius of the circle
	 */
	private double testRadius(Node n) {

		// if the node being passed in is one of the local variables
		if (n.equals(bigLocalVariable) || n.equals(mediumLocalVariable)
				|| n.equals(smallLocalVariable)) {
			return 0;
		}
		try {
			Circle circle = (Circle) n;

			double radius = circle.getRadius();
			return radius;
		} catch (ClassCastException e) {
			System.out.print("Class Cast Exception " + e);
		}

		return 0;
	}

	/**
	 * createLinks() - Creates the links between the objects in the reference
	 * pool. The drag and drop function is used here. The object that the
	 * reference to going to is the object that is dragged. That object is
	 * passed in first and its x and y coordinates are stored. Once the mouse is
	 * released on the second object its x and y coordinates are taken and a
	 * line is placed between the two objects
	 * 
	 * @param node
	 *            - The Node being passed into the method
	 */
	private void createLinks(Node node) {

		// a counter is used so the first object, ie the one that is dragged
		// only gets access to this part of the method. That objects x and y
		// coordinates and taken and will be used to create the line
		if (createLinksCounter == 0) {

			object = node;
			xTo = node.getLayoutX();
			yTo = node.getLayoutY();
			createLinksCounter++;
			return;
		}

		// if the x and y coordinates of the object that the line is going from
		// have not already been set by a previous method
		if (xFrom == 0 && yFrom == 0) {
			xFrom = node.getLayoutX();
			yFrom = node.getLayoutY();
		}

		// make sure an object cannot draw a line to itself
		if (xTo == xFrom) {
			return;
		}

		if (assignLinks(object, node)) {

			Line line = new Line();

			line.setStrokeWidth(3);

			referencePool.getChildren().add(line);

			line.setStartX(xFrom);
			line.setStartY(yFrom);

			line.setEndX(xTo);
			line.setEndY(yTo);
			line.setVisible(true);
			line.setStroke(Color.BLACK);

			lines.add(line);

		}

		// set the counter and the four coordinates back to 0 before the next
		// link is created
		xTo = 0;
		yTo = 0;
		xFrom = 0;
		yFrom = 0;
		createLinksCounter = 0;

	}

	/**
	 * setText() - Sets the text for the GUI. The text will hold information
	 * telling the user what tasks they have available to them. The text changes
	 * each time the user opts to go to the next phase of the simulation.
	 * 
	 */
	private void setText() {

		if (phase == 0) {
			textArea.setText(" ---- Phase 2: Assign Local Variables and References ---- "
					+ "\n"
					+ "\nFor this phase of the simulator you are asked to assign local variables "
					+ "and references between the reference pool objects.You can do this using "
					+ "drag and drop."
					+ "\n -- STEP 1: ASSIGN LOCAL VARIABLES --\n"
					+ "- Drag an object over to the local variable you want it to reference. "
					+ " The object must be of the same type as the local variable. "
					+ " Once an object is picked to reference a local variable, it cannot be changed."
					+ "\n -- STEP 2: ASSIGN REFERENCES -- \n"
					+ "- Drag an object over to a second object that you want to reference it."
					+ " Remember... An object can only hold one reference to an object of the same "
					+ "size and one reference to an object that is smaller than it");
		}
		if (phase == 1) {
			textArea.setText(" ------ Phase 3: Garbage Collection ------ "
					+ "\n"
					+ "---This is the garbage collection phase of the simulation---"
					+ " "
					+ "-In this phase you are asked to go through the steps involved in the garbage collection.  "
					+ " "
					+ "-Simply click on the 'step through' button to step through the garbage collection simulation "
					+ "\n"
					+ "-Here you will be able to see the referenced objects remain in the object pool,"
					+ "while the unreferenced objects will be removed."
					+ ""
					+ "-The memory space being occupied by unreferenced objects in the tospace memory area and the"
					+ "handle pool will be made available. The tospace will then be cleared and copied over to the"
					+ "fromspace memory location");
		}
		if (phase == 2) {
			textArea.setText(""
					+ "-----------------------------------------------"
					+ "\n  -------- END OF SIMULATION -------"
					+ "\n-----------------------------------------------");

		}
	}

	/**
	 * populateLists() - Populates the lists containing the panels inside each
	 * of the memory locations.Each of the memory locations are a JavaFX Pane
	 * object with 15 individual Panes inside that are its children. The
	 * getChildren method is called on each of the Panes so a list of their
	 * children Panes are passed into a list where they can be accessed and
	 * manipulated. A boolean is used to ensure this method is only called once,
	 * whenever the first fish is allocated.
	 * 
	 * 
	 */
	private void populateLists() {

		tospaceChildren = tospace.getChildren();
		fromspaceChildren = fromspace.getChildren();
		handlePoolChildren = handlePool.getChildren();
		populateLists = false;

		localVariables[0] = bigLocalVariable;
		localVariables[1] = mediumLocalVariable;
		localVariables[2] = smallLocalVariable;

		coverPane.toBack();
	}

	/**
	 * setUpDragAndDrop() - Adds an event handler to the objects that are on the
	 * reference pool. This sets up the objects for a drag and drop function.
	 * Both objects are passed into the createLinks() method which will draw the
	 * lines and create the references between the objects.
	 * 
	 * @param n
	 *            - the node that is being passed into the method
	 * 
	 */
	private void setUpDragAndDrop(final Node n) {
		n.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				/* drag was detected, start drag-and-drop gesture */
				System.out.println("onDragDetected");

				System.out.println("\n X: " + n.getLayoutX());

				/* allow any transfer mode */
				Dragboard db = drag.startDragAndDrop(TransferMode.ANY);

				/* put a string on dragboard */
				ClipboardContent content = new ClipboardContent();
				content.putString("Place on object");
				db.setContent(content);

				// createLinks(n);
				event.consume();
			}
		});

		n.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				System.out.println("onDragOver");

				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				if (event.getGestureSource() != n
						&& event.getDragboard().hasString()) {
					/* allow for both copying and moving, whatever user chooses */
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}

				event.consume();
			}
		});

		n.setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* mouse moved away, remove the graphical cues */

				createLinks(n);

				event.consume();
			}
		});

	}

	/**
	 * setReferenceLinesCoordinates() - Sets the coordinates of the reference
	 * lines dependent on which fish object has been added to the tospace in
	 * what order. Each of the lines are created dynamically by
	 * theSetReferenceLines() method. This method makes the decision as to where
	 * the line should go and what colour that it should be. A string is taken
	 * in as a parameter to distinguish between the three different fish. The
	 * handleCounter is used to tell what area in the handle pool is going to be
	 * referenced
	 * 
	 * @param styleClass
	 *            - A String that represents the style class for the pane
	 */
	private void setReferenceLinesCoordinates(String styleClass) {

		int count = handleCounter;

		switch (count) {

		case 1:
			if (tospaceCounter == 1) {

				setReferenceLines(345, 59, styleClass, 0, 62, 4);

			} else if (tospaceCounter == 2) {

				setReferenceLines(344, 69, styleClass, 163, 65, 4);

			} else if (tospaceCounter == 3) {

				setReferenceLines(336, 77, styleClass, 143, 81, 4);

			}
			break;

		case 2:
			if (tospaceCounter == 2) {

				setReferenceLines(345, 80, styleClass, 0, 62, 4);
			} else if (tospaceCounter == 3) {

				setReferenceLines(344, 87, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 4) {

				setReferenceLines(334, 96, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 5) {

				setReferenceLines(332, 109, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 6) {

				setReferenceLines(324, 121, styleClass, 128, 106, 4);
			}

			break;

		case 3:
			if (tospaceCounter == 3) {

				setReferenceLines(345, 101, styleClass, 0, 62, 4);
			} else if (tospaceCounter == 4) {

				setReferenceLines(344, 106, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 5) {

				setReferenceLines(336, 114, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 6) {

				setReferenceLines(332, 128, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 7) {

				setReferenceLines(324, 137, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 8) {

				setReferenceLines(313, 150, styleClass, 120, 127, 4);
			} else if (tospaceCounter == 9) {

				setReferenceLines(303, 160, styleClass, 116, 147, 4);
			}
			break;

		case 4:

			if (tospaceCounter == 4) {

				setReferenceLines(345, 122, styleClass, 0, 62, 4);
			} else if (tospaceCounter == 5) {

				setReferenceLines(343, 132, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 6) {

				setReferenceLines(336, 138, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 7) {

				setReferenceLines(331, 149, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 8) {

				setReferenceLines(323, 155, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 9) {

				setReferenceLines(313, 165, styleClass, 120, 127, 4);
			} else if (tospaceCounter == 10) {

				setReferenceLines(301, 180, styleClass, 116, 149, 4);
			} else if (tospaceCounter == 11) {

				setReferenceLines(296, 192, styleClass, 114, 163, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(286, 200, styleClass, 112, 182, 4);
			}
			break;

		case 5:
			if (tospaceCounter == 5) {

				setReferenceLines(345, 142, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 6) {

				setReferenceLines(343, 149, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 7) {

				setReferenceLines(336, 158, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 8) {

				setReferenceLines(331, 171, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 9) {

				setReferenceLines(323, 181, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 10) {

				setReferenceLines(312, 190, styleClass, 120, 127, 4);
			} else if (tospaceCounter == 11) {

				setReferenceLines(302, 197, styleClass, 116, 149, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(295, 207, styleClass, 114, 163, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(286, 221, styleClass, 112, 182, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(278, 230, styleClass, 109, 198, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(263, 241, styleClass, 107, 227, 4);
			}
			break;

		case 6:
			if (tospaceCounter == 6) {

				setReferenceLines(344, 161, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 7) {

				setReferenceLines(344, 169, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 8) {

				setReferenceLines(336, 176, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 9) {

				setReferenceLines(332, 192, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 10) {

				setReferenceLines(323, 201, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 11) {

				setReferenceLines(312, 207, styleClass, 120, 127, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(302, 214, styleClass, 116, 149, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(295, 228, styleClass, 114, 163, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(286, 243, styleClass, 112, 182, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(278, 255, styleClass, 109, 198, 4);
			}
			break;

		case 7:
			if (tospaceCounter == 7) {

				setReferenceLines(344, 183, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 8) {

				setReferenceLines(344, 192, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 9) {

				setReferenceLines(336, 201, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 10) {

				setReferenceLines(332, 212, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 11) {

				setReferenceLines(323, 221, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(312, 231, styleClass, 120, 127, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(301, 239, styleClass, 116, 149, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(295, 259, styleClass, 114, 163, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(287, 263, styleClass, 112, 182, 4);
			}
			break;

		case 8:
			if (tospaceCounter == 8) {

				setReferenceLines(344, 204, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 9) {

				setReferenceLines(343, 212, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 10) {

				setReferenceLines(336, 223, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 11) {

				setReferenceLines(332, 235, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(323, 224, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(313, 254, styleClass, 120, 127, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(301, 264, styleClass, 116, 149, 4);
			} else if (tospaceCounter >= 15) {

				setReferenceLines(295, 274, styleClass, 114, 163, 4);
			}
			break;

		case 9:
			if (tospaceCounter == 9) {

				setReferenceLines(344, 224, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 10) {

				setReferenceLines(343, 237, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 11) {

				setReferenceLines(336, 243, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(332, 257, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(323, 269, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(313, 277, styleClass, 120, 127, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(301, 285, styleClass, 116, 149, 4);
			}
			break;

		case 10:
			if (tospaceCounter == 10) {

				setReferenceLines(344, 245, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 11) {

				setReferenceLines(343, 256, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(336, 260, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(332, 276, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(323, 286, styleClass, 128, 106, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(313, 295, styleClass, 120, 127, 4);
			}
			break;

		case 11:
			if (tospaceCounter == 11) {

				setReferenceLines(344, 266, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 12) {

				setReferenceLines(343, 275, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(336, 282, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(332, 295, styleClass, 138, 89, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(323, 309, styleClass, 128, 106, 4);
			}
			break;

		case 12:
			if (tospaceCounter == 12) {

				setReferenceLines(344, 287, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 13) {

				setReferenceLines(343, 298, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(336, 306, styleClass, 143, 81, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(332, 318, styleClass, 138, 89, 4);
			}
			break;

		case 13:
			if (tospaceCounter == 13) {

				setReferenceLines(344, 310, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 14) {

				setReferenceLines(343, 316, styleClass, 163, 65, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(336, 327, styleClass, 143, 81, 4);
			}
			break;

		case 14:
			if (tospaceCounter == 14) {

				setReferenceLines(344, 330, styleClass, 0, 64, 4);
			} else if (tospaceCounter == 15) {

				setReferenceLines(343, 343, styleClass, 163, 65, 4);
			}
			break;

		case 15:
			setReferenceLines(344, 352, styleClass, 0, 64, 4);
			break;
		}

	}

	/**
	 * setFromspaceReferenceLines() - Sets the coordinates of the reference
	 * lines dependent on which fish object has been added to the fromspace in
	 * what order. Each of the lines are created dynamically by
	 * theSetReferenceLines() method. This method makes the decision as to where
	 * the line should go and what colour that it should be. A string is taken
	 * in as a parameter to distinguish between the three different fish. The
	 * handleCounter is used to tell what area in the handle pool is going to be
	 * referenced
	 * 
	 * @param handleIndex
	 *            -
	 */
	private void setFromspaceReferenceLines(int handleIndex) {

		// get the CSS style class for the pane in the handlePool
		String styleClass = handlePoolChildren.get(handleIndex).getId();

		// if it is not assigned any colour, get out of this method
		if (styleClass == "poolObjects") {
			return;
		}

		// add to the fromspace counter. The amount is dependent on the size of
		// the memory needed
		if (styleClass == "bigFish") {
			fromspaceCounter += 3;
		} else if (styleClass == "mediumFish") {
			fromspaceCounter += 2;
		} else if (styleClass == "smallFish") {
			fromspaceCounter += 1;
		}

		switch (handleIndex) {

		case 0:
			if (fromspaceCounter == 1) {

				setReferenceLines(604, 59, styleClass, 0, 62, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(604, 67, styleClass, 198, 65, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(596, 77, styleClass, 34, 81, 4);

			}
			break;

		case 1:
			if (fromspaceCounter == 1) {

				setReferenceLines(603, 69, styleClass, 343, 67, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(605, 76, styleClass, 0, 63, 4);
			} else if (fromspaceCounter == 3) {

				setReferenceLines(605, 85, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 4) {

				setReferenceLines(597, 95, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(591, 109, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(583, 117, styleClass, 49, 106, 4);
			}

			break;

		case 2:
			if (fromspaceCounter == 1) {

				setReferenceLines(595, 77, styleClass, 152, 81, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(603, 89, styleClass, 346, 67, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(604, 95, styleClass, 0, 62, 4);
			} else if (fromspaceCounter == 4) {

				setReferenceLines(604, 105, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(597, 115, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(591, 125, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(583, 137, styleClass, 49, 106, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(572, 148, styleClass, 56, 124, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(564, 160, styleClass, 61, 140, 4);
			}
			break;

		case 3:

			if (fromspaceCounter == 1) {

				setReferenceLines(590, 88, styleClass, 137, 91, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(595, 98, styleClass, 152, 81, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(603, 110, styleClass, 346, 67, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(604, 118, styleClass, 0, 62, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(605, 128, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(597, 138, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(591, 152, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(583, 160, styleClass, 49, 106, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(572, 171, styleClass, 56, 124, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(564, 183, styleClass, 61, 140, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(555, 196, styleClass, 65, 163, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(545, 206, styleClass, 68, 182, 4);
			}
			break;
		case 4:
			if (fromspaceCounter == 1) {

				setReferenceLines(581, 97, styleClass, 129, 108, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(589, 108, styleClass, 137, 91, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(594, 118, styleClass, 152, 81, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(602, 130, styleClass, 346, 67, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(602, 193, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(604, 149, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(596, 161, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(593, 173, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(585, 182, styleClass, 49, 106, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(574, 191, styleClass, 56, 124, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(566, 204, styleClass, 61, 140, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(557, 217, styleClass, 65, 163, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(547, 227, styleClass, 68, 182, 4);
				// --------------------------------------sort out--------
			} else if (fromspaceCounter == 14) {

				setReferenceLines(278, 230, styleClass, 109, 198, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(263, 241, styleClass, 107, 227, 4);
			}
			break;

		case 5:
			if (fromspaceCounter == 1) {

				setReferenceLines(573, 108, styleClass, 125, 124, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(581, 118, styleClass, 129, 108, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(589, 129, styleClass, 137, 91, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(602, 139, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(602, 159, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(603, 159, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(603, 169, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(594, 118, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(591, 113, styleClass, 44, 89, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(583, 202, styleClass, 49, 106, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(572, 211, styleClass, 56, 125, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(564, 224, styleClass, 61, 140, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(555, 237, styleClass, 65, 163, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(545, 247, styleClass, 68, 182, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(534, 275, styleClass, 70, 200, 4);
			}
			break;

		case 6:
			if (fromspaceCounter == 1) {

				setReferenceLines(564, 121, styleClass, 118, 142, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(574, 130, styleClass, 125, 124, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(582, 140, styleClass, 129, 108, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(590, 151, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(595, 161, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(603, 173, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(605, 181, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(606, 192, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(595, 203, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(592, 215, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(584, 224, styleClass, 46, 106, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(573, 233, styleClass, 56, 125, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(565, 246, styleClass, 61, 140, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(556, 259, styleClass, 65, 163, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(546, 269, styleClass, 68, 182, 4);
			}
			break;

		case 7:
			if (fromspaceCounter == 1) {

				setReferenceLines(553, 127, styleClass, 116, 163, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(563, 139, styleClass, 118, 142, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(573, 148, styleClass, 125, 124, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(581, 158, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(589, 169, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(594, 179, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(602, 191, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(604, 199, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(605, 209, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(594, 221, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(591, 233, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(589, 242, styleClass, 49, 106, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(572, 251, styleClass, 56, 125, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(564, 264, styleClass, 61, 140, 4);
			} else if (fromspaceCounter >= 15) {

				setReferenceLines(555, 277, styleClass, 65, 163, 4);
			}
			break;

		case 8:
			if (fromspaceCounter == 1) {

				setReferenceLines(543, 143, styleClass, 112, 182, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(553, 151, styleClass, 116, 163, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(563, 163, styleClass, 118, 142, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(573, 172, styleClass, 125, 124, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(581, 182, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(589, 193, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(594, 203, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(602, 215, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(604, 223, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(605, 233, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(594, 245, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(591, 257, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(583, 266, styleClass, 49, 106, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(572, 275, styleClass, 56, 125, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(564, 288, styleClass, 61, 140, 4);
			}
			break;

		case 9:
			if (fromspaceCounter == 1) {

				setReferenceLines(535, 151, styleClass, 109, 200, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(543, 165, styleClass, 112, 182, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(553, 173, styleClass, 116, 163, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(563, 187, styleClass, 118, 142, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(573, 194, styleClass, 125, 124, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(581, 204, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(589, 215, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(594, 225, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(602, 237, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(604, 245, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(605, 255, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(594, 267, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(591, 279, styleClass, 44, 89, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(583, 288, styleClass, 49, 106, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(572, 297, styleClass, 124, 56, 4);
			}
			break;

		case 10:
			if (fromspaceCounter == 1) {

				setReferenceLines(525, 160, styleClass, 108, 219, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(535, 170, styleClass, 109, 200, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(543, 184, styleClass, 112, 182, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(553, 192, styleClass, 116, 163, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(563, 204, styleClass, 118, 142, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(573, 213, styleClass, 125, 124, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(581, 223, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(589, 234, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(594, 244, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(602, 256, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(604, 264, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(605, 274, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(594, 286, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(591, 298, styleClass, 44, 90, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(583, 307, styleClass, 49, 106, 4);
			}
			break;
		case 11:
			if (fromspaceCounter == 1) {

				setReferenceLines(516, 172, styleClass, 106, 240, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(525, 181, styleClass, 108, 219, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(535, 191, styleClass, 109, 200, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(543, 205, styleClass, 112, 182, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(553, 213, styleClass, 116, 163, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(563, 225, styleClass, 118, 142, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(573, 234, styleClass, 125, 124, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(581, 244, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(589, 255, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(594, 265, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(602, 277, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(604, 285, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(605, 295, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(596, 306, styleClass, 34, 81, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(591, 319, styleClass, 44, 90, 4);
			}
			break;

		case 12:
			if (fromspaceCounter == 1) {

				setReferenceLines(507, 178, styleClass, 105, 258, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(516, 192, styleClass, 106, 240, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(525, 201, styleClass, 108, 219, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(535, 211, styleClass, 109, 200, 4);
			} else if (fromspaceCounter == 5) {

				setReferenceLines(543, 225, styleClass, 112, 182, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(553, 233, styleClass, 116, 163, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(563, 245, styleClass, 118, 142, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(573, 254, styleClass, 125, 124, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(581, 264, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(589, 275, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(594, 285, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(602, 297, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(604, 305, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(605, 315, styleClass, 198, 65, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(594, 327, styleClass, 35, 81, 4);
			}
			break;

		case 13:
			if (fromspaceCounter == 1) {

				setReferenceLines(498, 187, styleClass, 104, 275, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(507, 198, styleClass, 105, 258, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(516, 211, styleClass, 106, 240, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(525, 220, styleClass, 108, 219, 4);

			} else if (fromspaceCounter == 5) {

				setReferenceLines(535, 230, styleClass, 109, 200, 4);
			} else if (fromspaceCounter == 6) {

				setReferenceLines(543, 244, styleClass, 112, 182, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(554, 252, styleClass, 116, 163, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(563, 264, styleClass, 118, 142, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(574, 273, styleClass, 125, 124, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(581, 283, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(590, 294, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(594, 304, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(602, 316, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(605, 324, styleClass, 0, 64, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(606, 334, styleClass, 198, 65, 4);
			}
			break;

		case 14:

			if (fromspaceCounter == 1) {

				setReferenceLines(487, 195, styleClass, 103, 295, 4);

			} else if (fromspaceCounter == 2) {

				setReferenceLines(498, 207, styleClass, 104, 275, 4);

			} else if (fromspaceCounter == 3) {

				setReferenceLines(507, 218, styleClass, 105, 258, 4);

			} else if (fromspaceCounter == 4) {

				setReferenceLines(516, 231, styleClass, 106, 240, 4);

			} else if (fromspaceCounter == 5) {

				setReferenceLines(525, 240, styleClass, 108, 219, 4);

			} else if (fromspaceCounter == 6) {

				setReferenceLines(535, 250, styleClass, 109, 200, 4);
			} else if (fromspaceCounter == 7) {

				setReferenceLines(543, 264, styleClass, 112, 182, 4);
			} else if (fromspaceCounter == 8) {

				setReferenceLines(554, 272, styleClass, 116, 163, 4);
			} else if (fromspaceCounter == 9) {

				setReferenceLines(563, 284, styleClass, 118, 142, 4);
			} else if (fromspaceCounter == 10) {

				setReferenceLines(574, 293, styleClass, 125, 124, 4);
			} else if (fromspaceCounter == 11) {

				setReferenceLines(581, 303, styleClass, 129, 108, 4);
			} else if (fromspaceCounter == 12) {

				setReferenceLines(590, 314, styleClass, 137, 91, 4);
			} else if (fromspaceCounter == 13) {

				setReferenceLines(594, 324, styleClass, 152, 81, 4);
			} else if (fromspaceCounter == 14) {

				setReferenceLines(602, 336, styleClass, 343, 67, 4);
			} else if (fromspaceCounter == 15) {

				setReferenceLines(606, 344, styleClass, 0, 64, 4);
			}
			break;
		}
	}

	/**
	 * localVariableDropSetup() - Adds an event handler to the local variables.
	 * They will be set up to allow one of the objects in the reference pool to
	 * be dropped on the local variables.
	 * 
	 * @param n
	 *            - A Node object
	 */
	public void localVariableDropSetup(final Node n) {
		n.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				System.out.println("onDragOver");

				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				if (event.getGestureSource() != n
						&& event.getDragboard().hasString()) {
					/* allow for both copying and moving, whatever user chooses */
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}

				event.consume();
			}
		});

		n.setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* mouse moved away, remove the graphical cues */

				createLinks(n);

				event.consume();
			}
		});
	}

	@FXML
	public void handleDrag(MouseEvent event) {

		drag.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				/* drag was detected, start drag-and-drop gesture */
				System.out.println("onDragDetected");

				/* allow any transfer mode */
				Dragboard db = drag.startDragAndDrop(TransferMode.ANY);

				/* put a string on dragboard */
				ClipboardContent content = new ClipboardContent();
				content.putString("Place on object");
				db.setContent(content);

				event.consume();
			}
		});

		bigLocalVariable.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				System.out.println("onDragOver");

				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				if (event.getGestureSource() != bigLocalVariable
						&& event.getDragboard().hasString()) {
					/* allow for both copying and moving, whatever user chooses */
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}

				event.consume();
			}
		});

		bigLocalVariable.setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* mouse moved away, remove the graphical cues */
				// bigLocalVariable.setVisible(false);

				// createLinks(bigLocalVariable, drag);

				event.consume();
			}
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
