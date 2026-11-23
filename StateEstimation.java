import java.util.Objects;

public class StateEstimation {

    // init
    private Propellant simPropellant;
    private Nozzle simNozzle;
    private ThermodynamicSolver solver;

    private final double ambientPressure;

    // Results
    private double contractionRatio;
    private double expansionRatio;
    private double entranceMach;
    private double designExitMach;
    private double designExitPressure;
    private double exitShockPressure;
    private NozzleRegime regime;
    private double shockUpstreamMach = Double.NaN; // NaN unless regime is SHOCK_IN_NOZZLE
    private double shockArea = Double.NaN;
    private double kn = Double.NaN;                // NaN unless built from a Propellant
    private double optimalAltitude;
    private FlowState exitState;
    private FlowState[] flowStates;
    private double[] stageAreas;
    private ThrustData[] stageThrustData;
    private ThrustData performanceData;

    // Use when chamber pressure is given directly THIS ISNT NEEDED ANYMORE
    public StateEstimation(double gamma, double gasConstant, double chamberPressure,
            double chamberTemp, double ambientPressure, Nozzle nozzle) {
        Objects.requireNonNull(nozzle, "Nozzle must not be null");

        double entranceArea = nozzle.getEntranceArea();
        double throatArea = nozzle.getThroatArea();
        double exitArea = nozzle.getExitArea();

        // The area-Mach solver needs A/A* > 1 on both sides of the throat
        if (throatArea <= 0 || entranceArea <= throatArea || exitArea <= throatArea) {
            throw new IllegalArgumentException(String.format(
                    "Invalid nozzle geometry: entrance (%.4f) and exit (%.4f) must both be larger than throat (%.4f) m^2",
                    entranceArea, exitArea, throatArea));
        }
        if (!(ambientPressure > 0)) {
            throw new IllegalArgumentException("Ambient pressure must be positive");
        }

        this.simPropellant = new Propellant();
        this.simNozzle = nozzle;
        this.solver = new ThermodynamicSolver(gamma, gasConstant, chamberPressure, chamberTemp);
        this.ambientPressure = ambientPressure;

        // calcFinal() needs state from calcPrelim()
        calcPrelim();
        calcFinal();
    }

    // Chamber pressure solved from the propellant. Its area is the burning surface A_b
    public StateEstimation(Propellant propellant, double ambientPressure, Nozzle nozzle) {
        this(Objects.requireNonNull(propellant, "Propellant must not be null").getSpecificHeatRatio(),
                propellant.getGasConstant(),
                chamberPressureForSolver(propellant, Objects.requireNonNull(nozzle, "Nozzle must not be null").getThroatArea()),
                propellant.getFlameTemperature(),
                ambientPressure,
                nozzle);
        this.simPropellant = propellant;
        this.kn = propellant.getPropellantArea() / nozzle.getThroatArea();
    }

    private static double chamberPressureForSolver(Propellant prop, double throatArea) {
        return ThermodynamicSolver.equilibriumChamberPressure(prop.getPropellantArea(), throatArea,
                prop.getDensity(), prop.getBurnRateCoefficient(), prop.getPressureExponent(),
                prop.getSpecificHeatRatio(), prop.getGasConstant(), prop.getFlameTemperature());
    }

    private void calcPrelim() {
        double entranceArea = simNozzle.getEntranceArea();
        double throatArea = simNozzle.getThroatArea();
        double exitArea = simNozzle.getExitArea();

        this.contractionRatio = entranceArea / throatArea;
        this.expansionRatio = exitArea / throatArea;
        this.entranceMach = solver.solveMachFromAreaRatio(contractionRatio, false); // Subsonic
        this.designExitMach = solver.solveMachFromAreaRatio(expansionRatio, true);  // Supersonic
        this.designExitPressure = solver.designExitPressure(expansionRatio);
        this.exitShockPressure = solver.exitShockPressure(expansionRatio);

        // Decide the flow regime (throws if the nozzle won't choke)
        this.regime = solver.classifyRegime(expansionRatio, ambientPressure);

        FlowState combustor = solver.computeIsentropicFlow("Combustor", 0.0);
        FlowState entrance = solver.computeIsentropicFlow("Entrance", entranceMach);
        FlowState throat = solver.computeIsentropicFlow("Throat", 1.0); // Choked

        if (regime == NozzleRegime.SHOCK_IN_NOZZLE) {
            // Subsonic exit at ambient pressure fixes the exit Mach and the post-shock total pressure
            double exitMach = solver.solveShockInNozzleExitMach(ambientPressure, throatArea, exitArea);
            double exitTotalPressure = ambientPressure * solver.stagnationPressureRatio(exitMach);

            // The total pressure lost tells us the shock strength, which tells us where it sits
            this.shockUpstreamMach = solver.solveMachFromTotalPressureRatio(
                    exitTotalPressure / solver.getChamberPressure());
            this.shockArea = solver.areaRatioFromMach(shockUpstreamMach) * throatArea;

            FlowState preShock = solver.computeIsentropicFlow("Pre-Shock", shockUpstreamMach);
            FlowState postShock = solver.computeNormalShock("Post-Shock", preShock);
            this.exitState = solver.computeIsentropicFlow("Exit (subsonic)", exitMach, exitTotalPressure);

            this.flowStates = new FlowState[] { combustor, entrance, throat, preShock, postShock, exitState };
            this.stageAreas = new double[] { entranceArea, entranceArea, throatArea, shockArea, shockArea, exitArea };
        } else {
            // Shock-free inside; any mismatch with ambient is resolved outside nozzle
            this.exitState = solver.computeIsentropicFlow("Exit", designExitMach);
            this.flowStates = new FlowState[] { combustor, entrance, throat, exitState };
            this.stageAreas = new double[] { entranceArea, entranceArea, throatArea, exitArea };
        }

        simNozzle.stateOne = combustor.getPressure();
        simNozzle.stateTwo = entrance.getPressure();
        simNozzle.stateThree = throat.getPressure();
        simNozzle.stateFour = exitState.getPressure();
        simNozzle.stateFive = ambientPressure; // The exhaust jet ends up at ambient pressure
    }

