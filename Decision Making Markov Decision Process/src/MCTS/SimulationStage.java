package MCTS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;
import problem.Action;
import problem.ActionType;
import problem.Level;
import problem.ProblemSpec;
import problem.Tire;
import problem.TirePressure;
import simulator.MCTSSimulator;
import simulator.Simulator;
import simulator.State;

public class SimulationStage {
	ProblemSpec ps;
	int level;
	int steps;
	boolean isTheBestDriver = false;

	public SimulationStage(ProblemSpec ps) {
		this.ps = ps;
		this.level = ps.getLevel().getLevelNumber();
		//		System.out.println("ps : " + ps);
	}

	public boolean checkStatus(MCTSState state) {
		if(state != null && state.getPos() < ps.getN() 
				&& state.getTotalSteps() < ps.getMaxT() ) {
			//			System.out.println("Steps : " + state.getTotalSteps() + " - Current position " + state.getPos());
			//			System.out.println();
			return true;
		}
		return false;
	}
	public int[] simulateRandomAction(Node node) {
		Node tempNode = new Node(node);
		MCTSState tempState = tempNode.getState();
		MCTSState lastState = null;

		MCTSSimulator simulator = new MCTSSimulator(ps, tempState);
		boolean stateStatus = checkStatus(tempState);

		while (tempState != null && stateStatus) {
			lastState = new MCTSState(tempState, simulator.getSteps());
			switch (this.level){
			case 1 :
				tempState = chooseActionByStrategiesLevel1(simulator, tempState);
				break;

			case 2 :
				tempState = chooseActionByStrategiesLevel2(simulator, tempState);
				break; 

			case 3 :
				tempState = chooseActionByStrategiesLevel3(simulator, tempState);
				break;

			case 4 :
				tempState = chooseActionByStrategiesLevel4(simulator, tempState);
				break;

			case 5 :
				tempState = chooseActionByStrategiesLevel5(simulator, tempState);
				break;
			}

			stateStatus = checkStatus(tempState);
		}
		
		int[] moveRecord = new int[9];

		//		System.out.println("Reach " + simulator.getCurrentState().getPos() + " in "  + simulator.getSteps());
		if (tempState == null) {
			moveRecord = getMoveRecord(lastState, simulator);
			return moveRecord;
		}

		moveRecord = getMoveRecord(tempState, simulator);
		
		return moveRecord;
	}

	public int[] getMoveRecord(MCTSState lastState, MCTSSimulator simulator) {
		int[] moveRecord = new int[10];
		moveRecord[0] = Board.probSpec.getN() - lastState.getPos();
		moveRecord[1] = simulator.a1Record[10];
		moveRecord[2] = simulator.a1Record[11];
		moveRecord[3] = simulator.countingAction;
		moveRecord[9] = simulator.lastMove;
		for(int i = 0; i < 5; i++) {
			moveRecord[i + 4] = simulator.a1Record[i];
		}
		
		return moveRecord;
	}

	public int getBetterPositiveMoves(double[] probsCurrentDriver, double[] probsOtherDriver) {
		double probA = 0, probB = 0;
		for(int i = 5; i < 10; i++) {
			probA += probsCurrentDriver[i];
			probB += probsOtherDriver[i];
		}

		if(probA >= probB) {
			return 0;
		}

		return 1;
	}

	public MCTSState chooseActionByStrategiesLevel1(MCTSSimulator simulator, MCTSState state) {
		double[] originalMoveProbs = simulator.getMoveProbs();

		//find the best driver for positive moves - only do once - A3
		originalMoveProbs = findTheBestDriveAndChange(state, simulator); 
		MCTSSimulator tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());

