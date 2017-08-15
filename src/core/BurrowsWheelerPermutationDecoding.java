package core;

import gui.ViewerPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.*;

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
    public ViewerPane getViewer() {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
            private TextField permutationIndexField = new TextField();
            private Button launcher = new Button();
            private int readoutIndex = 0;
            private BurrowsWheelerTransformationCore.Permutation permutation;
            private Button permutationMenu = new Button();
            private GridPane table = new GridPane();

            {
                this.launcher.setText("Launch");
                this.launcher.setOnMouseClicked(event -> {
                    try {
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
                                GridPane.setRowIndex(matrixField, i);
                                GridPane.setColumnIndex(matrixField, j);
                                this.table.getChildren().add(matrixField);
                            }
                        }
                        BurrowsWheelerPermutationDecoding.this.launch(this.inputField.getText(), this.readoutIndex, this.permutation, this.parseFlags(Integer.parseInt(this.permutationIndexField.getText()), this.inputField.getText().length()));
                    } catch (NumberFormatException e) {
                        // TODO make popup that index input must be a number
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
                // TODO set icon and tooltip of permutationMenu
                this.permutationMenu.setOnMouseClicked(event -> {
                    // TODO make popup menu that allows for setting the mapping for each lowercase character
                });
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
