package consoleVersion1;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * WhiskerRobot class represents a robot that can navigate an arena, detect treasures and bombs, and handle collisions.
 * Author: Zichen Liao
 */
public class WhiskerRobot {
    protected double x, y, radius, whiskerLength, angle;
    protected static ArrayList<WhiskerRobot> robots = new ArrayList<>();
    public boolean isMoving = false;
    protected static ArrayList<Rectangle> obstacles = new ArrayList<>();
    protected static double[] treasurePosition;
    protected static double[] bombPosition;
    protected double detectionRadius = 100;

    /**
     * Constructs a WhiskerRobot with specified parameters.
     *
     * @param x            the x-coordinate of the robot
     * @param y            the y-coordinate of the robot
     * @param radius       the radius of the robot
     * @param whiskerLength the length of the whiskers
     * @param angle        the initial angle of the robot
     */
    public WhiskerRobot(double x, double y, double radius, double whiskerLength, double angle) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.whiskerLength = whiskerLength * 0.7;
        this.angle = angle;
    }

    /**
     * Sets the position of the treasure.
     *
     * @param x the x-coordinate of the treasure
     * @param y the y-coordinate of the treasure
     */
    public static void setTreasurePosition(double x, double y) {
        treasurePosition = new double[]{x, y};
    }

    /**
     * Sets the position of the bomb.
     *
     * @param x the x-coordinate of the bomb
     * @param y the y-coordinate of the bomb
     */
    public static void setBombPosition(double x, double y) {
        bombPosition = new double[]{x, y};
    }

    /**
     * Gets the position of the treasure.
     *
     * @return the position of the treasure
     */
    public static double[] getTreasurePosition() {
        return treasurePosition;
    }

    /**
     * Gets the position of the bomb.
     *
     * @return the position of the bomb
     */
    public static double[] getBombPosition() {
        return bombPosition;
    }

    /**
     * Checks if a target position is within the detection radius.
     *
     * @param targetX the x-coordinate of the target
     * @param targetY the y-coordinate of the target
     * @return true if the target is within the detection radius, false otherwise
     */
    private boolean isWithinDetectionRadius(double targetX, double targetY) {
        double distance = Math.sqrt(Math.pow(this.x - targetX, 2) + Math.pow(this.y - targetY, 2));
        return distance <= detectionRadius;
    }

    /**
     * Calculates the angle towards a target position.
     *
     * @param targetX the x-coordinate of the target
     * @param targetY the y-coordinate of the target
     * @return the angle to the target in degrees
     */
    private double calculateAngleToTarget(double targetX, double targetY) {
        double deltaX = targetX - this.x;
        double deltaY = targetY - this.y;
        double angleToTarget = Math.toDegrees(Math.atan2(deltaY, deltaX));
        if (angleToTarget < 0) {
            angleToTarget += 360;
        }
        return angleToTarget;
    }

    /**
     * Gets the whisker line segments of the robot.
     *
     * @return an ArrayList of Line objects representing the whisker lines
     */
    private ArrayList<Line> getWhiskerLine() {
        double rx1 = this.x + (radius * 0.6) * Math.cos(Math.toRadians(angle + 22.5));
        double ry1 = this.y + (radius * 0.6) * Math.sin(Math.toRadians(angle + 22.5));
        double rx2 = this.x + (radius + whiskerLength) * Math.cos(Math.toRadians(angle + 22.5));
        double ry2 = this.y + (radius + whiskerLength) * Math.sin(Math.toRadians(angle + 22.5));

        double lx1 = this.x + (radius * 0.6) * Math.cos(Math.toRadians(angle - 22.5));
        double ly1 = this.y + (radius * 0.6) * Math.sin(Math.toRadians(angle - 22.5));
        double lx2 = this.x + (radius + whiskerLength) * Math.cos(Math.toRadians(angle - 22.5));
        double ly2 = this.y + (radius + whiskerLength) * Math.sin(Math.toRadians(angle - 22.5));

        Line rightWhisker = new Line(rx1, ry1, rx2, ry2);
        Line leftWhisker = new Line(lx1, ly1, lx2, ly2);

        ArrayList<Line> whiskerLines = new ArrayList<>();
        whiskerLines.add(rightWhisker);
        whiskerLines.add(leftWhisker);

        return whiskerLines;
    }

    /**
     * Checks for collisions and turns the robot if necessary.
     *
     * @param arenaWidth  the width of the arena
     * @param arenaHeight the height of the arena
     */
    public void checkCollisionAndTurn(double arenaWidth, double arenaHeight) {
        ArrayList<Line> whiskerLines = getWhiskerLine();

        for (Line whisker : whiskerLines) {
            double[] coords = whisker.getXY();

            boolean hitLeftWall = coords[0] <= 0 || coords[2] <= 0;
            boolean hitRightWall = coords[0] >= arenaWidth || coords[2] >= arenaWidth;
            boolean hitTopWall = coords[1] <= 0 || coords[3] <= 0;
            boolean hitBottomWall = coords[1] >= arenaHeight || coords[3] >= arenaHeight;

            if (hitLeftWall || hitRightWall || hitTopWall || hitBottomWall) {
                handleCollision(hitLeftWall || hitRightWall);
                return;
            }

            for (Rectangle obstacle : obstacles) {
                if (lineIntersectsRectangle(whisker, obstacle)) {
                    boolean isHorizontalCollision =
                            Math.abs(Math.cos(Math.toRadians(angle))) >
                                    Math.abs(Math.sin(Math.toRadians(angle)));
                    handleCollision(isHorizontalCollision);
                    return;
                }
            }
        }
    }

    /**
     * Handles the collision by adjusting the angle of the robot.
     *
     * @param isHorizontalCollision true if the collision is horizontal, false otherwise
     */
    private void handleCollision(boolean isHorizontalCollision) {
        if (isHorizontalCollision) {
            angle = 180 - angle;
        } else {
            angle = -angle;
        }
        angle = (angle + 360) % 360;
    }

    /**
     * Checks if a line intersects with a rectangle.
     *
     * @param line the line to check
     * @param rect the rectangle to check against
     * @return true if the line intersects the rectangle, false otherwise
     */
    private boolean lineIntersectsRectangle(Line line, Rectangle rect) {
        double[] coords = line.getXY();
        double x1 = coords[0], y1 = coords[1], x2 = coords[2], y2 = coords[3];

        return lineIntersectsLine(x1, y1, x2, y2, rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY()) ||
                lineIntersectsLine(x1, y1, x2, y2, rect.getX(), rect.getY(), rect.getX(), rect.getY() + rect.getHeight()) ||
                lineIntersectsLine(x1, y1, x2, y2, rect.getX() + rect.getWidth(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()) ||
                lineIntersectsLine(x1, y1, x2, y2, rect.getX(), rect.getY() + rect.getHeight(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
    }

    /**
     * Checks if two lines intersect.
     *
     * @param x1 the x-coordinate of the first line's start point
     * @param y1 the y-coordinate of the first line's start point
     * @param x2 the x-coordinate of the first line's end point
     * @param y2 the y-coordinate of the first line's end point
     * @param x3 the x-coordinate of the second line's start point
     * @param y3 the y-coordinate of the second line's start point
     * @param x4 the x-coordinate of the second line's end point
     * @param y4 the y-coordinate of the second line's end point
     * @return true if the lines intersect, false otherwise
     */
    private boolean lineIntersectsLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double denominator = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (denominator == 0) return false;

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;

        return ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1;
    }

    /**
     * Turns the robot by a specified angle.
     *
     * @param deltaAngle the angle to turn
     */
    public void turn(double deltaAngle) {
        this.angle += deltaAngle;
        this.angle = this.angle % 360;
    }

    /**
     * Starts the movement of the robot.
     */
    public void startMoving() {
        isMoving = true;
    }

    /**
     * Stops the movement of the robot.
     */
    public void stopMoving() {
        isMoving = false;
    }

    /**
     * Draws the robot on the canvas.
     *
     * @param gc the GraphicsContext to draw on
     */
    public void draw(GraphicsContext gc) {
        ArrayList<Line> whiskerLines = getWhiskerLine();

        checkCollisionAndTurn(gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        boolean hasCollision = false;

        for (Line whisker : whiskerLines) {
            for (Rectangle obstacle : obstacles) {
                if (lineIntersectsRectangle(whisker, obstacle)) {
                    hasCollision = true;
                    break;
                }
            }
            if (hasCollision) break;
        }

        if (!hasCollision) {
            if (treasurePosition != null && isWithinDetectionRadius(treasurePosition[0], treasurePosition[1])) {
                angle = calculateAngleToTarget(treasurePosition[0], treasurePosition[1]);
            } else if (bombPosition != null && isWithinDetectionRadius(bombPosition[0], bombPosition[1])) {
                angle = calculateAngleToTarget(bombPosition[0], bombPosition[1]);
            }
        }

        if (isMoving) {
            double speedFactor = 3.5;
            double newX = x + Math.cos(Math.toRadians(angle)) * speedFactor;
            double newY = y + Math.sin(Math.toRadians(angle)) * speedFactor;

            if (treasurePosition != null) {
                double distToTreasure = Math.sqrt(Math.pow(newX - treasurePosition[0], 2) +
                        Math.pow(newY - treasurePosition[1], 2));
                if (distToTreasure < radius) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Congratulations! You win!");
                        alert.showAndWait();
                        clearAllContent(gc);
                    });
                    isMoving = false;
                    return;
                }
            }

            if (bombPosition != null) {
                double distToBomb = Math.sqrt(Math.pow(newX - bombPosition[0], 2) +
                        Math.pow(newY - bombPosition[1], 2));
                if (distToBomb < radius) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Failure");
                        alert.setHeaderText(null);
                        alert.setContentText("You failed, try again?");
                        alert.showAndWait();
                        clearAllContent(gc);
                    });
                    isMoving = false;
                    return;
                }
            }

            x = newX;
            y = newY;
        }

        double scaledRadius = radius * 0.6;

        gc.setFill(new Color(255 / 255.0, 128 / 255.0, 128 / 255.0, 1));
        gc.fillOval(this.x - scaledRadius, this.y - scaledRadius, scaledRadius * 2, scaledRadius * 2);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);

        double wheelLength = scaledRadius * 0.8;
        double wheelOffset = scaledRadius * 1.2;

        double leftWheelMidX = this.x - wheelOffset * Math.sin(Math.toRadians(angle));
        double leftWheelMidY = this.y + wheelOffset * Math.cos(Math.toRadians(angle));
        double leftWheelStartX = leftWheelMidX - (wheelLength/2) * Math.cos(Math.toRadians(angle));
        double leftWheelStartY = leftWheelMidY - (wheelLength/2) * Math.sin(Math.toRadians(angle));
        double leftWheelEndX = leftWheelMidX + (wheelLength/2) * Math.cos(Math.toRadians(angle));
        double leftWheelEndY = leftWheelMidY + (wheelLength/2) * Math.sin(Math.toRadians(angle));
    
        double rightWheelMidX = this.x + wheelOffset * Math.sin(Math.toRadians(angle));
        double rightWheelMidY = this.y - wheelOffset * Math.cos(Math.toRadians(angle));
        double rightWheelStartX = rightWheelMidX - (wheelLength / 2) * Math.cos(Math.toRadians(angle));
        double rightWheelStartY = rightWheelMidY - (wheelLength / 2) * Math.sin(Math.toRadians(angle));
        double rightWheelEndX = rightWheelMidX + (wheelLength / 2) * Math.cos(Math.toRadians(angle));
        double rightWheelEndY = rightWheelMidY + (wheelLength / 2) * Math.sin(Math.toRadians(angle));

        gc.strokeLine(leftWheelStartX, leftWheelStartY, leftWheelEndX, leftWheelEndY);
        gc.strokeLine(rightWheelStartX, rightWheelStartY, rightWheelEndX, rightWheelEndY);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        for (Line whisker : whiskerLines) {
            double[] coords = whisker.getXY();
            if (coords.length < 4) {
                System.err.println("Error: Line.getXY() returned array with insufficient length.");
                continue;
            }
            gc.strokeLine(coords[0], coords[1], coords[2], coords[3]);
        }
    }

    /**
     * Adds a robot object to the robots list.
     *
     * @param robot the robot to add
     */
    public static void addRobot(WhiskerRobot robot) {
        robots.add(robot);
    }

    /**
     * Gets the list of robots.
     *
     * @return the list of robots
     */
    public static ArrayList<WhiskerRobot> getRobots() {
        return robots;
    }

    /**
     * Adds an obstacle to the obstacles list.
     *
     * @param obstacle the obstacle to add
     */
    public static void addObstacle(Rectangle obstacle) {
        obstacles.add(obstacle);
    }

    /**
     * Gets the list of obstacles.
     *
     * @return the list of obstacles
     */
    public static ArrayList<Rectangle> getObstacles() {
        return obstacles;
    }

    /**
     * Clears all content from the canvas and resets the game state.
     *
     * @param gc the GraphicsContext to clear
     */
    private void clearAllContent(GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        WhiskerRobot.getRobots().clear();
        WhiskerRobot.getObstacles().clear();

        treasurePosition = null;
        bombPosition = null;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    /**
     * Moves the treasure and bomb in the specified direction.
     *
     * @param direction the direction to move ("w", "a", "s", "d")
     */
    public static void moveTreasureAndBomb(String direction) {
        if (treasurePosition == null || bombPosition == null) return;

        double moveStep = 10.0;

        switch (direction.toLowerCase()) {
            case "w":
                treasurePosition[1] -= moveStep;
                bombPosition[1] -= moveStep;
                break;
            case "s":
                treasurePosition[1] += moveStep;
                bombPosition[1] += moveStep;
                break;
            case "a":
                treasurePosition[0] -= moveStep;
                bombPosition[0] -= moveStep;
                break;
            case "d":
                treasurePosition[0] += moveStep;
                bombPosition[0] += moveStep;
                break;
        }
    }

    /**
     * Checks and adjusts the bounds of the treasure and bomb positions.
     *
     * @param canvasWidth  the width of the canvas
     * @param canvasHeight the height of the canvas
     */
    public static void checkAndAdjustBounds(double canvasWidth, double canvasHeight) {
        if (treasurePosition != null) {
            treasurePosition[0] = Math.max(20, Math.min(canvasWidth - 20, treasurePosition[0]));
            treasurePosition[1] = Math.max(20, Math.min(canvasHeight - 20, treasurePosition[1]));
        }

        if (bombPosition != null) {
            bombPosition[0] = Math.max(20, Math.min(canvasWidth - 20, bombPosition[0]));
            bombPosition[1] = Math.max(20, Math.min(canvasHeight - 20, bombPosition[1]));
        }
    }
}