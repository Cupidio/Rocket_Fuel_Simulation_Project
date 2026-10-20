public class StateEstimation {

    // init
    private Propellant simPropellant;
    private Combustion_test simCombustor;
    private Nozzle simNozzle;
    private ThermodynamicSolver solver;

    // Constants
    private double AMBIENT_PRESSURE = 101325.0; // Sea level
    private double G0 = 9.80665;

    public StateEstimation() {
        this.simPropellant = new Propellant();
        this.simCombustor = new Combustion_test();
        this.simNozzle = new Nozzle();

        // Initialize the solver at the class level
        this.solver = new ThermodynamicSolver(1.4, 287.05, 2_000_000, 3000);
    }

    public static void main(String[] args) {
        StateEstimation simulation = new StateEstimation();
        simulation.calcPrelim();
        simulation.calcFinal();
    }

    public void calcPrelim() {
        // Define the physical geometry in your Domain Model
        simNozzle.setEntranceArea(0.15); // m^2
        simNozzle.setThroatArea(0.05); // m^2
        simNozzle.setExitArea(0.2115); // m^2 (Example yielding an Expansion Ratio of 4.23)

        // Calculate Area Ratios
        double contractionRatio = simNozzle.getEntranceArea() / simNozzle.getThroatArea();
        double expansionRatio = simNozzle.getExitArea() / simNozzle.getThroatArea();

        // solve for Mach numbers based on the geometry
        double entranceMach = solver.solveMachFromAreaRatio(contractionRatio, false); // Subsonic
        double exitMach = solver.solveMachFromAreaRatio(expansionRatio, true); // Supersonic
        double throatMach = 1.0; // Choked flow assumption

        // Calculate states using the solver with the new dynamic Mach numbers
        FlowState combustor = solver.computeIsentropicFlow("Combustor", 0.0);
        FlowState entrance = solver.computeIsentropicFlow("Entrance", entranceMach);
        FlowState throat = solver.computeIsentropicFlow("Throat", throatMach);
        FlowState exit = solver.computeIsentropicFlow("Exit", exitMach);

        FlowState exhaust = new FlowState(
                "Exhaust (Plume)",
                AMBIENT_PRESSURE,
                exit.getTemperature(),
                exit.getMachNumber(),
                exit.getVelocity());

        // Map the pressures into your UML data models
        simNozzle.stateOne = combustor.getPressure();
        simNozzle.stateTwo = entrance.getPressure();
        simNozzle.stateThree = throat.getPressure();
        simNozzle.stateFour = exit.getPressure();
        simNozzle.stateFive = exhaust.getPressure();
        simCombustor.stateOne = combustor.getPressure();

        // Print the complete preliminary report
        System.out.println("--- NOZZLE KINEMATICS ---");
        System.out.printf("Contraction Ratio: %.2f  -> Entrance Mach: %.3f\n", contractionRatio, entranceMach);
        System.out.printf("Expansion Ratio:   %.2f  -> Exit Mach:     %.3f\n\n", expansionRatio, exitMach);

        System.out.println("--- PRELIMINARY STATE ESTIMATION ---");
        combustor.printState();
        entrance.printState();
        throat.printState();
        exit.printState();
        exhaust.printState();
    }

    public void calcFinal() {
        // to-do change this
        double exitMach = 3.0; // Design expansion ratio

        // Ask the solver for the thrust performance data
        ThrustData performance = solver.calculateThrustState(simNozzle.getThroatArea(), exitMach, AMBIENT_PRESSURE);

        // Extract values from the ThrustData object
        double totalThrust = performance.getThrustForce();
        double massFlowRate = performance.getMassFlowRate();
        double exitPressure = performance.getExitPressure();

        // Calculate Specific Impulse (Isp)
        double isp = totalThrust / (massFlowRate * G0);

        // Calculate Optimal Altitude (Optional extra logic)
        double l = -0.0065;
        double t0 = 288.15;
        double exponent = -(287.05 * l) / G0;
        double optimalAlt = (t0 / l) * (Math.pow((exitPressure / AMBIENT_PRESSURE), exponent) - 1.0);

        // Print the Report
        System.out.println("--- FINAL ENGINE PERFORMANCE METRICS ---");
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