    private void calcFinal() {
        double massFlow = solver.chokedMassFlow(simNozzle.getThroatArea());
        this.stageThrustData = new ThrustData[flowStates.length];

        for (int i = 0; i < flowStates.length; i++) {
            this.stageThrustData[i] = solver.calculateThrust(
                    massFlow, flowStates[i], stageAreas[i], ambientPressure);
        }

        // Keep performanceData as the exit stage
        this.performanceData = stageThrustData[stageThrustData.length - 1];

        // Alt where ambient pressure equals design exit pressure
        this.optimalAltitude = StandardAtmosphere.altitudeForPressure(designExitPressure);
    }

    public void printReport() {
        System.out.println("=== COMBUSTION CHAMBER ===");
        if (simPropellant.getName() != null) {
            System.out.printf("Propellant:       %s (Kn = %.1f)\n", simPropellant.getName(), kn);
        }
        System.out.printf("Chamber Pressure: %,.0f Pa (%s)\n", solver.getChamberPressure(),
                Double.isNaN(kn) ? "given" : "solved");
        System.out.printf("Chamber Temp:     %.1f K\n", solver.getChamberTemp());
        System.out.printf("c*:               %.1f m/s\n\n", solver.getCharacteristicVelocity());

        System.out.println("=== NOZZLE ===");
        System.out.printf("Contraction Ratio: %.2f  -> Entrance Mach:    %.3f\n", contractionRatio, entranceMach);
        System.out.printf("Expansion Ratio:   %.2f  -> Design Exit Mach: %.3f\n", expansionRatio, designExitMach);
        System.out.printf("Back Pressure:     %,.0f Pa\n", ambientPressure);
        System.out.printf("Shock at exit if:  Pa >= %,.0f Pa\n", exitShockPressure);
        System.out.printf("Design exit P:     %,.0f Pa\n", designExitPressure);
        System.out.printf("Regime: %s - %s\n\n", regime, regime.getDescription());

        if (hasNormalShock()) {
            System.out.println("=== NORMAL SHOCK IN NOZZLE ===");
            System.out.printf("Upstream Mach: %.3f\n", shockUpstreamMach);
            System.out.printf("Shock Area:    %.4f m^2 (throat %.4f, exit %.4f)\n\n",
                    shockArea, simNozzle.getThroatArea(), simNozzle.getExitArea());
        }

        System.out.println("=== STATE ESTIMATION ===");
        for (int i = 0; i < flowStates.length; i++) {
            flowStates[i].printState();
            ThrustData td = stageThrustData[i];
            System.out.printf("Stage Thrust: %,.0f N | Stage Isp: %,.1f s\n\n",
                    td.getThrustForce(), td.getIsp());
        }

        double totalThrust = performanceData.getThrustForce();

        System.out.println("=== FINAL ENGINE PERFORMANCE METRICS ===");
        System.out.printf("Mass Flow Rate:   %.2f kg/s\n", performanceData.getMassFlowRate());
        System.out.printf("Total Thrust:     %,.0f N (%.2f kN)\n", totalThrust, totalThrust / 1000.0);
        System.out.printf("Specific Impulse: %,.1f s\n", performanceData.getIsp());
        System.out.printf("Optimal Alt:      %s\n", getOptimalAltitudeText());
    }

    // Getters
    public FlowState[] getFlowStates() { 
        return flowStates; }
    public FlowState getExitState() { 
        return exitState; }
    public ThrustData[] getStageThrustData() { 
        return stageThrustData; }
    public ThrustData getPerformanceData() { 
        return performanceData; }
    public NozzleRegime getRegime() { 
        return regime; }
    public String getRegimeDescription() { 
        return regime.getDescription(); }
    public double getChamberPressure() { 
        return solver.getChamberPressure(); }
    public double getChamberTemp() { 
        return solver.getChamberTemp(); }
    public double getCharacteristicVelocity() { 
        return solver.getCharacteristicVelocity(); }
    public double getAmbientPressure() { 
        return ambientPressure; }
    public double getContractionRatio() { 
        return contractionRatio; }
    public double getExpansionRatio() { 
        return expansionRatio; }
    public double getEntranceMach() { 
        return entranceMach; }
    public double getDesignExitMach() { 
        return designExitMach; }
    public double getDesignExitPressure() { 
        return designExitPressure; }
    public double getExitShockPressure() { 
        return exitShockPressure; }
    public double getShockUpstreamMach() { 
        return shockUpstreamMach; }
    public double getShockArea() { 
        return shockArea; }
    public double getKn() { 
        return kn; }
    public double getOptimalAltitude() { 
        return optimalAltitude; }

    // dont know if this is needed
    public boolean hasNormalShock() {
        return regime == NozzleRegime.SHOCK_IN_NOZZLE;
    }

    public String getOptimalAltitudeText() {
        if (Double.isNaN(optimalAltitude)) {
            return "Below sea level";
        }
        if (Double.isInfinite(optimalAltitude)) {
            return "Above 11,000 m";
        }
        return String.format("%,.0f m", optimalAltitude);
    }
}