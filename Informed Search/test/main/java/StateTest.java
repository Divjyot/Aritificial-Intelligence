package main.java;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class StateTest {



    public void printPuzzle(ArrayList aState){

        for (int i = 0; i < aState.size(); i++) {
            System.out.print(aState.get(i) + " ");
            if (i==2 ||i==5 ||i==8) System.out.print("\n");
        }
        System.out.print("\n");
    }


    @Test
    public void getSuccessor() {
        //  72_456831
        //  a child : 7_2456831
        State iniState = new State("7_2456831");
        System.out.println("INITIAL:");
        this.printPuzzle(iniState.state);

        for (State.ACTIONS action : State.ACTIONS.values()) {

            System.out.print("ACTION: " + action.toString() + "\n") ;
            State newState = iniState.getSuccessor(action);
            System.out.print("New : "+newState + "\n");
            System.out.print("Ini : "+iniState+ "\n");

            if (newState == null) { System.out.print("ILLEGAL ACTION -> " + action.toString() + "\n\n"); continue; }

            this.printPuzzle(newState.state);
        }

    }


}