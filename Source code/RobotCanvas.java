package consoleVersion1;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.collections.ObservableList;
import java.util.ArrayList;

/**
 * RobotCanvas class represents a canvas for drawing robots and obstacles in the Robot Arena.
 * @author Zichen Liao
 */
public class RobotCanvas extends Canvas {
    private Image predatorImage;
    private Image preyImage;
    private Image obstacleImage;

    /**
     * Constructs a RobotCanvas with specified width and height.
     * 
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    public RobotCanvas(int width, int height) {
        super(width, height);
        predatorImage = new Image("file:C:/Users/Henry/Desktop/progamming/JAVA_DATA/Learning_Java/src/consoleVersion1/lion.png");
        preyImage = new Image("file:C:/Users/Henry/Desktop/progamming/JAVA_DATA/Learning_Java/src/consoleVersion1/deer.png");
        obstacleImage = new Image("file:C:/Users/Henry/Desktop/progamming/JAVA_DATA/Learning_Java/src/consoleVersion1/Tree.png");
    }

    /**
     * Draws the robots on the canvas.
     * 
     * @param robots the list of robots to draw
     */
    public void drawRobots(ObservableList<Robot> robots) {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
        gc.strokeRect(0, 0, this.getWidth(), this.getHeight());
        for (Robot r : robots) {
            if (r instanceof Predator) {
                gc.drawImage(predatorImage, r.getX() * 20, r.getY() * 20, 20, 20);
            } else if (r instanceof Prey) {
                gc.drawImage(preyImage, r.getX() * 20, r.getY() * 20, 20, 20);
            }
        }
    }

    /**
     * Draws the obstacles on the canvas.
     * 
     * @param obstacles the list of obstacles to draw
     */
    public void drawObstacles(ObservableList<Obstacle> obstacles) {
        GraphicsContext gc = this.getGraphicsContext2D();
        for (Obstacle o : obstacles) {
            gc.drawImage(obstacleImage, o.getX() * 20, o.getY() * 20, 20, 20);
        }
    }

    /**
     * Draws whisker robots and their obstacles on the canvas.
     * 
     * @param robots the list of whisker robots to draw
     */
    public void drawWhiskerRobots(ArrayList<WhiskerRobot> robots) {
        GraphicsContext gc = this.getGraphicsContext2D();
        
        // Draw obstacles
        gc.setFill(Color.GREEN);
        for (Rectangle obstacle : WhiskerRobot.getObstacles()) {
            gc.fillRect(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());
        }
        
        // Draw robots
        for (WhiskerRobot robot : robots) {
            robot.draw(gc);
        }
    }
}