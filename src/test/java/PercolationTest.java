import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class PercolationTest {

    private WeightedQuickUnionUF extractUnionFind(Percolation percolation) {
        Class<?> secretClass = percolation.getClass();
        Field unionFindField = null;
        try {
            unionFindField = secretClass.getDeclaredField("unionFind");
            unionFindField.setAccessible(true);
            return (WeightedQuickUnionUF) unionFindField.get(percolation);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    class Constructor {
        @Test
        void instantiatesUnionFindWithVirtualNodes() {
            Percolation percolation = new Percolation(10);
            assertEquals(0, percolation.numberOfOpenSites());
            assertFalse(percolation.isOpen(10, 10));
        }

        @Test
        void doesNotAllowNonPositiveN() {
            assertThrows(IllegalArgumentException.class, () -> new Percolation(0));
        }
    }

    @Nested
    class Percolates {
        @Test
        void doesNotPercolateIfThereIsNotAPathBetweenTopAndBottomVirtualNode() {
            Percolation percolation = new Percolation(3);
            percolation.open(1, 2);
            percolation.open(2, 2);
            assertFalse(percolation.percolates());
        }

        @Test
        void percolatesIfThereIsAPathBetweenTopAndBottomVirtualNode() {
            Percolation percolation = new Percolation(3);
            percolation.open(1, 2);
            percolation.open(2, 2);
            percolation.open(3, 2);
            assertTrue(percolation.percolates());
        }

        @Test
        void percolatesWithSingleSite() {
            Percolation percolation = new Percolation(1);
            percolation.open(1, 1);
            assertTrue(percolation.percolates());
        }

        @Test
        void percolatesWithFourSites() {
            Percolation percolation = new Percolation(2);
            percolation.open(2, 2);
            percolation.open(1, 1);
            percolation.open(1, 2);

            assertTrue(percolation.percolates());
        }
    }

    @Nested
    class IsFull {
        @Test
        void isNotFullIfThereIsNotAPathBetweenTopVirtualNodeAndTargetSite() {
            Percolation percolation = new Percolation(3);
            percolation.open(2, 2);
            assertFalse(percolation.isFull(2, 2));
        }

        @Test
        void isFullIfThereIsAnOpenSiteInTheTopRow() {
            Percolation percolation = new Percolation(3);
            percolation.open(1, 2);
            assertTrue(percolation.isFull(1, 2));
        }

        @Test
        void isFullIfThereAPathBetweenTopVirtualNodeAndTargetSite() {
            Percolation percolation = new Percolation(3);
            percolation.open(1, 2);
            percolation.open(2, 2);
            assertTrue(percolation.isFull(2, 2));
        }

        @Test
        void returnsWrongValueAfterOneSiteOpened() {
            Percolation percolation = new Percolation(6);
            percolation.open(1, 6);
            assertTrue(percolation.isFull(1, 6));
        }

        @Test
        void doesNotAllowRowValueLessThan1() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isFull(0, 3));
        }

        @Test
        void doesNotAllowRowValueGreaterThanN() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isFull(4, 3));
        }

        @Test
        void doesNotAllowColValueLessThan1() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isFull(3, 0));
        }

        @Test
        void doesNotAllowColValueGreaterThanN() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isFull(3, 4));
        }

        @Test
        void doesNotBackwash() {
            Percolation percolation = new Percolation(3);
            percolation.open(2, 1);
            percolation.open(1, 3);
            percolation.open(2, 3);
            percolation.open(3, 1);
            percolation.open(3, 3);
            assertFalse(percolation.isFull(3, 1));
            assertTrue(percolation.percolates());
        }

        @Test
        void passesInput20TestCase() throws IOException {
            Percolation percolation = loadPercolationFromResourceFile("input20.txt");
            assertFalse(percolation.isFull(18, 1));
        }

        @Test
        void passesInput6TestCase() throws IOException {
            Percolation percolation = new Percolation(6);
            percolation.open(1, 6);
            percolation.open(2, 6);
            percolation.open(3, 6);
            percolation.open(4, 6);
            percolation.open(5, 6);
            assertFalse(percolation.percolates());
        }
    }

    @Nested
    class IsOpen {
        @Test
        void opensTopLeftCorner() {
            Percolation percolation = new Percolation(3);
            percolation.open(1, 1);
            assertTrue(percolation.isOpen(1, 1));
        }

        @Test
        void doesNotAllowRowValueLessThan1() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isOpen(0, 3));
        }

        @Test
        void doesNotAllowRowValueGreaterThanN() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isOpen(4, 3));
        }

        @Test
        void doesNotAllowColValueLessThan1() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isOpen(3, 0));
        }

        @Test
        void doesNotAllowColValueGreaterThanN() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.isOpen(3, 4));
        }
    }

    @Nested
    class Open {
        @Test
        void opensSiteAndConnectsWithNeighbourToTheLeft() {
            Percolation percolation = new Percolation(3);
            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
            percolation.open(2, 3);
            percolation.open(2, 2);
            assertEquals(unionFind.find(5), unionFind.find(6));
        }

        @Test
        void opensSiteAndConnectsWithNeighbourToTheRight() {
            Percolation percolation = new Percolation(3);
            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
            percolation.open(2, 1);
            percolation.open(2, 2);
            assertEquals(unionFind.find(5), unionFind.find(4));
        }

        @Test
        void opensSiteAndConnectsWithUpperNeighbour() {
            Percolation percolation = new Percolation(3);
            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
            percolation.open(1, 2);
            percolation.open(2, 2);
            assertEquals(unionFind.find(5), unionFind.find(2));
        }

        @Test
        void opensSiteAndConnectsWithLowerNeighbour() {
            Percolation percolation = new Percolation(3);
            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
            percolation.open(3, 2);
            percolation.open(2, 2);
            assertEquals(unionFind.find(5), unionFind.find(8));
        }

        @Test
        void opensSiteAndDoesNotConnectWithPreviousNeighbourInAboveRow() {
            Percolation percolation = new Percolation(3);
            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
            percolation.open(1, 3);
            percolation.open(2, 1);
            assertNotEquals(unionFind.find(4), unionFind.find(3));
        }

        @Test
        void opensSiteAndDoesNotConnectWithNextNeighbourInBelowRow() {
            Percolation percolation = new Percolation(3);
            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
            percolation.open(3, 1);
            percolation.open(2, 3);
            assertNotEquals(unionFind.find(7), unionFind.find(6));
        }

        @Test
        void opensFirstRowSiteAndConnectsWithFirstVirtualNode() {
            Percolation percolation = new Percolation(3);
            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
            percolation.open(1, 1);
            percolation.open(1, 2);
            percolation.open(1, 3);
            assertEquals(unionFind.find(0), unionFind.find(1));
            assertEquals(unionFind.find(0), unionFind.find(2));
            assertEquals(unionFind.find(0), unionFind.find(3));
        }

//        @Test
//        void opensLastRowSiteAndConnectsWithLastVirtualNode() {
//            Percolation percolation = new Percolation(3);
//            WeightedQuickUnionUF unionFind = extractUnionFind(percolation);
//            percolation.open(3, 1);
//            percolation.open(3, 2);
//            percolation.open(3, 3);
//            assertEquals(unionFind.find(10), unionFind.find(7));
//            assertEquals(unionFind.find(10), unionFind.find(8));
//            assertEquals(unionFind.find(10), unionFind.find(9));
//        }

        @Test
        void doesNotAllowRowValueLessThan1() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.open(0, 3));
        }

        @Test
        void doesNotAllowRowValueGreaterThanN() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.open(4, 3));
        }

        @Test
        void doesNotAllowColValueLessThan1() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.open(3, 0));
        }

        @Test
        void doesNotAllowColValueGreaterThanN() {
            Percolation percolation = new Percolation(3);
            assertThrows(IllegalArgumentException.class, () -> percolation.open(3, 4));
        }
    }

    @Nested
    class NumberOfOpenSites {
        @Test
        void countsNumberOfOpenSites() {
            Percolation percolation = new Percolation(3);
            percolation.open(1, 1);
            assertEquals(1, percolation.numberOfOpenSites());
        }

        @Test
        void doesNotCountTheOpeningOfASiteMoreThanOnce() {
            Percolation percolation = new Percolation(3);
            percolation.open(1, 1);
            percolation.open(1, 1);
            assertEquals(1, percolation.numberOfOpenSites());
        }
    }

    private Percolation loadPercolationFromResourceFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) throw new IllegalArgumentException("File not found: " + fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Percolation percolation = new Percolation(Integer.parseInt(bufferedReader.readLine()));
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            Pattern pattern = Pattern.compile("\\s*(\\d+)\\s*(\\d+)");
            Matcher matcher = pattern.matcher(line);
            assertTrue(matcher.find());
            int row = Integer.parseInt(matcher.group(1));
            int col = Integer.parseInt(matcher.group(2));
            percolation.open(row, col);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return percolation;
    }
}
