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

        public ValueFetchableTestUnit(BurrowsWheelerTransformationCore core) {
            super(core, () -> BurrowsWheelerFastDecodingTest.this.reachedBegin = true, () -> BurrowsWheelerFastDecodingTest.this.reachedEnd = true);
        }

        public void launch(String input, int index) {
            super.launch(input, index);
        }

        public String getResult() {
            return this.result;
        }

        public boolean isReset() {
            for (int i = 0; i < this.characters.size(); i++) {
                if (!this.characters.get(i).hasIndex(i)) return false;
            }
            return true;
        }

    }

    private BurrowsWheelerTransformationCore core;
    private ValueFetchableTestUnit uut;
    private boolean reachedBegin;
    private boolean reachedEnd;

    @Before
    public void resetCoreAndAlgorithm() {
        this.core = new BurrowsWheelerTransformationCore(20);
        this.uut = new ValueFetchableTestUnit(this.core);
        this.reachedBegin = false;
        this.reachedEnd = false;
    }

    @Test
    public void testAlgorithm1() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("snnaaa", 0);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("ananas", this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm2() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("bpraipckae", 4);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("backpapier", this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm3() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("pssmipissii", 4);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("mississippi", this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

}
