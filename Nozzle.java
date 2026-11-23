public class Nozzle {
    
    public double stateOne;
    public double stateTwo;
    public double stateThree;
    public double stateFour;
    public double stateFive;

    // Physical attributes
    private double entranceArea;
    private double throatArea;
    private double exitArea;
    private double nozzleLength;

    public Nozzle(double entranceArea, double throatArea, double exitArea) {
        setEntranceArea(entranceArea);
        setThroatArea(throatArea);
        setExitArea(exitArea);
    }

    public double getEntranceArea() { 
        return entranceArea; 
    }
    public void setEntranceArea(double entranceArea) { 
        if (entranceArea <= 0) {
            throw new IllegalArgumentException("Entrance area must be positive");
        }
        this.entranceArea = entranceArea; 
    }

    public double getThroatArea() { 
        return throatArea; 
    }
    public void setThroatArea(double throatArea) { 
        if (throatArea <= 0) {
            throw new IllegalArgumentException("Throat area must be positive");
        }
        this.throatArea = throatArea; 
    }

    public double getExitArea() { 
        return exitArea; 
    }
    public void setExitArea(double exitArea) { 
        if (exitArea <= 0) {
            throw new IllegalArgumentException("Exit area must be positive");
        }
        this.exitArea = exitArea; 
    }

    public double getNozzleLength() { 
        return nozzleLength; 
    }
    public void setNozzleLength(double nozzleLength) { 
        if (nozzleLength <= 0) {
            throw new IllegalArgumentException("Nozzle length must be positive");
        }
        this.nozzleLength = nozzleLength; 
    }
}