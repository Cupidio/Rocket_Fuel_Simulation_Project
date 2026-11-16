public class Combustion_test {
    // Physical attributes
    private double combustorLength;
    private double combustorArea;

    // c: CHECK THE ID FROM THE PRELIM
    // public Combustion_test() {
    //     super(0, 0.0, 0.0, 0.0, 0.0, 0.0);
    // }

    public double getCombustorLength() {
        return combustorLength;
    }

    public void setCombustorLength(double combustorLength) {
        if (combustorLength <= 0) {
            throw new IllegalArgumentException("Combustor length must be greater than 0");
        }
        this.combustorLength = combustorLength;
    }

    public double getCombustorArea() {
        return combustorArea;
    }

    public void setCombustorArea(double combustorArea) {
        if (combustorArea <= 0) {
            throw new IllegalArgumentException("Combustor area must be greater than 0");
        }
        this.combustorArea = combustorArea;
    }
}
