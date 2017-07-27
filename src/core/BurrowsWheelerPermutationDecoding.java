package core;

import gui.ViewerPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import runtimeframework.DebugQueue;
import runtimeframework.DebugStep;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerPermutationDecoding extends BurrowsWheelerIntuitiveDecoding {

    private class PermutationInput extends Parent {
        private TextField mapping = new TextField();

        public BurrowsWheelerTransformationCore.Permutation confirm() {
            String text = mapping.getText();
            if (text.length() != 26) return null;
            String compare = "abcdefghijklmnopqrstuvwxyz";
            return original -> text.charAt(compare.indexOf(original));
        }
    }

    private BurrowsWheelerTransformationCore.Permutation permutation;
    private int index;

    public BurrowsWheelerPermutationDecoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        super(core, onPreBegin, onPostEnd);
    }

    protected void launch(String input, int decodingIndex, BurrowsWheelerTransformationCore.Permutation permutation) {
        super.launch(input);
        this.index = decodingIndex;
        this.permutation = permutation;
    }

    private void permutate() {
        // make deep copy of inputTable
        BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[] lines = new BurrowsWheelerTransformationCore.BurrowsWheelerTableLine[this.inputTable.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = this.inputTable[i].contentCopy(i);
        }
        String textOnIndex = this.inputTable[this.index].toString();
        // align all lines to the index line
        for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine line : lines) {
            while (!line.isConvertible(textOnIndex, this.permutation)) {
                line.rotateLeft();
            }
        }
        // find smallest unique prefix
        String prefix = "";
        for (char c : textOnIndex.toCharArray()) {
            prefix += c;
            if (!textOnIndex.substring(1).contains(prefix)) break; // smallest unique substring found in this case
        }
        // until a line is different on from the prefix, compare lines
        for (BurrowsWheelerTransformationCore.BurrowsWheelerTableLine line : lines) {
            switch ((int)Math.signum(line.toString().compareTo(textOnIndex))) {
                case 0:
                    continue;
                case 1: // permutate only if different line is lexically larger
                    this.inputTable[this.index] = this.permutation.permutate(this.inputTable[this.index]);
                    return;
                case -1:
                    return;
            }
        }

    }

    private void revertPermutate() {

        BurrowsWheelerTransformationCore.BurrowsWheelerTableLine inputTableLine = this.permutation.permutate(this.inputTable[this.index]);
        if (inputTableLine.toString().compareTo(this.inputTable[this.index].toString()) < 0) {
            this.inputTable[this.index] = inputTableLine;
        }

    }

    @Override
    public DebugQueue getExecution() {
        DebugQueue queue = super.getExecution();
        queue.add(DebugStep.builder()
                .setForward(BurrowsWheelerPermutationDecoding.this::permutate)
                .setBackward(BurrowsWheelerPermutationDecoding.this::revertPermutate)
                .build());
        return queue;
    }

    @Override
    public ViewerPane getViewer() {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
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
                                TextField matrixField = new TextField();
                                matrixField.setAlignment(Pos.CENTER);
                                // TODO make sure the text of matrixField updates to this.inputTable[i].toString().charAt(j) whenever inputTable changes
                                GridPane.setRowIndex(matrixField, i);
                                GridPane.setColumnIndex(matrixField, j);
                                this.table.getChildren().add(matrixField);
                            }
                        }
                        BurrowsWheelerPermutationDecoding.this.launch(this.inputField.getText());
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
