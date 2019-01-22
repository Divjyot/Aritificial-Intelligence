package problem;

import MCTS.*;
import simulator.MCTSSimulator;
import simulator.Simulator;
import simulator.State;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long startTime = System.currentTimeMillis();
		ProblemSpec ps;
		try {

			// Default Initial setup
			String level = "1";
			String inputFile =  "examples/level_"+level+"/input_lvl"+level+".txt"; //"examples/level_1/input_lvl1.txt";
            String outputFile =  "myOutputFile_lvl" + level;

			//Default Time limit
			long timeLimit = 5000;


			if(args.length==3) {
				inputFile = args[0];
				outputFile = args[1];
				timeLimit = Integer.valueOf(args[2]);
			}
			else if(args.length==2) {
					inputFile = args[0];
					outputFile = args[1];
			}else if (args.length==1){
				System.out.println("Not enough arguments to run");
				return;
			}


			//Load problem specifications
		    ps = new ProblemSpec(inputFile);
			System.out.println(ps.toString());
			System.out.println("Running for time limit :" + timeLimit);

			System.out.println("\n\nReached Goal? -" + (test(ps,outputFile,timeLimit) ? "yes" : "no" ) );

		} catch (IOException e) {
			System.out.println("IO Exception occurred");
			System.exit(1);
		}
		System.out.println("Finished loading!");
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long actualMemUsed = afterUsedMem - beforeUsedMem;
		System.out.println("Memory Used: " + actualMemUsed);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Time Elapsed: " + elapsedTime);
	}

	static public boolean test(ProblemSpec ps, String OutputFile, long timeLimit){
        boolean isPassed = false;
		Board board = new Board(ps,timeLimit);
		MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(board);
		int countMoving = 1;
		
		Simulator simu = new Simulator(ps,OutputFile);
		Node aNode= new Node (new MCTSState(simu.reset()));

		//get and set the best driver
		String theBestDriver = board.getTheBestDriver();
		if (!theBestDriver.isEmpty()) {
			simu.step(new Action(ActionType.CHANGE_DRIVER, theBestDriver));
		}

		int i = 0;
		while(!simu.isGoalState(aNode.getState())){
			i ++;
//			System.out.println("Iteration: " + i + " Simu Step : " + simu.getSteps());
			Action policy =  mcts.findNextAction(aNode);
			
			//countMoving = checkAndAddPreviousAction(board, countMoving, policy);

            //check and filling fuel before performing any movement
			fuelCheck(policy,board,aNode,simu);

			State nextState = simu.step(policy);

			if(nextState==null) {
				break;
			}
			
			//set step again
			MCTSState tmpState = new MCTSState(nextState);
			tmpState.setTotalSteps(simu.getSteps());
			aNode = new Node(tmpState);
		}

		if(simu.isGoalState(aNode.getState())){
			isPassed = true;
		}

		return isPassed ;
	}

	private static void fuelCheck(Action policy, Board board, Node aNode, Simulator simu){
        if(policy.getActionType().getActionNo() == ActionType.MOVE.getActionNo()) {
            MCTSSimulator tmpSimulator = new MCTSSimulator(board.probSpec, aNode.getState());
            int fuelRequired = tmpSimulator.getFuelConsumption();
            int currentFuel = tmpSimulator.getCurrentState().getFuel();
            if (fuelRequired > currentFuel) {
                if (fuelRequired/10 <= 1) fuelRequired = 10;
                else if (fuelRequired/10 > 1 && fuelRequired/10 <= 2) fuelRequired = 20;
                else fuelRequired = 30;

                //Filling fuel by using simulator
                simu.step(new Action(ActionType.ADD_FUEL, fuelRequired));
                //System.out.println("Added fuel :," + fuelRequired);
            }
        }
    }
}
