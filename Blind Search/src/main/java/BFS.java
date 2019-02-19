package main.java;

import java.util.*;

public class BFS {

    public List<State> getStateSeq() {
        return stateSeq;
    }

    public  List<State.ACTIONS> actionSeq = new ArrayList<State.ACTIONS>();
    private List<State> stateSeq = new ArrayList<>();

    int noOfSteps = 0;


    public int getNoOfSteps() {
        return noOfSteps;
    }

    State iniState;
    State goalState;

    LinkedList<State> queue;


    public List<State.ACTIONS> getActionSeq() {
        return actionSeq;
    }



    public BFS(String initial, String goal) {

        this.iniState   = new State(initial);
        this.iniState.setAction(null);
        this.iniState.setParent(null);

        this.goalState  = new State(goal);
        this.queue      = new LinkedList<>();
        this.queue.add(this.iniState);


        while(queue.size()!=0){

            State polledState = queue.poll();

            if (polledState.getState().equals(this.goalState.getState())) {
                System.out.println("SOLVED: YES:\n");
                this.createStateSeq(polledState);
                return;
            }

            this.addChildrenStates(polledState);

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

        this.noOfSteps = this.actionSeq.size();


    }


    public void addChildrenStates(State currentState){

        for (State.ACTIONS action : State.ACTIONS.values()) {

            State newState = currentState.getSuccessor(action);

            if (newState == null){
                continue;
            }

            if (currentState.getParent() != null){

//                System.out.println("Current's Parent State :");
//                currentState.getParent().printState();
//
//                System.out.println("Current State :" + " " + currentState.getAction());
//                currentState.printState();
//
//                System.out.println("Action performed: " + action);
//
//                System.out.println("New Child State :");
//                newState.printState();
//                System.out.println("----------------");
//
                if (currentState.getParent().compareStateWith(newState)){
//                    System.out.println("skipping GRAND PARENT === CHILD -------");
                    continue;
                }

            }

            this.queue.add(newState);
        }
    }


}
