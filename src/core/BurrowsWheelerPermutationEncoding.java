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

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerPermutationEncoding extends BurrowsWheelerStandardEncoding {
    private BurrowsWheelerTransformationCore.Permutation permutation;
    protected boolean[] permutated;

    public BurrowsWheelerPermutationEncoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        super(core, onPreBegin, onPostEnd);
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
    public ViewerPane getViewer() {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private Button launcher = new Button();
            private BurrowsWheelerTransformationCore.Permutation permutation;
            private Button permutationMenu = new Button();
            private GridPane table = new GridPane();
            // TODO display fields for results

            {
                this.launcher.setText("Launch");
                this.launcher.setOnAction(event -> {
                    for (int i = 0; i < this.inputField.getText().length(); i++) {
                        for (int j = 0; j < this.inputField.getText().length(); j++) {
                            final int currentI = i;
                            final int currentJ = j;
                            TextField matrixField = new TextField();
                            matrixField.setAlignment(Pos.CENTER);
                            matrixField.setEditable(false);
                            matrixField.setText(BurrowsWheelerPermutationEncoding.this.inputTable[i].toString());
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
                            GridPane.setRowIndex(matrixField, i);
                            GridPane.setColumnIndex(matrixField, j);
                            this.table.getChildren().add(matrixField);
                        }
                    }
                    BurrowsWheelerPermutationEncoding.this.launch(this.inputField.getText().toLowerCase());
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

            @Override
            protected ObservableList<Node> getChildren() {
                ObservableList<Node> nodes = FXCollections.observableArrayList();
                nodes.add(this.inputField);
                nodes.add(this.launcher);
                nodes.add(this.permutationMenu);
                nodes.add(this.table);
                return nodes;
            }
        };
    }
}
