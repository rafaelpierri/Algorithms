import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int n;
    private final boolean[] openSites;
    private final WeightedQuickUnionUF unionFind;
    private int openSitesCount = 0;

    public Percolation(int n) {
        if (n < 1) throw new IllegalArgumentException();
        this.n = n;
        this.unionFind = new WeightedQuickUnionUF(n * n + 2);
        this.openSites = new boolean[n * n + 2];
        this.openSites[0] = true;
        this.openSites[this.openSites.length - 1] = true;
    }

    public void open(int row, int col) {
        validateArguments(row, col);

        int target = getTarget(row, col);
        if (this.openSites[target]) return;

        this.openSites[target] = true;
        this.openSitesCount++;

        if (target <= n) {
            unite(0, target);
        }

        if (target > n * (n - 1)) {
            unite(n * n + 1, target);
        }

        if ((target - 1) % n != 0 && this.openSites[target - 1]) {
            unite(target - 1, target);
        }

        if (target % n != 0 && this.openSites[target + 1]) {
            unite(target + 1, target);
        }

        if (target - n > 0 && this.openSites[target - n]) {
            unite(target - n, target);
        }

        if (target + n <= n * n && this.openSites[target + n]) {
            unite(target + n, target);
        }

    }

    private void unite(int targetA, int targetB) {
        int valueA = this.unionFind.find(targetA);
        int valueB = this.unionFind.find(targetB);

        if (valueA < valueB) {
            this.unionFind.union(targetA, targetB);
        } else {
            this.unionFind.union(targetB, targetA);
        }
    }

    private void validateArguments(int row, int col) {
        if (row < 1) throw new IllegalArgumentException();
        if (row > n) throw new IllegalArgumentException();
        if (col < 1) throw new IllegalArgumentException();
        if (col > n) throw new IllegalArgumentException();
    }

    public boolean isOpen(int row, int col) {
        validateArguments(row, col);
        int target = getTarget(row, col);
        return this.openSites[target];
    }

    public boolean isFull(int row, int col) {
        validateArguments(row, col);
        int target = getTarget(row, col);

        return this.unionFind.find(0) == this.unionFind.find(target);
    }

    private int getTarget(int row, int col) {
        return this.n * (row - 1) + col;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.openSitesCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.unionFind.find(0) == this.unionFind.find(n * n + 1);
    }

    // test client (optional)
    public static void main(String[] args) {
    }
}
