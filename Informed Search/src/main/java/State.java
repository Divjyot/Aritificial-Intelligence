package main.java;

import javax.xml.bind.annotation.XmlElementDecl;
import java.util.*;


public class State {

    public enum ACTIONS{
        UP(1),
        DOWN(2),
        LEFT(3),
        RIGHT(4);
        private int numVal;

        ACTIONS(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }


    private double  cost = 0 ;
    private State parent = null;

    public String getStateString() {
        return stateString;
    }

    private String stateString;
    private State.ACTIONS action = null;
    public ArrayList<Integer> state = new ArrayList<>(9);


    public State(){ }

    public State(String aState) {
        this.stateString = aState;
        this.convertToInt(aState.replace('_','0'));
    }

    public void setParent(State parent) {
        this.parent = parent;
    }


    public ArrayList<Integer> getState() {
        return state;
    }

    public State getParent() {
        return parent;
    }

    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    public State getSuccessor(ACTIONS action) {

        int spaceIndex = this.state.indexOf(Global.HOLE_REP);

        State succState = new State();
        copyStates(this,succState);
        succState.action = action;

        switch (action) {
            case UP: {
                if (spaceIndex == 0 || spaceIndex == 1 || spaceIndex == 2) return null;
                Collections.swap(succState.state, spaceIndex, spaceIndex - 3);
            }
            break;
            case DOWN: {
                if (spaceIndex == 6 || spaceIndex == 7 || spaceIndex == 8) return null;
                Collections.swap(succState.state, spaceIndex, spaceIndex + 3);
            }
            break;
            case LEFT: {
                if (spaceIndex == 0 || spaceIndex == 3 || spaceIndex == 6) return null;
                Collections.swap(succState.state, spaceIndex, spaceIndex - 1);
            }
            break;
            case RIGHT: {
                if (spaceIndex == 2 || spaceIndex == 5 || spaceIndex == 8) return null;
                Collections.swap(succState.state, spaceIndex, spaceIndex + 1);
            }
            break;
        }

        succState.parent = this;
        succState.stateString = this.toString();
        succState.cost = this.cost + 1;

        return succState;

    }

    static void copyStates(State state1 , State state2){

            for (Integer ele : state1.state) {
                state2.state.add(ele);
            }
    }

    public ACTIONS getAction() {
        return action;
    }

    public void setAction(State.ACTIONS action) {
        this.action = action;
    }

    public void printState(){

        System.out.println("~STATE~" + "\t" + " & ACTION " + this.getAction() + "\t Cost: " + this.cost);
        System.out.println("~~~~~~~~");
        for (int i = 0; i < this.state.size(); i++) {
            System.out.print(this.state.get(i) + " ");
            if (i==2 ||i==5 ||i==8) System.out.print("\n");
        }
        System.out.println("~~~~~~~");
        System.out.print("\n");
    }

    public String toStringState(){
        String str = "";
        for (int i = 0; i < this.state.size(); i++) {
            str = str + this.state.get(i) + " ";
            if (i==2 ||i==5 ||i==8) System.out.print("\n");
        }
        System.out.print("\n");
        return str;
    }

    public String toString(){
        String str = "";
        for (int i = 0; i < this.state.size(); i++) {
            str = str + this.state.get(i);
        }
        return str;
    }



    public boolean compareStateWith(State aState){
        return (this.state.equals(aState.state));
    }

    /**
     * Helper method to convert string into int
     * @param state String state "123450678"
     */
    private void convertToInt(String state){
        char [] charArr = state.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            this.state.add(Character.digit(charArr[i],10));
        }

    }



}
