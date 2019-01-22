package helper;

import MCTS.MCTSState;
import MCTS.Node;
import org.w3c.dom.NodeList;
import problem.Action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.min;

/**
 * provide ("should") necessary functions to print and produce outputs for functions and
 * the whole program.
 */

/**
 * prints the output from main program.
 *
 * Output format:  A number;  8-component tuple;  action tuple
 *
 *  current_time_stamp ;
 *  {
 *  (1)car_current_cell_index, (2)slip, (3)breakdown, (4)car_type,
 *  (5)driver_name, (6)tire_type, (7)current_fuel_level, (8)current_tire_pressure
 *  };
 *  {
 *  (index_of_action1:value_if_required), (index_of_action2:value_of_action2_if_required),....
 *  }
 *
 * 2nd tuple can have 'n.a.' if no action is performed
 * which may be in case of waiting for slip/breakdown, during fuelling(greater than 1)
 *
 *
 *
 */
public class Output {

    int maxLines= 0;
    List <Line> outputList = new LinkedList<>();;
    List<String> outputStringList = new LinkedList<>();

    public Output() {
    }

    public List<String> outputStringList(){
        return outputStringList;
    }

    public Output(int n, int maxT) {
        maxLines = min(n+1,maxT+1);
    }

    public boolean addLine(int step, Node node ,List <Action> actions ){

        Line line  = new Line(step, node.getState(),actions);
        if(outputList.size()<=maxLines){
            outputList.add(line);
            outputStringList.add(line.toString());
            return true;
        }
        return false;
    }
}
