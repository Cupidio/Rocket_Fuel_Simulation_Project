public class ThermodynamicSolver {

    private double chamberTemp;
    private double specificHeatRatio; // Gamma
    private double gasConstant;
    private double chamberPressure;

    public ThermodynamicSolver(double specificHeatRatio, double gasConstant, double chamberPressure,
            double chamberTemp) 
    {
        this.specificHeatRatio = specificHeatRatio;
        this.gasConstant = gasConstant;
        this.chamberPressure = chamberPressure;
        this.chamberTemp = chamberTemp;
    }

    public FlowState computeIsentropicFlow(String stageName, double machNumber) 
    {
        double gamma = specificHeatRatio;

        if (machNumber == 0.0) {
            return new FlowState(stageName, chamberPressure, chamberTemp, 0.0, 0.0);
        }

        double temperatureRatio = 1 + ((gamma - 1) / 2) * Math.pow(machNumber, 2);
        double temp = chamberTemp / temperatureRatio;
        double pressureRatio = Math.pow(temperatureRatio, gamma / (gamma - 1));
        double pressure = chamberPressure / pressureRatio;
        double velocity = machNumber * Math.sqrt(gamma * gasConstant * temp);

        return new FlowState(stageName, pressure, temp, machNumber, velocity);
    }

    public double solveMachFromAreaRatio(double areaRatio, boolean isSupersonic) 
    {
        double gamma = this.specificHeatRatio;

        // Bounds based on flow regime
        double low = isSupersonic ? 1.00001 : 0.00001;
        double high = isSupersonic ? 15.0 : 0.99999;
        double tolerance = 1e-6;
        double midMach = 0;

        // using bisection method cause area-Mach have no closed form answer
        for (int i = 0; i < 100; i++) {
            midMach = (low + high) / 2.0;

            // Area-Mach equations
            //Technically I can take the gammaFactor and tempRatio out of the for loop as these values
            //dont change for an isentropic flow, but I think im going to keep it as gamma isnt constant
            // in the CFD
            double gammaFactor = 2.0 / (gamma + 1.0);
            double tempRatio = 1.0 + ((gamma - 1.0) / 2.0) * Math.pow(midMach, 2);
            double exponent = (gamma + 1.0) / (2.0 * (gamma - 1.0));
            double calculatedRatio = (1.0 / midMach) * Math.pow( gammaFactor* tempRatio, exponent);

            if (Math.abs(calculatedRatio - areaRatio) < tolerance) {
                break;
            }

            // change bounds based on flow behavior 
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

    public ThrustData calculateThrustState(double throatArea, double exitMach, double ambientPressure) 
    {
        double gamma = specificHeatRatio;

        // Calculate Mass Flow Rate
        double gammaTerm = Math.sqrt(gamma / gasConstant)
                * Math.pow(2.0 / (gamma + 1.0), (gamma + 1.0) / (2.0 * (gamma - 1.0)));
        double massFlowRate = (chamberPressure * throatArea / Math.sqrt(chamberTemp)) * gammaTerm;

        // Get Exit State
        FlowState exitState = computeIsentropicFlow("Exit", exitMach);
        double exitVelocity = exitState.getVelocity();
        double exitDensity = exitState.getPressure() / (gasConstant * exitState.getTemperature());
        double exitArea = massFlowRate / (exitDensity * exitVelocity);
        double momentumThrust = massFlowRate * exitVelocity;
        double pressureThrust = (exitState.getPressure() - ambientPressure) * exitArea;
        double totalThrust = momentumThrust + pressureThrust;

        return new ThrustData(totalThrust, exitState.getPressure(), massFlowRate);
    }
}
