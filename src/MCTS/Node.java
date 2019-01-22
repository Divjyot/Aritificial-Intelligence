package MCTS;

import problem.Action;
import problem.ActionType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represent each node of the tree created by the Monte Carlo
 * Tree Search Algorithm. A node has a State, the parent node and the list
 * of child nodes.
 */
public class Node {

    /** This is the state of the game */
    MCTSState state;

    /** This is the parent node */
    Node parent;

    /** This is the list of all children nodes */
    List<Node> childArray;  // Might need improved data structure

    /**
     * Constructor for the Node class
     * @param state
     */
    public Node(MCTSState state) {
        this.state = state;
        this.childArray = new ArrayList<Node>();
    }

    public Node(Node node) {
        this.childArray = new ArrayList<>();
        this.state = new MCTSState(node.getState());
        if (node.getParent() != null)
            this.parent = node.getParent();
        List<Node> childArray = node.getChildArray();
        for (Node child : childArray) {
            this.childArray.add(new Node(child));
        }
    }

    /**
     * Empty constructor for Node Class
     */
    public Node() {

    }

    /**
     * getter function to get the state of the node.
     * @return
     */
    public MCTSState getState() {
        return this.state;
    }

    /**
     * setter function to set the state of the node.
     * @param state
     */
    public void setState(MCTSState state) {
        this.state = state;
    }

    /**
     * getter function to get the parent node of the current node.
     * @return
     */
    public Node getParent() {
        return this.parent;
    }

    /**
     * setter function to set the parent node of the current node.
     * @param parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /** getter function to get the list of all children node of a
     * current node.
     * @return
     */
    public List<Node> getChildArray() {
        return this.childArray;
    }

    /**
     * setter function to set the list of children nodes of the current
     * node
     * @param childArray
     */
    public void setChildArray(List<Node> childArray) {
        this.childArray = childArray;
    }

    /**
     * Returns the child node with the greatests winner score.
     * @return
     */
    public Node getChildWithMaxScore() {

//        System.out.println("Child Array Size " + this.childArray.size());
        return Collections.max(this.childArray, Comparator.comparing(c -> {
//            System.out.println("Child Score:" + c.getState().getWinScore());
            return c.getState().getWinScore();
        }));
    }

	public Node getNodeWithoutChangingAction(Object[] actions, List<Node> firstNNode) {
		for(int i =0; i < firstNNode.size(); i++) {
			int count = 0;
    		for(int j =0; j < actions.length; j++) {
    			if( ((Action) actions[j]).getActionType() == ActionType.CHANGE_CAR) {
    				if(firstNNode.get(i).state.getCarType() != ((Action) actions[j]).getCarType()) {
    					count = 1;
    				}
    			} else if (((Action) actions[j]).getActionType() == ActionType.CHANGE_DRIVER) {
    				if(firstNNode.get(i).state.getDriver() != ((Action) actions[j]).getDriverType()) {
    					count = 1;
    				}
    			} else if (((Action) actions[j]).getActionType() == ActionType.CHANGE_TIRES) {
    				if(firstNNode.get(i).state.getTireModel() != ((Action) actions[j]).getTireModel()) {
    					count = 1;
    				}
    			} else if (((Action) actions[j]).getActionType() == ActionType.CHANGE_PRESSURE) {
    				if(firstNNode.get(i).state.getTirePressure() != ((Action) actions[j]).getTirePressure()) {
    					count = 1;
    				}
    			}
    		}
    		if (count == 0) return firstNNode.get(i);
    	}
		
		return null;
	}

    public Node getRandomChildNode() {
        int noOfPossibleMoves = this.childArray.size();
        int selectRandom = (int) (Math.random() * noOfPossibleMoves);
        return this.childArray.get(selectRandom);
    }

    public void toStringActionBetweenNodes(Board board,Node node1,Node node2){
        Action action = board.getAction(node1,node2);
        System.out.println("Action between " + node1.getState() + " & " + node2.getState() +  " is\n" +
                action.getText()+"\n\n");

    }

    public void toStringChildrenFromActionsOfNode(Board board){
        for (Node childNode:
             this.childArray) {
            Action action = board.getAction(this,childNode);
            System.out.println("Action that gen." + childNode.getState()+ "\nis: " + action.getText());
            System.out.println("----------------------------\n");

        }

        System.out.println("========================\n");

    }
}
