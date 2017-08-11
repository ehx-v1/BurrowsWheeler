package test;

import core.BurrowsWheelerPermutationDecoding;
import core.BurrowsWheelerTransformationCore;
import runtimeframework.DebugQueue;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by root on 28.07.2017.
 */
public class BurrowsWheelerPermutationDecodingTest {

    private class ValueFetchableTestUnit extends BurrowsWheelerPermutationDecoding {

        public ValueFetchableTestUnit(BurrowsWheelerTransformationCore core, Runnable preBegin, Runnable postEnd) {
            super(core, preBegin, postEnd);
        }

        public void launch (String input, int index, BurrowsWheelerTransformationCore.Permutation permutation) {
            super.launch(input, index, permutation);
        }

        public String getResult() {
            return this.inputTable[this.index].toString();
        }

        public boolean isReset() {
            for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine line : this.inputTable) {
                for (char c : line.toString().substring(0, line.length() - 2).toCharArray()) { // all characters except last must be \0
                    if (c != '\0') return false;
                }
            }
            return true;
        }

    }

    private BurrowsWheelerTransformationCore core;
    private ValueFetchableTestUnit uut;
    private boolean reachedBegin;
    private boolean reachedEnd;

    private void assertProduces (String input, int index, String expectedOutput) {
        assertProduces(input, index, original -> original, expectedOutput);
    }

    private void assertProduces (String input, int index, BurrowsWheelerTransformationCore.Permutation permutation, String expectedOutput) {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch(input, index, permutation);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals(expectedOutput, this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
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
        assertProduces("snnaaa", 0, "ananas");
    }

    @Test
    public void testAlgorithm2() {
        assertProduces("bpraipckae", 4, "backpapier");
    }

    @Test
    public void testAlgorithm3() {
        assertProduces("pssmipissii", 4, "mississippi");
    }

    @Test
    public void testAlgorithm4() {
        assertProduces("uodusk", 3, "sudoku");
    }

    @Test
    public void testAlgorithm5() {
        assertProduces("mkproyitaaarrt", 5, "mariokartparty");
    }

    @Test
    public void testAlgorithm6() {
        assertProduces("ntneoidn", 4, "nintendo");
    }

    @Test
    public void testAlgorithm7() {
        assertProduces("drrbeeee", 4, "erdbeere");
    }

    @Test
    public void testAlgorithm8() {
        assertProduces("lgrgealare", 6, "lagerregal");
    }

    @Test
    public void testAlgorithm9() {
        assertProduces("sirgauteeee", 8, "saeugetiere");
    }

    @Test
    public void testAlgorithm10() {
        assertProduces("yyyrrrbbbeeeccc", 3, "cybercybercyber");
    }

    @Test
    public void testAlgorithm11() {
        assertProduces("snsaaa", 0, original -> {
            switch (original) {
                case 'n':
                    return 's';
                case 's':
                    return 'n';
                default:
                    return original;
            }
        }, "ananas");
    }

    @Test
    public void testAlgorithm12() {
        assertProduces("cbrkaaippe", 2, original -> {
            switch (original) {
                case 'c':
                    return 'p';
                case 'p':
                    return 'c';
                default:
                    return original;
            }
        }, "backpapier");
    }

    @Test
    public void testAlgorithm13() {
        assertProduces("ssmpipppiii", 4, original -> {
            switch (original) {
                case 'p':
                    return 's';
                case 's':
                    return 'p';
                default:
                    return original;
            }
        }, "mississippi");
    }

    @Test
    public void testAlgorithm14() {
        assertProduces("uusdku", 5, original -> {
            switch (original) {
                case 'o':
                    return 'u';
                case 'u':
                    return 'o';
                default:
                    return original;
            }
        }, "sudoku");
    }

    @Test
    public void testAlgorithm15() {
        assertProduces("mkprrroyitaaat", 7, original -> {
            switch (original) {
                case 'i':
                    return 't';
                case 't':
                    return 'i';
                default:
                    return original;
            }
        }, "mariokartparty");
    }

    @Test
    public void testAlgorithm16() {
        assertProduces("nntoeiin", 3, original -> {
            switch (original) {
                case 'd':
                    return 'i';
                case 'i':
                    return 'd';
                default:
                    return original;
            }
        }, "nintendo");
    }

    @Test
    public void testAlgorithm17() {
        assertProduces("erbrdded", 5, original -> {
            switch (original) {
                case 'd':
                    return 'e';
                case 'e':
                    return 'd';
                default:
                    return original;
            }
        }, "erdbeere");
    }

    @Test
    public void testAlgorithm18() {
        assertProduces("rlggeelara", 6, original -> {
            switch (original) {
                case 'a':
                    return 'e';
                case 'e':
                    return 'a';
                default:
                    return original;
            }
        }, "lagerregal");
    }

    @Test
    public void testAlgorithm19() {
        assertProduces("rgrgeallae", 6, original -> {
            switch (original) {
                case 'l':
                    return 'r';
                case 'r':
                    return 'l';
                default:
                    return original;
            }
        }, "lagerregal");
    }

    @Test
    public void testAlgorithm20() {
        assertProduces("llggeellaa", 6, original -> {
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
        }, "lagerregal");
    }

}
