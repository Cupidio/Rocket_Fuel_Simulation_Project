public class CombustionGas {
    private static final double UNIVERSALGASCONST = 8314.0;

    private final double specificHeatRatio;
    private final double flameTemp;
    private final double molarMass;

    private final double specificGasConst;
    private final double cP;
    private final double kerckhoveConst; 
    private final double cStar;

    public CombustionGas(double specificHeatRatio, double flameTemp, double molarMass) {
        if (specificHeatRatio <= 1) {
            throw new IllegalArgumentException("Specific heat ratio must be greater than 1, but was " + specificHeatRatio);
        }
        this.specificHeatRatio = specificHeatRatio;
        this.flameTemp         = InputCheck.requirePositive("Flame temperature", flameTemp);
        this.molarMass         = InputCheck.requirePositive("Molar mass", molarMass);

        double k = specificHeatRatio;
        specificGasConst = UNIVERSALGASCONST / molarMass;
        cP               = k * specificGasConst / (k - 1);
        kerckhoveConst   = Math.sqrt(k) * Math.pow(2 / (k + 1), (k + 1) / (2 * (k - 1)));
        cStar            = Math.sqrt(specificGasConst * flameTemp) / kerckhoveConst;
    }

    public double pressure(double density, double axialMomentum, double radialMomentum, double totalEnergy) {
        double axialVelocity  = axialMomentum / density;
        double radialVelocity = radialMomentum / density;
        double kineticEnergy  = 0.5 * density * (axialVelocity * axialVelocity + radialVelocity * radialVelocity);
        return (specificHeatRatio - 1) * (totalEnergy - kineticEnergy);
    }

    public double totalEnergy(double density, double axialVelocity, double radialVelocity, double pressure) {
        double kineticEnergy = 0.5 * density * (axialVelocity * axialVelocity + radialVelocity * radialVelocity);
        return pressure / (specificHeatRatio - 1) + kineticEnergy;
    }

    public double soundSpeed(double pressure, double density) {
        return Math.sqrt(specificHeatRatio * pressure / density);
    }

    public double temperature(double pressure, double density) {
        return pressure / (density * specificGasConst);
    }

    public double getSpecificHeatRatio() {
        return specificHeatRatio;
    }

    public double getFlameTemp() {
        return flameTemp;
    }

    public double getMolarMass() {
        return molarMass;
    }

    public double getSpecificGasConst() {
        return specificGasConst;
    }

    public double getCP() {
        return cP;
    }

    public double getKerckhoveConst() {
        return kerckhoveConst;
    }

    public double getCStar() {
        return cStar;
    }
}
