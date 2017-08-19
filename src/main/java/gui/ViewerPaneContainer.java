package gui;

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
}
