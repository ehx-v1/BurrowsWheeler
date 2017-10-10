package core;

import gui.ViewerPane;
import util.runtimeframework.DebugQueue;
import util.runtimeframework.DebugStep;

import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
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
public class BurrowsWheelerFastDecoding extends Observable implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub {

    protected static class IndexedCharacter implements Comparable<IndexedCharacter> {
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

        public boolean hasIndex (int index) {
            return this.index == index;
        }

    }

    protected ObservableList<IndexedCharacter> characters;
    private int index;
    protected String result; // will be used in the visualization

    public BurrowsWheelerFastDecoding(BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        core.addImplementation(this, onPreBegin, onPostEnd);
    }

    protected void launch(String input, int index) {
        this.characters = FXCollections.observableArrayList();
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

    protected void revertMakeIndexes() {
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
            this.result += this.characters.get(currentChar).content;
            currentChar = this.characters.get(currentChar).index;
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
    public ViewerPane getViewer(Stage stage) {
        return new ViewerPane() {
            private TextField inputField = new TextField();
            private TextField indexField = new TextField();
            private Button launcher = new Button();
            private GridPane matching = new GridPane();
            private Stage indexOutOfWordErrorWindow = new Stage();
            private Stage indexNotANumberErrorWindow = new Stage();

            {
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
                StackPane error2Root = new StackPane(); // TODO replace with appropriate layout element
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
                this.launcher.setText("Launch");
                this.launcher.setOnMouseClicked(event -> {
                    try {
                        if (Integer.parseInt(this.indexField.getText()) >= this.inputField.getText().length()) {
                            this.indexOutOfWordErrorWindow.show();
                            return;
                        }
                        BurrowsWheelerFastDecoding.this.launch(this.inputField.getText(), Integer.parseInt(this.indexField.getText()));
                        for (int i = 0; i < this.inputField.getText().length(); i++) {
                            final int currentI = i;
                            TextField charText = new TextField();
                            charText.setAlignment(Pos.CENTER);
                            charText.setText(BurrowsWheelerFastDecoding.this.characters.get(i).content + "");

                            TextField charIndex = new TextField();
                            charIndex.setAlignment(Pos.CENTER);
                            charIndex.setText(BurrowsWheelerFastDecoding.this.characters.get(i).index + "");

                            BurrowsWheelerFastDecoding.this.characters.addListener((ListChangeListener<IndexedCharacter>) c -> {
                                charText.setText(BurrowsWheelerFastDecoding.this.characters.get(currentI).content + "");
                                charIndex.setText(BurrowsWheelerFastDecoding.this.characters.get(currentI).index + "");
                            });
                            GridPane.setColumnIndex(charText, i);
                            this.matching.getChildren().add(charText);
                            GridPane.setColumnIndex(charIndex, i);
                            GridPane.setRowIndex(charIndex, 1);
                            this.matching.getChildren().add(charIndex);
                        }
                    } catch (NumberFormatException e) {
                        this.indexNotANumberErrorWindow.show();
                    }
                });
                HBox topLine = new HBox();
                topLine.getChildren().add(this.inputField);
                topLine.getChildren().add(this.indexField);
                topLine.getChildren().add(this.launcher);
                this.getChildren().add(topLine);
                this.getChildren().add(this.matching);
            }

            @Override
            public void update(Observable o, Object arg) {
                // nothing to update in this algorithm
            }

            @Override
            public boolean isAssociatedWith (BurrowsWheelerTransformationCore.Algorithms algorithm) {
                return algorithm == BurrowsWheelerTransformationCore.Algorithms.BW_STANDARD_DECODE_FAST;
            }
        }; // normally it would be added to the algorithm's observers, but there's no content in this algorithm that could not be observed by a dedicated observer
    }
}
