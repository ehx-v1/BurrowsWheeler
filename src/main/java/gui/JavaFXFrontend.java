package gui;

import core.BurrowsWheelerTransformationCore;
import util.runtimeframework.DebugQueue;
import util.AlgorithmUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

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
        Stage popup1Stage = new Stage();
        Stage popup2Stage = new Stage();
        Stage settingsStage = new Stage();
        Stage errorStage = new Stage();
        StackPane subroot1 = new StackPane();
        TextField message1 = new TextField();
        message1.setEditable(false);
        message1.setText("Beginning of algorithm reached!");
        Button confirm1 = new Button();
        confirm1.setDefaultButton(true);
        confirm1.setText("OK");
        confirm1.setOnMouseClicked(event -> popup1Stage.hide());
        subroot1.getChildren().addAll(message1, confirm1);
        Scene popup1Scene = new Scene(subroot1); // TODO size subwindow
        popup1Stage.setTitle("Info");
        popup1Stage.setScene(popup1Scene);
        popup1Stage.initStyle(StageStyle.DECORATED);
        popup1Stage.initModality(Modality.NONE);
        popup1Stage.initOwner(primaryStage);
        StackPane subroot2 = new StackPane();
        TextField message2 = new TextField();
        message2.setEditable(false);
        message2.setText("End of algorithm reached!");
        Button confirm2 = new Button();
        confirm2.setDefaultButton(true);
        confirm2.setText("OK");
        confirm2.setOnMouseClicked(event -> popup2Stage.hide());
        subroot2.getChildren().addAll(message2, confirm2);
        Scene popup2Scene = new Scene(subroot2); // TODO size subwindow
        popup2Stage.setTitle("Info");
        popup2Stage.setScene(popup2Scene);
        popup2Stage.initStyle(StageStyle.DECORATED);
        popup2Stage.initModality(Modality.NONE);
        popup2Stage.initOwner(primaryStage);
        StackPane subroot3 = new StackPane();
        TextField errorMessage = new TextField();
        errorMessage.setEditable(false);
        errorMessage.setText("Please enter a number for mximal word length.");
        Button confirm3 = new Button();
        confirm3.setDefaultButton(true);
        confirm3.setText("OK");
        confirm3.setOnMouseClicked(event -> errorStage.hide());
        subroot3.getChildren().addAll(errorMessage, confirm3);
        Scene errorScene = new Scene(subroot3);
        errorStage.setTitle("Error"); // TODO size subwindow
        errorStage.setScene(errorScene);
        errorStage.initStyle(StageStyle.DECORATED);
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.initOwner(primaryStage);
        StackPane subroot4 = new StackPane();
        TextField limitSettingLabel = new TextField();
        limitSettingLabel.setEditable(false);
        limitSettingLabel.setText("Maximal word length:");
        TextField limitSettingInput = new TextField();
        Button confirmSettings = new Button();
        confirmSettings.setDefaultButton(true);
        confirmSettings.setText("OK");
        confirmSettings.setOnMouseClicked(event -> {
            /*
            workaround for replacing
            if (!Integer.isInt(limitSettingInput.getText())) {
                errorStage.showAndWait();
                return;
            }
            since Integer.isInt(String) does not exist
             */
            try {
                Integer.parseInt(limitSettingInput.getText());
            } catch (NumberFormatException e) {
                errorStage.showAndWait();
                return;
            }
            try (OutputStream stream = this.initFileAndMakeStream()){
                this.config.setProperty("maxLength", limitSettingInput.getText());
                this.config.store(stream, null);
            } catch (IOException e) {
                System.err.println("Warning: File \"" + PROPERTY_FILE + "\" cannot be written");
            }
        });
        Button cancelSettings = new Button();
        cancelSettings.setCancelButton(true);
        cancelSettings.setText("Cancel");

        subroot4.getChildren().addAll(limitSettingLabel, limitSettingInput, confirmSettings);
        Scene settingsScene = new Scene(subroot4); // TODO size subwindow
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
        try (InputStream input = new FileInputStream(new File(PROPERTY_FILE))) {
            this.config.load(input);
            String maxLength = this.config.getProperty("maxLength");
            return Integer.parseInt(maxLength);
        } catch (IOException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" cannot be read\nUsing default maximal length 20...");
            return 20;
        } catch (NumberFormatException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" is invalid\nProperty \"maxLength\" is no int\nUsing default maximal length 20...");
            return 20;
        }
    }

    private OutputStream initFileAndMakeStream() throws IOException {
            File propertyFile = new File(PROPERTY_FILE);
            if (!propertyFile.exists()) {
                propertyFile.createNewFile();
            }
            return new FileOutputStream(propertyFile);
    }

    public static void main(String[] args) {
        Application.launch(JavaFXFrontend.class, args);
    }

}
