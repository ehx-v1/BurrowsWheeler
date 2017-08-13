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
public class BurrowsWheelerStandardEncoding implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub {
    protected String input;
    protected BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[] inputTable;
    protected int filledLines;
    protected int inputLimit;

    public BurrowsWheelerStandardEncoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        this.inputLimit = core.getMaxInputLength();
        this.input = "";
        this.inputTable = null;
        this.filledLines = 0;
        core.addImplementation(this, onPreBegin, onPostEnd);
    }

    protected void launch( String input) {
        this.input = input;
        this.inputTable = new BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[this.input.length()];
        for (int i = 0; i < this.input.length(); i++) {
            this.inputTable[i] = new BurrowsWheelerTransformationCore.BurrowsWheelerTableLine(this.input.length(), i);
        }
        for (int i = 0; i < this.input.length(); i++) {
            this.inputTable[0].overwriteLast(this.input.charAt(i));
            this.inputTable[0].rotateLeft(); // rotates 1 too far but doesn't matter since all rotations needed
        }
        this.filledLines = 1;
    }

    protected void fillLine() {
        if (this.filledLines < this.input.length()) {
            this.inputTable[this.filledLines] = this.inputTable[this.filledLines - 1].contentCopy(this.filledLines);
            this.inputTable[this.filledLines].rotateLeft();
        }
        this.filledLines++;
    }

    protected void revertFillLine() {
        --this.filledLines;
        if (this.filledLines < this.input.length()) {
            this.inputTable[this.filledLines] = new BurrowsWheelerTransformationCore.BurrowsWheelerTableLine(this.input.length(), this.filledLines); // reset to no actual content
        }
    }

    protected void sort() {
        Arrays.sort(this.inputTable, BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.sortingAheadComparator());
    }

    protected void revertSort() {
        Arrays.sort(this.inputTable, BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.firstSortingRevertComparator());
    }

    @Override
    public DebugQueue getExecution() {
        DebugQueue queue = new DebugQueue();
        DebugStep.DebugStepBuilder builder = DebugStep.builder();
        for (int i = 1; i < this.inputLimit; i++) { // all lines except one are yet to fill
            queue.add(builder.setForward(BurrowsWheelerStandardEncoding.this::fillLine)
                    .setBackward(BurrowsWheelerStandardEncoding.this::revertFillLine)
                    .build());
        }
        queue.add(builder.setForward(BurrowsWheelerStandardEncoding.this::sort)
                         .setBackward(BurrowsWheelerStandardEncoding.this::revertSort)
                         .build());
        return queue;
    }

    @Override
    public ViewerPane getViewer() {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private Button launcher = new Button();
            private GridPane table = new GridPane();

            {
                this.launcher.setText("Launch");
                this.launcher.setOnAction(event -> {
                    if (this.inputField.getText().length() > BurrowsWheelerStandardEncoding.this.inputLimit) {
                        // TODO make popup
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
                    BurrowsWheelerStandardEncoding.this.launch(this.inputField.getText());
                });
            }

            @Override
            protected ObservableList<Node> getChildren() {
                ObservableList<Node> nodes = FXCollections.observableArrayList();
                nodes.add(this.inputField);
                nodes.add(this.launcher);
                nodes.add(this.table);
                return nodes;
            }
        };
    }

}
