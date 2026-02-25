module org.example.assignment03 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.assignment03 to javafx.fxml;
    exports org.example.assignment03;
}