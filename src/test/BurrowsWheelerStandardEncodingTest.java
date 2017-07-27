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

        public ValueFetchableTestUnit(BurrowsWheelerTransformationCore core) {
            super(core, () -> BurrowsWheelerStandardEncodingTest.this.reachedBegin = true, () -> BurrowsWheelerStandardEncodingTest.this.reachedEnd = true);
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
        this.uut.launch("ananas");
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("snnaaa", this.uut.getResult());
        assertEquals(0, this.uut.getIndexResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm2() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("backpapier");
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("bpraipckae", this.uut.getResult());
        assertEquals(2, this.uut.getIndexResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm3() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("mississippi");
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("pssmipissii", this.uut.getResult());
        assertEquals(4, this.uut.getIndexResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm4() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("sudoku");
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("uodusk", this.uut.getResult());
        assertEquals(3, this.uut.getIndexResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm5() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("mariokartparty");
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("mkproyitaaarrt", this.uut.getResult());
        assertEquals(5, this.uut.getIndexResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm6() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("nintendo");
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("ntneoidn", this.uut.getResult());
        assertEquals(4, this.uut.getIndexResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

    @Test
    public void testAlgorithm7() {
        DebugQueue queue = this.core.getRegisteredAlgorithm(BurrowsWheelerTransformationCore.Algorithms.values()[0]);
        this.uut.launch("erdbeere");
        while (!this.reachedEnd) {
            queue.stepForward();
        }
        assertEquals("drrbeeee", this.uut.getResult());
        assertEquals(4, this.uut.getIndexResult());
        while (!this.reachedBegin) {
            queue.stepBack();
        }
        assertTrue(this.uut.isReset());
    }

}
