package MCTS;

import problem.*;
import simulator.MCTSSimulator;
import simulator.State;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.max;

/**
 * This class is used to represent the State in which the game is
 * This state is used in the Monte Carlo Tree Search algorithm
 */
public class MCTSState extends State {

    public int GOAL_REWARD_VALUE = 1;

    private int steps;
    private int visitCount = 0 ;
    public double winScore = 0;//Integer.MIN_VALUE; // Q(s,a)

    public MCTSState(int pos, boolean slip, int slipTimeLeft, boolean breakdown, int breakdownTimeLeft, String carType,
                     int fuel, TirePressure tirePressure, String driver, Tire tireModel) {
        super(pos, slip, breakdown, carType, fuel, tirePressure, driver, tireModel);
    }

    public MCTSState(MCTSState state) {
        super(state.getPos(), state.isInSlipCondition(), state.isInBreakdownCondition(), state.getCarType(),
                state.getFuel(), state.getTirePressure(), state.getDriver(), state.getTireModel());
    }

    public MCTSState(MCTSState state, int steps) {
        super(state.getPos(), state.isInSlipCondition(), state.isInBreakdownCondition(), state.getCarType(),
                state.getFuel(), state.getTirePressure(), state.getDriver(), state.getTireModel());
        this.steps = steps;
    }

    public MCTSState(State state) {
        super(state.getPos(), state.isInSlipCondition(), state.isInBreakdownCondition(),
                state.getCarType(), state.getFuel(), state.getTirePressure(), state.getDriver(), state.getTireModel());

    }

    public MCTSState(State state, int steps) {
        super(state.getPos(), state.isInSlipCondition(), state.isInBreakdownCondition(),
                state.getCarType(), state.getFuel(), state.getTirePressure(), state.getDriver(), state.getTireModel());
        this.steps = steps;
    }

    public int getTotalSteps() {
        return this.steps;
    }

    public void setTotalSteps(int steps) {
        this.steps = steps;
    }

    /**
     * getter function for visitCount
     * @return number of visits for a 'node' which has state
     */
    public int getVisitCount() {
        return this.visitCount;
    }

    /**
     * setter function for visitCount
     * @param visitCount sets visitCount
     */
    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    /**
     * getter function for winScore
     * @return win score
     */
    public double getWinScore() {
        return this.winScore;
    }

    /**
     * setter function for winScore
     * @param winScore sets winScore
     */
    public void setWinScore(double winScore) {
        this.winScore = winScore;
    }


    /**
     * MDP logic
     *
     *
     *
     * @return list of all possible states
     */
    public List<MCTSState> getAllPossibleStates(Board board){

        List<MCTSState> listAllPossibleStates = new LinkedList<>();
        Level level = board.getLevel();
        int levelNumber = level.getLevelNumber();

        switch (levelNumber){

            case 1:
                listAllPossibleStates = getAllPossibleStatesLevel1(board);
                break;

            case 2:
                listAllPossibleStates = getAllPossibleStatesLevel2(board);
                break;

            case 3:
                listAllPossibleStates =  getAllPossibleStatesLevel3(board);
                break;

            case 4:
                listAllPossibleStates = getAllPossibleStatesLevel4(board);
                break;

            case 5:
                listAllPossibleStates = getAllPossibleStatesLevel5(board);
                break;

            default:
        }

        return listAllPossibleStates;
    }


