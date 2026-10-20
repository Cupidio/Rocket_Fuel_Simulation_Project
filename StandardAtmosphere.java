public class StandardAtmosphere {
    private static final double P0 = 101325.0; // Sea level standard pressure (Pa)
    private static final double T0 = 288.15; // Sea level standard temperature (K)
    private static final double L = -0.0065; // Temperature lapse rate (K/m)
    private static final double G0 = 9.80665; // Standard gravity (m/s^2)
    private static final double R_AIR = 287.05;// Specific gas constant for air

    public static double getSeaLevelPressure() {
        return P0;
    }

    public static double getStandardGravity() {
        return G0;
    }

    // Calculates altitude from pressure using the ISA Troposphere model (up to
    // 11,000m)
    public static double calculateAltitudeFromPressure(double pressure) {
        double exponent = -(R_AIR * L) / G0;
        return (T0 / L) * (Math.pow((pressure / P0), exponent) - 1.0);
    }
}