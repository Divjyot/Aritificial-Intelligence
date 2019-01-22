package helper;

import MCTS.MCTSState;
import problem.Action;
import problem.ActionType;

import java.util.ArrayList;
import java.util.List;

public class Line {

    private int timeStep;
    private MCTSState firstTuple;
    private List<Action> secondTuple;

    public Line(int timeStep, MCTSState firstTuple, List<Action> secondTuple) {
        this.timeStep = timeStep;
        this.firstTuple = firstTuple;
        this.secondTuple = secondTuple;
    }

    public String toString(){

        String comp1 = String.valueOf(this.timeStep);
        String comp2 = "(" + firstTuple.getPos() + "," + firstTuple.isInSlipCondition() +
                    "," + firstTuple.isInBreakdownCondition() + "," + firstTuple.getCarType() +
                    "," + firstTuple.getDriver() + "," + firstTuple.getTireModel() +
                    "," + firstTuple.getFuel() + "," + firstTuple.getTirePressure() + ")";

        String comp3 = "";
        for (int i = 0; i < secondTuple.size(); i++) {

            Action action = secondTuple.get(i);
            ActionType actionType = action .getActionType();

            int actionNum = actionType.getActionNo();

            String actionNo = "(A" + String.valueOf(actionNum);
            String actionValue = "";

            switch (actionNum){
                case 1:
                    actionValue = "";
                    break;
                case 2:
                    actionValue = action.getCarType();
                    break;
                case 3:
                    actionValue = ":" + action.getDriverType();
                    break;
                case 4:
                    actionValue = ":" + action.getTireModel().toString();
                    break;
                case 5:
                    actionValue = ":" + String.valueOf(action.getFuel());
                    break;
                case 6:
                    actionValue = ":" + action.getTirePressure().asString();
                    break;
                case 7:
                    actionValue = ":" + action.getCarType() + " " + action.getDriverType();
                    break;
                case 8:
                    actionValue = ":" + action.getTireModel().toString() + " " + String.valueOf(action.getFuel())
                    + " " + action.getTirePressure().asString();
                    break;
                    default:
                        actionValue = "(n.a.)";
                    break;
            }
            actionValue = actionValue + ")";
            comp3 = comp3 + ( actionNo  + actionValue);

            if(secondTuple.size()>1) comp3 = comp3 + ",";
        }

        return comp1 + ";" + comp2 + ";" + comp3;

    }
}
