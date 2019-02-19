package main.java;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * BLIND SEARCH
 */

public class Global {

    static boolean PRINT_LOGS = false;

    static  List<State.ACTIONS> actionSeq = new ArrayList<State.ACTIONS>();
    static int noOfSteps = 0 ;
    static long elapsedTime = 0;
    static String initialState ;
    static String goalState;

    /**
     *
     *          * arg0 = initial state
     *          * arg1 = goal state
     *          * arg2 = BFS or DFS
     *
     * @param args
     */
    public static void main(String[] args) {

        initialState = args[0];
        goalState = args[1];

        if (parityCheck(initialState,goalState) == false) {System.out.println("Puzzle CANNOT be SOLVED"); return;}

        long startTime = System.currentTimeMillis();

        if (args[2].equals("BFS")){

            System.out.println("Performing Breath First Search:\n");

            BFS bfsSearch = new BFS(args[0], args[1]);
            actionSeq  = bfsSearch.getActionSeq();
            noOfSteps = bfsSearch.getNoOfSteps();

        }

        if (args[2].equals("DFS")){
            System.out.println("Performing Depth First Search:\n");

            DFS dfsSearch = new DFS(args[0], args[1]);
            actionSeq  = dfsSearch.getActionSeq();
            noOfSteps = dfsSearch.getNoOfSteps();

        }

        long stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;

        printAnswers();
    }

    static void printAnswers(){

        System.out.print("a. Action sequence: { ");
        for (State.ACTIONS act:  actionSeq ) {
            System.out.print(act + " ");
        }
        System.out.print(" }");

        System.out.println("\nb. No of Steps to solve: " + noOfSteps);


        DateFormat df = new SimpleDateFormat("HH 'hours', mm 'mins,' ss.SSS 'seconds'");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        System.out.println("c. Time to solve " + df.format(new Date(elapsedTime)));
        System.out.println("c. Time in milliseconds " + elapsedTime + "ms");
    }


    /**
     *
     * @return flag : if true then puzzle can be solved.
     */
    //TODO implementation
    private static boolean parityCheck (String ini, String goal){
     int initialParity = 0;
     int goalParity = 0;

     State  initial = new State(ini);
     State  goalSt = new State(goal);

     // Inversions for initial state
        for (int i = 0; i < 9; i++) {

            if (initial.getState().get(i) != (0)) {

//                System.out.println("\nN(s, " + initial.getState().get(i) + ") ");

                for (int j = 8; j > i; j--) {

                    if (initial.getState().get(i) > initial.getState().get(j)
                            && (initial.getState().get(j) != 0)) {
//                        System.out.println("Initial Parity : " + initial.getState().get(i) + ">" + initial.getState().get(j));
                        initialParity++;
                    }
                }
            }

//            System.out.println("+++++++++++++++" );

            if (goalSt.getState().get(i) != (0)) {

                for (int j = 8; j > i; j--) {

                    if (goalSt.getState().get(i) > goalSt.getState().get(j)
                            && goalSt.getState().get(j) != 0) {
//                        System.out.println("Goal Parity : " + goalSt.getState().get(i) + ">" + goalSt.getState().get(j));
                        goalParity++;
                    }

                }
            }
        }

        System.out.println("Initial Parity : " + initialParity);
        System.out.println("Goal Parity : " + goalParity);


        /**
         *  & : bitwise operator
         *  & with 1 reduces the int to last binary digit
         *  last digit is always 1  for odd number
         *  last digit is always 0 for even number
         *
         *  int i
         *  int g
         *
         *  i & 1 gets me even or odd in terms of 1 or 0
         *
         *  if both 1 then both odd therefore same parity
         *  else if both 0 then both even therefore same parity
         *  else they have different i.e. either one is odd and other is even or Vice Versa.
         */
     return (initialParity & 1) ==  (goalParity & 1);

    }

}
