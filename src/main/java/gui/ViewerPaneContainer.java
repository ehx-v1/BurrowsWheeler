package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Created by root on 15.06.2017.
 */
public class ViewerPaneContainer extends Parent {
    private ViewerPane pane;

    public ViewerPane getPane() {
        return pane;
    }

    public void setPane(ViewerPane pane) {
        this.pane = pane;
    }

    @Override
    protected ObservableList<Node> getChildren() {
        return FXCollections.observableArrayList(this.getPane());
    }
}
