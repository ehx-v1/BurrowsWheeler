package core;

import gui.ViewerPane;
import util.runtimeframework.DebugQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;

import javafx.stage.Stage;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerTransformationCore {

    public enum Algorithms {
        BW_STANDARD_ENCODE,
        BW_STANDARD_DECODE_SHOWGENERALIDEAS,
        BW_STANDARD_DECODE_FAST,
        BW_STANDARD_DECODE_INTUITIVE,
        BW_PERMUTATIONS_ENCODE,
        BW_PERMUTATIONS_DECODE
    }

    public interface AlgorithmImplementationStub {

        DebugQueue getExecution();

        ViewerPane getViewer (Stage stage);

    }

    public interface Permutation {

        /**
         * Permutates a given character with its counterpart. The cycle length of the permutation is always assumed to be 2. Permutations with other cycle lengths (or even with no cycle at all) are technically
         * possible, but will cause unintended effects. For instance, reverting permutational operations will not work correctly.
         * @param original the character to be permutated
         * @return the corresponding permutated character
         */
        char permutate (char original);

        default String permutate (String original) {
            String output = "";
            for (char c : original.toCharArray()) {
                output += this.permutate(c);
            }
            return output;
        }

        default BurrowsWheelerTableLine permutate (BurrowsWheelerTableLine original) {
            BurrowsWheelerTableLine output = new BurrowsWheelerTableLine(original.length(), original.position);
            for (char c : original.toString().toCharArray()) {
                output.overwriteLast(this.permutate(c));
                output.rotateLeft();
            }
            output.rotateRight();
            return output;
        }

        default void setMapping (char original, char replace) {
            // empty method, needed for being visible when overridden by anonymous classes (not used other than on anonymous classes)
        }

    }

    private List<DebugQueue> implementedAlgorithms = new ArrayList<>(Algorithms.values().length);

    private int maxInputLength;

    public BurrowsWheelerTransformationCore (int maxInputLength) {
        this.maxInputLength = maxInputLength;
    }

    public int getMaxInputLength() {
        return maxInputLength;
    }

    public void addImplementation (AlgorithmImplementationStub stub, Runnable onPreBegin, Runnable onPostEnd) {
        this.implementedAlgorithms.add(new DebugQueue(stub.getExecution(), onPreBegin, onPostEnd));
    }

    public DebugQueue getRegisteredAlgorithm (String algorithmName) {
        return this.getRegisteredAlgorithm(Algorithms.valueOf(algorithmName));
    }

    public DebugQueue getRegisteredAlgorithm (Algorithms algorithm) {
        return this.implementedAlgorithms.get(algorithm.ordinal());
    }

    public static class BurrowsWheelerTableLine extends Observable{
        private char[] content;
        public final int position;

        public BurrowsWheelerTableLine(int size, int position) {
            this.content = new char[size];
            this.position = position;
            for (int i = 0; i < this.length(); i++) {
                this.content[i] = '\0';
            }
        }

        public void overwriteLast(char c) {
            this.content[this.length() - 1] = c;
            this.setChanged();
            this.notifyObservers();
        }

        public void rotateLeft() {
            char round = this.content[0];
            System.arraycopy(this.content, 1, this.content, 0, this.length() - 1);
            this.content[this.length() - 1] = round;
            this.setChanged();
            this.notifyObservers();
        }

        public void rotateRight() {
            char round = this.content[this.length() - 1];
            for (int i = 1; i < this.length(); i++) {
                this.content[this.length() - i] = this.content[this.length() - (i + 1)];
            }
            this.content[0] = round;
            this.setChanged();
            this.notifyObservers();
        }

        public int length() {
            return this.content.length;
        }

        public boolean isSecondSlotFilled() {
            return this.length() > 1 && this.content[1] != '\0'; // assumes that EoS character is not actually used
        }

        @Override
        public String toString() {
            return String.valueOf(content);
        }

        public static Comparator<BurrowsWheelerTableLine> sortingComparator() {
            return (o1, o2) -> Character.compare(o1.content[0], o2.content[0]);
        }

        public static Comparator<BurrowsWheelerTableLine> sortingAheadComparator() {
            return Comparator.comparing(BurrowsWheelerTableLine::toString);
        }

        public static Comparator<BurrowsWheelerTableLine> sortingRevertComparator() {
            return (o1, o2) -> Character.compare(o1.content[1], o2.content[1]);
        }

        public static Comparator<BurrowsWheelerTableLine> firstSortingRevertComparator() {
            return Comparator.comparingInt(line -> line.position);
        }

        public BurrowsWheelerTableLine contentCopy (int newPosition) {
            BurrowsWheelerTableLine line = new BurrowsWheelerTableLine(this.length(), newPosition);
            line.content = Arrays.copyOf(this.content, this.content.length);
            return line;
        }

        public boolean isConvertible(String eval, Permutation optConverter) {
            return this.toString().equals(eval)
                    || this.toString().equals(optConverter.permutate(eval));
        }

    }
}
