public class ThrustData {
    private double thrustForce;
    private double exitPressure;
    private double massFlowRate;

    public ThrustData(double thrustForce, double exitPressure, double massFlowRate) {
        this.thrustForce = thrustForce;
        this.exitPressure = exitPressure;
        this.massFlowRate = massFlowRate;
    }

    public double getThrustForce() { return thrustForce; }
    public double getExitPressure() { return exitPressure; }
    public double getMassFlowRate() { return massFlowRate; }
}
