package org.example.assignment03;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class RobotController {
    @FXML
    private Label welcomeText;
    @FXML
    private ImageView mazeView;
    @FXML
    private ImageView imgView;
    @FXML
    private AnchorPane anchorPane;

    //grabs focus to ensure arrow key inputs work
    @FXML
    public void initialize(){
        anchorPane.setFocusTraversable(true);
        Platform.runLater(() -> {
            anchorPane.requestFocus();
        });
        anchorPane.setOnMouseClicked(e -> anchorPane.requestFocus()); // Makes sure the arrow keys and inputs are focused on the maze you click

    }

    //listener for arrow keys, connected to the anchor pane of the scene itself for best detection
    @FXML
    public void handleKey(KeyEvent event) {
        switch(event.getCode()){
            case UP -> move(0,-5);
            case DOWN -> move(0,5);
            case LEFT -> move(-5,0);
            case RIGHT -> move(5,0);
        }
        event.consume(); // Stops the tabs from switching when you press the arrow keys


    }

    //calculates new position of robot and checks for wall, if no wall then the robot moves to the new position
    @FXML
    private void move(int dx, int dy) {
        //potential new location of robot, before wall check
        double newX = imgView.getLayoutX() + dx;
        double newY = imgView.getLayoutY() + dy;

        //rotating the img based on the direction it faces
        double imgX = imgView.getLayoutX();
        double imgY = imgView.getLayoutY();
        if (imgX>newX){
            imgView.setRotate(0);
            imgView.setScaleX(-1);
        }
        else if (imgX<newX) {
            imgView.setRotate(0);
            imgView.setScaleX(1);
        }
        if (imgY>newY){
            imgView.setRotate(270);
            imgView.setScaleX(1);
        }
        else if (imgY<newY) {
            imgView.setRotate(270);
            imgView.setScaleX(-1);
        }


        //Compute robot center in SCENE coordinates
        double centerX = newX + imgView.getFitWidth() / 2;
        double centerY = newY + imgView.getFitHeight() / 2;

        //Convert scene coords to mazeView local coords
        var localPoint = mazeView.parentToLocal(centerX, centerY);

        double lx = localPoint.getX();
        double ly = localPoint.getY();

        //Convert mazeView local coords to image pixel coords
        double displayedWidth  = mazeView.getBoundsInParent().getWidth();
        double displayedHeight = mazeView.getBoundsInParent().getHeight();

        double scaleX = mazeView.getImage().getWidth()  / displayedWidth;
        double scaleY = mazeView.getImage().getHeight() / displayedHeight;

        int px = (int) Math.round(lx * scaleX);
        int py = (int) Math.round(ly * scaleY);

        //bounds check
        if (px < 0 || py < 0 ||
                px >= mazeView.getImage().getWidth() ||
                py >= mazeView.getImage().getHeight()) {
            System.out.println("Out of bounds → treat as wall");
            return;
        }

        //Read pixel
        Color c = mazeView.getImage().getPixelReader().getColor(px, py);

        //Collision check

        if (!isBlue(c)) {

            imgView.setLayoutX(newX);
            imgView.setLayoutY(newY);
            System.out.println("Moved!");
        } else {
            System.out.println("Wall!");
        }
    }

    //checks color of wall
    private boolean isBlue(Color c) {
        return c.getBlue() > 0.4 && c.getRed() < 0.5;
    }
}
