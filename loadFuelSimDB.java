import java.util.ArrayList;

public class loadFuelSimDB {
    private ArrayList<Prelim> pre;
    private ArrayList<Final> fin;

    public loadFuelSimDB() {
        pre = new ArrayList<>();
        fin = new ArrayList<>();
    }

    public void addPrelim(Prelim prelim) {
        pre.add(prelim);
    }

    public void addFinal(Final finalState) {
        fin.add(finalState);
    }

    public ArrayList<Prelim> getPrelimStates() {
        return pre;
    }

    public ArrayList<Final> getFinalStates() {
        return fin;
    }

    public Prelim findPrelimByID(String id) {
        for (Prelim p : pre) {
            if (p.getId() == Integer.parseInt(id)) {
                return p;
            }
        }
        return null;
    }

    public Final findFinalByID(String id) {
        for (Final f : fin) {
            if (f.getId() == Integer.parseInt(id)) {
                return f;
            }
        }
        return null;
    }

}
