package org.example.assignment03;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class RobotController {
    @FXML
    private Label welcomeText;
    @FXML
    private ImageView mazeView;
    @FXML
    private ImageView imgView;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button carButton;
    @FXML
    private Button solveButton;

    boolean carMode;

    //grabs focus to ensure arrow key inputs work
    @FXML
    public void initialize(){
        anchorPane.setFocusTraversable(true);
        Platform.runLater(() -> {
            anchorPane.requestFocus();
        });
        anchorPane.setOnMouseClicked(e -> anchorPane.requestFocus()); // Makes sure the arrow keys and inputs are focused on the maze you click
        carMode = true;
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
        //only applies to the car.png and not robot
        if(carMode) {
            double imgX = imgView.getLayoutX();
            double imgY = imgView.getLayoutY();
            if (imgX > newX) {
                imgView.setRotate(0);
                imgView.setScaleX(-1);
            } else if (imgX < newX) {
                imgView.setRotate(0);
                imgView.setScaleX(1);
            }
            if (imgY > newY) {
                imgView.setRotate(270);
                imgView.setScaleX(1);
            } else if (imgY < newY) {
                imgView.setRotate(270);
                imgView.setScaleX(-1);
            }
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
    //switches between car mode and robot mode (defaults to car mode)
    public void changeMode(){
        carMode = !carMode;
        //sets imgView to car
        if(carMode){
            Image car = new Image(getClass().getResource("/org/example/assignment03/car.png").toExternalForm());
            imgView.setImage(car);
            carButton.setText("Switch to Robot");
            solveButton.setDisable(true);
            solveButton.setOpacity(0.0);

        //sets imgView to robot
        }else{
            Image robot = new Image(getClass().getResource("/org/example/assignment03/robot.png").toExternalForm());
            imgView.setImage(robot);
            carButton.setText("Switch to Car");
            solveButton.setDisable(false);
            solveButton.setOpacity(1.0);
        }
    }

    //auto-solve sequence for robot
    @FXML
    public void moveRobotToExit() {

        // Movement sequence — each step executes 2 seconds after the previous
        int[][] movements = {
                {20, 0},    // 20 units to the right
                {0, -100},   // 20 units up
                {205, 0},   // 205 units to the right
                {0, -50},   // 50 units up
                {55, 0},    // 55 units to the right
                {0, 200},   // 200 units down
                {55, 0},    // 50 units to the right
                {0, -100},  // 110 units up
                {110, 0},   // 110 units to the right
                {0, -100},  // 100 units up
                {55, 0},    // 55 units to the right
                {0, 140},   // 140 units down
                {35, 0},    // 30 units to the right
                {0, -5},   // 100 units down
                //  {30, 0}     // 30 units to the right
        };


        // Animate the robot smoothly through each step using TranslateTransition
        // Duration is proportional to distance so the robot moves at a consistent speed
        SequentialTransition sequence = new SequentialTransition();

        for (int[] movement : movements) {
            int dx = movement[0];
            int dy = movement[1];
            int distance = Math.abs(dx) + Math.abs(dy);

            TranslateTransition tt = new TranslateTransition(Duration.millis(distance * 10), imgView);
            tt.setByX(dx);
            tt.setByY(dy);

            // After each segment finishes, commit the translation into layoutX/Y
            // and reset translateX/Y so the next segment starts cleanly
            tt.setOnFinished(e -> {
                imgView.setLayoutX(imgView.getLayoutX() + imgView.getTranslateX());
                imgView.setLayoutY(imgView.getLayoutY() + imgView.getTranslateY());
                imgView.setTranslateX(0);
                imgView.setTranslateY(0);
            });

            sequence.getChildren().add(tt);
        }

        sequence.play();
    }
}
