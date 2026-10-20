public class Prelim {

    private int id;
    private double stateOne;
    private double stateTwo;
    private double stateThree;
    private double stateFour;
    private double stateFive;

    public Prelim(int id, double stateOne, double stateTwo, double stateThree, double stateFour, double stateFive) {
        this.id = id;
        this.stateOne = stateOne;
        this.stateTwo = stateTwo;
        this.stateThree = stateThree;
        this.stateFour = stateFour;
        this.stateFive = stateFive;
    }

    // C: Change the states into temperature and pressure values for the final code
    public void setStateOne(double stateOne) {
        this.stateOne = stateOne;
    }

    public void setStateTwo(double stateTwo) {
        this.stateTwo = stateTwo;
    }

    public void setStateThree(double stateThree) {
        this.stateThree = stateThree;
    }

    public void setStateFour(double stateFour) {
        this.stateFour = stateFour;
    }

    public void setStateFive(double stateFive) {
        this.stateFive = stateFive;
    }

    public double getStateOne() {
        return stateOne;
    }

    public double getStateTwo() {
        return stateTwo;
    }

    public double getStateThree() {
        return stateThree;
    }

    public double getStateFour() {
        return stateFour;
    }

    public double getStateFive() {
        return stateFive;
    }

    public int getId() {
        return id;
    }

}
