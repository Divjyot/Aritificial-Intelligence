package main.java;

import java.util.*;

public class DFS {

    private List<State.ACTIONS> actionSeq = new ArrayList<State.ACTIONS>();
    private List<State> stateSeq = new ArrayList<>();
    private int noOfSteps = 0;

    private State iniState;
    private State goalState;

    private Stack<State> stackContainer = new Stack<>();

    private HashSet<ArrayList> hashSet = new HashSet<>();

    public List<State.ACTIONS> getActionSeq() {
        return actionSeq;
    }

    public List<State> getStateSeq() {
        return stateSeq;
    }

    public int getNoOfSteps() {
        return noOfSteps;
    }

    public State getIniState() {
        return iniState;
    }

    public State getGoalState() {
        return goalState;
    }

    public DFS(String initial, String goal) {

        this.iniState =  new State(initial);
        this.iniState.setAction(null);
        this.iniState.setParent(null);

        this.goalState = new State(goal);
        stackContainer.add(this.iniState);

        if (Global.PRINT_LOGS) {
            System.out.println("Initial State: ");
            this.iniState.printState();
        }

        while (stackContainer.empty()== false){

            State unStackedState = this.stackContainer.peek();
            this.stackContainer.pop();
            this.hashSet.add(unStackedState.state);

            if (Global.PRINT_LOGS) {

                System.out.println(" - Popped State: ");
//            unStackedState.printState();
                System.out.println(unStackedState.toStringState());
            }

            if (unStackedState.getState().equals(this.goalState.getState())) {
                System.out.println("SOLVED: YES:\n");
                this.createStateSeq(unStackedState);
                return;
            }

            this.addChildrenStates(unStackedState);
        }

        System.out.println("STACK Empty but Not Found");

    }


    public void addChildrenStates(State currentState){

        if (Global.PRINT_LOGS) {
            System.out.println("Adding Children of :");
            currentState.printState();
            System.out.println(currentState.toStringState());
        }

        for (State.ACTIONS action : State.ACTIONS.values()) {

            State newState = currentState.getSuccessor(action);

            if (newState == null || this.hashSet.contains(newState.getState())){ continue; }
//            if (){ continue; }

            this.stackContainer.push(newState);

            if (Global.PRINT_LOGS) { System.out.println("+ Pushed :"); System.out.println(newState.toStringState()); }

        }
    }


    private void createStateSeq (State state){

        State reachedState = state;
        while (reachedState.getParent()!=null){
            this.stateSeq.add(reachedState);
            reachedState = reachedState.getParent();
        }

        this.stateSeq.add(reachedState);

        Collections.reverse(this.stateSeq);

        for (int i = 0; i < this.stateSeq.size(); i++) {
            if (Global.PRINT_LOGS) {
                System.out.println(this.stateSeq.get(i).getAction());
//            this.stateSeq.get(i).printState();
                System.out.println(this.stateSeq.get(i).toStringState());
            }

            if(this.stateSeq.get(i).getAction()!=null) this.actionSeq.add(this.stateSeq.get(i).getAction());
        }

        this.noOfSteps = this.actionSeq.size();

    }

}
