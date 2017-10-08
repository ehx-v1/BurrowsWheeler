package gui;

import core.BurrowsWheelerTransformationCore;
import javafx.scene.layout.VBox;

import java.util.Observer;

/**
 * Created by root on 23.04.2017.
 */
public abstract class ViewerPane extends VBox implements Observer {

    public interface UpdateLinker {
        void adjustContent();
    }

    public abstract boolean isAssociatedWith (BurrowsWheelerTransformationCore.Algorithms algorithm);

}
