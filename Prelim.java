public class Prelim {

    private int id;
    private double stateOne;
    private double stateTwo;
    private double stateThree;
    private double stateFour;

    public Prelim(int id, double stateOne, double stateTwo, double stateThree, double stateFour) {
        this.id = id;
        this.stateOne = stateOne;
        this.stateTwo = stateTwo;
        this.stateThree = stateThree;
        this.stateFour = stateFour;
    }


    //C: Change the states into temperature and pressure values for the final code    
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

    public int getId() {
        return id;
    }

}
