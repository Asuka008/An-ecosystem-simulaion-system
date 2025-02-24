package consoleVersion1;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.util.Random;
import javafx.geometry.Insets;

/**
 * RobotInterface class representing the user interface for the Robot Arena application.
 * @author Zichen Liao
 * It allows users to interact with robots, manage the arena, and visualize the simulation.
 */
public class RobotInterface extends Application {
    private RobotArena myArena;
    private RobotCanvas rc;
    private AnimationTimer animationTimer;
    private Random random = new Random();
    private TextArea infoPanel;
    private boolean isAnimationRunning = false;
    private int selectedRobotIndex = -1;
    private Label selectedRobotLabel;

    @Override
    public void start(Stage primaryStage) {
        random = new Random();
        myArena = new RobotArena(25, 25);
        rc = new RobotCanvas(500, 500);

        // Initialize obstacles
        myArena.addRandomObstacles(2, 1.0, 1.0); // The width and height of the obstacles are 1.0
        rc.drawRobots(myArena.getObservableRobots());
        rc.drawObstacles(FXCollections.observableArrayList(myArena.getObstacles()));

        // Scene control area
        GridPane sceneControlPane = new GridPane();
        TextField widthField = new TextField("500");
        TextField heightField = new TextField("500");
        TextField treeCountField = new TextField("2");
        Button applySceneButton = new Button("Apply Scene");

        sceneControlPane.add(new Label("Canvas Width:"), 0, 0);
        sceneControlPane.add(widthField, 1, 0);
        sceneControlPane.add(new Label("Canvas Height:"), 0, 1);
        sceneControlPane.add(heightField, 1, 1);
        sceneControlPane.add(new Label("Trees:"), 0, 2);
        sceneControlPane.add(treeCountField, 1, 2);
        sceneControlPane.add(applySceneButton, 1, 3);

        applySceneButton.setOnAction(e -> {
            int canvasWidth = Integer.parseInt(widthField.getText());
            int canvasHeight = Integer.parseInt(heightField.getText());
            int treeCount = Integer.parseInt(treeCountField.getText());
            rc.setWidth(canvasWidth);
            rc.setHeight(canvasHeight);
            myArena.clearArena();
            myArena.addRandomObstacles(treeCount, 0.2, 0.2); // Use rectangle width and height when adding obstacles
            rc.drawRobots(myArena.getObservableRobots());
            rc.drawObstacles(FXCollections.observableArrayList(myArena.getObstacles()));
        });

        // Robot control area
        VBox robotControlBox = new VBox(10);
        TextField predatorCountField = new TextField("0");
        TextField preyCountField = new TextField("0");
        Button applyRobotButton = new Button("Apply Robots");

        robotControlBox.getChildren().addAll(new Label("Predators:"), predatorCountField, new Label("Preys:"), preyCountField, applyRobotButton);

        applyRobotButton.setOnAction(e -> {
            int predatorCount = Integer.parseInt(predatorCountField.getText());
            int preyCount = Integer.parseInt(preyCountField.getText());
            myArena.clearRobots();
            for (int i = 0; i < predatorCount; i++) {
                myArena.addRobot(new Predator(random.nextInt(myArena.getXSize()), random.nextInt(myArena.getYSize()), random.nextDouble() * 360));
            }
            for (int i = 0; i < preyCount; i++) {
                myArena.addRobot(new Prey(random.nextInt(myArena.getXSize()), random.nextInt(myArena.getYSize()), random.nextDouble() * 360));
            }
            rc.drawRobots(myArena.getObservableRobots());
            rc.drawObstacles(FXCollections.observableArrayList(myArena.getObstacles()));
            if (!myArena.getRobots().isEmpty()) {
                selectedRobotIndex = 0;
                updateSelectedRobotLabel();
            }
        });

        infoPanel = new TextArea();
        infoPanel.setEditable(false);
        infoPanel.setPrefRowCount(5);
        infoPanel.setPrefColumnCount(50);
        infoPanel.setPrefHeight(200);  
        infoPanel.setPrefWidth(50);   
        infoPanel.setMaxWidth(200);    
        infoPanel.setWrapText(true);

        // Button control area
        HBox buttonBox = new HBox(10);
        Button beginButton = new Button("Begin");
        Button stopButton = new Button("Stop");
        Button saveButton = new Button("Save");
        Button loadButton = new Button("Load");
        Button testModeButton = new Button("Test Mode"); // Add Test Mode button

        beginButton.setOnAction(e -> startAnimation());
        stopButton.setOnAction(e -> stopAnimation());
        saveButton.setOnAction(e -> saveStateToFile());
        loadButton.setOnAction(e -> {
            loadStateFromFile();
            rc.drawRobots(myArena.getObservableRobots());
            rc.drawObstacles(FXCollections.observableArrayList(myArena.getObstacles()));
            // Update counts in the configuration box
            widthField.setText(String.valueOf((int) rc.getWidth()));
            heightField.setText(String.valueOf((int) rc.getHeight()));
            treeCountField.setText(String.valueOf(myArena.getObstacles().size()));
            predatorCountField.setText(String.valueOf(myArena.getRobots().size()));
        });

        // Event handling for Test Mode button
        testModeButton.setOnAction(e -> {
            // Clear and redraw canvas
            rc.getGraphicsContext2D().clearRect(0, 0, rc.getWidth(), rc.getHeight());
            rc.getGraphicsContext2D().setStroke(Color.BLACK);
            rc.getGraphicsContext2D().setLineWidth(2);
            rc.getGraphicsContext2D().strokeRect(0, 0, rc.getWidth(), rc.getHeight());
            
            WhiskerRobot.getRobots().clear();
            WhiskerRobot.getObstacles().clear();
            // Create obstacles
            double obstacleSize = 40;
            WhiskerRobot.getObstacles().clear();
            
            for (int i = 0; i < 4; i++) {
                double obstacleX = random.nextDouble() * (rc.getWidth() - 2 * obstacleSize) + obstacleSize;
                double obstacleY = random.nextDouble() * (rc.getHeight() - 2 * obstacleSize) + obstacleSize;
                Rectangle obstacle = new Rectangle(obstacleX, obstacleY, obstacleSize, obstacleSize);
                WhiskerRobot.addObstacle(obstacle);
            }
            
            // Draw obstacles using the drawObstacles function
            drawObstacles(rc.getGraphicsContext2D());
            
            // Create treasure and bomb
            double treasureX, treasureY, bombX, bombY;
            double minDistance = 200; // Minimum distance between treasure and bomb
            
            do {
                treasureX = random.nextDouble() * (rc.getWidth() - 40) + 20;
                treasureY = random.nextDouble() * (rc.getHeight() - 40) + 20;
                bombX = random.nextDouble() * (rc.getWidth() - 40) + 20;
                bombY = random.nextDouble() * (rc.getHeight() - 40) + 20;
            } while (Math.sqrt(Math.pow(treasureX - bombX, 2) + Math.pow(treasureY - bombY, 2)) < minDistance);
            
            WhiskerRobot.setTreasurePosition(treasureX, treasureY);
            WhiskerRobot.setBombPosition(bombX, bombY);
            
            // Create WhiskerRobot
            double initialX = rc.getWidth() / 2;
            double initialY = rc.getHeight() / 2;
            double initialRadius = 30;
            double initialWhiskerLength = 40;
            double initialAngle = random.nextDouble() * 360;
            
            WhiskerRobot robot = new WhiskerRobot(initialX, initialY, initialRadius, initialWhiskerLength, initialAngle);
            WhiskerRobot.addRobot(robot);
            
            // Draw everything
            GraphicsContext gc = rc.getGraphicsContext2D();
            
            // Draw obstacles
            for (Rectangle obstacle : WhiskerRobot.getObstacles()) {
                gc.strokeRect(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());
            }
            
            // Draw treasure (gold star)
            gc.setFill(Color.GOLD);
            drawStar(gc, treasureX, treasureY, 15);
            
            // Draw bomb (blue circle)
            gc.setFill(Color.BLUE);
            gc.fillOval(bombX - 10, bombY - 10, 20, 20);
            
            // Draw robot
            for (WhiskerRobot r : WhiskerRobot.getRobots()) {
                r.draw(gc);
            }
        });
        buttonBox.getChildren().addAll(beginButton, stopButton, saveButton, loadButton, testModeButton); // Add Test Mode button to button box

        // Place scene control area and robot control area on the left
        VBox leftControl = new VBox(10);
        leftControl.getChildren().addAll(
            sceneControlPane,
            robotControlBox,
            buttonBox,
            infoPanel  // Add info panel to the bottom of the left control area
        );
        leftControl.setPadding(new Insets(10));

        // Layout
        BorderPane layout = new BorderPane();
        layout.setLeft(leftControl); // Set left control area to the left

        // Set Canvas to center
        layout.setCenter(rc); // Place Canvas in the center

        // Right control panel
        VBox rightControl = new VBox(10);
        rightControl.setPadding(new Insets(10));

        // Selected robot display
        selectedRobotLabel = new Label("No robot selected");
        selectedRobotLabel.setStyle("-fx-padding: 5; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");
        selectedRobotLabel.setMinWidth(150);

        // Control buttons
        Button moveButton = new Button("Move");
        Button deleteButton = new Button("Delete");
        Button nextButton = new Button("Next");

        // Set preferred width for buttons
        moveButton.setPrefWidth(150);
        deleteButton.setPrefWidth(150);
        nextButton.setPrefWidth(150);

        // Button actions
        moveButton.setOnAction(e -> moveSelectedRobot());
        deleteButton.setOnAction(e -> deleteSelectedRobot());
        nextButton.setOnAction(e -> selectNextRobot());

        // Add all controls to right panel
        rightControl.getChildren().addAll(
            selectedRobotLabel,
            moveButton,
            deleteButton,
            nextButton
        );

        // Add right control to layout
        layout.setRight(rightControl);

        // Place buttons at the bottom
        layout.setBottom(buttonBox); // Place buttons at the bottom
        // Add MenuBar at the top
        MenuBar menuBar = new MenuBar();

        // Create About Menu
        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem = new MenuItem("About Robot Arena");
        aboutItem.setOnAction(e -> {
            Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
            aboutAlert.setTitle("About Robot Arena");
            aboutAlert.setHeaderText("Robot Arena Simulation v1.0");
            aboutAlert.setContentText(
                "Robot Arena is an interactive simulation environment where robots navigate " +
                "through obstacles while searching for treasure and avoiding bombs.\n\n" +
                "Features:\n" +
                "• Whisker-based robot navigation\n" +
                "• Dynamic obstacle placement\n" +
                "• Treasure hunting mechanics\n" +
                "• Bomb avoidance system\n" +
                "• Real-time animation\n\n" +
                "© 2024 Robot Arena Project"
            );
            aboutAlert.showAndWait();
        });
        aboutMenu.getItems().add(aboutItem);

        // Create Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem helpItem = new MenuItem("User Guide");
        helpItem.setOnAction(e -> {
            Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
            helpAlert.setTitle("Robot Arena Help");
            helpAlert.setHeaderText("How to Use Robot Arena");
            helpAlert.setContentText(
                "Controls:\n" +
                "• Begin - Start the robot simulation\n" +
                "• Stop - Pause the simulation\n" +
                "• Save - Save current arena state\n" +
                "• Load - Load previous arena state\n" +
                "• Test Mode - Enter testing environment\n\n" +
                "Keyboard Controls:\n" +
                "• W - Move treasure up\n" +
                "• A - Move treasure left\n" +
                "• S - Move treasure down\n" +
                "• D - Move treasure right\n\n" +
                "Tips:\n" +
                "• Robots use whiskers to detect obstacles\n" +
                "• Gold stars represent treasure\n" +
                "• Blue circles represent bombs\n" +
                "• Green rectangles are obstacles"
            );
            helpAlert.showAndWait();
        });
        helpMenu.getItems().add(helpItem);

        // Add menus to menubar
        menuBar.getMenus().addAll(aboutMenu, helpMenu);

        // Add MenuBar to the top of your layout
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 1050, 600); // Adjust window size to 1050x600
        scene.setOnKeyPressed(event -> {
            if (!isAnimationRunning) {  // Only respond to keyboard when animation is not running
                String key = event.getText().toLowerCase();
                if ("wasd".contains(key)) {
                    WhiskerRobot.moveTreasureAndBomb(key);
                    WhiskerRobot.checkAndAdjustBounds(rc.getWidth(), rc.getHeight());
                    
                    // Redraw canvas
                    GraphicsContext gc = rc.getGraphicsContext2D();
                    gc.clearRect(0, 0, rc.getWidth(), rc.getHeight());
                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(2);
                    gc.strokeRect(0, 0, rc.getWidth(), rc.getHeight());
                    
                    // Redraw obstacles
                    drawObstacles(gc);
                    
                    // Redraw Treasure
                    double[] treasurePos = WhiskerRobot.getTreasurePosition();
                    if (treasurePos != null) {
                        gc.setFill(Color.GOLD);
                        drawStar(gc, treasurePos[0], treasurePos[1], 15);
                    }
                    
                    // Redraw Bomb
                    double[] bombPos = WhiskerRobot.getBombPosition();
                    if (bombPos != null) {
                        gc.setFill(Color.BLUE);
                        gc.fillOval(bombPos[0] - 10, bombPos[1] - 10, 20, 20);
                    }
                    
                    // Redraw robot
                    for (WhiskerRobot robot : WhiskerRobot.getRobots()) {
                        robot.draw(gc);
                    }
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setTitle("Robot Arena");
        primaryStage.show();
    }

    /**
     * Starts the animation of the robots in the arena.
     */
    private void startAnimation() {
        isAnimationRunning = true;
        if (animationTimer == null) {
            animationTimer = new AnimationTimer() {
                private long lastUpdate = 0;
    
                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= 50_000_000) { // Update every 50 milliseconds
                        // Clear canvas
                        rc.getGraphicsContext2D().clearRect(0, 0, rc.getWidth(), rc.getHeight());
                        
                        // Redraw canvas border
                        rc.getGraphicsContext2D().setStroke(Color.BLACK);
                        rc.getGraphicsContext2D().setLineWidth(2);
                        rc.getGraphicsContext2D().strokeRect(0, 0, rc.getWidth(), rc.getHeight());
                        
                        myArena.updateRobots();
                        rc.drawRobots(myArena.getObservableRobots());
                        rc.drawObstacles(FXCollections.observableArrayList(myArena.getObstacles()));
                        
                        // Draw obstacles
                        drawObstacles(rc.getGraphicsContext2D());
                        updateInfoPanel();

                        // Redraw Treasure and Bomb
                        double[] treasurePos = WhiskerRobot.getTreasurePosition();
                        double[] bombPos = WhiskerRobot.getBombPosition();
                        
                        if (treasurePos != null) {
                            rc.getGraphicsContext2D().setFill(Color.GOLD);
                            drawStar(rc.getGraphicsContext2D(), treasurePos[0], treasurePos[1], 15);
                        }
                        
                        if (bombPos != null) {
                            rc.getGraphicsContext2D().setFill(Color.BLUE);
                            rc.getGraphicsContext2D().fillOval(bombPos[0] - 10, bombPos[1] - 10, 20, 20);
                        }
    
                        // Update and draw WhiskerRobot
                        for (WhiskerRobot robot : WhiskerRobot.getRobots()) {
                            if (robot.isMoving) {
                                robot.checkCollisionAndTurn(rc.getWidth(), rc.getHeight());
                                robot.draw(rc.getGraphicsContext2D());
                            }
                        }
                        
                        lastUpdate = now;
                    }
                }
            };
        }
        
        // Start all WhiskerRobot
        for (WhiskerRobot robot : WhiskerRobot.getRobots()) {
            robot.startMoving();
        }
        
        animationTimer.start();
    }
    
    /**
     * Stops the animation of the robots in the arena.
     */
    private void stopAnimation() {
        isAnimationRunning = false;
        if (animationTimer != null) {
            animationTimer.stop();
        }
        for (WhiskerRobot robot : WhiskerRobot.getRobots()) {
            robot.stopMoving(); // Stop movement
        }
    }

    /**
     * Saves the current state of the arena and robots to a file.
     */
    private void saveStateToFile() {
        TextFile tf = new TextFile("Robot data files", "txt");
        if (tf.createFile()) {
            tf.putNextLine("Canvas Size: " + (int) rc.getWidth() + " " + (int) rc.getHeight());
            tf.putNextLine("Arena Size: " + myArena.getXSize() + " " + myArena.getYSize());
            for (Robot r : myArena.getRobots()) {
                String type = r instanceof Predator ? "Predator" : "Prey";
                tf.putNextLine(type + ": " + r.getId() + " " + r.getX() + " " + r.getY() + " " + r.getAngle());
            }
            for (Obstacle o : myArena.getObstacles()) {
                tf.putNextLine("Obstacle: " + o.getX() + " " + o.getY() + " " + o.getWidth() + " " + o.getHeight());
            }
            tf.closeWriteFile();
            showAlert("Success", "State saved!");
        } else {
            showAlert("Error", "Save cancelled.");
        }
    }

    /**
     * Loads the state of the arena and robots from a file.
     */
    private void loadStateFromFile() {
        TextFile tf = new TextFile("Robot data files", "txt");
        if (tf.openFile()) {
            myArena.clearArena();
            if (tf.getNextline()) {
                String[] canvasSize = tf.nextLine().split(" ");
                rc.setWidth(Double.parseDouble(canvasSize[2]));
                rc.setHeight(Double.parseDouble(canvasSize[3]));
            }
            if (tf.getNextline()) {
                String[] arenaSize = tf.nextLine().split(" ");
                myArena = new RobotArena(Integer.parseInt(arenaSize[2]), Integer.parseInt(arenaSize[3]));
            }
            while (tf.getNextline()) {
                String[] data = tf.nextLine().split(" ");
                if (data[0].equals("Predator:")) {
                    Predator p = new Predator(
                            Double.parseDouble(data[2]),
                            Double.parseDouble(data[3]),
                            Double.parseDouble(data[4])
                    );
                    myArena.addRobot(p);
                } else if (data[0].equals("Prey:")) {
                    Prey p = new Prey(
                            Double.parseDouble(data[2]),
                            Double.parseDouble(data[3]),
                            Double.parseDouble(data[4])
                    );
                    myArena.addRobot(p);
                } else if (data[0].equals("Obstacle:")) {
                    Obstacle o = new Obstacle(
                            Double.parseDouble(data[1]),
                            Double.parseDouble(data[2]),
                            Double.parseDouble(data[3]),
                            Double.parseDouble(data[4])
                    );
                    myArena.addObstacle(o);
                }
            }
            tf.closeFile();
            showAlert("Success", "State loaded!");
        } else {
            showAlert("Error", "Load cancelled.");
        }
    }

    /**
     * Displays an alert with the specified title and message.
     * 
     * @param title the title of the alert
     * @param message the message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Draws the obstacles on the canvas.
     * 
     * @param gc the GraphicsContext used for drawing
     */
    private void drawObstacles(GraphicsContext gc) {
        gc.setFill(Color.GREEN); // Set fill color to green
        gc.setStroke(Color.BLACK); // Set border color to black
        gc.setLineWidth(2); // Set border width
        for (Rectangle obstacle : WhiskerRobot.getObstacles()) {
            gc.fillRect(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());
            gc.strokeRect(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());
        }
    }

    /**
     * Updates the information panel with the current state of the arena and robots.
     */
    private void updateInfoPanel() {
        StringBuilder info = new StringBuilder();
        info.append("Canvas Size: ").append((int)rc.getWidth()).append(" ").append((int)rc.getHeight()).append("\n");
        info.append("Arena Size: ").append(myArena.getXSize()).append(" ").append(myArena.getYSize()).append("\n");
        
        for (Robot r : myArena.getRobots()) {
            String type = r instanceof Predator ? "Predator" : "Prey";
            info.append(type).append(": ")
                .append(r.getId()).append(" ")
                .append(String.format("%.2f", r.getX())).append(" ")
                .append(String.format("%.2f", r.getY())).append(" ")
                .append(String.format("%.2f", r.getAngle()))
                .append("\n");
        }
        
        for (Obstacle o : myArena.getObstacles()) {
            info.append("Obstacle: ")
                .append(String.format("%.2f", o.getX())).append(" ")
                .append(String.format("%.2f", o.getY())).append(" ")
                .append(String.format("%.2f", o.getWidth())).append(" ")
                .append(String.format("%.2f", o.getHeight()))
                .append("\n");
        }
        
        infoPanel.setText(info.toString());
    }

    /**
     * Draws a star shape at the specified position.
     * 
     * @param gc the GraphicsContext used for drawing
     * @param centerX the x-coordinate of the center of the star
     * @param centerY the y-coordinate of the center of the star
     * @param radius the radius of the star
     */
    private void drawStar(GraphicsContext gc, double centerX, double centerY, double radius) {
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * i / 5 - Math.PI / 2;
            double r = (i % 2 == 0) ? radius : radius / 2;
            xPoints[i] = centerX + r * Math.cos(angle);
            yPoints[i] = centerY + r * Math.sin(angle);
        }
        
        gc.beginPath();
        gc.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < 10; i++) {
            gc.lineTo(xPoints[i], yPoints[i]);
        }
        gc.closePath();
        gc.fill();
    }

    /**
     * Moves the currently selected robot in the arena.
     */
    private void moveSelectedRobot() {
        if (selectedRobotIndex >= 0 && selectedRobotIndex < myArena.getRobots().size()) {
            Robot robot = myArena.getRobots().get(selectedRobotIndex);
            robot.tryToMove(myArena);  // Use tryToMove() instead of move()
            
            // Redraw canvas
            rc.drawRobots(myArena.getObservableRobots());
            rc.drawObstacles(FXCollections.observableArrayList(myArena.getObstacles()));
            
            // Update info panel
            updateInfoPanel();
        }
    }
    
    /**
     * Deletes the currently selected robot from the arena.
     */
    private void deleteSelectedRobot() {
        if (selectedRobotIndex >= 0 && selectedRobotIndex < myArena.getRobots().size()) {
            myArena.getRobots().remove(selectedRobotIndex);
            
            // If there are remaining robots, select the next one or the last one
            if (!myArena.getRobots().isEmpty()) {
                if (selectedRobotIndex >= myArena.getRobots().size()) {
                    selectedRobotIndex = myArena.getRobots().size() - 1;
                }
            } else {
                selectedRobotIndex = -1;
            }
            
            // Redraw canvas
            rc.drawRobots(myArena.getObservableRobots());
            rc.drawObstacles(FXCollections.observableArrayList(myArena.getObstacles()));
            
            // Update info panel and selected robot label
            updateInfoPanel();
            updateSelectedRobotLabel();
        }
    }
    
    /**
     * Selects the next robot in the arena.
     */
    private void selectNextRobot() {
        if (!myArena.getRobots().isEmpty()) {
            selectedRobotIndex = (selectedRobotIndex + 1) % myArena.getRobots().size();
            updateSelectedRobotLabel();
        }
    }
    
    /**
     * Updates the label displaying the currently selected robot's information.
     */
    private void updateSelectedRobotLabel() {
        if (selectedRobotIndex >= 0 && selectedRobotIndex < myArena.getRobots().size()) {
            Robot robot = myArena.getRobots().get(selectedRobotIndex);
            String type = robot instanceof Predator ? "Predator" : "Prey";
            selectedRobotLabel.setText(type + " " + robot.getId());
        } else {
            selectedRobotLabel.setText("No robot selected");
        }
    }
}