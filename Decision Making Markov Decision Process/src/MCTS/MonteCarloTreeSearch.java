package MCTS;

import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import problem.Action;
import problem.ActionType;
import simulator.MCTSSimulator;
import simulator.State;
import problem.ProblemSpec;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the class used to implement the MonteCarlo Tree Search algorithm
 * It follows the four steps that are used to implement the Monte Carlo Tree
 * Search Algorithm i.e. Selection, Expansion, Simulation and Backpropagation.
 */
public class MonteCarloTreeSearch {

    private Board board;
    int step  = 0;
    public MonteCarloTreeSearch(Board board) {
        this.board = board;
    }

    /**
     * This function is used to find the next move.
     *
     * @return
     */
    public Action findNextAction(Node rootNode){

        // mcts (temporary) tree which is built during the four phases.
        Tree tree = new Tree();
        tree.setRootNode(rootNode);

        //Note: This can be up-to max 15000
        long end = System.currentTimeMillis() + board.timeLimit;

        Node promisingNode = new Node();

        while (System.currentTimeMillis() < end) {

            // Selection
            promisingNode = selectPromisingNode(rootNode);

            // Expansion
            expandNode(promisingNode);

            // Simulation
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }

            //[HowFarFromGoal, No. of Slips, No. of Breakdowns, Total visits (within simulation)
            //how many times -4, how many times -3, how many times -2, how many times -1, how many times 0 
            int[] costArray = simulateRandomPlayout(nodeToExplore);

            //Back-Propagation
            backPropogation(nodeToExplore, costArray);
        }

        step = step + 1;
        Node winnerNode = rootNode.getChildWithMaxScore();

        Action action = Tree.compareNodes(rootNode,winnerNode);

        return action;
    }

    /**
     * This function is used to implement the selection phase of the
     * MonteCarlo Search Algorithm. This will use UCT to find selecting
     * the next move.
     * @param rootNode
     * @return
     */
    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    /**
     * This function is used for the expansion phase where the function
     * recommends a leaf node which should be expanded.
     *
     * @param node
     */
    private void expandNode(Node node) {
        List<MCTSState> possibleStates = node.getState().getAllPossibleStates(this.board);
        possibleStates.forEach(state -> {

            Node newNode = new Node(state);
            newNode.setParent(node);
            node.getChildArray().add(newNode);
        });
    }

    /**
     * This function implements the simulation phase where a node is picked
     * randomly and simulated along the tree. Please note that this is the
     * simulation of Monte Carlo Tree Search algorithm and is different from
     * the simulation in given source code.
     *
     * @param node
     *
     * cost array [0] = how far goal was from terminal state at end of simulation.
     * cost array [1] = No. of Slips.
     * cost array [2] = No. of Breakdowns.
     * cost array [3] = Total visits
     * cost array [4] = how many times -4
     * cost array [5] = how many times -3
     * cost array [6] = how many times -2
     * cost array [7] = how many times -1
     * cost array [8] = how many times 0
     * @return
     */
    private int[] simulateRandomPlayout(Node node) {
        SimulationStage simulation = new SimulationStage(board.probSpec);
        int[] moveRecord = simulation.simulateRandomAction(node);
        return moveRecord;
    }

    /**
     *
     * This function implements the back propogation step of the Monte Carlo
     *     Tree Search algorithm.
     *
     * @param nodeToExplore
     * @param costArray
     */
    private void backPropogation(Node nodeToExplore, int[] costArray) {

        Node tempNode = nodeToExplore;

        while (tempNode != null) {
            // Increment visit count.
            tempNode.getState().incrementVisit();

            //Update the win score
            tempNode.getState().updateWinScore(costArray);

            // set temp to temp's parent.
            tempNode = tempNode.getParent();
        }

    }

}
