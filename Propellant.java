public class Propellant {

    private String name;
    private double propellantArea;
    private double density;
    private double burnRateCoefficient;
    private double pressureExponent;
    private double specificHeatRatio;
    private double flameTemperature;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPropellantArea() {
        return propellantArea;
    }

    public void setPropellantArea(double propellantArea) {
        this.propellantArea = propellantArea;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getBurnRateCoefficient() {
        return burnRateCoefficient;
    }

    public void setBurnRateCoefficient(double burnRateCoefficient) {
        this.burnRateCoefficient = burnRateCoefficient;
    }

    public double getPressureExponent() {
        return pressureExponent;
    }

    public void setPressureExponent(double pressureExponent) {
        this.pressureExponent = pressureExponent;
    }

    public double getSpecificHeatRatio() {
        return specificHeatRatio;
    }

    public void setSpecificHeatRatio(double specificHeatRatio) {
        this.specificHeatRatio = specificHeatRatio;
    }

    public double getFlameTemperature() {
        return flameTemperature;
    }

    public void setFlameTemperature(double flameTemperature) {
        this.flameTemperature = flameTemperature;
    }
}
