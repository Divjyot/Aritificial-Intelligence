package main.java;

import java.util.Comparator;

public class StateCompare implements Comparator<StateCost>
{
    @Override
    public int compare(StateCost state1, StateCost state2)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
        if (state1.getCostHeuristics() < state2.getCostHeuristics())
        {
            return -1;
        }
        if (state1.getCostHeuristics() > state2.getCostHeuristics())
        {
            return 1;
        }
        return 0;
    }
}