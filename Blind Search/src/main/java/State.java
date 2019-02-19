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


    public void setParent(State parent) {
        this.parent = parent;
    }

    private State parent = null;

    public ArrayList<Integer> state = new ArrayList<>(9);

    public String getStateString() {
        return stateString;
    }

    private String stateString;

    public State(){
        this.state = new ArrayList<>(9);
    }

    private boolean expanded = false;
    private State.ACTIONS action = null;


    public State(String aState) {
        this.stateString = aState;
        this.state = new ArrayList(9);
        this.convertToInt(aState.replace('_','0'));
    }

    private void convertToInt(String state){
        char [] charArr = state.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            this.state.add(Character.digit(charArr[i],10));
        }

    }

    public ArrayList<Integer> getState() {
        return state;
    }

    public State getParent() {
        return parent;
    }

    public State getSuccessor(ACTIONS action) {

        int spaceIndex = this.state.indexOf(0);

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

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setAction(State.ACTIONS action) {
        this.action = action;
    }


    public void printState(){

        System.out.println("~STATE~" + "\t\t" + " & ACTION " + this.getAction());
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

    public boolean compareStateWith(State aState){
        return (this.state.equals(aState.state));
    }



}
