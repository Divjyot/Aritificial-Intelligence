package main.java;

public class StateCost {
    private State state;
    private double cost;
    private double heuristics;
    private double costHeuristics;

    public StateCost(State state) {
        this.state = state;
        String stateStr = state.getStateString().replace('0','_');
        this.heuristics = Heuristics.heuristic2(stateStr,Global.goalState);
        this.cost = state.getCost();
        this.costHeuristics = this.heuristics + this.cost;
    }

    public State getCostState() {
        return state;
    }


    public double getCostHeuristics() {
        return this.costHeuristics;
    }
}

