package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * Created by root on 15.06.2017.
 */
public class ViewerPaneContainer extends Parent {
    private ViewerPane pane;
    private StackPane layoutCore = new StackPane();

    public ViewerPaneContainer (ViewerPane pane) {
        this.pane = pane;
        if (pane != null) {
            this.layoutCore.getChildren().add(this.pane);
        }
    }

    public ViewerPaneContainer() {
        this (null);
    }

    public ViewerPane getPane() {
        return this.pane;
    }

    public void setPane(ViewerPane pane) {
        this.pane = pane;
        this.layoutCore.getChildren().clear();
        if (pane != null) {
            this.layoutCore.getChildren().add(this.pane);
        }
    }

    @Override
    protected ObservableList<Node> getChildren() {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(this.layoutCore));
    }
}
