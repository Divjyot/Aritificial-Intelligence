package MCTS;
import helper.Output;
import problem.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * This class represents the path in which the cars will be
 * moving and it is represented using an array and different
 * parts of this array represents different terrain.
 */
public class Board {

	public static ProblemSpec probSpec;
	public long timeLimit;
	public Map<ActionType, Action> previousActionList;

	public Board(ProblemSpec ps,
			long timeLimit) {
		this.probSpec = ps;
		this.timeLimit = timeLimit;
		//resetPrevActionList();
	}

	Level getLevel (){
		return probSpec.getLevel();
	}
	int getMaxT(){
		return probSpec.getMaxT();
	}

	/**
	 *
	 * @param initialNode
	 * @param finalNode
	 * @return action that is performed from initial node to  final node or null if the action is performed
	 */
	public  Action getAction(Node initialNode, Node finalNode) throws NullPointerException {

		MCTSState state1 = initialNode.getState();
		MCTSState state2 = finalNode.getState();

		boolean isA1k_0 = state1.isPosSameAndRestParametersAreSameToo(state2);
		boolean isA1 =  isA1k_0 || state1.isPosChangedOrSlipOrBreakDown(state2); // A1
		boolean isA2 =  state1.isCarTypeChanged(state2); // A2
		boolean isA3 =  state1.isDriverChanged(state2); // A3
		boolean isA4 =  state1.isTireModelChanged(state2); // A4 : tire change
		boolean isA5 = state1.isFuelChanged(state2); //A5 : fuel add
		boolean isA6 = state1.isTirePressureChanged(state2);//A6: pressure change
		boolean isA7 = state1.isCarTypeANDDriverChanged(state2); //A7: A2+ A3
		boolean isA8 = state1.isTire_ModelANDFuelANDTire_PressureChanged(state2); //A4-A6
		System.out.println("A1:" + isA1 + "\n" + "A2:" + isA2 + "\n" + "A3:" + isA3 + "\n" + "A4:" + isA4 + "\n" + "A5:" + isA5 + "\n" + "A6:" + isA6 + "\n" + "A7:" + isA7 + "\n" + "A8:" + isA8 + "\n");

		try {
			switch (getLevel().getLevelNumber()) {

			case 1: {
				Action action = returnAction1_4(isA1, isA2, isA3, isA4, initialNode, finalNode);

				if(action!=null){
					return action;
				}else {
					throw  new NullPointerException();
				}

			}
			case 2: case 3: {

				//1. Check for actions 1-4
				Action action1_4 = returnAction1_4(isA1, isA2, isA3, isA4, initialNode, finalNode);
				if(action1_4!=null){
					return action1_4;
				}

				//2. if action1-4 are null, then Check for action 5-6
				Action action5_6 = returnAction5_6(isA5,isA6,initialNode,finalNode);
				if(action5_6 != null) {
					return action5_6;
				}else {
					// if 5_6 are still null then throw exception.
					throw  new NullPointerException();
				}


			}
			case 4: {


				//1. Check for actions 1-4
				Action action1_4 = returnAction1_4(isA1, isA2, isA3, isA4, initialNode, finalNode);
				if(action1_4!=null){
					return action1_4;
				}

				Action action5_6 = returnAction5_6(isA5,isA6,initialNode,finalNode);
				if(action5_6 != null) {
					return action5_6;
				}

				// 3. Must be A7.
				if(isA7){
					Action action7 = new Action(ActionType.CHANGE_CAR_AND_DRIVER, state2.getCarType(), state2.getDriver());
					return action7;
				} else {
					// if not A7 are still null then throw exception.
					throw  new NullPointerException();
				}


			}
			case 5: {



				//1. Check for actions 1-4
				Action action1_4 = returnAction1_4(isA1, isA2, isA3, isA4, initialNode, finalNode);
				if(action1_4!=null){
					return action1_4;
				}

				//2. if action1-4 are null, then Check for action 5-6
				Action action5_6 = returnAction5_6(isA5,isA6,initialNode,finalNode);
				if(action5_6 != null) {
					return action5_6;
				}

				// 3. it can be A7.
				if(isA7){
					Action action7 = new Action(ActionType.CHANGE_CAR_AND_DRIVER, state2.getCarType(), state2.getDriver());
					return action7;
				}

				//4. Must be A8
				if (isA8){
					Action action8 = new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, state2.getTireModel(), state2.getFuel(),state2.getTirePressure());
					return action8;
				}else {
					// if not A8, then throw null exception.
					throw  new NullPointerException();
				}
			}
			}

		} catch (NullPointerException e) {
			System.out.println("Null Action returned: " + e);
			System.out.println("Error: Null Action returned/Invalid Action performed at " + getLevel().getLevelNumber() + " this can be either combination of actions performed" +
					"or actions performed which were not allowed in Level:"+ getLevel().getLevelNumber());
			System.out.println("Error: Invalid Action performed at " + getLevel().getLevelNumber() + "(SHOULD NEVER HAPPEN) and  Reasons can be:" + "\n" +
					"1. This can be either combination of more than one change of state parameters\n" +
					"2. Or No change happened between root node state and its children\n"+
					"3. Or actions performed which were not allowed in Level:" + getLevel().getLevelNumber());
		}
		return null;
	}

	private Action returnAction1_4(boolean isA1,boolean isA2, boolean isA3,boolean isA4, Node initialNode, Node finalNode ){


		if(isA1 && !isA2 && !isA3 && !isA4) { // Only Action A1
			Action returnAction = new Action(ActionType.MOVE);
			returnAction.setMovementSteps(finalNode.getState().getPos() - initialNode.getState().getPos());
			return returnAction;

		} else if(!isA1 && isA2 && !isA3 && !isA4) {
			Action returnAction = new Action(ActionType.CHANGE_CAR, finalNode.getState().getCarType());
			return returnAction;

		} else if(!isA1 && !isA2 && isA3 && !isA4) {
			Action returnAction = new Action(ActionType.CHANGE_DRIVER, finalNode.getState().getDriver());
			return returnAction;

		} else if(!isA1 && !isA2 && !isA3 && isA4) {
			Action returnAction =  new Action(ActionType.CHANGE_TIRES,finalNode.getState().getTireModel());
			return returnAction;

		}else{

			return null;
		}

	}


	private  Action returnAction5_6(boolean isA5, boolean isA6,Node initialNode, Node finalNode ){
		if (isA5) {
			return new Action(ActionType.ADD_FUEL, finalNode.getState().getFuel());
		} else if (isA6) {
			return new Action(ActionType.CHANGE_PRESSURE, finalNode.getState().getTirePressure());
		} else {
			return null;
		}
	}
	
	public String getTheBestDriver() {
		List<String> drivers = this.probSpec.getDriverOrder();
		double[] probsBestDriver = this.probSpec.getDriverMoveProbability().get(drivers.get(0));
		String theBestDriver = "";

		for(int i = 1; i < drivers.size(); i++) {
			double[] probsOtherDriver = this.probSpec.getDriverMoveProbability().get(drivers.get(i));

			if(getBetterPositiveMoves(probsBestDriver, probsOtherDriver) != 0) {
				theBestDriver = drivers.get(i);
			}
		}
		
		return theBestDriver;
	}
	
	private int getBetterPositiveMoves(double[] probsCurrentDriver, double[] probsOtherDriver) {
		double probA = 0, probB = 0;
		for(int i = 5; i < 10; i++) {
			probA += probsCurrentDriver[i];
			probB += probsOtherDriver[i];
		}

		if(probA > probB) {
			return 0;
		} else if (probA == probB) {
			if(probsCurrentDriver[10] <= probsOtherDriver[10]) return 0;
			else return 1;
		}

		return 1;
	}
}