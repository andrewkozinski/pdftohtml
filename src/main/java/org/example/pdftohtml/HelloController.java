package org.example.pdftohtml;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelloController {

    @FXML
    private WebView htmlView = new WebView();

    private File inputtedFile = null;

    private ArrayList<File> inputtedFiles = null;

    @FXML
    private Button generateButton;

    @FXML
    private MenuItem optionsMenuItem;

    @FXML
    private TextField headingTextField;

    //Used to determine what type of heading the heading text should be if the user chooses to have heading text
    @FXML
    private ToggleGroup headingType;

    //Class where user selected options will be stored
    private Options userOptions;

    @FXML
    private ListView<String> pdfNamesListView;

    private String selectedItem;

    public void initialize() {
        //String userDirectory = Paths.get("").toAbsolutePath().toString();
    }

    /**
     * Generates the header for an HTML file
     * @param fileWriter
     */
    private void generateHead(FileWriter fileWriter) {

        try {
            fileWriter.write("<head>");
            fileWriter.write("<meta charset=\"utf-8\">");
            fileWriter.write("<link rel=\"stylesheet\" href=\"styles.css\">");
            fileWriter.write("<title>OutputTest</title></head>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates the header for an HTML file with a specified styles file
     * @param fileWriter
     * @param styles
     */
    private void generateHead(FileWriter fileWriter, String styles) {

        try {
            fileWriter.write("<head>");
            fileWriter.write("<meta charset=\"utf-8\">");
            fileWriter.write("<link rel=\"stylesheet\" href=\"" + styles + "\">");
            fileWriter.write("<title>OutputTest</title></head>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateHTML() {
        System.out.println("Ran");

        //Create new folder, this is where the HTML files will be generated.
        String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH-mm-ss"));
        System.out.println(curTime);
        //Create the folder where the generated HTML will be stored.

        if(userOptions != null && userOptions.getOutputFolder() != null) {
            curTime = userOptions.getOutputFolder().getAbsolutePath() + "/" + curTime;
        }

        new File(curTime).mkdir();
        //Create a folder where PDF files will be copied to and stored to be embedded in the pdf webpages
        new File(curTime + "/resources").mkdir();
        //Create a folder where the html pages with html embedded will be stored.
        new File(curTime + "/pdfpages").mkdir();

        //Create the HTML index file
        File newFile = new File(curTime + "/index.html");


        //Write to the HTML file
        try {
            FileWriter fileWriter = new FileWriter(newFile);

            fileWriter.write("<!DOCTYPE html>");
            fileWriter.write("<html lang=\"en\">");
            generateHead(fileWriter);
            fileWriter.write("<body>");

            fileWriter.write("<p>");
            generateHeadingText(fileWriter);
            //generateEmbededPDF(fileWriter);
            handleGeneratePDFPage(fileWriter, curTime);
            fileWriter.write("</p></body>");
            fileWriter.write("</html>");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Now, create the CSS file

        File css = new File(curTime + "/styles.css");

        try {
            FileWriter cssWriter = new FileWriter(css);
            cssWriter.write("html, body { height:100%;}");

            generateStylings(cssWriter);

            cssWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //generateButton.setDisable(true);

    }

    /**
     * Generates an HTML file with a PDF embedded into it
     * @param fileWriter
     * @param dir
     */
    public void handleGeneratePDFPage(FileWriter fileWriter, String dir) {

        //Generate an HTML file for each PDF with the PDF embedded into the site
        for (File file : inputtedFiles) {

            //Create new HTML file for the PDF

            File newFile = new File(dir + "/pdfpages/" + file.getName() + ".html");

            //Create a copy of the PDF and put it in the resources folder :)
            File copy = new File(dir + "/resources/" + file.getName());
            try {
                Files.copy(file.toPath(), copy.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //fileWriter for the new file
            try {
                FileWriter pdfWriter = new FileWriter(newFile);

                pdfWriter.write("<!DOCTYPE html>");
                pdfWriter.write("<html lang=\"en\">");
                generateHead(pdfWriter, "../styles.css");
                pdfWriter.write("<body>");
                generateEmbeddedPDF(pdfWriter, copy, dir);
                pdfWriter.write("</body></html>");
                pdfWriter.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Now, on the main index.html page, we need to reference this new html file
            try {
                //fileWriter.write("<p><a href=\"" + newFile.getAbsolutePath() + "\">" + newFile.getName() + "</a></p>");
                fileWriter.write("<p><a href=\"pdfpages/" + newFile.getName() + "\">" + newFile.getName() + "</a></p>");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }




        //Create new file for the PDF
        //File newFile = new File("");

    }

    /**
     * When called, writes HTML to a file that embeds a PDF into the webpage
     * Referenced this page in figuring out how to embed a PDF file in HTML: https://pspdfkit.com/blog/2018/open-pdf-in-your-web-app/
     * @param fileWriter
     */
    private void generateEmbeddedPDF(FileWriter fileWriter, File pdf, String dir) {

        //Don't actually need to do this here, do in main pdf generation method
        //First copy the specified PDF to the resources folder
        //File copied = new File(dir + "/resources/" + pdf.getName());


        //Writes an HTML object to display a PDF, has an error message if a browser does not support pdfs.
        try {
            fileWriter.write("<object\n" + "\tdata=\"../resources/" + pdf.getName() + "\"\n");

            fileWriter.write("\ttype=\"application/pdf\"\n" +
                    "\twidth=\"100%\"\n" +
                    "\theight=\"100%\"\n" +
                    "\tclass=\"pdf\"\n" +
                    ">\n" +
                    "<iframe\n" +
                    "\t\tsrc=\"../resources/" + pdf.getName() + "\"\n" +
                    "\t\twidth=\"100%\"\n" +
                    "\t\theight=\"100%\"\n" +
                    "\t\tstyle=\"border: none;\"\n" +
                    "\t>" +
                    "\t<p>\n" +
                    "\t\tYour browser does not support PDFs.\n" +
                    //"\t\t<a href=\"https://example.com/test.pdf\">Download the PDF</a>\n" +
                    //"\t\t.\n" +
                    "\t</p> </iframe>\n" +
                    "</object>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleLoadPDFButton() {
        FileChooser fileChooser = new FileChooser();
        File current = null;
        try {
            current = new File(new File(".").getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(current);
        //File selectedFile = fileChooser.showOpenDialog(null);

        List<File> input = fileChooser.showOpenMultipleDialog(null);

        ObservableList<String> observableList = pdfNamesListView.getItems();

        if(input != null) {
            inputtedFiles = new ArrayList<>(input);
            generateButton.setDisable(false);
            loadListView();
        }


        /*
        if(selectedFile != null) {
            inputtedFile = selectedFile;
            generateButton.setDisable(false);
        }

         */

    }


    /**
     * Lets user select an HTML file for the WebViewer to use
     * Unfortunately, with the way the PDF files are embedded, the WebViewer cannot display them
     */
    public void getHTML() {
        FileChooser fileChooser = new FileChooser();
        File current = null;
        try {
            current = new File(new File(".").getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(current);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("Successfully selected file: " + selectedFile.getAbsolutePath());

            WebEngine webEngine = htmlView.getEngine();
            webEngine.load(selectedFile.toURI().toString());

        }


    }

    public void handleCloseMenuItem() {
        Platform.exit();
    }

    /**
     * When called, open the options menu window. Any options the user changed will be saved as well.
     */
    public void handleOptionsMenuItem() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));
            Parent root = loader.load();
            //Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("options.fxml")));
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Options");

            Scene scene = new Scene(root);

            //When escape key is pressed, close window
             scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                 @Override
                 public void handle(KeyEvent event) {
                     if (Objects.requireNonNull(event.getCode()) == KeyCode.ESCAPE) {
                         primaryStage.close();
                     }
                 }
             });

             primaryStage.setScene(scene);
            //primaryStage.setScene(new Scene(root));
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(optionsMenuItem.getParentPopup().getOwnerWindow());
            primaryStage.resizableProperty().setValue(false);

            OptionsController optionsController = loader.getController();

            if(userOptions != null) {
                optionsController.setUserOptions(userOptions);
            }

            primaryStage.showAndWait();

            if(!(userOptions != null && optionsController.userOptions == null)) {
                userOptions = optionsController.userOptions;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles clicking an item in the listview
     * @param event
     */
    public void handleListViewItemsMouseClick(MouseEvent event) {
        ObservableList<String> observableList = pdfNamesListView.getItems();
        int selectedIndex = pdfNamesListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < observableList.size()) {
            selectedItem = observableList.get(selectedIndex);
        }
    }

    /**
     * When called will remove the currently selected item
     */
    public void handleRemoveItem() {
        if(selectedItem != null) {
            ObservableList<String> observableList = pdfNamesListView.getItems();
            pdfNamesListView.getItems().remove(selectedItem);
            inputtedFiles.removeIf(file -> file.getName().equals(selectedItem));
            loadListView();

            //If no files left, disable generation
            if(inputtedFiles.isEmpty()) {
                generateButton.setDisable(true);
            }

        }

    }

    /**
     * When called displays all items in inputtedFiles into the listview
     */
    public void loadListView() {
        ObservableList<String> observableList = pdfNamesListView.getItems();
        observableList.clear();
        //Add each file name to the list view
        for(File file : inputtedFiles) {
            observableList.add(file.getName());
        }
    }

    /**
     * When called writes a HTML header
     */
    private void generateHeadingText(FileWriter fileWriter) {

        if(headingTextField.getText() != null) {

            RadioButton selected = (RadioButton) headingType.getSelectedToggle();
            //System.out.println(selected.getText());

            switch (selected.getText()) {
                case "h1":
                    try {
                        fileWriter.write("<h1>" + headingTextField.getText() + "</h1>");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "h2":
                    try {
                        fileWriter.write("<h2>" + headingTextField.getText() + "</h2>");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "h3":
                    try {
                        fileWriter.write("<h3>" + headingTextField.getText() + "</h3>");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "h4":
                    try {
                        fileWriter.write("<h4>" + headingTextField.getText() + "</h4>");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "h5":
                    try {
                        fileWriter.write("<h5>" + headingTextField.getText() + "</h5>");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "h6":
                    try {
                        fileWriter.write("<h6>" + headingTextField.getText() + "</h6>");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }

        }

    }

    /**
     * Method which writes CSS depending on user selected preferences
     * @param cssWriter
     */
    private void generateStylings(FileWriter cssWriter) {

        if(userOptions != null) {

            try {
                cssWriter.write("h1 {text-align: " + userOptions.gethPosition() + ";");

                if(userOptions.gethFontFamily() != null) {
                    cssWriter.write("font-family: " + userOptions.gethFontFamily() + ";");
                }

                if(userOptions.gethFontSize() != null && !userOptions.gethFontSize().isEmpty()) {
                    cssWriter.write("font-size: " + userOptions.gethFontSize() + "px;");
                }

                cssWriter.write("} p {text-align: " + userOptions.getpPosition() + ";");

                if(userOptions.getpFontFamily() != null) {
                    cssWriter.write("font-family: " + userOptions.getpFontFamily() + ";");
                }

                if(userOptions.getpFontSize() != null && !userOptions.getpFontSize().isEmpty()) {
                    cssWriter.write("font-size: " + userOptions.getpFontSize() + "px;");
                }

                cssWriter.write("}");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }


    //Notes to self: not working for some reason with extra file creation, figure out please
    public void test() {
        /*
        String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH-mm-ss"));
        System.out.println(curTime);
        new File(curTime).mkdir();
        new File(curTime + "/resources").mkdir();

        File newFile = new File("resume.pdf");

        File copied = new File(curTime + "/resources" + "/test.pdf");
        try {
            //FileWriter test = new FileWriter(copied);
            //test.write("Hello world!");
            //test.close();
            Files.copy(newFile.toPath(), copied.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */

        //Radio button code
        /*
        RadioButton test = (RadioButton) headingType.getSelectedToggle();
        System.out.println(fontSelectionMenuButton.getText());

        switch (test.getText()) {
            case "h1":
                System.out.println("Is h1");
                break;
            case "h2":
                System.out.println("Is h2");
                break;
            case "h3":
                System.out.println("is h3");
                break;
            case "h4":
                System.out.println("Is h4");
                break;
            case "h5":
                System.out.println("Is h5");
                break;
            case "h6":
                System.out.println("Is h6");
                break;
        }

         */

    }

}