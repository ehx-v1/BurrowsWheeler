package gui;

import util.AlgorithmUtils;
import core.BurrowsWheelerTransformationCore;
import runtimeframework.DebugQueue;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by root on 14.04.2017.
 */
public class JavaFXFrontend extends Application {
    private BurrowsWheelerTransformationCore core;
    private List<BurrowsWheelerTransformationCore.AlgorithmImplementationStub> impls;
    private DebugQueue currentQueue;

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        ToolBar top = new ToolBar();
        MenuBar menu = new MenuBar();
        Menu actualMenu = new Menu();
        menu.getMenus().add(actualMenu);
        ToggleGroup menuGroup = new ToggleGroup();
        this.core = new BurrowsWheelerTransformationCore(this.readMaxLengthFromConfig());
        this.impls = new ArrayList<>();
        ViewerPaneContainer paneContainer = new ViewerPaneContainer();
        for (BurrowsWheelerTransformationCore.Algorithms algorithm : BurrowsWheelerTransformationCore.Algorithms.values()) {
            RadioMenuItem item = new RadioMenuItem(AlgorithmUtils.algorithmCaption(algorithm));
            item.setToggleGroup(menuGroup);
            this.impls.add(AlgorithmUtils.createAlgorithm(this.core, algorithm, () -> {
                // TODO make popup
            }, () -> {
                // TODO make popup
            }));
            actualMenu.getItems().add(item);
            item.setOnAction(event -> {
                if (!item.isSelected()) return;
                paneContainer.setPane(this.impls.get(algorithm.ordinal()).getViewer());
                this.currentQueue = this.core.getRegisteredAlgorithm(algorithm);
            });
        }
        root.getChildren().add(paneContainer);
        top.getItems().add(menu);
        Button back = new Button();
        back.setTooltip(new Tooltip("Step back"));
        // TODO set icon for back
        back.setOnMouseClicked(event -> this.currentQueue.stepBack());
        top.getItems().add(back);
        Button forward = new Button();
        forward.setTooltip(new Tooltip("Step forward"));
        // TODO set icon for forward
        forward.setOnMouseClicked(event -> this.currentQueue.stepForward());
        top.getItems().add(forward);
        Button settings = new Button();
        settings.setTooltip(new Tooltip("Settings"));
        // TODO set icon for settings
        settings.setOnMouseClicked(event -> {
            // TODO open settings window (and make sure config has an effect)
        });
        top.getItems().add(settings);
        root.getChildren().add(top);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Burrows Wheeler Transformation - Universal GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int readMaxLengthFromConfig() {
        // TODO
        return 20; // keep as fallback number if config cannot be read
    }

    public static void main(String[] args) {
        Application.launch(JavaFXFrontend.class, args);
    }

}
