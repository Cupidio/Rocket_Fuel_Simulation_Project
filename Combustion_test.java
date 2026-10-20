public class Combustion_test extends Prelim {
    // Physical attributes
    private double combustorLength;
    private double combustorArea;

    // c: CHECK THE ID FROM THE PRELIM
    public Combustion_test() {
        super(0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public double getCombustorLength() {
        return combustorLength;
    }

    public void setCombustorLength(double combustorLength) {
        this.combustorLength = combustorLength;
    }

    public double getCombustorArea() {
        return combustorArea;
    }

    public void setCombustorArea(double combustorArea) {
        this.combustorArea = combustorArea;
    }
}