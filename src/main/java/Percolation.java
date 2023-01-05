import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private static final int SITE_OPENING = 0;
    private static final int CONNECTS_WITH_TOP = 1;
    private static final int CONNECTS_WITH_BOTTOM = 2;
    private final int n;
    private final boolean[][] settings;
    private final WeightedQuickUnionUF unionFind;
    private int openSitesCount = 0;
    private boolean percolates;

    public Percolation(int n) {
        if (n < 1) throw new IllegalArgumentException();
        this.n = n;
        this.unionFind = new WeightedQuickUnionUF(n * n);
        this.settings = new boolean[n * n][3];
    }

    public void open(int row, int col) {
        validateArguments(row, col);

        int target = getTarget(row, col);
        if (this.settings[target][SITE_OPENING]) return;

        this.settings[target][SITE_OPENING] = true;
        this.openSitesCount++;

        if (target < n) {
            this.settings[target][CONNECTS_WITH_TOP] = true;
        }

        if (target >= n * (n - 1)) {
            this.settings[target][CONNECTS_WITH_BOTTOM] = true;
        }

        if (this.settings[target][CONNECTS_WITH_TOP] && this.settings[target][CONNECTS_WITH_BOTTOM]) {
            this.percolates = true;
        }

        if (target > 0 && target / n == (target - 1) / n && this.settings[target - 1][SITE_OPENING]) {
            unite(target - 1, target);
        }

        if (target < n * n - 1 && target / n == (target + 1) / n && this.settings[target + 1][SITE_OPENING]) {
            unite(target + 1, target);
        }

        if (target - n >= 0 && this.settings[target - n][SITE_OPENING]) {
            unite(target - n, target);
        }

        if (target + n < n * n && this.settings[target + n][SITE_OPENING]) {
            unite(target + n, target);
        }

    }

    private void unite(int targetA, int targetB) {
        if (this.settings[targetA][CONNECTS_WITH_TOP] || this.settings[targetB][CONNECTS_WITH_TOP]) {
            this.settings[targetA][CONNECTS_WITH_TOP] = true;
            this.settings[targetB][CONNECTS_WITH_TOP] = true;
        }

        if (this.settings[targetA][CONNECTS_WITH_BOTTOM] || this.settings[targetB][CONNECTS_WITH_BOTTOM]) {
            this.settings[targetA][CONNECTS_WITH_BOTTOM] = true;
            this.settings[targetB][CONNECTS_WITH_BOTTOM] = true;
        }

        if (this.settings[targetA][CONNECTS_WITH_TOP] && this.settings[targetB][CONNECTS_WITH_BOTTOM]) {
            this.percolates = true;
        }

        this.unionFind.union(targetA, targetB);
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
        return this.settings[target][SITE_OPENING];
    }

    public boolean isFull(int row, int col) {
        validateArguments(row, col);
        int target = getTarget(row, col);

        return this.settings[target][CONNECTS_WITH_TOP];
    }

    private int getTarget(int row, int col) {
        return this.n * (row - 1) + col - 1;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.openSitesCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.percolates;
    }

    // test client (optional)
    public static void main(String[] args) {
    }
}
