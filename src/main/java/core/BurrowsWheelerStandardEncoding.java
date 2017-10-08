package core;

import gui.ViewerPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.AlgorithmUtils;
import util.ThisShouldNotHappenException;
import util.runtimeframework.DebugQueue;
import util.runtimeframework.DebugStep;

import java.util.*;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerStandardEncoding extends Observable implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub {

    private enum Message {
        HEAD_ERROR,
        BUTTONLABEL_CONFIRM,
        BUTTONLABEL_LAUNCH,
        ERROR_LENGTH_EXCEEDS_LIMIT;

        private String getCaption (AlgorithmUtils.Locale locale) {
            switch (locale) {
                case DE:
                    return this.getCaptionDE();
                case EN:
                    return this.getCaptionEN();
                default:
                    throw new ThisShouldNotHappenException("Enum value out of enum");
            }
        }

        private String getCaptionDE() {
            switch (this) {
                case HEAD_ERROR:
                    return "Fehler";
                case BUTTONLABEL_CONFIRM:
                    return "OK";
                case BUTTONLABEL_LAUNCH:
                    return "Starten";
                case ERROR_LENGTH_EXCEEDS_LIMIT:
                    return "Bitte ein Wort eingeben, das kürzer als die Maximallänge ist,\noder die Maximallänge an das gewünschte Wort anpassen.";
                default:
                    throw new ThisShouldNotHappenException("Enum value out of enum");
            }
        }

        private String getCaptionEN() {
            switch (this) {
                case HEAD_ERROR:
                    return "Error";
                case BUTTONLABEL_CONFIRM:
                    return "OK";
                case BUTTONLABEL_LAUNCH:
                    return "Launch";
                case ERROR_LENGTH_EXCEEDS_LIMIT:
                    return "Please enter a word that's shorter than the length limit,\nor change the length limit for your word to fit.";
                default:
                    throw new ThisShouldNotHappenException("Enum value out of enum");
            }
        }
    }

    protected String input;
    protected BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[] inputTable;
    protected int filledLines;
    protected int inputLimit;
    protected AlgorithmUtils.Locale locale;

    public BurrowsWheelerStandardEncoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd, AlgorithmUtils.Locale locale) {
        this.inputLimit = core.getMaxInputLength();
        this.input = "";
        this.inputTable = null;
        this.filledLines = 0;
        this.locale = locale;
        core.addImplementation(this, onPreBegin, onPostEnd);
    }

    public BurrowsWheelerStandardEncoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        this(core, onPreBegin, onPostEnd, AlgorithmUtils.Locale.DE);
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
    public ViewerPane getViewer(Stage stage) {
        ViewerPane pane = new ViewerPane() {
            private TextField inputField = new TextField();
            private Button launcher = new Button();
            private GridPane table = new GridPane();
            private Stage wordLengthExceedsLimitErrorWindow = new Stage();
            private List<UpdateLinker> linkers = new ArrayList<>();

            { // TODO position children?
                VBox error3Root = new VBox();
                TextField error3Message = new TextField();
                error3Message.setEditable(false);
                error3Message.setText(Message.ERROR_LENGTH_EXCEEDS_LIMIT.getCaption(BurrowsWheelerStandardEncoding.this.locale));
                error3Message.setAlignment(Pos.TOP_CENTER);
                Button error3OK = new Button();
                error3OK.setText(Message.BUTTONLABEL_CONFIRM.getCaption(BurrowsWheelerStandardEncoding.this.locale));
                error3OK.setOnMouseClicked(event -> this.wordLengthExceedsLimitErrorWindow.hide());
                error3Root.getChildren().addAll(error3Message, error3OK);
                Scene error3Scene = new Scene(error3Root);
                this.wordLengthExceedsLimitErrorWindow.setTitle(Message.HEAD_ERROR.getCaption(BurrowsWheelerStandardEncoding.this.locale));
                this.wordLengthExceedsLimitErrorWindow.setScene(error3Scene);
                this.wordLengthExceedsLimitErrorWindow.initStyle(StageStyle.DECORATED);
                this.wordLengthExceedsLimitErrorWindow.initModality(Modality.NONE);
                this.wordLengthExceedsLimitErrorWindow.initOwner(stage);
                this.launcher.setText(Message.BUTTONLABEL_LAUNCH.getCaption(BurrowsWheelerStandardEncoding.this.locale));
                this.launcher.setOnAction(event -> {
                    if (this.inputField.getText().length() > BurrowsWheelerStandardEncoding.this.inputLimit) {
                        this.wordLengthExceedsLimitErrorWindow.show();
                        return;
                    }
                    BurrowsWheelerStandardEncoding.this.launch(this.inputField.getText().toLowerCase());
                    for (int i = 0; i < this.inputField.getText().length(); i++) {
                        for (int j = 0; j < this.inputField.getText().length(); j++) {
                            final int currentI = i;
                            final int currentJ = j;
                            TextField matrixField = new TextField();
                            matrixField.setAlignment(Pos.CENTER);
                            matrixField.setEditable(false);
                            matrixField.setText(BurrowsWheelerStandardEncoding.this.inputTable[i].toString());
                            // make sure the text of matrixField updates to this.inputTable[i].toString().charAt(j) whenever inputTable changes
                            BurrowsWheelerStandardEncoding.this.inputTable[i].addObserver(new Observer() {
                                private BurrowsWheelerTransformationCore.BurrowsWheelerTableLine observedLine = BurrowsWheelerStandardEncoding.this.inputTable[currentI];

                                @Override
                                public void update(Observable o, Object arg) {
                                        matrixField.setText(observedLine.toString().charAt(currentJ) + "");

                                }
                            });
                            this.linkers.add(() -> {
                                ObservableList<Node> matrix = this.table.getChildren();
                                for (int i1 = 0; i1 < matrix.size(); i1++) {
                                    ((TextField) matrix.get(i1)).setText(BurrowsWheelerStandardEncoding.this.inputTable[i1].toString());
                                }
                                this.layout();
                            });
                            GridPane.setRowIndex(matrixField, i);
                            GridPane.setColumnIndex(matrixField, j);
                            this.table.getChildren().add(matrixField);
                        }
                    }
                });
            }

            @Override
            public void update(Observable o, Object arg) {
                if (o == BurrowsWheelerStandardEncoding.this) {
                    for (UpdateLinker linker : this.linkers) {
                        linker.adjustContent();
                    }
                }
            }

            @Override
            public boolean isAssociatedWith (BurrowsWheelerTransformationCore.Algorithms algorithm) {
                return algorithm == BurrowsWheelerTransformationCore.Algorithms.BW_STANDARD_ENCODE;
            }

            @Override
            public ObservableList<Node> getChildren() {
                ObservableList<Node> nodes = FXCollections.observableArrayList();
                nodes.add(this.inputField);
                nodes.add(this.launcher);
                nodes.add(this.table);
                return FXCollections.unmodifiableObservableList(nodes);
            }
        };
        this.addObserver(pane);
        return pane;
    }

}
