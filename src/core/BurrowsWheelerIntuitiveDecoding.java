package core;

import gui.ViewerPane;
import runtimeframework.DebugQueue;
import runtimeframework.DebugStep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Arrays;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerIntuitiveDecoding implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub {
    private String input;
    private int inputLimit;
    protected BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[] inputTable;

    public BurrowsWheelerIntuitiveDecoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        this.inputLimit = core.getMaxInputLength();
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
        // TODO make sure not to overshoot
        for (int i = 0; i < this.input.length(); i++) {
            this.inputTable[i].overwriteLast(this.input.charAt(i));
        }
    }

    private void revertAppend() {
        // TODO make sure not to overshoot
        for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputLine : this.inputTable) {
            inputLine.overwriteLast('\0');
        }
    }

    private void rotate() {
        // TODO make sure not to overshoot
        for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputLine : this.inputTable) {
            inputLine.rotateRight();
        }
    }

    private void revertRotate() {
        // TODO make sure not to overshoot
        for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputLine : this.inputTable) {
            inputLine.rotateLeft();
        }
    }

    private void sort() {
        Arrays.sort(inputTable, BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.sortingComparator());
    }

    private void revertSort() {
        // either all or none of the table lines contain an actual second character to compare
        Arrays.sort(inputTable,
                inputTable[0].isSecondSlotEmpty() ?
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
    public ViewerPane getViewer() {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
            private Button launcher = new Button();
            private int readoutIndex = 0;
            private GridPane table = new GridPane();

            {
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
                                TextField matrixField = new TextField();
                                matrixField.setAlignment(Pos.CENTER);
                                // TODO make sure the text of matrixField updates to this.inputTable[i].toString().charAt(j) whenever inputTable changes
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
