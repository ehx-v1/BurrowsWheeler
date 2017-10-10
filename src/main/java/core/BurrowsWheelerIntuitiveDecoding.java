package core;

import gui.ViewerPane;
import util.runtimeframework.DebugQueue;
import util.runtimeframework.DebugStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerIntuitiveDecoding extends Observable implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub {
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
            }
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
        this.setChanged();
        this.notifyObservers();
    }

    protected void revertSort() {
        // should be immutable at iterations beyond the end
        // either all or none of the table lines contain an actual second character to compare
        Arrays.sort(inputTable,
                inputTable[0].isSecondSlotFilled() ?
                        BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.sortingRevertComparator()
                        : BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.firstSortingRevertComparator());
        this.setChanged();
        this.notifyObservers();
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
        ViewerPane pane = new ViewerPane() {
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
            private Button launcher = new Button();
            private int readoutIndex = 0;
            private GridPane table = new GridPane();
            private Stage indexOutOfWordErrorWindow = new Stage();
            private Stage indexNotANumberErrorWindow = new Stage();
            private Stage wordLengthExceedsLimitErrorWindow = new Stage();
            private List<UpdateLinker> linkers = new ArrayList<>();

            { // TODO position children
                StackPane error1Root = new StackPane(); // TODO replace with appropriate layout element
                TextField error1Message = new TextField();
                error1Message.setEditable(false);
                error1Message.setText("Please select an index within the word you enter.");
                Button error1OK = new Button();
                error1OK.setText("OK");
                error1OK.setOnMouseClicked(event -> this.indexOutOfWordErrorWindow.hide());
                error1Root.getChildren().addAll(error1Message, error1OK);
                Scene error1Scene = new Scene(error1Root);
                this.indexOutOfWordErrorWindow.setTitle("Error");
                this.indexOutOfWordErrorWindow.setScene(error1Scene);
                this.indexOutOfWordErrorWindow.initStyle(StageStyle.DECORATED);
                this.indexOutOfWordErrorWindow.initModality(Modality.NONE);
                this.indexOutOfWordErrorWindow.initOwner(stage);
                StackPane error2Root = new StackPane();
                TextField error2Message = new TextField();
                error2Message.setEditable(false);
                error2Message.setText("Please enter a number for the index.");
                Button error2OK = new Button();
                error2OK.setText("OK");
                error2OK.setOnMouseClicked(event -> this.indexNotANumberErrorWindow.hide());
                error2Root.getChildren().addAll(error2Message, error2OK);
                Scene error2Scene = new Scene(error2Root);
                this.indexNotANumberErrorWindow.setTitle("Error");
                this.indexNotANumberErrorWindow.setScene(error2Scene);
                this.indexNotANumberErrorWindow.initStyle(StageStyle.DECORATED);
                this.indexNotANumberErrorWindow.initModality(Modality.NONE);
                this.indexNotANumberErrorWindow.initOwner(stage);
                StackPane error3Root = new StackPane(); // TODO replace with appropriate layout element
                TextField error3Message = new TextField();
                error3Message.setEditable(false);
                error3Message.setText("Please enter a word that's shorter than the length limit,\nor change the length limit for your word to fit.");
                error3Message.setAlignment(Pos.TOP_CENTER);
                Button error3OK = new Button();
                error3OK.setText("OK");
                error3OK.setOnMouseClicked(event -> this.wordLengthExceedsLimitErrorWindow.hide());
                error3Root.getChildren().addAll(error3Message, error3OK);
                Scene error3Scene = new Scene(error3Root);
                this.wordLengthExceedsLimitErrorWindow.setTitle("Error");
                this.wordLengthExceedsLimitErrorWindow.setScene(error3Scene);
                this.wordLengthExceedsLimitErrorWindow.initStyle(StageStyle.DECORATED);
                this.wordLengthExceedsLimitErrorWindow.initModality(Modality.NONE);
                this.wordLengthExceedsLimitErrorWindow.initOwner(stage);
                this.launcher.setText("Launch");
                this.launcher.setOnMouseClicked(event -> {
                    try {
                        if (this.inputField.getText().length() > BurrowsWheelerIntuitiveDecoding.this.inputLimit) {
                            this.wordLengthExceedsLimitErrorWindow.show();
                            return;
                        }
                        this.readoutIndex = Integer.parseInt(this.indexField.getText());
                        if (this.readoutIndex >= this.inputField.getText().length()) {
                            this.readoutIndex = 0;
                            this.indexOutOfWordErrorWindow.show();
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
                                ViewerPane origin = this;
                                BurrowsWheelerIntuitiveDecoding.this.inputTable[i].addObserver(new Observer() {
                                    private BurrowsWheelerTransformationCore.BurrowsWheelerTableLine observedLine = BurrowsWheelerIntuitiveDecoding.this.inputTable[currentI];

                                    @Override
                                    public void update(Observable o, Object arg) {
                                        if (o == observedLine) {
                                            matrixField.setText(observedLine.toString().charAt(currentJ) + "");
                                        }
                                        origin.layout();
                                    }
                                });
                                this.linkers.add(() -> {
                                    ObservableList<Node> matrix = this.table.getChildren();
                                    for (int i1 = 0; i1 < matrix.size(); i1++) {
                                        ((TextField) matrix.get(i1)).setText(BurrowsWheelerIntuitiveDecoding.this.inputTable[i1].toString());
                                    }
                                    this.layout();
                                });
                                GridPane.setRowIndex(matrixField, i);
                                GridPane.setColumnIndex(matrixField, j);
                                this.table.getChildren().add(matrixField);
                            }
                        }
                        BurrowsWheelerIntuitiveDecoding.this.launch(this.inputField.getText());
                    } catch (NumberFormatException e) {
                        this.indexNotANumberErrorWindow.show();
                    }
                });
                HBox topLine = new HBox();
                topLine.getChildren().add(this.inputField);
                topLine.getChildren().add(this.indexField);
                topLine.getChildren().add(this.launcher);
                this.getChildren().add(topLine);
                this.getChildren().add(this.table);
            }

            @Override
            public void update(Observable o, Object arg) {
                if (o == BurrowsWheelerIntuitiveDecoding.this) {
                    for (UpdateLinker linker : this.linkers) {
                        linker.adjustContent();
                    }
                }
            }

            @Override
            public boolean isAssociatedWith (BurrowsWheelerTransformationCore.Algorithms algorithm) {
                return algorithm == BurrowsWheelerTransformationCore.Algorithms.BW_STANDARD_DECODE_INTUITIVE;
            }
        };
        this.addObserver(pane);
        return pane;
    }

}
