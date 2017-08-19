package gui;

import core.BurrowsWheelerTransformationCore;
import util.runtimeframework.DebugQueue;
import util.AlgorithmUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

/**
 * Created by root on 14.04.2017.
 */
public class JavaFXFrontend extends Application {
    private BurrowsWheelerTransformationCore core;
    private List<BurrowsWheelerTransformationCore.AlgorithmImplementationStub> impls;
    private DebugQueue currentQueue;
    private Properties config;
    public final static String PROPERTY_FILE = "config.properties";

    @Override
    public void start(Stage primaryStage) throws Exception { // TODO position children
        this.config = new Properties();
        StackPane root = new StackPane();
        StackPane subroot1 = new StackPane();
        Scene popup1Scene = new Scene(subroot1); // TODO size subwindow
        Stage popup1Stage = new Stage();
        popup1Stage.setTitle("Info");
        popup1Stage.setScene(popup1Scene);
        popup1Stage.initStyle(StageStyle.DECORATED);
        popup1Stage.initModality(Modality.NONE);
        popup1Stage.initOwner(primaryStage);
        // TODO fill popup1Stage with a text and a button
        StackPane subroot2 = new StackPane();
        Scene popup2Scene = new Scene(subroot2); // TODO size subwindow
        Stage popup2Stage = new Stage();
        popup2Stage.setTitle("Info");
        popup2Stage.setScene(popup2Scene);
        popup2Stage.initStyle(StageStyle.DECORATED);
        popup2Stage.initModality(Modality.NONE);
        popup2Stage.initOwner(primaryStage);
        // TODO fill popup2Stage with a text and a button
        StackPane subroot3 = new StackPane();
        Scene settingsScene = new Scene(subroot3); // TODO size subwindow
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Info");
        settingsStage.setScene(settingsScene);
        settingsStage.initStyle(StageStyle.DECORATED);
        settingsStage.initModality(Modality.NONE);
        settingsStage.initOwner(primaryStage);
        // TODO fill settingsStage with all options and Confirm/Cancel buttons
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
            this.impls.add(AlgorithmUtils.createAlgorithm(this.core, algorithm, popup1Stage::show, popup2Stage::show));
            actualMenu.getItems().add(item);
            item.setOnAction(event -> {
                if (!item.isSelected()) return;
                paneContainer.setPane(this.impls.get(algorithm.ordinal()).getViewer(primaryStage));
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
        settings.setOnMouseClicked(event -> settingsStage.show());
        top.getItems().add(settings);
        root.getChildren().add(top);
        Scene mainWindow = new Scene(root); // TODO size main window
        primaryStage.setTitle("Burrows Wheeler Transformation - Universal GUI");
        primaryStage.setScene(mainWindow);
        primaryStage.show();
    }

    private int readMaxLengthFromConfig() {
        try {
            InputStream input = new FileInputStream(new File(PROPERTY_FILE));
            this.config.load(input);
            Object maxLength = this.config.get("maxLength");
            if (maxLength instanceof Integer)  return (Integer)maxLength;
            else if (maxLength instanceof String) return Integer.parseInt((String)maxLength);
            else {
                System.err.println("Warning: File \"" + PROPERTY_FILE + "\" is invalid\nProperty \"maxLength\" is no int\nUsing default maximal length 20...");
                return 20;
            }
        } catch (IOException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" cannot be read\nUsing default maximal length 20...");
            return 20;
        } catch (NumberFormatException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" is invalid\nProperty \"maxLength\" is no int\nUsing default maximal length 20...");
            return 20;
        }
    }

    public static void main(String[] args) {
        Application.launch(JavaFXFrontend.class, args);
    }

}
