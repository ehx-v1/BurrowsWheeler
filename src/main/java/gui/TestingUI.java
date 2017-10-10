package gui;

import core.BurrowsWheelerTransformationCore;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import util.ThisShouldNotHappenException;
import util.AlgorithmUtils;

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
import javafx.scene.layout.StackPane;

/**
 * Created by root on 14.04.2017.
 */
public class TestingUI extends Application {

    private enum Message {
        HEAD_MAIN,
        HEAD_INFO,
        HEAD_ERROR,
        INFO_BEGIN_REACHED,
        INFO_END_REACHED,
        INFO_LIMIT_NO_NUMBER,
        INFO_APPLY_ON_RESTART,
        TEXTLABEL_MAXLENGTH,
        TEXTLABEL_LOCALE,
        TEXTLABEL_GERMAN,
        TEXTLABEL_ENGLISH,
        BUTTONLABEL_CONFIRM,
        BUTTONLABEL_CANCEL,
        TOOLTIP_FORWARD,
        TOOLTIP_BACK,
        MISC_ALGORITHMS,
        MISC_SETTINGS;

        private String getCaption(AlgorithmUtils.Locale locale) {
            switch (locale) {
                case DE:
                    return getCaptionDE();
                case EN:
                    return getCaptionEN();
                default:
                    throw new ThisShouldNotHappenException("Enum value out of enum");
            }
        }

        private String getCaptionDE() {
            switch (this) {
                case HEAD_MAIN:
                    return "Allgemeine Präsentations-GUI für die Burrows-Wheeler-Transformation";
                case HEAD_INFO:
                    return "Info";
                case HEAD_ERROR:
                    return "Fehler";
                case INFO_BEGIN_REACHED:
                    return "Anfang des Algorithmus erreicht!";
                case INFO_END_REACHED:
                    return "Ende des Algorithmus erreicht!";
                case INFO_LIMIT_NO_NUMBER:
                    return "Bitte für die maximale Wortlänge eine Zahl eingeben.";
                case INFO_APPLY_ON_RESTART:
                    return "Das Ändern der maximalen Wortlänge wird bei Neustart übernommen.";
                case TEXTLABEL_MAXLENGTH:
                    return "Maximale Wortlänge:";
                case TEXTLABEL_LOCALE:
                    return "Sprache:";
                case TEXTLABEL_GERMAN:
                    return "Deutsch";
                case TEXTLABEL_ENGLISH:
                    return "Englisch/English";
                case BUTTONLABEL_CONFIRM:
                    return "OK";
                case BUTTONLABEL_CANCEL:
                    return "Abbrechen";
                case TOOLTIP_FORWARD:
                    return "Nächster Schritt";
                case TOOLTIP_BACK:
                    return "Vorheriger Schritt";
                case MISC_ALGORITHMS:
                    return "Algorithmen";
                case MISC_SETTINGS:
                    return "Einstellungen";
                default:
                    throw new ThisShouldNotHappenException("Enum value out of enum");
            }
        }

        private String getCaptionEN() {
            switch (this) {
                case HEAD_MAIN:
                    return "Burrows Wheeler Transformation - Universal Showcase GUI";
                case HEAD_INFO:
                    return "Info";
                case HEAD_ERROR:
                    return "Error";
                case INFO_BEGIN_REACHED:
                    return "Beginning of algorithm reached!";
                case INFO_END_REACHED:
                    return "End of algorithm reached!";
                case INFO_LIMIT_NO_NUMBER:
                    return "Please enter a number for maximal word length.";
                case INFO_APPLY_ON_RESTART:
                    return "Changes to the maximal word length will apply on restart.";
                case TEXTLABEL_MAXLENGTH:
                    return "Maximal word length:";
                case TEXTLABEL_LOCALE:
                    return "Language:";
                case TEXTLABEL_GERMAN:
                    return "German/Deutsch";
                case TEXTLABEL_ENGLISH:
                    return "English";
                case BUTTONLABEL_CONFIRM:
                    return "OK";
                case BUTTONLABEL_CANCEL:
                    return "Cancel";
                case TOOLTIP_FORWARD:
                    return "Step forward";
                case TOOLTIP_BACK:
                    return "Step back";
                case MISC_ALGORITHMS:
                    return "Algorithms";
                case MISC_SETTINGS:
                    return "Settings";
                default:
                    throw new ThisShouldNotHappenException("Enum value out of enum");
            }
        }

    }

