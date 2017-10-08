package core;

import gui.ViewerPane;
import javafx.scene.layout.HBox;
import util.AlgorithmUtils;
import util.ThisShouldNotHappenException;
import util.runtimeframework.DebugQueue;
import util.runtimeframework.DebugStep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerPermutationEncoding extends BurrowsWheelerStandardEncoding {

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

    private BurrowsWheelerTransformationCore.Permutation permutation;
    protected boolean[] permutated;

    public BurrowsWheelerPermutationEncoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd, AlgorithmUtils.Locale locale) {
        super(core, onPreBegin, onPostEnd, locale);
    }

    public BurrowsWheelerPermutationEncoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        this(core, onPreBegin, onPostEnd, AlgorithmUtils.Locale.DE);
    }

    protected void launch(String input, BurrowsWheelerTransformationCore.Permutation permutation) {
        super.launch(input);
        this.permutation = permutation;
        this.permutated = new boolean[this.inputTable.length];
        for (int i = 0; i < this.permutated.length; i++) {
            this.permutated[i] = false;
        }
    }

    private void permutate() {
        for (int i = 0; i < this.inputTable.length; i++) {
            BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputTableLine = this.permutation.permutate(this.inputTable[i]);
            if (inputTableLine.toString().compareTo(this.inputTable[i].toString()) < 0) {
                this.inputTable[i] = inputTableLine;
                this.permutated[i] = true;
            }
        }
    }

    private void revertPermutate() {
        for (int i = 0; i < Math.min(this.inputTable.length, this.permutated.length); i++) {
            if (this.permutated[i]) {
                this.inputTable[i] = this.permutation.permutate(this.inputTable[i]);
            }
        }
    }

    @Override
    public DebugQueue getExecution() {
        DebugQueue queue = super.getExecution();
        queue.add(queue.size() - 2, DebugStep.builder()
                .setForward(BurrowsWheelerPermutationEncoding.this::permutate)
                .setBackward(BurrowsWheelerPermutationEncoding.this::revertPermutate)
                .build());
        return queue;
    }

    @Override
    public ViewerPane getViewer(Stage stage) {
        ViewerPane pane = new ViewerPane() {
            private TextField inputField = new TextField();
            private Button launcher = new Button();
            private BurrowsWheelerTransformationCore.Permutation permutation;
            private Button permutationMenu = new Button();
            private GridPane table = new GridPane();
            private Stage actualPermutationMenu = new Stage();
            private Stage wordLengthExceedsLimitErrorWindow = new Stage();
            private List<UpdateLinker> linkers = new ArrayList<>();
            // TODO display fields for results

            { // TODO position children
                StackPane error3Root = new StackPane(); // TODO replace with appropriate layout element
                TextField error3Message = new TextField();
                error3Message.setEditable(false);
                error3Message.setText(Message.ERROR_LENGTH_EXCEEDS_LIMIT.getCaption(BurrowsWheelerPermutationEncoding.this.locale));
                error3Message.setAlignment(Pos.TOP_CENTER);
                Button error3OK = new Button();
                error3OK.setText(Message.BUTTONLABEL_CONFIRM.getCaption(BurrowsWheelerPermutationEncoding.this.locale));
                error3OK.setOnMouseClicked(event -> this.wordLengthExceedsLimitErrorWindow.hide());
                error3Root.getChildren().addAll(error3Message, error3OK);
                Scene error3Scene = new Scene(error3Root);
                this.wordLengthExceedsLimitErrorWindow.setTitle(Message.HEAD_ERROR.getCaption(BurrowsWheelerPermutationEncoding.this.locale));
                this.wordLengthExceedsLimitErrorWindow.setScene(error3Scene);
                this.wordLengthExceedsLimitErrorWindow.initStyle(StageStyle.DECORATED);
                this.wordLengthExceedsLimitErrorWindow.initModality(Modality.NONE);
                this.wordLengthExceedsLimitErrorWindow.initOwner(stage);
                this.launcher.setText("Launch");
                this.launcher.setOnAction(event -> {
                    if (this.inputField.getText().length() > BurrowsWheelerPermutationEncoding.this.inputLimit) {
                        this.wordLengthExceedsLimitErrorWindow.show();
                        return;
                    }
                    BurrowsWheelerPermutationEncoding.this.launch(this.inputField.getText().toLowerCase());
                    for (int i = 0; i < this.inputField.getText().length(); i++) {
                        for (int j = 0; j < this.inputField.getText().length(); j++) {
                            final int currentI = i;
                            final int currentJ = j;
                            TextField matrixField = new TextField();
                            matrixField.setAlignment(Pos.CENTER);
                            matrixField.setEditable(false);
                            matrixField.setText(BurrowsWheelerPermutationEncoding.this.inputTable[i].toString().charAt(j) + "");
                            // make sure the text of matrixField updates to this.inputTable[i].toString().charAt(j) whenever inputTable changes
                            BurrowsWheelerPermutationEncoding.this.inputTable[i].addObserver(new Observer() {
                                private BurrowsWheelerTransformationCore.BurrowsWheelerTableLine observedLine = BurrowsWheelerPermutationEncoding.this.inputTable[currentI];

                                @Override
                                public void update(Observable o, Object arg) {
                                    if (o == observedLine) {
                                        matrixField.setText(observedLine.toString().charAt(currentJ) + "");
                                    }
                                }
                            });
                            this.linkers.add(() -> {
                                ObservableList<Node> matrix = this.table.getChildren();
                                for (int i1 = 0; i1 < matrix.size(); i1++) {
                                    ((TextField) matrix.get(i1)).setText(BurrowsWheelerPermutationEncoding.this.inputTable[i1].toString());
                                }
                                this.layout();
                            });
                            GridPane.setRowIndex(matrixField, i);
                            GridPane.setColumnIndex(matrixField, j);
                            this.table.getChildren().add(matrixField);
                        }
                    }
                });
                this.permutation = new BurrowsWheelerTransformationCore.Permutation() {
                    private Map<Character, Character> actualPermutation;

                    {
                        this.actualPermutation = new HashMap<>();
                        for (char c = 'a'; c <= 'z'; c++) {
                            this.actualPermutation.put(c, c);
                        }
                    }

                    @Override
                    public void setMapping(char orig, char replace) {
                        this.actualPermutation.put(Character.toLowerCase(orig), Character.toLowerCase(replace));
                    }

                    @Override
                    public char permutate(char original) {
                        return this.actualPermutation.get(original);
                    }
                };
                StackPane subroot = new StackPane(); // TODO replace with appropriate layout element
                // fill actualPermutationMenu with permutation mapping text fields and a confirm button that sets the permutation mappings
                TextField[] labelFields = new TextField[26];
                TextField[] inputFields = new TextField[26];
                for (char c = 'a'; c <= 'z'; c++) {
                    labelFields[c - 'a'] = new TextField();
                    labelFields[c - 'a'].setEditable(false);
                    labelFields[c - 'a'].setText(c + "");
                    GridPane.setRowIndex(labelFields[c - 'a'], c - 'a');
                    inputFields[c - 'a'] = new TextField();
                    inputFields[c - 'a'].setText(c + "");
                    GridPane.setRowIndex(inputFields[c - 'a'], c - 'a');
                    GridPane.setColumnIndex(inputFields[c - 'a'], 1);
                }
                GridPane permutationTable = new GridPane();
                permutationTable.getChildren().addAll(Stream.concat(Arrays.stream(labelFields), Arrays.stream(inputFields)).collect(Collectors.toList()));
                subroot.getChildren().add(permutationTable);
                Button confirmer = new Button();
                confirmer.setText("OK");
                confirmer.setOnMouseClicked(event -> {
                    for (int i = 0; i < 26; i++) {
                        this.permutation.setMapping(labelFields[i].getText().charAt(0), inputFields[i].getText().charAt(0));
                    }
                    this.actualPermutationMenu.hide();
                });
                subroot.getChildren().add(confirmer);
                Scene subscene = new Scene(subroot);
                this.actualPermutationMenu.setScene(subscene);
                this.actualPermutationMenu.initStyle(StageStyle.DECORATED);
                this.actualPermutationMenu.initModality(Modality.NONE);
                this.actualPermutationMenu.initOwner(stage);
                // TODO set icon of permutationMenu
                this.permutationMenu.setTooltip(new Tooltip("Set permutation..."));
                this.permutationMenu.setOnMouseClicked(event -> this.actualPermutationMenu.show());
            }

            @Override
            public void update(Observable o, Object arg) {
                if (o == BurrowsWheelerPermutationEncoding.this) {
                    for (UpdateLinker linker : this.linkers) {
                        linker.adjustContent();
                    }
                }
            }

            @Override
            public boolean isAssociatedWith (BurrowsWheelerTransformationCore.Algorithms algorithm) {
                return algorithm == BurrowsWheelerTransformationCore.Algorithms.BW_PERMUTATIONS_ENCODE;
            }

            @Override
            public ObservableList<Node> getChildren() {
                ObservableList<Node> nodes = FXCollections.observableArrayList();
                HBox topLine = new HBox();
                topLine.getChildren().add(this.inputField);
                topLine.getChildren().add(this.permutationMenu);
                topLine.getChildren().add(this.launcher);
                nodes.add(topLine);
                nodes.add(this.table);
                return FXCollections.unmodifiableObservableList(nodes);
            }
        };
        this.addObserver(pane);
        return pane;
    }
}
