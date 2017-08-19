package core;

import gui.ViewerPane;
import util.runtimeframework.DebugQueue;
import util.runtimeframework.DebugStep;

import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerIntuitiveDecoding implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub {
    protected String input;
    protected int inputLimit;
    protected BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[] inputTable;
    protected int stepCount;

    public BurrowsWheelerIntuitiveDecoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        this.inputLimit = core.getMaxInputLength();
        this.stepCount = 0;
        core.addImplementation(this, onPreBegin, onPostEnd);
    }

    protected void launch(String input) {
        this.input = input;
        this.inputTable = new BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[this.input.length()];
        for (int i = 0; i < this.inputTable.length; i++) {
            this.inputTable[i] = new BurrowsWheelerTransformationCore.BurrowsWheelerTableLine(this.input.length(), i);
        }
        this.append();
    }

    private void append() {
        if (this.stepCount < this.input.length()) {
            for (int i = 0; i < this.input.length(); i++) {
                this.inputTable[i].overwriteLast(this.input.charAt(i));
                System.out.println(this.inputTable[i]);
            }
            System.out.println();
        }
    }

    private void revertAppend() {
        if (this.stepCount < this.input.length()) {
            for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputLine : this.inputTable) {
                inputLine.overwriteLast('\0');
            }
        }
    }

    private void rotate() {
        if (this.stepCount < this.input.length()) {
            for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputLine : this.inputTable) {
                inputLine.rotateRight();
                System.out.println(inputLine);
            }
            System.out.println();
        }
        this.stepCount++;
    }

    private void revertRotate() {
        --this.stepCount;
        if (this.stepCount < this.input.length()) {
            for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputLine : this.inputTable) {
                inputLine.rotateLeft();
            }
        }
    }

    protected void sort() {
        // should be immutable at iterations beyond the end
        Arrays.sort(inputTable, BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.sortingComparator());
        for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputLine : this.inputTable) {
            System.out.println(inputLine);
        }
        System.out.println();
    }

    protected void revertSort() {
        // should be immutable at iterations beyond the end
        // either all or none of the table lines contain an actual second character to compare
        Arrays.sort(inputTable,
                inputTable[0].isSecondSlotFilled() ?
                        BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.sortingRevertComparator()
                        : BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.firstSortingRevertComparator());
    }

    @Override
    public DebugQueue getExecution() {
        DebugQueue queue = new DebugQueue();
        DebugStep.DebugStepBuilder builder = DebugStep.builder();
        // fill queue
        for (int i = 0; i < inputLimit - 1; i++) { // the steps are supposed to be repeated until the table is full, which takes exactly length() - 1 cycles
            queue.add(builder.setForward(BurrowsWheelerIntuitiveDecoding.this::rotate)
                    .setBackward(BurrowsWheelerIntuitiveDecoding.this::revertRotate)
                    .build());
            queue.add(builder.setForward(BurrowsWheelerIntuitiveDecoding.this::sort)
                    .setBackward(BurrowsWheelerIntuitiveDecoding.this::revertSort)
                    .build());
            queue.add(builder.setForward(BurrowsWheelerIntuitiveDecoding.this::append)
                    .setBackward(BurrowsWheelerIntuitiveDecoding.this::revertAppend)
                    .build());
        }
        return queue;
    }

    @Override
    public ViewerPane getViewer(Stage stage) {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
            private Button launcher = new Button();
            private int readoutIndex = 0;
            private GridPane table = new GridPane();

            { // TODO position children
                this.launcher.setText("Launch");
                this.launcher.setOnMouseClicked(event -> {
                    try {
                        if (this.inputField.getText().length() > BurrowsWheelerIntuitiveDecoding.this.inputLimit) {
                            // TODO make popup
                            return;
                        }
                        this.readoutIndex = Integer.parseInt(this.indexField.getText());
                        if (this.readoutIndex >= this.inputField.getText().length()) {
                            this.readoutIndex = 0;
                            // TODO make popup that index out of word
                            return;
                        }
                        for (int i = 0; i < this.inputField.getText().length(); i++) {
                            for (int j = 0; j < this.inputField.getText().length(); j++) {
                                final int currentI = i;
                                final int currentJ = j;
                                TextField matrixField = new TextField();
                                matrixField.setAlignment(Pos.CENTER);
                                matrixField.setEditable(false);
                                matrixField.setText(BurrowsWheelerIntuitiveDecoding.this.inputTable[i].toString());
                                // make sure the text of matrixField updates to this.inputTable[i].toString().charAt(j) whenever inputTable changes
                                BurrowsWheelerIntuitiveDecoding.this.inputTable[i].addObserver(new Observer() {
                                    private BurrowsWheelerTransformationCore.BurrowsWheelerTableLine observedLine = BurrowsWheelerIntuitiveDecoding.this.inputTable[currentI];

                                    @Override
                                    public void update(Observable o, Object arg) {
                                        if (o == observedLine) {
                                            matrixField.setText(observedLine.toString().charAt(currentJ) + "");
                                        }
                                    }
                                });
                                GridPane.setRowIndex(matrixField, i);
                                GridPane.setColumnIndex(matrixField, j);
                                this.table.getChildren().add(matrixField);
                            }
                        }
                        BurrowsWheelerIntuitiveDecoding.this.launch(this.inputField.getText());
                    } catch (NumberFormatException e) {
                        // TODO make popup that index input must be a number
                    }
                });
            }

            @Override
            protected ObservableList<Node> getChildren() {
                ObservableList<Node> nodes = FXCollections.observableArrayList();
                nodes.add(this.inputField);
                nodes.add(this.indexField);
                nodes.add(this.launcher);
                nodes.add(this.table);
                return nodes;
            }
        };
    }

}
