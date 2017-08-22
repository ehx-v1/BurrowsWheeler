package core;

import gui.ViewerPane;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerPermutationDecoding extends BurrowsWheelerIntuitiveDecoding {

    private BurrowsWheelerTransformationCore.Permutation permutation;
    protected int index;
    private boolean[] permutated;

    public BurrowsWheelerPermutationDecoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        super(core, onPreBegin, onPostEnd);
    }

    protected void launch(String input, int decodingIndex, BurrowsWheelerTransformationCore.Permutation permutation, boolean[] permutated) {
        this.permutation = permutation;
        this.permutated = permutated;
        super.launch(this.permutate(input));
        this.index = decodingIndex;
    }

    private String permutate(String input) {
        String tmp = "";
        for (int i = 0; i < input.length(); i++) {
            if (this.permutated[i]) {
                tmp += this.permutation.permutate(input.charAt(i));
            } else {
                tmp += input.charAt(i);
            }
        }
        return tmp;
    }

    @Override
    protected void sort() {
        Arrays.sort(this.inputTable, (o1, o2) -> Character.compare(this.withPermutation(o1, 0), this.withPermutation(o2, 0)));
    }

    @Override
    protected void revertSort() {
        Arrays.sort(this.inputTable, inputTable[0].isSecondSlotFilled() ?
                (o1, o2) -> Character.compare(this.withPermutation(o1, 1), this.withPermutation(o2, 1))
                : BurrowsWheelerTransformationCore.BurrowsWheelerTableLine.firstSortingRevertComparator());
    }

    private char withPermutation (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine line, int charIndex) {
        return this.permutated[Arrays.asList(this.inputTable).indexOf(line)] ? this.permutation.permutate(line.toString().charAt(charIndex)) : line.toString().charAt(charIndex);
    }

    @Override
    public ViewerPane getViewer(Stage stage) {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
            private TextField permutationIndexField = new TextField();
            private Button launcher = new Button();
            private int readoutIndex = 0;
            private BurrowsWheelerTransformationCore.Permutation permutation;
            private Button permutationMenu = new Button();
            private GridPane table = new GridPane();
            private Stage actualPermutationMenu = new Stage();
            private Stage indexOutOfWordErrorWindow = new Stage();
            private Stage indexNotANumberErrorWindow = new Stage();
            private Stage wordLengthExceedsLimitErrorWindow = new Stage();

            { // TODO position children
                StackPane error1Root = new StackPane();
                TextField error1Message = new TextField();
                error1Message.setEditable(false);
                error1Message.setText("Please select an index within the word you enter.");
                Button error1OK = new Button();
                error1OK.setText("OK");
                error1OK.setOnMouseClicked(event -> this.indexOutOfWordErrorWindow.hide());
                error1Root.getChildren().addAll(error1Message, error1OK);
                Scene error1Scene = new Scene(error1Root); // TODO size subwindow
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
                Scene error2Scene = new Scene(error2Root); // TODO size subwindow
                this.indexNotANumberErrorWindow.setTitle("Error");
                this.indexNotANumberErrorWindow.setScene(error2Scene);
                this.indexNotANumberErrorWindow.initStyle(StageStyle.DECORATED);
                this.indexNotANumberErrorWindow.initModality(Modality.NONE);
                this.indexNotANumberErrorWindow.initOwner(stage);
                StackPane error3Root = new StackPane();
                TextField error3Message = new TextField();
                error3Message.setEditable(false);
                error3Message.setText("Please enter a word that's shorter than the length limit,\nor change the length limit for your word to fit.");
                error3Message.setAlignment(Pos.TOP_CENTER);
                Button error3OK = new Button();
                error3OK.setText("OK");
                error3OK.setOnMouseClicked(event -> this.wordLengthExceedsLimitErrorWindow.hide());
                error3Root.getChildren().addAll(error3Message, error3OK);
                Scene error3Scene = new Scene(error3Root); // TODO size subwindow
                this.wordLengthExceedsLimitErrorWindow.setTitle("Error");
                this.wordLengthExceedsLimitErrorWindow.setScene(error3Scene);
                this.wordLengthExceedsLimitErrorWindow.initStyle(StageStyle.DECORATED);
                this.wordLengthExceedsLimitErrorWindow.initModality(Modality.NONE);
                this.wordLengthExceedsLimitErrorWindow.initOwner(stage);
                this.launcher.setText("Launch");
                this.launcher.setOnMouseClicked(event -> {
                    try {if (this.inputField.getText().length() > BurrowsWheelerPermutationDecoding.this.inputLimit) {
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
                                matrixField.setText(BurrowsWheelerPermutationDecoding.this.inputTable[i].toString());
                                // make sure the text of matrixField updates to this.inputTable[i].toString().charAt(j) whenever inputTable changes
                                BurrowsWheelerPermutationDecoding.this.inputTable[i].addObserver(new Observer() {
                                    private BurrowsWheelerTransformationCore.BurrowsWheelerTableLine observedLine = BurrowsWheelerPermutationDecoding.this.inputTable[currentI];

                                    @Override
                                    public void update(Observable o, Object arg) {
                                        if (o == observedLine) {
                                            matrixField.setText(observedLine.toString().charAt(currentJ) + "");
                                        }
                                    }
                                });
                                // TODO ensure clean update on sorting
                                GridPane.setRowIndex(matrixField, i);
                                GridPane.setColumnIndex(matrixField, j);
                                this.table.getChildren().add(matrixField);
                            }
                        }
                        BurrowsWheelerPermutationDecoding.this.launch(this.inputField.getText(), this.readoutIndex, this.permutation, this.parseFlags(Integer.parseInt(this.permutationIndexField.getText()), this.inputField.getText().length()));
                    } catch (NumberFormatException e) {
                        this.indexNotANumberErrorWindow.show();
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

                    public void setMapping(char orig, char replace) {
                        this.actualPermutation.put(Character.toLowerCase(orig), Character.toLowerCase(replace));
                    }

                    @Override
                    public char permutate(char original) {
                        return this.actualPermutation.get(original);
                    }
                };
                StackPane subroot = new StackPane();
                // fill actualPermutationMenu with permutation mapping text fields and a confirm button that sets the permutation mappings
                TextField[] labelFields = new TextField[26];
                TextField[] inputFields = new TextField[26];
                for (char c = 'a'; c < 'z'; c++) {
                    labelFields[c - 'a'] = new TextField();
                    labelFields[c - 'a'].setEditable(false);
                    labelFields[c - 'a'].setText(c + "");
                    inputFields[c - 'a'] = new TextField();
                    inputFields[c - 'a'].setText(c + "");
                    subroot.getChildren().addAll(labelFields[c - 'a'], inputFields[c - 'a']);
                }
                Button confirmer = new Button();
                confirmer.setText("OK");
                confirmer.setOnMouseClicked(event -> {
                    for (int i = 0; i < 26; i++) {
                        this.permutation.setMapping(labelFields[i].getText().charAt(0), inputFields[i].getText().charAt(0));
                    }
                    this.actualPermutationMenu.hide();
                });
                subroot.getChildren().add(confirmer);
                Scene subscene = new Scene(subroot); // TODO size subwindow
                this.actualPermutationMenu.setTitle("Set permutations...");
                this.actualPermutationMenu.setScene(subscene);
                this.actualPermutationMenu.initStyle(StageStyle.DECORATED);
                this.actualPermutationMenu.initModality(Modality.NONE);
                this.actualPermutationMenu.initOwner(stage);
                // TODO set icon of permutationMenu
                this.permutationMenu.setTooltip(new Tooltip("Set permutation..."));
                this.permutationMenu.setOnMouseClicked(event -> this.actualPermutationMenu.show());
            }

            private boolean[] parseFlags(int flagContainer, int sizeToParse) {
                boolean[] result = new boolean[sizeToParse];
                for (int i = 0; i < sizeToParse; i++) {
                    result[i] = (flagContainer % 2 != 0);
                    flagContainer /= 2;
                }
                return result;
            }

            @Override
            protected ObservableList<Node> getChildren() {
                ObservableList<Node> nodes = FXCollections.observableArrayList();
                nodes.add(this.inputField);
                nodes.add(this.indexField);
                nodes.add(this.launcher);
                nodes.add(this.permutationMenu);
                return nodes;
            }
        };
    }
}