		//only change car type and tire model if steps are greater than 2
		if(simulator.getStepLeft() >= 2) {
			List<Pair<Boolean,Action>> actionList = new ArrayList<Pair<Boolean,Action>>();

			//find good cars for positive moves - A2
			String car = state.getCarType();
			List<String> cars = ps.getCarOrder();
			Action action = new Action(ActionType.CHANGE_CAR, cars.get(1 - ps.getCarIndex(car)));
			tmpSimulator.performA2(action);

			double[] probsOtherCar = tmpSimulator.getMoveProbs();
			if(getBetterPositiveMoves(originalMoveProbs, probsOtherCar) == 0) {
				actionList.add(new Pair<Boolean, Action>(false, new Action(ActionType.CHANGE_CAR, car)));
			} else actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_CAR, cars.get(1 - ps.getCarIndex(car)))));

			//find good tires for positive moves - A4
			for(int i = 0; i < 4; i++) {
				Tire tire = state.getTireModel();
				if(i != ps.getTireIndex(tire)) {
					List<Tire> tires = ps.getTireOrder();
					action = new Action(ActionType.CHANGE_TIRES, tires.get(i));
					tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState()); //update simulator with original simulator
					tmpSimulator.performA4(action);
					double[] probsOtherTire = tmpSimulator.getMoveProbs();
					if(getBetterPositiveMoves(originalMoveProbs, probsOtherTire) == 0) {
						actionList.add(new Pair<Boolean, Action>(false,new Action(ActionType.CHANGE_TIRES, tire)));
					} else actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_TIRES, tires.get(i))));
				}
			}

			//randomly choose an action {A2, A4}
			Pair<Boolean, Action> pair = actionList.get(new Random().nextInt(actionList.size()));
			if(pair.getKey()) { //having a change on CarType or Tire Model
				simulator.step(pair.getValue());
			} 
		} 

		//do randomly move - A1
		State tmpState = simulator.step(new Action(ActionType.MOVE));

		if(tmpState != null) {
			MCTSState result = new MCTSState(tmpState);
			result.setTotalSteps(simulator.getSteps());
			return result;
		}

		return simulator.getCurrentState();
	}

	public MCTSState chooseActionByStrategiesLevel2(MCTSSimulator simulator, MCTSState state) {
		//		MCTSSimulator simulator = new MCTSSimulator(ps, state);
		double[] originalMoveProbs = simulator.getMoveProbs();

		//find the best driver for positive moves - only do once A3
		findTheBestDriveAndChange(state, simulator); 
		MCTSSimulator tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());

		//only change car type and tire model if steps are greater than 2
		if(simulator.getStepLeft() >= 2) {
			List<Pair<Boolean,Action>> actionList = new ArrayList<Pair<Boolean,Action>>();

			//find good cars for positive moves - A2
			createActionsFromGoodCarType(state.getCarType(), simulator, actionList);

			//find good tires for positive moves - A4
			createActionsFromGoodTire(state.getTireModel(), simulator, actionList);

			//find change pressure to the tires - A6
			//exchange fuel to reduce chances of slips - Always set pressure to 50%
			//TODO - Discussion
			if(state.getTirePressure() != TirePressure.FIFTY_PERCENT) {
				simulator.step(new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT));
				originalMoveProbs = simulator.getMoveProbs();
			}

			//randomly choose an action {A2, A4, A6}
			Pair<Boolean, Action> pair = actionList.get(new Random().nextInt(actionList.size()));
			if(pair.getKey()) { //check if it's current value or not
				simulator.step(pair.getValue());
			} 

			//add fuel if not enough to move - A5
			int fuelRequired = simulator.getFuelConsumption();
			int currentFuel = simulator.getCurrentState().getFuel();
			if (fuelRequired > currentFuel) {
				if (fuelRequired/10 < 1) fuelRequired = 10;
				else if (fuelRequired/10 < 2) fuelRequired = 20;
				else if (fuelRequired/10 < 3) fuelRequired = 30;
				else if (fuelRequired/10 < 4) fuelRequired = 40;
				simulator.step(new Action(ActionType.ADD_FUEL, fuelRequired));
			}
		}

		//do randomly move - A1
		State tmpState = simulator.step(new Action(ActionType.MOVE));

		if(tmpState != null) {
			return new MCTSState(tmpState);
		}

		return null;
	}

	public void createActionsFromGoodTire(Tire currentTire, MCTSSimulator simulator,
			List<Pair<Boolean, Action>> actionList) {
		MCTSSimulator tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());
		double[] originalMoveProbs = simulator.getMoveProbs();
		boolean isCurrentTireStillGood = false;

		List<Tire> tires = new ArrayList<Tire>(ps.getTireOrder());
		Tire[] bestTires = new Tire[2];
		bestTires[0] = currentTire;
		tires.remove(currentTire);

		//only choose 1st and 2nd rating tires
		for(int i = 0; i< tires.size(); i++) {
			Action action = new Action(ActionType.CHANGE_TIRES, tires.get(i));
			tmpSimulator.performA4(action);
			double[] probsOtherTire = tmpSimulator.getMoveProbs();
			if(getBetterPositiveMoves(originalMoveProbs, probsOtherTire) != 0) {
				Tire tmp = bestTires[0];
				bestTires[0] = tires.get(i); 
				bestTires[1] = tmp;
			}

			//reset simulator to the original state
			tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());
		}

		//add actions based on tire to actionList
		int i = 0;
		while(i < bestTires.length && bestTires[i] != null) {
			Action action = new Action(ActionType.CHANGE_TIRES, bestTires[i]);
			if(bestTires[i] != currentTire) {
				actionList.add(new Pair<Boolean, Action>(true, action));
			} else actionList.add(new Pair<Boolean, Action>(false, action));
			i++;
		}
	}

	public void createActionsFromGoodCarType(String currentCar, MCTSSimulator simulator, 
			List<Pair<Boolean, Action>> actionList) {
		MCTSSimulator tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());
		double[] originalMoveProbs = simulator.getMoveProbs();
		int ratingOfCurrentCar = 0;

		List<String> cars = new ArrayList<String>(ps.getCarOrder());
		cars.remove(currentCar);

		double[] previousProbsOtherCar = null;
		Action action = new Action(ActionType.CHANGE_CAR, cars.get(0));
		for(int i = 0; i < cars.size(); i++) {
			tmpSimulator.performA2(action);
			double[] probsOtherCar = tmpSimulator.getMoveProbs();
			if (previousProbsOtherCar == null) 
				previousProbsOtherCar = tmpSimulator.getMoveProbs();
			if(getBetterPositiveMoves(originalMoveProbs, probsOtherCar) == 0) {
				ratingOfCurrentCar++;
				previousProbsOtherCar = null;
			} else {
				if (previousProbsOtherCar  == null) {
					previousProbsOtherCar = tmpSimulator.getMoveProbs();
				} else {
					if(getBetterPositiveMoves(previousProbsOtherCar, probsOtherCar) != 0) {
						previousProbsOtherCar = tmpSimulator.getMoveProbs();;
					}
				}

				action = new Action(ActionType.CHANGE_CAR, cars.get(i));
				if(i + 1 == cars.size()) {
					actionList.add(new Pair<Boolean, Action>(true, action));
				}
			}

			//reset simulator to the original state
			tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());
		}

		//add current car to actionList to increase the randomness
		if(ratingOfCurrentCar >= cars.size() -1 ) {
			actionList.add(new Pair<Boolean, Action>(false, new Action(ActionType.CHANGE_CAR, currentCar)));				
		}
	}

	public double[] findTheBestDriveAndChange(MCTSState state, MCTSSimulator simulator) {
		double[] originalMoveProbs = simulator.getMoveProbs();;
		String aBetterDriver = "";
		if (!isTheBestDriver) {		
			String currentDriver = state.getDriver();
			double[] probsCurrentDriver = ps.getDriverMoveProbability().get(currentDriver);

			List<String> drivers = new ArrayList<String>(ps.getDriverOrder());
			drivers.remove(currentDriver);

			for(int i = 0; i < drivers.size(); i++) {
				double[] probsOtherDriver = ps.getDriverMoveProbability().get(drivers.get(i));

				if(getBetterPositiveMoves(probsCurrentDriver, probsOtherDriver) != 0) {
					aBetterDriver = drivers.get(i);
				}
			}

			if(aBetterDriver.isEmpty()) {
				isTheBestDriver = true;
			} else {
				Action action = new Action(ActionType.CHANGE_DRIVER, aBetterDriver);
				simulator.step(action);
				originalMoveProbs = simulator.getMoveProbs(); //update moveProbs again

				isTheBestDriver = true;
			}
		}
		return originalMoveProbs;
	}

	public MCTSState chooseActionByStrategiesLevel3(MCTSSimulator simulator, MCTSState state) {
		double[] originalMoveProbs = simulator.getMoveProbs();

		//find the best driver for positive moves - only do once A3
		findTheBestDriveAndChange(state, simulator); 
		MCTSSimulator tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());

		//only change car type and tire model if steps are greater than 2
		if(simulator.getStepLeft() >= 2) {
			List<Pair<Boolean,Action>> actionList = new ArrayList<Pair<Boolean,Action>>();

			//find good cars for positive moves - A2
			createActionsFromGoodCarType(state.getCarType(), simulator, actionList);

			//find good tires for positive moves - A4
			createActionsFromGoodTire(state.getTireModel(), simulator, actionList);

			//find change pressure to the tires - A6
			//exchange fuel to reduce chances of slips - Always set pressure to 50% or 75%
			//TODO - Discussion
			if(state.getTirePressure() == TirePressure.ONE_HUNDRED_PERCENT)
			{
				actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT)));
				actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.SEVENTY_FIVE_PERCENT)));
			} else if (state.getTirePressure() == TirePressure.SEVENTY_FIVE_PERCENT) {
				actionList.add(new Pair<Boolean, Action>(false, new Action(ActionType.CHANGE_PRESSURE, TirePressure.SEVENTY_FIVE_PERCENT)));
				actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT)));
			} else {
				actionList.add(new Pair<Boolean, Action>(false, new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT)));
				actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.SEVENTY_FIVE_PERCENT)));
			}

			//randomly choose an action {A2, A4, A6}
			int r = new Random().nextInt(actionList.size());
			//			System.out.println("Random choosing an action - " + r);
			Pair<Boolean, Action> pair = actionList.get(r);
			if(pair.getKey()) { //check if it's current value or not
				simulator.step(pair.getValue());
				//				System.out.println("Doing action - " + pair.getValue().getActionType().toString());
			} 

			//add fuel if not enough to move - A5
			int fuelRequired = simulator.getFuelConsumption();
			int currentFuel = simulator.getCurrentState().getFuel();
			if (fuelRequired > currentFuel) {
				if (fuelRequired/10 < 1) fuelRequired = 10;
				else if (fuelRequired/10 < 2) fuelRequired = 20;
				else if (fuelRequired/10 < 3) fuelRequired = 30;
				else if (fuelRequired/10 < 4) fuelRequired = 40;
				simulator.step(new Action(ActionType.ADD_FUEL, fuelRequired));
				//				System.out.println("Filling fuel - " + fuelRequired);
			}
		}

		//do randomly move - A1
		State tmpState = simulator.step(new Action(ActionType.MOVE));

		if(tmpState != null) {
			MCTSState result = new MCTSState(tmpState);
			result.setTotalSteps(simulator.getSteps());
			return result;
		}

		return null;
	}

	public MCTSState chooseActionByStrategiesLevel4(MCTSSimulator simulator, MCTSState state) {
		//only change car type and tire model if steps are greater than 2
		if(simulator.getStepLeft() > 2) {
			double[] originalMoveProbs = simulator.getMoveProbs();
			MCTSSimulator tmpSimulator = new MCTSSimulator(ps, simulator.getCurrentState());

			//find the best driver
			String aBetterDriver = "";
			String currentDriver = state.getDriver();
			List<String> drivers = new ArrayList<String>(ps.getDriverOrder());
			double[] maxProbsDriver = ps.getDriverMoveProbability().get(drivers.get(0));

			for(int i = 1; i < drivers.size(); i++) {
				double[] tmpProbsOtherDriver = ps.getDriverMoveProbability().get(drivers.get(i));
				if(getBetterPositiveMoves(maxProbsDriver, tmpProbsOtherDriver) != 0) {
					aBetterDriver = drivers.get(i);
				}
			}

			//find the best car for the current state - either A7 or A2 or A3
			List<Pair<Boolean,Action>> actionList = new ArrayList<Pair<Boolean,Action>>();

			if(!aBetterDriver.isEmpty()) {
				List<Pair<Boolean,Action>> carActionList = new ArrayList<Pair<Boolean,Action>>();
				Action changeDriver = new Action(ActionType.CHANGE_DRIVER, aBetterDriver);
				tmpSimulator.performA3(changeDriver);
				createActionsFromGoodCarType(state.getCarType(), tmpSimulator, carActionList);
				if(carActionList.size() >= 2) {
					for (int i =0; i < carActionList.size(); i++) {
						Action a7 = new Action(ActionType.CHANGE_CAR_AND_DRIVER, carActionList.get(i).getValue().getCarType(), aBetterDriver);
						actionList.add(new Pair<Boolean, Action>(true, a7));
					}

					//perform A7 and remove the action from actionList
					int r = new Random().nextInt(actionList.size());
					Pair<Boolean, Action> pair = actionList.get(r);
					simulator.step(pair.getValue());
					actionList.remove(r);
				} else {
					actionList.add(carActionList.get(0));
				}
			} else {
				createActionsFromGoodCarType(state.getCarType(), simulator, actionList);
			}

			//find good tires for positive moves - A4
			createActionsFromGoodTire(state.getTireModel(), simulator, actionList);

			//find change pressure to the tires - A6
			//exchange fuel to reduce chances of slips - Always set pressure to 50% or 75%
			if(state.getTirePressure() == TirePressure.ONE_HUNDRED_PERCENT)
			{
				List<Pair<Boolean,Action>> pressureActionList = new ArrayList<Pair<Boolean,Action>>();
				pressureActionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT)));
				pressureActionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.SEVENTY_FIVE_PERCENT)));
				Pair<Boolean, Action> pair = pressureActionList.get(new Random().nextInt(pressureActionList.size()));
				simulator.step(pair.getValue());
			} else if (state.getTirePressure() == TirePressure.SEVENTY_FIVE_PERCENT) {
				actionList.add(new Pair<Boolean, Action>(false, new Action(ActionType.CHANGE_PRESSURE, TirePressure.SEVENTY_FIVE_PERCENT)));
				actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT)));
			} else {
				actionList.add(new Pair<Boolean, Action>(false, new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT)));
				actionList.add(new Pair<Boolean, Action>(true, new Action(ActionType.CHANGE_PRESSURE, TirePressure.SEVENTY_FIVE_PERCENT)));
			}

			//randomly choose an action {A2, A4, A6}
			int r = new Random().nextInt(actionList.size());
			Pair<Boolean, Action> pair = actionList.get(r);
			if(pair.getKey()) { //check if it's current value or not
				simulator.step(pair.getValue());
				//				System.out.println("Doing action - " + pair.getValue().getActionType().toString());
			} 

			//add fuel if not enough to move - A5
			int fuelRequired = simulator.getFuelConsumption();
			int currentFuel = simulator.getCurrentState().getFuel();
			if (fuelRequired > currentFuel) {
				if (fuelRequired/10 < 1) fuelRequired = 10;
				else if (fuelRequired/10 < 2) fuelRequired = 20;
				else if (fuelRequired/10 < 3) fuelRequired = 30;
				else if (fuelRequired/10 < 4) fuelRequired = 40;
				simulator.step(new Action(ActionType.ADD_FUEL, fuelRequired));
				//				System.out.println("Filling fuel - " + fuelRequired);
			}
		}

		//do randomly move - A1
		State tmpState = simulator.step(new Action(ActionType.MOVE));

		if(tmpState != null) {
			MCTSState result = new MCTSState(tmpState);
			result.setTotalSteps(simulator.getSteps());
			return result;
		}

		return null;
	}


	public MCTSState chooseActionByStrategiesLevel5(MCTSSimulator simulator, MCTSState state) {
		if(simulator.getStepLeft() > 2) {
			//find the best driver
			String aBetterDriver = "";
			String currentDriver = state.getDriver();
			List<String> drivers = new ArrayList<String>(ps.getDriverOrder());
			double[] maxProbsDriver = ps.getDriverMoveProbability().get(drivers.get(0));

			for(int i = 1; i < drivers.size(); i++) {
				double[] tmpProbsOtherDriver = ps.getDriverMoveProbability().get(drivers.get(i));
				if(getBetterPositiveMoves(maxProbsDriver, tmpProbsOtherDriver) != 0) {
					aBetterDriver = drivers.get(i);
				}
			}

			List<Pair<Boolean,Action>> actionList = new ArrayList<Pair<Boolean,Action>>();

			//find good cars for positive moves - random choose one of them to change - A2
			createActionsFromGoodCarType(state.getCarType(), simulator, actionList);
			if(actionList.get(0).getKey()) {
				Pair<Boolean, Action> pair = actionList.get(new Random().nextInt(actionList.size()));
				if(pair.getKey()) {					
					simulator.step(pair.getValue());
					actionList = new ArrayList<Pair<Boolean,Action>>();
				}
			}
			
			//find good tires for positive moves - A4
			List<Pair<Boolean,Action>> tireActionList = new ArrayList<Pair<Boolean,Action>>();
			createActionsFromGoodTire(state.getTireModel(), simulator, tireActionList);
			
			//find change pressure to the tires - A6
			//only choose 50%
			if(tireActionList.get(0).getKey()) {
				simulator.step(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tireActionList.get(0).getValue().getTireModel(), 10, TirePressure.FIFTY_PERCENT));
			} else if (tireActionList.size() > 1){
				simulator.step(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tireActionList.get(1).getValue().getTireModel(), 10, TirePressure.FIFTY_PERCENT));
			}

			//add fuel if not enough to move - A5
			int fuelRequired = simulator.getFuelConsumption();
			int currentFuel = simulator.getCurrentState().getFuel();
			if (fuelRequired > currentFuel) {
				if (fuelRequired/10 < 1) fuelRequired = 10;
				else if (fuelRequired/10 < 2) fuelRequired = 20;
				else if (fuelRequired/10 < 3) fuelRequired = 30;
				else if (fuelRequired/10 < 4) fuelRequired = 40;
				simulator.step(new Action(ActionType.ADD_FUEL, fuelRequired));
			}
		}

		//do randomly move - A1
		State tmpState = simulator.step(new Action(ActionType.MOVE));

		if(tmpState != null) {
			MCTSState result = new MCTSState(tmpState);
			result.setTotalSteps(simulator.getSteps());
			return result;
		}

		return null;
	}

	public static void main(String[] args) throws IOException {
	}

}