    /**
     * MDP logic
     *
     *
     *
     * @return list of all possible states
     */
    public List<MCTSState> getAllPossibleStatesLevel1(Board board){

        List<MCTSState> listAllPossibleStates = new LinkedList<>();

        MCTSSimulator newSimulator = new MCTSSimulator(board.probSpec);
        newSimulator.setCurrentState(new MCTSState(this.copyState()));
        State tmpState = newSimulator.step(new Action(ActionType.MOVE));
        listAllPossibleStates.add(new MCTSState(tmpState, newSimulator.getSteps()));

        String car = this.getCarType();
        List<String> cars = new ArrayList<String>(board.probSpec.getCarOrder());
        cars.remove(car);
        Action action = new Action(ActionType.CHANGE_CAR, cars.get(0));
        newSimulator.setCurrentState(new MCTSState(this.copyState()));
        tmpState = newSimulator.step(action);
        listAllPossibleStates.add(new MCTSState(tmpState, newSimulator.getSteps()));

        Tire tire = this.getTireModel();
        List<Tire> tires = new ArrayList<Tire>(board.probSpec.getTireOrder());
        tires.remove(tire);
        for (int i=0; i<tires.size(); i++) {
            action = new Action(ActionType.CHANGE_TIRES, tires.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            tmpState = newSimulator.step(action);
            listAllPossibleStates.add(new MCTSState(tmpState, newSimulator.getSteps()));
        }
        return listAllPossibleStates;
    }

    /**
     * MDP logic
     *
     *
     *
     * @return list of all possible states
     */
    public List<MCTSState> getAllPossibleStatesLevel2(Board board){

        List<MCTSState> listAllPossibleStates = new LinkedList<>();
        //TODO: generate all possible states based on all the actions available in Level2

        MCTSSimulator newSimulator = new MCTSSimulator(board.probSpec);
        newSimulator.setCurrentState(new MCTSState(this.copyState()));
        State tmpState = newSimulator.step(new Action(ActionType.MOVE));
        listAllPossibleStates.add(new MCTSState(tmpState, newSimulator.getSteps()));

        String car = this.getCarType();
        List<String> cars = new ArrayList<String>(board.probSpec.getCarOrder());
        cars.remove(car);
        for (int i=0; i<cars.size();i++) {
            Action action = new Action(ActionType.CHANGE_CAR, cars.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            tmpState = newSimulator.step(action);
            listAllPossibleStates.add(new MCTSState(tmpState, newSimulator.getSteps()));
        }

        Tire tire = this.getTireModel();
        List<Tire> tires = new ArrayList<Tire>(board.probSpec.getTireOrder());
        tires.remove(tire);
        for (int i=0; i<tires.size(); i++) {
            Action action = new Action(ActionType.CHANGE_TIRES, tires.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            tmpState = newSimulator.step(action);
            listAllPossibleStates.add(new MCTSState(tmpState, newSimulator.getSteps()));
        }

        TirePressure tirePressure = this.getTirePressure();
        List<TirePressure> tirePressures = new ArrayList<>();
        tirePressures.add(TirePressure.FIFTY_PERCENT);
        tirePressures.add(TirePressure.SEVENTY_FIVE_PERCENT);
        tirePressures.remove(tirePressure);
        for (int i=0; i<tirePressures.size();i++){
            Action action = new Action(ActionType.CHANGE_PRESSURE, tirePressures.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            tmpState = newSimulator.step(action);
            listAllPossibleStates.add(new MCTSState(tmpState, newSimulator.getSteps()));
        }

        return listAllPossibleStates;
    }

    /**
     * MDP logic
     *
     *
     *
     * @return list of all possible states
     */
    public List<MCTSState> getAllPossibleStatesLevel3(Board board){

        List<MCTSState> listAllPossibleStates = new LinkedList<>();
        //TODO: generate all possible states based on all the actions available in Level1

        MCTSSimulator newSimulator = new MCTSSimulator(board.probSpec);
        newSimulator.setCurrentState(new MCTSState(this.copyState()));
        listAllPossibleStates.add(new MCTSState(newSimulator.step(new Action(ActionType.MOVE))));

        String car = this.getCarType();
        List<String> cars = new ArrayList<String>(board.probSpec.getCarOrder());
        cars.remove(car);
        for (int i=0; i<cars.size();i++) {
            Action action = new Action(ActionType.CHANGE_CAR, cars.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        Tire tire = this.getTireModel();
        List<Tire> tires = new ArrayList<Tire>(board.probSpec.getTireOrder());
        tires.remove(tire);
        for (int i=0; i<tires.size(); i++) {
            Action action = new Action(ActionType.CHANGE_TIRES, tires.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        TirePressure tirePressure = this.getTirePressure();
        List<TirePressure> tirePressures = new ArrayList<>();
        tirePressures.add(TirePressure.FIFTY_PERCENT);
        tirePressures.add(TirePressure.SEVENTY_FIVE_PERCENT);
        tirePressures.remove(tirePressure);
        for (int i=0; i<tirePressures.size();i++){
            Action action = new Action(ActionType.CHANGE_PRESSURE, tirePressures.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        return listAllPossibleStates;
    }

    /**
     * MDP logic
     *
     *
     *
     * @return list of all possible states
     */
    public List<MCTSState> getAllPossibleStatesLevel4(Board board){

        List<MCTSState> listAllPossibleStates = new LinkedList<>();
        //TODO: generate all possible states based on all the actions available in Level1

        MCTSSimulator newSimulator = new MCTSSimulator(board.probSpec);
        newSimulator.setCurrentState(new MCTSState(this.copyState()));
        listAllPossibleStates.add(new MCTSState(newSimulator.step(new Action(ActionType.MOVE))));

        String car = this.getCarType();
        List<String> cars = new ArrayList<String>(board.probSpec.getCarOrder());
        cars.remove(car);
        for (int i=0; i<cars.size();i++) {
            Action action = new Action(ActionType.CHANGE_CAR, cars.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        Tire tire = this.getTireModel();
        List<Tire> tires = new ArrayList<Tire>(board.probSpec.getTireOrder());
        tires.remove(tire);
        for (int i=0; i<tires.size(); i++) {
            Action action = new Action(ActionType.CHANGE_TIRES, tires.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        TirePressure tirePressure = this.getTirePressure();
        List<TirePressure> tirePressures = new ArrayList<>();
        tirePressures.add(TirePressure.FIFTY_PERCENT);
        tirePressures.add(TirePressure.SEVENTY_FIVE_PERCENT);
        tirePressures.remove(tirePressure);
        for (int i=0; i<tirePressures.size();i++){
            Action action = new Action(ActionType.CHANGE_PRESSURE, tirePressures.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        return listAllPossibleStates;
    }

    /**
     * MDP logic
     *
     *
     *
     * @return list of all possible states
     */
    public List<MCTSState> getAllPossibleStatesLevel5(Board board){
        List<MCTSState> listAllPossibleStates = new LinkedList<>();
        //TODO: generate all possible states based on all the actions available in Level1

        MCTSSimulator newSimulator = new MCTSSimulator(board.probSpec);
        newSimulator.setCurrentState(new MCTSState(this.copyState()));
        listAllPossibleStates.add(new MCTSState(newSimulator.step(new Action(ActionType.MOVE))));

        String car = this.getCarType();
        List<String> cars = new ArrayList<String>(board.probSpec.getCarOrder());
        cars.remove(car);
        for (int i=0; i<cars.size();i++) {
            Action action = new Action(ActionType.CHANGE_CAR, cars.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        List<Tire> tires = new ArrayList<Tire>(board.probSpec.getTireOrder());
        TirePressure tirePressure = this.getTirePressure();
        List<TirePressure> tirePressures = new ArrayList<>();
        tirePressures.add(TirePressure.FIFTY_PERCENT);
        tirePressures.add(TirePressure.SEVENTY_FIVE_PERCENT);
        tirePressures.remove(tirePressure);
        for (int i=0; i<tirePressures.size();i++){
            Action action = new Action(ActionType.CHANGE_PRESSURE, tirePressures.get(i));
            newSimulator.setCurrentState(new MCTSState(this.copyState()));
            listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
        }

        for (int i=0; i<tires.size(); i++) {
            for (int j=0; j<tirePressures.size();j++){
                Action action = new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tires.get(i), 50, tirePressures.get(j));
                newSimulator.setCurrentState(new MCTSState(this.copyState()));
                listAllPossibleStates.add(new MCTSState(newSimulator.step(action)));
            }
        }

        return listAllPossibleStates;
    }


    /**
     *  This is the function to calculate the score of a node
     * @return
     *
     * @param costArray
     */
    public void updateWinScore(int[] costArray) {

        // For each simulation.
        int terminalStateHowFarFromGoal = costArray[0];

        double q = 0;
        if(terminalStateHowFarFromGoal != 0){
            if (terminalStateHowFarFromGoal > 1) {
                q = q + (1 / Math.log(terminalStateHowFarFromGoal));
            } else {
                q = q + 4;
            }
        } else {
            q = q + 5;
        }
        if(costArray[9] == 6){
            q = q + ((-1) * Board.probSpec.getSlipRecoveryTime());
        }
        if(costArray[9] == 7){
            q = q + ((-1) * Board.probSpec.getRepairTime());
        }

        // Update over node score.
        this.winScore = ((this.winScore * this.visitCount)+ q) /  (this.visitCount+1);
    }

    /**
     * Calculates a value between 0 and 1, given the precondition that value
     * is between min and max. 0 means value = max, and 1 means value = min.
     */
    double normalize(double value, double min, double max) {
        return 1 - ((value - min) / (max - min));
    }

    // maximum number A1 actions (either move 1, 2, 3, 4, 5, or multiple times 5 according to current position)
    int getMaxLeapMovement(){

        int cellRemaining = (Board.probSpec.getN() - this.getPos());

        if (cellRemaining > 5){
            return (int)cellRemaining/5;
        }else{
            return 1;
        }

    }


    /**
     * Increments the visit-count after updating win-Score.
     */
    void incrementVisit() {
        this.visitCount++;
    }


    // HELPER METHODS : TO DETECT CHANGES IN TWO VALUES
    // NO METHOD FOR COMPARING : int pos, boolean slip, int slipTimeLeft, boolean breakdown, int breakdownTimeLeft,


    //A1
    boolean isPosChangedOrSlipOrBreakDown(MCTSState state){
        return (isPosChanged(this.getPos(),state.getPos()) ||
                (this.isInSlipCondition() != state.isInSlipCondition()) ||
                this.isInBreakdownCondition() != state.isInBreakdownCondition());
    }

    //A2
    boolean isCarTypeChanged(MCTSState state){
        return (isCarTypeChanged(this.getCarType(), state.getCarType()));
    }

    //A3
    boolean isDriverChanged (MCTSState state){
        return (isDriverChanged(this.getDriver(),state.getDriver()));
    }

    // A4
    boolean isTireModelChanged(MCTSState state) {
        return (isTireModelChanged(this.getTireModel(), state.getTireModel()));
    }

    //A5
    boolean isFuelChanged(MCTSState state){
        return isFuelLevelChanged(this.getFuel(),state.getFuel());
    }

    //A6
    boolean isTirePressureChanged(MCTSState state){
        return isTirePressureChanged(this.getTirePressure(),state.getTirePressure());
    }


    //Action A7 : A2+ A3
    boolean isCarTypeANDDriverChanged(MCTSState state){
        return (isCarTypeChanged(state)) && (isDriverChanged(state));
    }

    boolean isTire_ModelANDFuelANDTire_PressureChanged(MCTSState state){
        return (isTireModelChanged(state) && isFuelChanged(state) && isTirePressureChanged(state));
    }


    static private boolean isPosChanged(int pos1, int pos2){
        return (pos1!=pos2);
    }

    static private  boolean isTirePressureChanged(TirePressure tp1, TirePressure tp2){
        return (!tp1.asString().equals(tp2.asString()));
    }

    static private boolean isFuelLevelChanged(int fuel1, int fuel2 ){
        return (fuel1!=fuel2);
    }

    static private boolean isCarTypeChanged(String carType1, String carType2){
        return (!carType1.equals(carType2));
    }

    static private boolean isTireModelChanged(Tire tireModel1,Tire tireModel2){
        return (!tireModel1.asString().equals(tireModel2.asString()));
    }

    static  private boolean isDriverChanged(String driver1, String driver2){
        return (!driver1.equals(driver2));
    }


    public boolean isPosSameAndRestParametersAreSameToo(MCTSState state) {

        if(this.isInSlipCondition() == state.isInSlipCondition() // slip same
                && this.isInBreakdownCondition() == state.isInBreakdownCondition() // breakdown same
                && !this.isTirePressureChanged(state) // tire pressure same
                && !this.isFuelChanged(state) // fuel same
                && !this.isDriverChanged(state) // driver same
                && !this.isTireModelChanged(state) // tire model same
                && !this.isCarTypeChanged(state) // car type same
                && this.getPos() == state.getPos() // position same
                ){ //time steps increased
            return true;
        }

        return false;

    }

    /**
     * This function is used to create States for adding fuel
     * @param board
     * @return
     */
    public List<MCTSState> addFuel(Board board) {
        List<MCTSState> addFuel = new LinkedList<>();
        MCTSSimulator fuelSimulator = new MCTSSimulator(board.probSpec);
        fuelSimulator.setCurrentState(new MCTSState(this.copyState()));
        int currentFuelFactor = (int)Math.ceil((double) this.getFuel()/10);
        if(currentFuelFactor<5) {
            int j=1;
            for (int i=currentFuelFactor; i<5;i++) {
                Action action = new Action(ActionType.ADD_FUEL,(10*j));
                fuelSimulator.setCurrentState(new MCTSState(this.copyState()));
                addFuel.add(new MCTSState(fuelSimulator.step(action)));
                j++;
            }
        }
        return addFuel;
    }
}
