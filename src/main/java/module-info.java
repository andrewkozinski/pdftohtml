module org.example.pdftohtml {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;


    opens org.example.pdftohtml to javafx.fxml;
    exports org.example.pdftohtml;
}