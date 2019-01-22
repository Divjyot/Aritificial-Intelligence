package MCTS;

import java.util.Collections;
import java.util.Comparator;

public class UCT {

    /**
     * This function is used to find the best child node for a particular
     * parent node by calculating UCT values of each child node. The parent
     * node is passed to function as an argument.
     *
     * @param node
     * @return
     */
    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getState().getVisitCount();
        return Collections.max(
                node.getChildArray(),
                Comparator.comparing(c -> uctValue(parentVisit, c.getState().getWinScore(), c.getState().getVisitCount())));
    }

    /**
     * This function is used to calculate the UCT value
     *
     * @return
     */
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {

        double c = 1.41;
            if (nodeVisit == 0) {
                nodeVisit = nodeVisit + Integer.MIN_VALUE;
            }
            return ( (double) nodeWinScore/(double)nodeVisit) + (c * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit));

    }
}
