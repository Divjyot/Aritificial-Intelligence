package MCTS;

import problem.Action;
import problem.ActionType;
import problem.TirePressure;

/**
 * This class is used to represent the tree that the Monte Carlo
 * Tree Search Algorithm will be creating.
 */
public class Tree {

    /** This is the root node of the tree */
    Node root;

    public Tree() {
        this.root = new Node();
    }

    /**
     * getter function to get the root node
     * @return
     */
    public Node getRootNode() {
        return this.root;
    }

    /**
     * setter function to set the root node
     * @param rootNode
     */
    public void setRootNode(Node rootNode) {
        this.root = rootNode;
    }

    /**
     * Constructor for the Tree class
     * @param rootNode
     */
    public Tree(Node rootNode){
        this.root = rootNode;
    }

    public void addChild(Node parent, Node child) {
        parent.getChildArray().add(child);
    }

    public static Action compareNodes(Node initialNode, Node finalNode) {


        if(initialNode.getState().getPos() != finalNode.getState().getPos()) {
            Action returnAction = new Action(ActionType.MOVE);
            returnAction.setMovementSteps(finalNode.getState().getPos() - initialNode.getState().getPos());
            return returnAction;
        } else if((initialNode.getState().getCarType() != finalNode.getState().getCarType()) && (initialNode.getState().getDriver() != finalNode.getState().getDriver())) {
            Action returnAction = new Action(ActionType.CHANGE_CAR_AND_DRIVER, finalNode.getState().getCarType(), finalNode.getState().getDriver());
            return returnAction;
        } else if (isAction8(initialNode,finalNode)){
            Action returnAction = new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, finalNode.getState().getTireModel(), finalNode.getState().getFuel(), finalNode.getState().getTirePressure());
            return returnAction;
        } else if (initialNode.getState().getCarType() != finalNode.getState().getCarType()) {
            Action returnAction = new Action(ActionType.CHANGE_CAR, finalNode.getState().getCarType());
            return returnAction;
        } else if (initialNode.getState().getDriver() != finalNode.getState().getDriver()) {
            Action returnAction = new Action(ActionType.CHANGE_DRIVER, finalNode.getState().getDriver());
            return returnAction;
        } else if (initialNode.getState().getTireModel() != finalNode.getState().getTireModel()) {
            Action returnAction = new Action(ActionType.CHANGE_TIRES,finalNode.getState().getTireModel());
            return returnAction;
        } else if (initialNode.getState().getFuel() < finalNode.getState().getFuel()) {
            Action returnAction = new Action(ActionType.ADD_FUEL, finalNode.getState().getFuel());
            return returnAction;
        } else if (initialNode.getState().getTirePressure() != finalNode.getState().getTirePressure()){
            Action returnAction = new Action(ActionType.CHANGE_PRESSURE, finalNode.getState().getTirePressure());
            return returnAction;
        } else {
            Action returnAction = new Action(ActionType.MOVE);
            returnAction.setMovementSteps(finalNode.getState().getPos() - initialNode.getState().getPos());
            return returnAction;
        }
    }

    /**
     * This function is used to check if the action performed from the
     * initial node to the final node is Action 8.
     *
     * @param initialNode
     * @param finalNode
     * @return
     */
    public static boolean isAction8(Node initialNode, Node finalNode) {
        if((initialNode.getState().getTireModel() != finalNode.getState().getTireModel()) && (initialNode.getState().getFuel() < finalNode.getState().getFuel())) {
            return true;
        } else if ((initialNode.getState().getFuel() < finalNode.getState().getFuel()) && (initialNode.getState().getTirePressure() != finalNode.getState().getTirePressure())) {
            if (initialNode.getState().getCarType() != finalNode.getState().getCarType()) {
                return false;
            }
            return true;
        } else if ((initialNode.getState().getTireModel() != finalNode.getState().getTireModel()) && (finalNode.getState().getTirePressure() != TirePressure.ONE_HUNDRED_PERCENT)) {
            return true;
        }
        return false;
    }
}
