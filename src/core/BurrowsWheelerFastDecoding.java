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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerFastDecoding implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub {

    private static class IndexedCharacter implements Comparable<IndexedCharacter> {
        private char content;
        private int index;

        public IndexedCharacter(char c) {
            this.content = c;
            this.index = -1;
        }

        @Override
        public int compareTo(IndexedCharacter o) {
            return Character.compare(this.content, o.content);
        }

        public static Comparator<IndexedCharacter> reversionComparator() {
            return Comparator.comparingInt(character -> character.index);
        }

    }

    private List<IndexedCharacter> characters;
    private int index;
    protected String result; // will be used in the visualization

    public BurrowsWheelerFastDecoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        core.addImplementation(this, onPreBegin, onPostEnd);
    }

    protected void launch(String input, int index) {
        this.characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            this.characters.add(new IndexedCharacter(c));
        }
        this.index = index;
        this.result = "";
    }

    private void makeIndexes() {
        for (int i = 0; i < this.characters.size(); i++) {
            this.characters.get(i).index = i;
        }
    }

    private void revertMakeIndexes() {
        for (IndexedCharacter character : this.characters) {
            character.index = -1;
        }
    }

    private void sort() {
        Collections.sort(this.characters);
    }

    private void revertSort() {
        this.characters.sort(IndexedCharacter.reversionComparator());
    }

    private void chain() {
        int currentChar = this.index;
        do {
            currentChar = this.characters.get(currentChar).index;
            this.result += this.characters.get(currentChar).content;
        } while (currentChar != this.index);
    }

    private void revertChain() {
        this.result = "";
    }

    @Override
    public DebugQueue getExecution() {
        DebugQueue queue = new DebugQueue();
        DebugStep.DebugStepBuilder builder = DebugStep.builder();
        queue.add(builder.setForward(BurrowsWheelerFastDecoding.this::makeIndexes)
                .setBackward(BurrowsWheelerFastDecoding.this::revertMakeIndexes)
                .build());
        queue.add(builder.setForward(BurrowsWheelerFastDecoding.this::sort)
                .setBackward(BurrowsWheelerFastDecoding.this::revertSort)
                .build());
        queue.add(builder.setForward(BurrowsWheelerFastDecoding.this::chain)
                .setBackward(BurrowsWheelerFastDecoding.this::revertChain)
                .build());
        return queue;
    }

    @Override
    public ViewerPane getViewer() {
        return new ViewerPane() {
            private ObservableList<Node> childrenCache;
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
            private Button launcher = new Button();
            private GridPane matching = new GridPane();

            {
                this.launcher.setText("Launch");
                this.launcher.setOnMouseClicked(event -> {
                    try {
                        if (Integer.parseInt(this.indexField.getText()) >= this.inputField.getText().length()) {
                            // TODO make popup that index out of word
                            return;
                        }
                        BurrowsWheelerFastDecoding.this.launch(this.inputField.getText(), Integer.parseInt(this.indexField.getText()));
                        for (int i = 0; i < this.inputField.getText().length(); i++) {
                            TextField charText = new TextField();
                            charText.setAlignment(Pos.CENTER);
                            // TODO make sure the text of charText updates to index of characters.get(i) whenever it changes
                            GridPane.setRowIndex(charText, i);
                            this.matching.getChildren().add(charText);

                            TextField charIndex = new TextField();
                            charIndex.setAlignment(Pos.CENTER);
                            // TODO make sure the text of charText updates to content of characters.get(i) whenever it changes
                            GridPane.setRowIndex(charIndex, i);
                            GridPane.setColumnIndex(charIndex, 1);
                            this.matching.getChildren().add(charIndex);
                        }
                    } catch (NumberFormatException e) {
                        // TODO make popup that index input must be a number
                    }
                });
            }

            @Override
            protected ObservableList<Node> getChildren() {
                if (this.childrenCache == null) {
                    this.childrenCache = FXCollections.observableArrayList();
                    this.childrenCache.add(this.inputField);
                    this.childrenCache.add(this.indexField);
                    this.childrenCache.add(this.launcher);
                }
                return this.childrenCache;
            }
        };
    }
}
