package test;

import core.BurrowsWheelerIntuitiveDecoding;
import core.BurrowsWheelerTransformationCore;
import runtimeframework.DebugQueue;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by root on 27.07.2017.
 */
public class BurrowsWheelerIntuitiveDecodingTest {

    private class ValueFetchableTestUnit extends BurrowsWheelerIntuitiveDecoding {
        private int index;

        public ValueFetchableTestUnit(BurrowsWheelerTransformationCore core) {
            super(core, () -> BurrowsWheelerIntuitiveDecodingTest.this.reachedBegin = true, () -> BurrowsWheelerIntuitiveDecodingTest.this.reachedEnd = true);
        }

        public void launch(String input, int index) {
            this.index = index;
            super.launch(input);
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

    @Test
    public void testAlgorithm4() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("uodusk", 3);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("sudoku", this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm5() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("mkproyitaaarrt", 5);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("mariokartparty", this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm6() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("ntneoidn", 4);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("nintendo", this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm7() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("drrbeeee", 4);
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("erdbeere", this.uut.getResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

}
