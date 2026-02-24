module org.example.assignment03 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.assignment03 to javafx.fxml;
    exports org.example.assignment03;
}