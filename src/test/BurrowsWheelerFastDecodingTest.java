package test;

import core.BurrowsWheelerFastDecoding;
import core.BurrowsWheelerTransformationCore;
import runtimeframework.DebugQueue;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by root on 27.07.2017.
 */
public class BurrowsWheelerFastDecodingTest {

    private class ValueFetchableTestUnit extends BurrowsWheelerFastDecoding {
        private int index;

        public ValueFetchableTestUnit(BurrowsWheelerTransformationCore core, Runnable preBegin, Runnable postEnd) {
            super(core, preBegin, postEnd);
        }

        public void launch(String input, int index) {
            super.launch(input, index);
        }

        public String getResult() {
            return this.result;
        }

        public boolean isReset() {
            for (IndexedCharacter character : this.characters) {
                if (!character.hasIndex(-1)) return false;
            }
            return true;
        }

    }

    private class ValueFetchableTestUnit2 extends ValueFetchableTestUnit {
        private int index;

        public ValueFetchableTestUnit2(BurrowsWheelerTransformationCore core, Runnable preBegin, Runnable postEnd) {
            super(core, preBegin, postEnd);
        }

        @Override
        public boolean isReset() {
            for (int i = 0; i < this.characters.size(); i++) {
                if (!this.characters.get(i).hasIndex(i)) return false;
            }
            return true;
        }

        @Override
        protected void revertMakeIndexes() {
            // skip reverting
        }

    }

    private BurrowsWheelerTransformationCore core;
    private ValueFetchableTestUnit uut;
    private ValueFetchableTestUnit2 uut2;
    private boolean reachedBegin;
    private boolean reachedEnd;

    private ValueFetchableTestUnit getTestUnit (boolean first) {
        if (first) return uut;
        return uut2;
    }

    private void assertProduces (String input, int index, String expectedOutput) {
        for (int i = 0; i < 2; i++) {
            DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[i]);
            this.getTestUnit(i == 0).launch(input, index);
            while (!this.reachedEnd) {
                queue.stepForward();
            }
            assertEquals(expectedOutput, this.uut.getResult());
            while (!this.reachedBegin) {
                queue.stepBack();
            }
            assertTrue(this.uut.isReset());
        }
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
        assertProduces("bpraipckae", 2, "backpapier");
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

}
