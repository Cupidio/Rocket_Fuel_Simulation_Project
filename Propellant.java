public class Propellant {

    // All values in SI units
    private String name;
    private double propellantArea; // Burning surface area A_b (m^2)
    private double density; // kg/m^3
    private double burnRateCoefficient; // St. Robert's law a (m/s per Pa^n)
    private double pressureExponent; // St. Robert's law n
    private double specificHeatRatio; // Gamma of the combustion products
    private double flameTemperature; // K
    private double gasConstant; // Specific gas constant of the products, J/(kg*K)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public double getPropellantArea() {
        return propellantArea;
    }

    public void setPropellantArea(double propellantArea) {
        if (propellantArea <= 0) {
            throw new IllegalArgumentException("Propellant area must be positive");}
        this.propellantArea = propellantArea;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        if (density <= 0) {
            throw new IllegalArgumentException("Density must be positive"); }
        this.density = density;
    }

    public double getBurnRateCoefficient() {
        return burnRateCoefficient;
    }

    public void setBurnRateCoefficient(double burnRateCoefficient) {
        if (burnRateCoefficient <= 0) {
            throw new IllegalArgumentException("Burn rate coefficient must be positive");}
        this.burnRateCoefficient = burnRateCoefficient;
    }

    public double getPressureExponent() {
        return pressureExponent;
    }

    public void setPressureExponent(double pressureExponent) {
        if (pressureExponent < 0 || pressureExponent >= 1.0) {
            throw new IllegalArgumentException("Pressure exponent (n) must satisfy 0 <= n < 1");}
        this.pressureExponent = pressureExponent;
    }

    public double getSpecificHeatRatio() {
        return specificHeatRatio;
    }

    public void setSpecificHeatRatio(double specificHeatRatio) {
        if (specificHeatRatio <= 1.0) {
            throw new IllegalArgumentException("Specific heat ratio (gamma) must be > 1.0");}
        this.specificHeatRatio = specificHeatRatio;
    }

    public double getFlameTemperature() {
        return flameTemperature;
    }

    public void setFlameTemperature(double flameTemperature) {
        if (flameTemperature <= 0) {
            throw new IllegalArgumentException("Flame temperature must be positive");}
        this.flameTemperature = flameTemperature;
    }

    public double getGasConstant() {
        return gasConstant;
    }

    public void setGasConstant(double gasConstant) {
        if (gasConstant <= 0) {
            throw new IllegalArgumentException("Gas constant must be positive");}
        this.gasConstant = gasConstant;
    }
}
