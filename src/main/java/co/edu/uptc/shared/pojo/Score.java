package co.edu.uptc.shared.pojo;

public class Score {
    private int total;
    private int partial;

    public Score() {
        this.total = 0;
        this.partial = 0;
    }

    public void increment() {
        this.total++;
        this.partial++;
    }

    public void resetPartial() {
        this.partial = 0;
    }

    public int getTotal() {
        return total;
    }

    public int getPartial() {
        return partial;
    }
}
