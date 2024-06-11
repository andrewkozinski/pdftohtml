package org.example.pdftohtml;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class OptionsController {

    @FXML
    private TextField headingTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private ToggleGroup headingType;

    //Object where user selected options will be stored.
    public Options userOptions;

    @FXML
    private TextField outputTextField;

    private File output;

    @FXML
    private ChoiceBox<String> fontsChoiceBox;

    @FXML
    private ChoiceBox<String> paraFontsChoiceBox;

    @FXML
    private TextField fontSizeTextField;

    private File inputtedOutputFolder;

    private String hSelectedFont;

    private String hSelectedFontSize;

    @FXML
    private ToggleGroup hPosition;

    @FXML
    private ToggleGroup pPosition;

    @FXML
    private RadioButton hLeft;

    @FXML
    private RadioButton hCenter;

    @FXML
    private RadioButton hRight;

    @FXML
    private RadioButton pLeft;

    @FXML
    private RadioButton pCenter;

    @FXML
    private RadioButton pRight;

    @FXML
    private TextField pSelectedFontSize;

    private String pFontFamily;

    private String pFontSize;

    public void setUserOptions(Options inputUserOptions) {
        inputtedOutputFolder = inputUserOptions.getOutputFolder();
        if(inputtedOutputFolder != null) {
            outputTextField.setText(inputtedOutputFolder.getAbsolutePath());
        }

        if(inputUserOptions.gethFontFamily() != null && !inputUserOptions.gethFontFamily().isEmpty()) {
            hSelectedFont = inputUserOptions.gethFontFamily();
            fontsChoiceBox.setValue(inputUserOptions.gethFontFamily());
        }

        if(inputUserOptions.gethFontSize() != null && !inputUserOptions.gethFontSize().isEmpty()) {
            hSelectedFontSize = inputUserOptions.gethFontSize();
            fontSizeTextField.setText(inputUserOptions.gethFontSize());
        }

        switch(inputUserOptions.gethPosition()) {
            case("left"):
                hLeft.setSelected(true);
                break;
            case("center"):
                hCenter.setSelected(true);
                break;
            case("right"):
                hRight.setSelected(true);
                break;
        }

        switch(inputUserOptions.getpPosition()) {
            case("left"):
                pLeft.setSelected(true);
                break;
            case("center"):
                pCenter.setSelected(true);
                break;
            case("right"):
                pRight.setSelected(true);
                break;
        }

        //File outputFolder, String hFontSize, String hFontFamily, String hPosition, String pFontSize, String pFontFamily, String pPosition
        if(inputUserOptions.getpFontSize() != null && !inputUserOptions.getpFontSize().isEmpty()) {
            pFontSize = inputUserOptions.getpFontSize();
            pSelectedFontSize.setText(inputUserOptions.getpFontSize());
        }

        if(inputUserOptions.getpFontFamily() != null && !inputUserOptions.getpFontFamily().isEmpty()) {
            pFontFamily = inputUserOptions.getpFontFamily();
            paraFontsChoiceBox.setValue(inputUserOptions.getpFontFamily());
        }

    }

    public void initialize() {

        if(inputtedOutputFolder == null) {
            //Sets output file textFields text to be where the new folder will be output by default
            String userDirectory = Paths.get("").toAbsolutePath().toString();
            outputTextField.setText(userDirectory);
        }

        ObservableList<String> hFonts = fontsChoiceBox.getItems();
        ObservableList<String> pFonts = paraFontsChoiceBox.getItems();

        //Array for list of fonts
        String fontList[] = {"Arial, sans-serif", "Verdana, sans-serif", "Tahoma, sans-serif", "Trebuchet MS, sans-serif", "Times New Roman, serif",
                                "Georgia, serif", "Garamond, serif", "Courier New, monospace", "Brush Script MT, cursive", "Andrews Handwriting Regular", "Nicole Handwriting Regular"};

        hFonts.addAll(fontList);
        pFonts.addAll(fontList);

    }

    /**
     * When called closes the options menu window
     */
    public void closeOptions() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * When called saves the options the user inputted
     */
    public void handleSaveOptions() {

        String currentFont = fontsChoiceBox.getSelectionModel().getSelectedItem();
        if(currentFont != null) {
            hSelectedFont = currentFont;
        }

        String currentFontSize = fontSizeTextField.getText();
        if(currentFontSize != null) {
            hSelectedFontSize = currentFontSize;
        }

        //Create output file if user specified one
        if(output != null) {
            output.mkdir();
            File outputFolder = new File(output.getAbsolutePath());
            System.out.println(output.getAbsolutePath());
        }

        if(userOptions != null) {
            output = userOptions.getOutputFolder();
        }

        //Gets positioning input from user for heading elements
        RadioButton hPos = (RadioButton) hPosition.getSelectedToggle();
        String headingPosition = hPos.getText().toLowerCase();

        //Sets paragraph font family
        String currentPFontFamily = paraFontsChoiceBox.getSelectionModel().getSelectedItem();
        if(currentPFontFamily != null) {
            pFontFamily = currentPFontFamily;
        }

        //Sets paragraph font size
        String currentPFontSize = pSelectedFontSize.getText();
        if(currentPFontSize != null && !currentPFontSize.isEmpty()) {
            pFontSize = currentPFontSize;
        }

        //Gets positioning input from user for paragraph elements
        RadioButton pPos = (RadioButton) pPosition.getSelectedToggle();
        String paraPosition = pPos.getText().toLowerCase();

        //File outputFolder, String hFontSize, String hFontFamily, String hPosition, String pFontSize, String pFontFamily, String pPosition
        userOptions = new Options(output, hSelectedFontSize, hSelectedFont, headingPosition, pFontSize, pFontFamily, paraPosition);

        closeOptions();
    }

    /**
     * When called shows a save file dialog to the user, will allow user to create a new folder where the outputted files will be stored
     * Note: Will not actually create the folder until save button is pressed
     */
    public void handleSelectOutputButton() {
        FileChooser fileChooser = new FileChooser();

        File current = null;
        try {
            current = new File(new File(".").getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Folder (*.dir)", ".dir");
        //fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(current);
        File currentOutput = fileChooser.showSaveDialog(null);

        if(currentOutput != null) {
            outputTextField.setText(currentOutput.getAbsolutePath());
            output = currentOutput;
        }

    }

}