    private Properties config;
    public final static String PROPERTY_FILE = "config.properties";
    public final static String MAXLENGTH_PROPERTY = "maxLength";
    public final static String LOCALE_PROPERTY = "locale";

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.config = new Properties();
        AlgorithmUtils.Locale locale = this.readLocaleFromConfig();
        BorderPane root = new BorderPane();
        Stage popup1Stage = new Stage();
        Stage popup2Stage = new Stage();
        Stage settingsStage = new Stage();
        Stage errorStage = new Stage();
        StackPane subroot1 = new StackPane(); // TODO replace with appropriate layout element
        TextField message1 = new TextField();
        message1.setEditable(false);
        message1.setText(Message.INFO_BEGIN_REACHED.getCaption(locale));
        Button confirm1 = new Button();
        confirm1.setDefaultButton(true);
        confirm1.setText(Message.BUTTONLABEL_CONFIRM.getCaption(locale));
        confirm1.setOnMouseClicked(event -> popup1Stage.hide());
        subroot1.getChildren().addAll(message1, confirm1);
        Scene popup1Scene = new Scene(subroot1);
        popup1Stage.setTitle(Message.HEAD_INFO.getCaption(locale));
        popup1Stage.setScene(popup1Scene);
        popup1Stage.initStyle(StageStyle.DECORATED);
        popup1Stage.initModality(Modality.NONE);
        popup1Stage.initOwner(primaryStage);
        StackPane subroot2 = new StackPane(); // TODO replace with appropriate layout element
        TextField message2 = new TextField();
        message2.setEditable(false);
        message2.setText(Message.INFO_END_REACHED.getCaption(locale));
        Button confirm2 = new Button();
        confirm2.setDefaultButton(true);
        confirm2.setText(Message.BUTTONLABEL_CONFIRM.getCaption(locale));
        confirm2.setOnMouseClicked(event -> popup2Stage.hide());
        subroot2.getChildren().addAll(message2, confirm2);
        Scene popup2Scene = new Scene(subroot2);
        popup2Stage.setTitle(Message.HEAD_INFO.getCaption(locale));
        popup2Stage.setScene(popup2Scene);
        popup2Stage.initStyle(StageStyle.DECORATED);
        popup2Stage.initModality(Modality.NONE);
        popup2Stage.initOwner(primaryStage);
        StackPane subroot3 = new StackPane(); // TODO replace with appropriate layout element
        TextField errorMessage = new TextField();
        errorMessage.setEditable(false);
        errorMessage.setText(Message.INFO_LIMIT_NO_NUMBER.getCaption(locale));
        Button confirm3 = new Button();
        confirm3.setDefaultButton(true);
        confirm3.setText(Message.BUTTONLABEL_CONFIRM.getCaption(locale));
        confirm3.setOnMouseClicked(event -> errorStage.hide());
        subroot3.getChildren().addAll(errorMessage, confirm3);
        Scene errorScene = new Scene(subroot3);
        errorStage.setTitle(Message.HEAD_ERROR.getCaption(locale));
        errorStage.setScene(errorScene);
        errorStage.initStyle(StageStyle.DECORATED);
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.initOwner(primaryStage);
        GridPane subroot4 = new GridPane();
        TextField limitSettingLabel = new TextField();
        limitSettingLabel.setEditable(false);
        limitSettingLabel.setText(Message.TEXTLABEL_MAXLENGTH.getCaption(locale));
        TextField limitSettingInput = new TextField();
        limitSettingInput.setText(this.readMaxLengthFromConfig() + "");
        GridPane.setColumnIndex(limitSettingInput, 1);
        TextField localeSettingLabel = new TextField();
        localeSettingLabel.setText(Message.TEXTLABEL_LOCALE.getCaption(locale));
        GridPane.setRowIndex(localeSettingLabel, 1);
        ToggleGroup localeGroup = new ToggleGroup();
        RadioButton de = new RadioButton();
        de.setSelected(locale == AlgorithmUtils.Locale.DE);
        de.setText(Message.TEXTLABEL_GERMAN.getCaption(locale));
        de.setToggleGroup(localeGroup);
        RadioButton en = new RadioButton();
        en.setSelected(locale == AlgorithmUtils.Locale.EN);
        en.setText(Message.TEXTLABEL_ENGLISH.getCaption(locale));
        en.setToggleGroup(localeGroup);
        HBox radioButtonFrame = new HBox();
        radioButtonFrame.getChildren().addAll(de, en);
        GridPane.setRowIndex(radioButtonFrame, 1);
        GridPane.setColumnIndex(radioButtonFrame, 1);
        Button confirmSettings = new Button();
        confirmSettings.setDefaultButton(true);
        confirmSettings.setText(Message.BUTTONLABEL_CONFIRM.getCaption(locale));
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
                this.config.setProperty(MAXLENGTH_PROPERTY, limitSettingInput.getText());
                this.config.setProperty(LOCALE_PROPERTY, de.isSelected() ? AlgorithmUtils.Locale.DE.name() : AlgorithmUtils.Locale.EN.name());
                this.config.store(stream, null);
            } catch (IOException e) {
                System.err.println("Warning: File \"" + PROPERTY_FILE + "\" cannot be written");
            }
            // TODO read config and apply changes
            settingsStage.hide();
        });
        GridPane.setRowIndex(confirmSettings, 2);
        Button cancelSettings = new Button();
        cancelSettings.setCancelButton(true);
        cancelSettings.setText(Message.BUTTONLABEL_CANCEL.getCaption(locale));
        cancelSettings.setOnMouseClicked(event -> {
            limitSettingInput.setText(this.readMaxLengthFromConfig() + "");
            settingsStage.hide();
        });
        GridPane.setRowIndex(cancelSettings, 2);
        GridPane.setColumnIndex(cancelSettings, 1);
        subroot4.getChildren().addAll(limitSettingLabel, limitSettingInput, radioButtonFrame, confirmSettings, cancelSettings);
        Scene settingsScene = new Scene(subroot4);
        settingsStage.setTitle(Message.MISC_SETTINGS.getCaption(locale));
        settingsStage.setScene(settingsScene);
        settingsStage.initStyle(StageStyle.DECORATED);
        settingsStage.initModality(Modality.NONE);
        settingsStage.initOwner(primaryStage);
        ToolBar top = new ToolBar();
        MenuBar menu = new MenuBar();
        Menu actualMenu = new Menu();
        actualMenu.setText(Message.MISC_ALGORITHMS.getCaption(locale));
        menu.getMenus().add(actualMenu);
        StackPane viewerPack = new StackPane();
        for (BurrowsWheelerTransformationCore.Algorithms algorithm : BurrowsWheelerTransformationCore.Algorithms.values()) {
            MenuItem item = new MenuItem(AlgorithmUtils.algorithmCaption(algorithm, locale));
            actualMenu.getItems().add(item);
            TextField dummyViewable = new TextField();
            dummyViewable.setText(AlgorithmUtils.algorithmCaptionDE(algorithm));
            dummyViewable.setEditable(false);
            dummyViewable.setVisible(false);
            viewerPack.getChildren().add(dummyViewable);
            item.setOnAction(event -> {
                for (Node viewerPane : viewerPack.getChildren()) {
                    viewerPane.setVisible(viewerPane == dummyViewable);
                }
                viewerPack.layout();
            });
        }
        root.setCenter(viewerPack);
        top.getItems().add(menu);
        Button back = new Button();
        back.setTooltip(new Tooltip(Message.TOOLTIP_BACK.getCaption(locale)));
        back.setGraphic(new ImageView(new Image("assets/back.png")));
        back.setOnMouseClicked(event -> System.out.println("Would step back"));
        top.getItems().add(back);
        Button forward = new Button();
        forward.setTooltip(new Tooltip(Message.TOOLTIP_FORWARD.getCaption(locale)));
        forward.setGraphic(new ImageView(new Image("assets/forward.png")));
        forward.setOnMouseClicked(event -> System.out.println("Would step forward"));
        top.getItems().add(forward);
        Button settings = new Button();
        settings.setTooltip(new Tooltip(Message.MISC_SETTINGS.getCaption(locale)));
        settings.setGraphic(new ImageView(new Image("assets/settings.png")));
        settings.setOnMouseClicked(event -> settingsStage.show());
        top.getItems().add(settings);
        root.setTop(top);
        Scene mainWindow = new Scene(root, Math.max(top.getWidth(), viewerPack.getLayoutX()), top.getHeight() + viewerPack.getLayoutY());
        primaryStage.setTitle(Message.HEAD_MAIN.getCaption(locale));
        primaryStage.setScene(mainWindow);
        primaryStage.show();
    }

    private int readMaxLengthFromConfig() {
        try (InputStream input = new FileInputStream(new File(PROPERTY_FILE))) {
            this.config.load(input);
            String maxLength = this.config.getProperty(MAXLENGTH_PROPERTY);
            return Integer.parseInt(maxLength);
        } catch (IOException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" cannot be read\nUsing default maximal length 20...");
            return 20;
        } catch (NumberFormatException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" is invalid\nProperty \"" + MAXLENGTH_PROPERTY + "\" is no int\nUsing default maximal length 20...");
            return 20;
        }
    }

    private AlgorithmUtils.Locale readLocaleFromConfig() {
        try (InputStream input = new FileInputStream(new File(PROPERTY_FILE))) {
            this.config.load(input);
            String locale = this.config.getProperty(LOCALE_PROPERTY);
            return AlgorithmUtils.Locale.valueOf(locale);
        } catch (IOException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" cannot be read\nUsing default German locale...");
            return AlgorithmUtils.Locale.DE;
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: File \"" + PROPERTY_FILE + "\" is invalid\nProperty \"" + LOCALE_PROPERTY + "\" is no supported locale code\nUsing default German locale...");
            return AlgorithmUtils.Locale.DE;
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
        Application.launch(TestingUI.class, args);
    }

}


