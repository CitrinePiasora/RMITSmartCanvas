package com.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import java.awt.image.RenderedImage;

import com.controllers.Helpers.ControllerHelper;
import com.models.User;

public class BoardController implements Initializable {

    @FXML private ImageView profilePicture;
    @FXML private Label nameLabel;
    @FXML private Slider zoomSlider;
    @FXML private Label zoomPercent;
    @FXML private MenuItem saveButton;
    @FXML private MenuItem clearButton;
    @FXML private StackPane zoomPane;
    @FXML private Pane canvasPane;
    @FXML private Canvas FXCanvas;

    // initialize session variables
    private User currentUser;
    private Stage currentSession;

    // Initializes canvas related variables
    private GraphicsContext gcF;
    private double percentageCalc = 1.0;
    private int canvasH, canvasW;
    private boolean drawoval = false, drawrectangle = false, drawtext = false, drawImage = false;
    private double startX, startY, lastX, lastY, originX, originY;
    private double wh, hg;
    private Group currentNode;

    private Line top, left, right, bottom;
    private Rectangle topL, topR, botL, botR;
    private Circle rotCircle;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    // Function to initalize the board's variables
    public void initializeSession(User user, Stage currentSession, int canvasHeight, int canvasWidth, boolean canvasActive) {
        profilePicture.setImage(user.getImage());
        nameLabel.setText(user.getFullName());
        
        // Initialize Canvas Properties
        this.canvasH = canvasHeight;
        this.canvasW = canvasWidth;
        this.canvasPane.setPrefHeight(canvasHeight);
        this.canvasPane.setPrefWidth(canvasWidth);
        this.FXCanvas.setHeight(canvasHeight);
        this.FXCanvas.setWidth(canvasWidth);
        this.gcF = this.FXCanvas.getGraphicsContext2D();
        
        // Initialize state of menu items and canvas
        this.clearButton.setDisable(!canvasActive);
        this.saveButton.setDisable(!canvasActive);
        this.canvasPane.setVisible(canvasActive);

        // Initialize session
        this.currentUser = user; 
        this.currentSession = currentSession;

        // Adds a listener to the slider in order to change zoom level of canvas
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                double sliderValue = (double) zoomSlider.getValue();

                // Logic that calculates the current zoom %
                if(sliderValue > 0.0) {
                    percentageCalc = 1.0 + (sliderValue / 100.0);
                    zoomPercent.setText("Zoom: " + Integer.toString((int) sliderValue) + "%");
                } else if(sliderValue < 0.0){
                    percentageCalc = (100.0 + sliderValue) / 100.0;
                    zoomPercent.setText("Zoom: " + Integer.toString((int) sliderValue) + "%");
                } else if(sliderValue == 0.0 || 100.0 + sliderValue == 0.0){
                    percentageCalc = 1.0;
                    zoomPercent.setText("Zoom: 0%");
                }

                // Code that resizes elements based on the calculated zoom %
                ObservableList<Node> canvasElements = zoomPane.getChildren();
                for(Node element : canvasElements) {
                    element.setScaleX(percentageCalc);
                    element.setScaleY(percentageCalc);
                }
            }
            
        });
    }

    @FXML // FX Function, when profile button is pressed...
    public void profilePopUp(ActionEvent event) {
        // Creates a pop up to open up the profile management menu
        ControllerHelper.initStage("ProfileEdit", "Profile Management", currentSession, profilePicture.getImage(), currentUser, "popup");
    }

    @FXML // FX Function, when new canvas is pressed...
    void NewCanvas(ActionEvent event) {
        // Create a pop up to initialize dimensions of canvas
        ControllerHelper.initStage("NewCanvas", "Canvas Properties", this.currentSession, profilePicture.getImage(), currentUser, "popup");
    }

    @FXML // FX Function, when clear canvas is pressed...
    void clearCanvas(ActionEvent event) {
        // Clears all nodes on the canvaspane, wiping the canvas
        canvasPane.getChildren().clear();
    }

    @FXML // FX Function, when delete element is pressed...
    void deleteElement(ActionEvent event) {
        // Delete the currently selected Node
        canvasPane.getChildren().remove(this.currentNode);
        // Moves FXCanvas to front in case of stupidity
        FXCanvas.toFront();
    }

    @FXML // FX Function, when save as is pressed...
    void saveImage(ActionEvent event) {
        

        // initialize filechooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG files (*.png)", "*.png"));
        
        // Initialize file to write using filechooser save dialog
        File file = fileChooser.showSaveDialog(this.currentSession);

        if(file != null) {
            try {
                
                // stores current slider value, sets slider value to 0 to export image properly
                int currentZoom = (int) zoomSlider.getValue();
                zoomSlider.setValue(0);

                WritableImage exporting = new WritableImage(canvasW, canvasH);
                canvasPane.snapshot(null, exporting);
                RenderedImage exportedFile = SwingFXUtils.fromFXImage(exporting, null);
                ImageIO.write(exportedFile, "png", file);

                // restores old zoom value so the user doesn't see that the zoom had changed while saving
                zoomSlider.setValue(currentZoom);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML // FX Function, when log out button is pressed...
    void logOut() {
        // Switches current scene to login screen
        ControllerHelper.switchViews("Login", "Welcome to SmartCanvas", this.currentSession);
    }

    @FXML // FX Function, when the circle button is pressed...
    void circleBrush(ActionEvent event) {
        // If current brush is circle
        if(drawoval) {
            // Sets current brush as nothing
            drawoval = false;

        // Else set current brush as circle brush
        } else {
            drawoval = true;
            drawrectangle = false;
            drawtext = false;
            drawImage = false;
        }
    }
    
    @FXML // FX Function, when the rectangle button is pressed...
    void rectBrush(ActionEvent event) {
        // If current brush is rectangle
        if(drawrectangle) {
            // Sets current brush as nothing
            drawrectangle = false;
        // Else set current brush as rectangle brush
        } else {
            drawoval = false;
            drawrectangle = true;
            drawtext = false;
            drawImage = false;
        }
    }

    @FXML // FX Function, when the text button is pressed...
    void insertTextBox(ActionEvent event) {
        // If current brush is text
        if(drawtext) {
            // Sets current brush as nothing
            drawtext = false;
        // Else set current brush as text brush
        } else {
            drawoval = false;
            drawrectangle = false;
            drawtext = true;
            drawImage = false;
        }
    }

    @FXML // FX Function, when the image button is pressed...
    private void addImage() {
        // If current brush is image
        if(drawImage) {
            // Sets current brush as nothing
            drawImage = false;
        // Else set current brush as image brush
        } else {
            drawoval = false;
            drawrectangle = false;
            drawtext = false;
            drawImage = true;
        }
    }
    
    @FXML // FX Function, when the canvas button is pressed...
    void canvasColor(ActionEvent Event) {
        // Placeholder function to set canvas color to pink
        canvasPane.setStyle("-fx-background-color: pink; -fx-border-color: gray");
    }

    @FXML // FX Function, when user clicks mouse anywhere within canvaspane
    private void onMousePressedListener(MouseEvent e) {
        // Sets values for startx and starty
        this.startX = e.getX();
        this.startY = e.getY();

        // If text brush is active
        if(drawtext) {
            // Invokes the draw text function
            drawTextBox();
        // Else if image brush is active
        }
    }

    @FXML // FX Function, when user clicks on fxcanvas (invisible layer on top of canvaspane)
    private void FXMousePress(MouseEvent e) {
        // Sets values for startx and starty
        this.startX = e.getX();
        this.startY = e.getY();
    }

    @FXML // FX Function, when drags the mouse on fxcanvas (invisible layer on top of canvaspane)
    private void FXMouseDrag(MouseEvent e) {
        // Sets values for lastx and lasty
        this.lastX = e.getX();
        this.lastY = e.getY();

        // If rectangle brush is active
        if(drawrectangle) {
            // Invokes the draw rectangle effect function
            drawRectEffect();
        // Else if circle brush is active
        } else if(drawoval) {
            // Invokes the draw circle effect function
            drawOvalEffect();
        } else if(drawImage) {
            // Invokes the draw image function
            drawRectEffect();
        }
    }

    @FXML // FX Function, when mouse is released on fxcanvas (invisible layer on top of canvaspane)
    private void FXMouseRelease(MouseEvent e) {
        // If rectangle brush is active
        if(drawrectangle) {
            // Invokes the draw rectangle function
            drawRect();
        // Else if circle brush is active
        } else if(drawoval) {
            // Invokes the draw circle function
            drawOval();
        } else if(drawImage) {
            // Invokes the draw image function
            drawImage();
        }

        // Clears the graphicscontext for fxcanvas 
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
    }

    // Helper function that sets current node
    private void setCurrentNode(Group node) {
        if(this.currentNode != null) {
            for(int i = 1; i < this.currentNode.getChildren().size(); i++) {
                this.currentNode.getChildren().get(i).setDisable(true);
                this.currentNode.getChildren().get(i).setVisible(false);
            }
        }

        this.currentNode = node;
        for(int i = 1; i < this.currentNode.getChildren().size(); i++) {
            this.currentNode.getChildren().get(i).setDisable(false);
            this.currentNode.getChildren().get(i).setVisible(true);
        }
    }

    // Helper function to make given node draggable
    private void makeDraggable(Group node) {
        Node node1 = node.getChildren().get(0);

        node1.setOnMousePressed(e -> {
            originX = e.getSceneX() - node.getTranslateX();
            originY = e.getSceneY() - node.getTranslateY();

            drawImage = false;
            drawoval = false;
            drawrectangle = false;
            drawtext = false;
            setCurrentNode(node);
        });

        node1.setOnMouseDragged(e -> {
            node.setTranslateX(e.getSceneX() - originX);
            node.setTranslateY(e.getSceneY() - originY);
        });
    }

    // Helper function to help build lines of selection box
    private Line buildLine(double x1, double x2, double y1, double y2) {    
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.AQUA);
        line.setStrokeWidth(1);
        return line;
    }

    // Build a corner of the rectangle
    private Rectangle buildCorner (double x, double y) {

        // Create the rectangle
        Rectangle r = new Rectangle();
        r.setX(x);
        r.setY(y);
        r.setWidth(10);
        r.setHeight(10);
        r.setStroke(Color.rgb(0,0,0,0.75));
        r.setFill(Color.LIGHTGRAY);
        r.setStrokeWidth(1);
        r.setStrokeType(StrokeType.INSIDE);

        r.setCursor(Cursor.CROSSHAIR);

        // Initialize originx and originy on being clicked
        r.setOnMousePressed(e -> {
            originX = e.getSceneX() - r.getTranslateX();
            originY = e.getSceneY() - r.getTranslateY();

            drawImage = false;
            drawoval = false;
            drawrectangle = false;
            drawtext = false;
        });

        r.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - originX;
            double newY  = e.getSceneY() - originY;

            //
            
            // Get angle in radians
            // double a = Math.atan2(newY, newX);
            
            // double radianRot = Math.toRadians(r.getRotate());
            // wh = Math.cos(a);
            // hg = Math.sin(a);

            Rectangle tempRect = new Rectangle();
            tempRect.setX(r.getX()); tempRect.setY(r.getY()); tempRect.setWidth(10); tempRect.setHeight(10);
            tempRect.setTranslateX(newX); tempRect.setTranslateY(newY);

            if(r == topL) {  
                if(topR.getBoundsInParent().getMinX() - 15 >= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    botL.setTranslateX(newX);
                }

                if(botL.getBoundsInParent().getMinY() - 15 >= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    topR.setTranslateY(newY);
                } 

                Bounds rectangleInParent = r.getBoundsInParent();
                top.setStartX(rectangleInParent.getMinX() + 5); top.setStartY(rectangleInParent.getMaxY() - 5); top.setEndY(rectangleInParent.getMaxY() - 5);
                bottom.setStartX(rectangleInParent.getMinX() + 5);
                left.setStartX(rectangleInParent.getMinX() + 5); left.setEndX(rectangleInParent.getMinX() + 5); left.setStartY(rectangleInParent.getMaxY());
                right.setStartY(rectangleInParent.getMaxY());

            } else if(r == topR) {
                if(topL.getBoundsInParent().getMinX() + 15 <= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    botR.setTranslateX(newX);
                }

                if(botR.getBoundsInParent().getMinY() - 15 >= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    topL.setTranslateY(newY);
                } 

                Bounds rectangleInParent = r.getBoundsInParent();
                top.setEndX(rectangleInParent.getMinX() + 5); top.setStartY(rectangleInParent.getMaxY() - 5); top.setEndY(rectangleInParent.getMaxY() - 5);
                bottom.setEndX(rectangleInParent.getMinX() + 5);
                right.setStartX(rectangleInParent.getMinX() + 5); right.setEndX(rectangleInParent.getMinX() + 5); right.setStartY(rectangleInParent.getMaxY());
                left.setStartY(rectangleInParent.getMaxY());

            } else if(r == botL) {
                if(botR.getBoundsInParent().getMinX() - 15 >= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    topL.setTranslateX(newX);
                }
                
                if(topL.getBoundsInParent().getMinY() + 15 <= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    botR.setTranslateY(newY);
                } 

                Bounds rectangleInParent = r.getBoundsInParent();
                top.setStartX(rectangleInParent.getMinX() + 5);
                bottom.setStartX(rectangleInParent.getMinX() + 5); bottom.setEndY(rectangleInParent.getMinY() + 5); bottom.setStartY(rectangleInParent.getMinY() + 5);
                left.setStartX(rectangleInParent.getMinX() + 5); left.setEndX(rectangleInParent.getMinX() + 5); left.setEndY(rectangleInParent.getMaxY());
                right.setEndY(rectangleInParent.getMaxY());

            } else if(r == botR) {
                if(botL.getBoundsInParent().getMinX() + 15 <= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    topR.setTranslateX(newX);
                }
                
                if(topR.getBoundsInParent().getMinY() + 20 <= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    botL.setTranslateY(newY);
                } 

                Bounds rectangleInParent = r.getBoundsInParent();
                top.setEndX(rectangleInParent.getMinX() + 5);
                bottom.setEndX(rectangleInParent.getMinX() + 5); bottom.setStartY(rectangleInParent.getMinY() + 5); bottom.setEndY(rectangleInParent.getMinY() + 5);
                right.setStartX(rectangleInParent.getMinX() + 5); right.setEndX(rectangleInParent.getMinX() + 5); right.setEndY(rectangleInParent.getMaxY());
                left.setEndY(rectangleInParent.getMaxY());
                
            }
            
            startX = topL.getBoundsInParent().getMinX() + 5; lastX = topR.getBoundsInParent().getMinX() + 5;
            startY = topL.getBoundsInParent().getMinY() + 5; lastY = botR.getBoundsInParent().getMinY() + 5; 
            wh = lastX - startX;
            hg = lastY - startY;

            if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Rectangle")) {
                Rectangle temp = (Rectangle) currentNode.getChildren().get(0);
                temp.setHeight(hg); temp.setWidth(wh);
                temp.setX(startX); temp.setY(startY);
                currentNode.getChildren().set(0, temp);

            } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Ellipse")){
                Ellipse temp = (Ellipse) currentNode.getChildren().get(0);
                temp.setRadiusX(wh/2); temp.setRadiusY(hg/2);
                temp.setCenterX((startX + lastX)/ 2); temp.setCenterY((startY + lastY)/2);
                currentNode.getChildren().set(0, temp);

            } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.image.ImageView")) {
                ImageView temp = (ImageView) currentNode.getChildren().get(0);
                temp.setFitHeight(hg); temp.setFitWidth(wh);
                temp.relocate(startX, startY);
                currentNode.getChildren().set(0, temp);

            } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.image.ImageView")) {
                Label temp = (Label) currentNode.getChildren().get(0);
                temp.setPrefHeight(hg); temp.setPrefWidth(wh);
            }

            Circle tempCirc = (Circle) currentNode.getChildren().get(currentNode.getChildren().size() - 1);
            tempCirc.setCenterX((lastX + startX) / 2);
            tempCirc.setCenterY(startY);
        });

        return r;
    }

    // Helper function to help build the rotation handler
    private Circle rotationHandler(Group node) {
        Rotate rotate = new Rotate();
        Bounds nodeBounds = node.getBoundsInParent();
        node.getTransforms().add(rotate);
        rotate.setPivotX((nodeBounds.getMinX() + nodeBounds.getMaxX() + 10)/2);
        rotate.setPivotY(nodeBounds.getMinY() + 5);

        Circle handler = new Circle(5);
        handler.setCenterX((nodeBounds.getMinX() + nodeBounds.getMaxX() + 10)/2);
        handler.setCenterY(nodeBounds.getMinY() + 5);
        handler.setFill(Color.LIGHTGRAY);
        handler.setStroke(Color.rgb(0,0,0,0.75));
        handler.setCursor(Cursor.HAND);
        
        // Initialize originx and originy on being clicked
        handler.setOnMousePressed(e -> {
            originX = e.getSceneX() - handler.getTranslateX();
            originY = e.getSceneY() - handler.getTranslateY();
        });

        // When it's dragged rotate the box
        handler.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {

                // Used to get the scene position of the corner of the box
                Transform localToScene = node.getLocalToParentTransform();

                double x1 = originX, y1 = originY;

                double x2 = e.getSceneX();
                double y2 = e.getSceneY();

                double px = rotate.getPivotX() + localToScene.getTx();
                double py = rotate.getPivotY() + localToScene.getTy();

                // Work out the angle rotated
                double th1 = clockAngle(x1, y1, px, py);
                double th2 = clockAngle(x2, y2, px, py);

                double angle = rotate.getAngle();

                angle += th2 - th1;

                // Rotate the rectangle
                rotate.setAngle(angle);

                originX = e.getSceneX() - handler.getTranslateX();
                originY = e.getSceneY() - handler.getTranslateY();
            }
        });

        return handler;
    }

    // Helper function to calculate angle from 0 - 360 degrees
    public double clockAngle (double x, double y, double px, double py) {
        double dx = x - px;
        double dy = y - py;

        // Calculate the angle
        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        // In case of dy being 0
        if(dy < 0) {
            angle = 360 - angle;
        }

        return angle;
    }

    // Helper function that draws the circle based on the area selected
    private void drawOval() {
        // Calculates radiusx and radiusy of circle
        this.wh = (lastX - startX) / 2;
        this.hg = (lastY - startY) / 2;

        // Calculates the centerx and centery of circle (for placement)
        double centerX = (startX + lastX) / 2, centerY = (startY + lastY) / 2 ;

        // Initializes new circle
        Ellipse newOval = new Ellipse(centerX, centerY, wh, hg);
        newOval.setFill(Color.color(Math.random(), Math.random(), Math.random()));

        // Create the node group with the controls for resizing and rotation
        Group newGroup = createNodeGroup(newOval);

        // Adds new circle to children list of canvaspane
        canvasPane.getChildren().add(newGroup);
    }

    // Helper function that draws the rectangle based on the area selected
    private void drawRect() {
        // Calculates width and height of rectangle
        this.wh = lastX - startX;
        this.hg = lastY - startY;

        // Initializes new rectangle
        Rectangle newRect = new Rectangle(startX, startY, this.wh, this.hg);
        newRect.setFill(Color.color(Math.random(), Math.random(), Math.random()));

        // Create the node group with the controls for resizing and rotation
        Group newGroup = createNodeGroup(newRect);
        
        // Adds new rectangle (and its handlers) to children list of canvaspane
        canvasPane.getChildren().add(newGroup);
    }

    // Helper funtion that places a textbox on clicked area
    private void drawTextBox() {
        // Initialzes new textbox
        Label textBox = new Label("text");
        textBox.setTextFill(Color.BLACK);
        textBox.setStyle("-fx-border-color: black");
        textBox.toFront();

        // Moves new textbox to clicked location
        textBox.relocate(this.startX, this.startY);

        // Adds new textbox to children list of canvaspane
        canvasPane.getChildren().addAll(textBox);
    }

    // Helper function that places an image on clicked area
    private void drawImage() {
        // Initializes imageview
        ImageView img = new ImageView();

        // Calls helper function to set the imageview as selected image
        ControllerHelper.imageChooser(this.currentSession, img);
        
        // Moves new image to clicked location
        img.setFitHeight(this.hg);
        img.setFitWidth(this.wh);
        img.relocate(this.startX, this.startY);

        // Create the node group with the controls for resizing and rotation
        Group newGroup = createNodeGroup(img);

        // Adds new imageview (and its handlers) to children list of canvaspane
        canvasPane.getChildren().addAll(newGroup);

        // Sets drawimage to false so the user can click on other elements without turning the brush off
        this.drawImage = false;
    }

    // Helper function that displays an effect of the circle being drawn on fx canvas (invisible layer on top of canvaspane)
    private void drawOvalEffect() {
        // Calculates width and height of circle
        this.wh = lastX - startX;
        this.hg = lastY - startY;

        // Sets line width of graphicscontext
        gcF.setLineWidth(10);

        // Clears graphiccontext
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);

        // Start drawing the circle effect
        gcF.setFill(Color.AQUA);
        gcF.fillOval(startX, startY, this.wh, this.hg);
       }

    private void drawRectEffect() {
        // Calculates width and height of rectangle
        this.wh = lastX - startX;
        this.hg = lastY - startY;

        // Sets line width of graphicscontext
        gcF.setLineWidth(10);

        // Clears graphiccontext
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);

        // Start drawing rectangle effect
        gcF.setFill(Color.AQUA);
        gcF.fillRect(startX, startY, this.wh, this.hg);
    }

    // Helper function to initialize node group of the node + its resize rotate handlers
    private Group createNodeGroup(Node node) {
        Group newGroup = new Group();

        canvasPane.getChildren().removeAll(top, bottom, left, right, topL, botL, topR, botR, rotCircle);
        Bounds nodeBounds = node.getBoundsInParent();
        bottom = buildLine(nodeBounds.getMinX(), nodeBounds.getMaxX(), nodeBounds.getMaxY(), nodeBounds.getMaxY());
        top = buildLine(nodeBounds.getMinX(), nodeBounds.getMaxX(), nodeBounds.getMinY(), nodeBounds.getMinY());
        left = buildLine(nodeBounds.getMinX(), nodeBounds.getMinX(), nodeBounds.getMinY(), nodeBounds.getMaxY());
        right = buildLine(nodeBounds.getMaxX(), nodeBounds.getMaxX(), nodeBounds.getMinY(), nodeBounds.getMaxY());

        botL = buildCorner(nodeBounds.getMinX() - 5, nodeBounds.getMaxY() - 5);
        botR = buildCorner(nodeBounds.getMaxX() - 5, nodeBounds.getMaxY() - 5);
        topL = buildCorner(nodeBounds.getMinX() - 5, nodeBounds.getMinY() - 5);
        topR = buildCorner(nodeBounds.getMaxX() - 5, nodeBounds.getMinY() - 5);

        newGroup.getChildren().addAll(node, bottom, top, left, right, botL, botR, topL, topR);
        rotCircle = rotationHandler(newGroup);
        newGroup.getChildren().add(rotCircle);
        makeDraggable(newGroup);
        setCurrentNode(newGroup);

        return newGroup;
    }
}
