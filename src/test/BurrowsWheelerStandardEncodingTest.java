package test;

import core.BurrowsWheelerStandardEncoding;
import core.BurrowsWheelerTransformationCore;
import runtimeframework.DebugQueue;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by root on 22.07.2017.
 */
public class BurrowsWheelerStandardEncodingTest {
    private class ValueFetchableTestUnit extends BurrowsWheelerStandardEncoding {

        public ValueFetchableTestUnit(BurrowsWheelerTransformationCore core, Runnable preBegin, Runnable postEnd) {
            super(core, preBegin, postEnd);
        }

        @Override
        public void launch (String input) {
            super.launch(input);
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
        public int getIndexResult() {
            for (int i = 0; i < this.inputTable.length; i++) {
                if (this.inputTable[i].toString().equals(this.input)) return i;
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

    private void assertProduces (String input, String expectedResult, int expectedIndexResult) {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch(input);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals(expectedResult, this.uut.getResult());
        assertEquals(expectedIndexResult, this.uut.getIndexResult());
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

}
