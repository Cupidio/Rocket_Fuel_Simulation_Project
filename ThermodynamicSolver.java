public class ThermodynamicSolver {

    private double chamberTemp;
    private double exitVelocity;
    private double specificHeatRatio; // Gamma
    private double gasConstant;
    private double chamberPressure;

    public ThermodynamicSolver(double specificHeatRatio, double gasConstant, double chamberPressure,
            double chamberTemp) {
        this.specificHeatRatio = specificHeatRatio;
        this.gasConstant = gasConstant;
        this.chamberPressure = chamberPressure;
        this.chamberTemp = chamberTemp;
    }

    public FlowState computeIsentropicFlow(String stageName, double machNumber) {
        double gamma = specificHeatRatio;

        if (machNumber == 0.0) {
            return new FlowState(stageName, chamberPressure, chamberTemp, 0.0, 0.0);
        }

        double tRatio = 1 + ((gamma - 1) / 2) * Math.pow(machNumber, 2);
        double temp = chamberTemp / tRatio;
        double pRatio = Math.pow(tRatio, gamma / (gamma - 1));
        double pressure = chamberPressure / pRatio;
        double velocity = machNumber * Math.sqrt(gamma * gasConstant * temp);

        return new FlowState(stageName, pressure, temp, machNumber, velocity);
    }

    public double solveMachFromAreaRatio(double areaRatio, boolean isSupersonic) {
        double gamma = this.specificHeatRatio;

        // Define search bounds based on flow regime
        double low = isSupersonic ? 1.00001 : 0.00001;
        double high = isSupersonic ? 15.0 : 0.99999;
        double tolerance = 1e-6;
        double midMach = 0;

        // Bisection method to find the Mach number root
        for (int i = 0; i < 100; i++) {
            midMach = (low + high) / 2.0;

            // Isentropic Area-Mach equation
            double term1 = 2.0 / (gamma + 1.0);
            double term2 = 1.0 + ((gamma - 1.0) / 2.0) * Math.pow(midMach, 2);
            double exponent = (gamma + 1.0) / (2.0 * (gamma - 1.0));
            double calculatedRatio = (1.0 / midMach) * Math.pow(term1 * term2, exponent);

            if (Math.abs(calculatedRatio - areaRatio) < tolerance) {
                break;
            }

            // Adjust bounds based on flow regime behavior
            if (isSupersonic) {
                if (calculatedRatio < areaRatio)
                    low = midMach;
                else
                    high = midMach;
            } else {
                // In subsonic flow, Area Ratio decreases as Mach increases toward 1.0
                if (calculatedRatio > areaRatio)
                    low = midMach;
                else
                    high = midMach;
            }
        }
        return midMach;
    }

    public ThrustData calculateThrustState(double throatArea, double exitMach, double ambientPressure) {
        double gamma = specificHeatRatio;

        // Calculate Mass Flow Rate
        double gammaTerm = Math.sqrt(gamma / gasConstant)
                * Math.pow(2.0 / (gamma + 1.0), (gamma + 1.0) / (2.0 * (gamma - 1.0)));
        double massFlowRate = (chamberPressure * throatArea / Math.sqrt(chamberTemp)) * gammaTerm;

        // Get Exit State to find Exit Velocity and Area
        FlowState exitState = computeIsentropicFlow("Exit", exitMach);
        this.exitVelocity = exitState.getVelocity(); // Storing attribute

        double exitDensity = exitState.getPressure() / (gasConstant * exitState.getTemperature());
        double exitArea = massFlowRate / (exitDensity * this.exitVelocity);

        // Calculate Thrust
        double momentumThrust = massFlowRate * this.exitVelocity;
        double pressureThrust = (exitState.getPressure() - ambientPressure) * exitArea;
        double totalThrust = momentumThrust + pressureThrust;

        return new ThrustData(totalThrust, exitState.getPressure(), massFlowRate);
    }
}