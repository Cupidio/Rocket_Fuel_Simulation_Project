public class StateEstimation {

    // init
    private Propellant simPropellant;
    private Combustion_test simCombustor;
    private Nozzle simNozzle;
    private ThermodynamicSolver solver;

    // Constants
    // Sea level stuff this will be replaced with an atmo model (maybe)
    private double AMBIENT_PRESSURE = 101325.0; 
    private double G0 = 9.80665;

    public StateEstimation() {
        this.simPropellant = new Propellant();
        this.simCombustor = new Combustion_test();
        this.simNozzle = new Nozzle();
        // these values are just guess for our combustor state
        this.solver = new ThermodynamicSolver(1.4, 287.05, 2_000_000, 3000);
    }

    public static void main(String[] args) {
        StateEstimation simulation = new StateEstimation();
        simulation.calcPrelim();
        simulation.calcFinal();
    }

    public void calcPrelim()
    {
        // nozzle values that give us Mach 3 at the exit (just to test the code is right)
        simNozzle.setEntranceArea(0.15); // m^2
        simNozzle.setThroatArea(0.05); // m^2
        simNozzle.setExitArea(0.2115); // m^2 

        double contractionRatio = simNozzle.getEntranceArea() / simNozzle.getThroatArea();
        double expansionRatio = simNozzle.getExitArea() / simNozzle.getThroatArea();

        // solve for Mach numbers
        double entranceMach = solver.solveMachFromAreaRatio(contractionRatio, false); // Should be Subsonic
        double exitMach = solver.solveMachFromAreaRatio(expansionRatio, true); // Should be Supersonic
        double throatMach = 1.0; // Choked flow assumption

        // flow states
        FlowState combustor = solver.computeIsentropicFlow("Combustor", 0.0);
        FlowState entrance = solver.computeIsentropicFlow("Entrance", entranceMach);
        FlowState throat = solver.computeIsentropicFlow("Throat", throatMach);
        FlowState exit = solver.computeIsentropicFlow("Exit", exitMach);

        // This is incorrect as there are no shock relations added yet
        FlowState exhaust = new FlowState(
                "Exhaust (Plume)",
                AMBIENT_PRESSURE,
                exit.getTemperature(),
                exit.getMachNumber(),
                exit.getVelocity());

        // may not be needed for this section will depend on how PreLim is created
        simNozzle.stateOne = combustor.getPressure();
        simNozzle.stateTwo = entrance.getPressure();
        simNozzle.stateThree = throat.getPressure();
        simNozzle.stateFour = exit.getPressure();
        simNozzle.stateFive = exhaust.getPressure();
        
        // Broken need to fix this
        // simCombustor.stateOne = combustor.getPressure();

        // print report
        System.out.println("=== NOZZLE KINEMATICS ===");
        System.out.printf("Contraction Ratio: %.2f  -> Entrance Mach: %.3f\n", contractionRatio, entranceMach);
        System.out.printf("Expansion Ratio:   %.2f  -> Exit Mach:     %.3f\n\n", expansionRatio, exitMach);

        System.out.println("=== PRELIMINARY STATE ESTIMATION ===");
        combustor.printState();
        entrance.printState();
        throat.printState();
        exit.printState();
        exhaust.printState();
    }

    public void calcFinal() 
    {
        // TO-DO change this to accept calculated exit mach
        double exitMach = 3.0; // Design expansion ratio

        // get thrust data
        ThrustData performance = solver.calculateThrustState(simNozzle.getThroatArea(), exitMach, AMBIENT_PRESSURE);
        double totalThrust = performance.getThrustForce();
        double massFlowRate = performance.getMassFlowRate();
        double exitPressure = performance.getExitPressure();

 
        double isp = totalThrust / (massFlowRate * G0);

        // Calculate Optimal Altitude (rudimentary model this will be changed)
        double tempLapseRate = -0.0065;
        double t0 = 288.15;
        double exponent = -(287.05 * tempLapseRate) / G0;
        double optimalAlt = (t0 / tempLapseRate) * (Math.pow((exitPressure / AMBIENT_PRESSURE), exponent) - 1.0);

        // Print the Report
        System.out.println("=== FINAL ENGINE PERFORMANCE METRICS ===");
        System.out.printf("Mass Flow Rate: %.2f kg/s\n", massFlowRate);
        System.out.printf("Total Thrust:   %,.0f N (%.2f kN)\n", totalThrust, totalThrust / 1000.0);
        System.out.printf("Specific Impulse:%,.1f s\n", isp);

        if (optimalAlt > 11000) {
            System.out.printf("Optimal Alt:    > 11,000 m (Pe = %,.0f Pa)\n", exitPressure);
        } else {
            System.out.printf("Optimal Alt:    %,.0f meters\n", optimalAlt);
        }
    }

    // TODO Add shock relations

}
