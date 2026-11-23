// Used to meet layered diagram requirement of separate input validation - will be distributed to necessary classes at later time
public class InputCheck {
    public static double requirePositive(String name, double value) {
        if (value <= 0 || Double.isNaN(value)) {
            throw new IllegalArgumentException(name + " must be positive, but was " + value);
        }
        return value;
    }

    public static int requirePositive(String name, int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive, but was " + value);
        }
        return value;
    }
}
