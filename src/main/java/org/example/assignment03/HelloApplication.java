package org.example.assignment03;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("Maze2.fxml"));
        TabPane tPane  = new TabPane();
       Tab t1 = new Tab("Maze 1", fxmlLoader.load());
       Tab t2 = new Tab("Maze 2",fxmlLoader2.load());
       tPane.getTabs().addAll(t1,t2);

        Scene scene = new Scene(tPane, 575, 479);
        stage.setTitle("Maze!");
        stage.setScene(scene);
        stage.show();
    }




    public static void main(String[] args) {
        launch();
    }
}