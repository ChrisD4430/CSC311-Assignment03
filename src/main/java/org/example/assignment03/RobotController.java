package org.example.assignment03;

import javafx.application.Platform;
import javafx.animation.TranslateTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class RobotController {
    @FXML
    private Label welcomeText;
    @FXML
    private ImageView mazeView;
    @FXML
    private ImageView robotView;
    @FXML
    private AnchorPane anchorPane;

    /* 

    //grabs focus to ensure arrow key inputs work
    @FXML
    public void initialize(){
        anchorPane.setFocusTraversable(true);
        Platform.runLater(() -> {
            anchorPane.requestFocus();
        });
    }

    */

    @FXML
    public void initialize() {
        anchorPane.setFocusTraversable(true);
        Platform.runLater(() -> {
            anchorPane.requestFocus();
            moveRobotToExit(); // Start the movement sequence automatically
        });
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
    }

    //calculates new position of robot and checks for wall, if no wall then the robot moves to the new position
    @FXML
    private void move(int dx, int dy) {
        //potential new location of robot, before wall check
        double newX = robotView.getLayoutX() + dx;
        double newY = robotView.getLayoutY() + dy;

        //Compute robot center in SCENE coordinates
        double centerX = newX + robotView.getFitWidth() / 2;
        double centerY = newY + robotView.getFitHeight() / 2;

        //Convert scene coords to mazeView local coords
        var localPoint = mazeView.sceneToLocal(centerX, centerY);

        double lx = localPoint.getX();
        double ly = localPoint.getY();

        //Convert mazeView local coords to image pixel coords
        double scaleX = mazeView.getImage().getWidth()  / mazeView.getBoundsInLocal().getWidth();
        double scaleY = mazeView.getImage().getHeight() / mazeView.getBoundsInLocal().getHeight();

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
            robotView.setLayoutX(newX);
            robotView.setLayoutY(newY);
            System.out.println("Moved!");
        } else {
            System.out.println("Wall!");
        }
    }

    //checks color of wall
    private boolean isBlue(Color c) {
        return c.getBlue() > 0.4 && c.getRed() < 0.5;
    }

    @FXML
    public void moveRobotToExit() {

        /* // Old direct move calls (no delay between steps — commented out)
            move(20,0);
            move(0,-20);
            move(205,0);
            move(0,-50);
            move(55,0);
            move(0,200);
            move(50,0);
            move(0,-110);
            move(110,0);
            move(0,-100);
            move(55,0);
            move(0,140);
            move(30,0);
            move(0,100);
            move(30,0);
        */

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

            TranslateTransition tt = new TranslateTransition(Duration.millis(distance * 10), robotView);
            tt.setByX(dx);
            tt.setByY(dy);

            // After each segment finishes, commit the translation into layoutX/Y
            // and reset translateX/Y so the next segment starts cleanly
            tt.setOnFinished(e -> {
                robotView.setLayoutX(robotView.getLayoutX() + robotView.getTranslateX());
                robotView.setLayoutY(robotView.getLayoutY() + robotView.getTranslateY());
                robotView.setTranslateX(0);
                robotView.setTranslateY(0);
            });

            sequence.getChildren().add(tt);
        }

        sequence.play();
    }
}