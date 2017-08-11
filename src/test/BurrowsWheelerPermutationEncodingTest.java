package test;

import core.BurrowsWheelerPermutationEncoding;
import core.BurrowsWheelerTransformationCore;
import runtimeframework.DebugQueue;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by root on 22.07.2017.
 */
public class BurrowsWheelerPermutationEncodingTest {
    private class ValueFetchableTestUnit extends BurrowsWheelerPermutationEncoding {

        public ValueFetchableTestUnit(BurrowsWheelerTransformationCore core, Runnable preBegin, Runnable postEnd) {
            super(core, preBegin, postEnd);
        }

        @Override
        public void launch(String input, BurrowsWheelerTransformationCore.Permutation permutation) {
            super.launch(input, permutation);
        }

        // accumulating last characters
        public String getResult() {
            String last = "";
            for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine line : this.inputTable) {
                last += line.toString().charAt(line.length()-1);
            }
            return last;
        }

        // same method that is/will be used in the viewer
        public int getIndexResult(BurrowsWheelerTransformationCore.Permutation permutation) {
            for (int i = 0; i < this.inputTable.length; i++) {
                if (this.inputTable[i].isConvertible(this.input, permutation)) return i;
            }
            return -1;
        }

        public boolean isReset() {
            return this.filledLines <= 1;
        }

    }

    private BurrowsWheelerTransformationCore core;
    private ValueFetchableTestUnit uut;
    private boolean reachedBegin;
    private boolean reachedEnd;

    private void assertProduces (String input, BurrowsWheelerTransformationCore.Permutation permutation, String expectedResult, int expectedIndexResult) {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch(input, permutation);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals(expectedResult, this.uut.getResult());
        assertEquals(expectedIndexResult, this.uut.getIndexResult(permutation));
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    private void assertProduces (String input, String expectedResult, int expectedIndexResult) {
        assertProduces(input, original -> original, expectedResult, expectedIndexResult);
    }

    @Before
    public void resetCoreAndAlgorithm() {
        this.core = new BurrowsWheelerTransformationCore(20);
        this.uut = new ValueFetchableTestUnit(this.core, () -> this.reachedBegin = true, () -> this.reachedEnd = true);
        this.reachedBegin = false;
        this.reachedEnd = false;
    }

    @Test
    public void testAlgorithm1() {
        assertProduces("ananas", "snnaaa", 0);
    }

    @Test
    public void testAlgorithm2() {
        assertProduces("backpapier", "bpraipckae", 2);
    }

    @Test
    public void testAlgorithm3() {
        assertProduces("mississippi", "pssmipissii", 4);
    }

    @Test
    public void testAlgorithm4() {
        assertProduces("sudoku", "uodusk", 3);
    }

    @Test
    public void testAlgorithm5() {
        assertProduces("mariokartparty", "mkproyitaaarrt", 5);
    }

    @Test
    public void testAlgorithm6() {
        assertProduces("nintendo", "ntneoidn", 4);
    }

    @Test
    public void testAlgorithm7() {
        assertProduces("erdbeere", "drrbeeee", 4);
    }

    @Test
    public void testAlgorithm8() {
        assertProduces("lagerregal", "lgrgealare", 6);
    }

    @Test
    public void testAlgorithm9() {
        assertProduces("saeugetiere", "sirgauteeee", 8);
    }

    @Test
    public void testAlgorithm10() {
        assertProduces("cybercybercyber", "yyyrrrbbbeeeccc", 3);
    }

    @Test
    public void testAlgorithm11() {
        assertProduces("ananas", original -> {
            switch (original) {
                case 'n':
                    return 's';
                case 's':
                    return 'n';
                default:
                    return original;
            }
        }, "snsaaa", 0);
    }

    @Test
    public void testAlgorithm12() {
        assertProduces("backpapier", original -> {
            switch (original) {
                case 'c':
                    return 'p';
                case 'p':
                    return 'c';
                default:
                    return original;
            }
        }, "cbrkaaippe", 2);
    }

    @Test
    public void testAlgorithm13() {
        assertProduces("mississippi", original -> {
            switch (original) {
                case 'p':
                    return 's';
                case 's':
                    return 'p';
                default:
                    return original;
            }
        }, "ssmpipppiii", 4);
    }

    @Test
    public void testAlgorithm14() {
        assertProduces("sudoku", original -> {
            switch (original) {
                case 'o':
                    return 'u';
                case 'u':
                    return 'o';
                default:
                    return original;
            }
        }, "uusdku", 5);
    }

    @Test
    public void testAlgorithm15() {
        assertProduces("mariokartparty", original -> {
            switch (original) {
                case 'i':
                    return 't';
                case 't':
                    return 'i';
                default:
                    return original;
            }
        }, "mkprrroyitaaat", 7);
    }

    @Test
    public void testAlgorithm16() {
        assertProduces("nintendo", original -> {
            switch (original) {
                case 'd':
                    return 'i';
                case 'i':
                    return 'd';
                default:
                    return original;
            }
        }, "nntoeiin", 3);
    }

    @Test
    public void testAlgorithm17() {
        assertProduces("erdbeere", original -> {
            switch (original) {
                case 'd':
                    return 'e';
                case 'e':
                    return 'd';
                default:
                    return original;
            }
        }, "erbrdded", 5);
    }

    @Test
    public void testAlgorithm18() {
        assertProduces("lagerregal", original -> {
            switch (original) {
                case 'a':
                    return 'e';
                case 'e':
                    return 'a';
                default:
                    return original;
            }
        }, "rlggeelara", 6);
    }

    @Test
    public void testAlgorithm19() {
        assertProduces("lagerregal", original -> {
            switch (original) {
                case 'l':
                    return 'r';
                case 'r':
                    return 'l';
                default:
                    return original;
            }
        }, "rgrgeallae", 6);
    }

    @Test
    public void testAlgorithm20() {
        assertProduces("lagerregal", original -> {
            switch (original) {
                case 'a':
                    return 'e';
                case 'e':
                    return 'a';
                case 'l':
                    return 'r';
                case 'r':
                    return 'l';
                default:
                    return original;
            }
        }, "llggeellaa", 6);
    }

}
