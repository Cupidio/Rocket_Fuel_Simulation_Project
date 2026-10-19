public abstract class currentState {
    double stateOne;
    double stateTwo;
    double stateThree;
    double stateFour;

    public currentState(double stateOne, double stateTwo, double stateThree, double stateFour) {
        this.stateOne = stateOne;
        this.stateTwo = stateTwo;
        this.stateThree = stateThree;
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

}
