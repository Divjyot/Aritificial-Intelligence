package main.java;

public class Heuristics {



    /**
     * Core idea : lower bound on the amount of cost from now to goal state
     *
     * set  [minimum cost of each node ] h(x) = 0 for all x.... is a Admissible but it's
     * not use-able.
     *
     *
     * If h(x) is zero for each state, then h(x) dont tell extra information to figure which
     * branch to take.
     *
     * so we want h(x) be tighter lower bound to (closer) zero.
     *
     * For example:
     *  a Constraints on how many number of moves it takes to go to goal.
     *
     *  Assume : only blank square is misplaced then minimum number of moves it need is a 'number '
     *
     *   6  8  2            6  7  2
     *   1  3  4 ---->      1  _  3
     *   5  7  _            8  5  6
     *
     *
     *   2nd Example  to extend the heuristic
     *
     *
     *   no. of steps for _ to move to right
     *   no. of steps for 1 to move to right
     *   no. of steps for 2 to move to right
     *
     *   and so on.
     *
     *   sum all and divide by 2. as one move moves two tiles.
     *
     *
     *   3rd Example
     *
     *   ignore blank tile, only number blank tiles.
     *   Sum all ...
     *
     *
     */


        /**
         * Heuristic 1 : counting min. number of step for blank space to move to its goal place
        * @param current
        * @param goal
        */

        static public double heuristic1(String current, String goal) {

            double cost = heuristic1(current,goal , '_', "9");
//            System.out.println("heuristic1 Total Cost is "  + cost );
            return cost;
        }

        /**
         * Heuristic 1 : counting min. number of step for blank space to move to its goal place
         */
        static private double heuristic1(String current, String goal, char c, String withString){

            double cost = 0;
            String regex = "[^"+c+ "]";

            String currentO = current.replaceAll(regex, withString);
            currentO = currentO.replace(c,'_');

            String goalO = goal.replaceAll(regex, withString);
            goalO = goalO.replace(c,'_');

    //        System.out.println(currentO + "\n" +  goalO);
    //        Replace currentO and goalO with current and goal to get real number of steps
    //        String [] arr1B = {currentO,goalO,"BFS"};
    //        Global.main(arr1B);

            BFS bfs = new BFS(currentO,goalO);
            cost = bfs.getNoOfSteps();

            return cost;
        }

    /**
     * For each number and hole this heuristic returns min. no. of steps required to reach same as goal irrespective of
     * what other number position is while calculating heuristic of a specific number.
     * @param current
     * @param goal
     * @return
     */

    static public double heuristic2(String current, String goal) {
        return heuristic2(current,goal,false);

    }

    static private double heuristic2(String current, String goal, boolean skipHole){

        double cost = 0;
        String regex;
        for (char c:
             current.toCharArray()) {
            if (skipHole && c == '_') continue;
            cost  = cost  + heuristic1(current, goal , c, "9");
        }
//        System.out.println("heuristic-2 Total Cost is half of "  + cost + " that is " + cost/2 + " as one move moves two tiles.");
        return (cost);
    }

    /**
     * Same as heuristic2 expect
     *
     */
    static public double heuristic3(String current, String goal){
        return heuristic2(current,goal,true);
    }


}
