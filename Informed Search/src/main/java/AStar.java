package main.java;

import java.util.*;

public class AStar {

    public  List<State.ACTIONS> actionSeq = new ArrayList<State.ACTIONS>();

    private int steps = 0;
    private List<State> stateSeq = new ArrayList<>();

    private StateCost initialState;
    private StateCost goalState;

    Comparator<StateCost> comparator = new StateCompare();
    PriorityQueue<StateCost> prQueue = new PriorityQueue<>(10, comparator );

    public AStar(String initialStateString, String goalStateString) {
        this.initialState = new StateCost (new State(initialStateString));
        this.goalState = new StateCost ( new State(goalStateString));

        prQueue.add(this.initialState);

        while (prQueue.size()!=0){

            StateCost polledState = prQueue.poll();

            if (polledState.getCostState().compareStateWith(this.goalState.getCostState())) {
                if (Global.PRINT_LOGS) { System.out.println("SOLVED: YES:\n"); }
                this.createStateSeq(polledState.getCostState());
                return;

            }
            this.addChildrenStates(polledState.getCostState());
        }

        System.out.println("QUEUE Empty but Not Found");

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
                System.out.println("|");
                System.out.println(this.stateSeq.get(i).getAction());
                System.out.println("|");
                System.out.println("V");
                this.stateSeq.get(i).printState();
            }
            if(this.stateSeq.get(i).getAction()!=null) this.actionSeq.add(this.stateSeq.get(i).getAction());
        }

        this.steps = this.actionSeq.size();


    }

    public List<State.ACTIONS> getActionSeq() {
        return actionSeq;
    }
    public int getNoOfSteps() { return steps; }


    public void addChildrenStates(State currentState){

        for (State.ACTIONS action : State.ACTIONS.values()) {

            State newState = currentState.getSuccessor(action);
            if (newState == null){
                continue;
            }

            if (currentState.getParent() != null){
                if (currentState.getParent().compareStateWith(newState)){
                    continue;
                }
            }

            this.prQueue.add(new StateCost(newState));
        }
    }

}
