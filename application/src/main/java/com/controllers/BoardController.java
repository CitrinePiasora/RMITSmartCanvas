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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import java.awt.image.RenderedImage;

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

    private User currentUser;
    private Stage currentSession;
    private GraphicsContext gcF;
    private double percentageCalc = 1.0;
    private int canvasH, canvasW;
    private boolean drawoval = false, drawrectangle = false, drawtext = false, drawImage = false;
    double startX, startY, lastX, lastY, originX, originY;
    double wh, hg;

    private Node currentNode;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    public void initializeSession(User user, Stage currentSession, int canvasHeight, int canvasWidth, boolean canvasActive) {
        profilePicture.setImage(user.getImage());
        nameLabel.setText(user.getFullName());
        
        // Initialize Canvas Properties
        this.canvasH = canvasHeight;
        this.canvasW = canvasWidth;
        this.canvasPane.maxHeight(canvasHeight);
        this.canvasPane.maxWidth(canvasWidth);
        this.canvasPane.setMinHeight(canvasHeight);
        this.canvasPane.setMinWidth(canvasWidth);
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
        Stage currentStage = (Stage) profilePicture.getScene().getWindow();
        ControllerHelper.initStage("ProfileEdit", "Profile Management", currentStage, profilePicture.getImage(), currentUser, "popup");
    }


    @FXML // FX Function, when new canvas is pressed...
    void NewCanvas(ActionEvent event) {
        Stage currentStage = (Stage) profilePicture.getScene().getWindow();
        ControllerHelper.initStage("NewCanvas", "Canvas Properties", currentStage, profilePicture.getImage(), currentUser, "popup");
    }

    @FXML
    void clearCanvas(ActionEvent event) {
        canvasPane.getChildren().clear();
    }

    @FXML
    void deleteElement(ActionEvent event) {
        canvasPane.getChildren().remove(this.currentNode);
    }

    @FXML
    void saveImage(ActionEvent event) {
        

        // initialize filechooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG files (*.png)", "*.png"));
        
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

    @FXML
    void logOut() {
        ControllerHelper.switchViews("Login", "Welcome to SmartCanvas", this.currentSession);
    }

    @FXML
    void circleBrush(ActionEvent event) {
        if(drawoval) {
            drawoval = false;
        } else {
            drawoval = true;
            drawrectangle = false;
            drawtext = false;
            drawImage = false;
        }
    }
    
    @FXML
    void rectBrush(ActionEvent event) {
        if(drawrectangle) {
            drawrectangle = false;
        } else {
            drawoval = false;
            drawrectangle = true;
            drawtext = false;
            drawImage = false;
        }
    }

    @FXML
    void insertTextBox(ActionEvent event) {
        if(drawtext) {
            drawtext = false;
        } else {
            drawoval = false;
            drawrectangle = false;
            drawtext = true;
            drawImage = false;
        }
    }

    @FXML
    private void addImage() {
        if(drawImage) {
            drawImage = false;
        } else {
            drawoval = false;
            drawrectangle = false;
            drawtext = false;
            drawImage = true;
        }
    }
    
    @FXML
    void canvasColor(ActionEvent Event) {
        canvasPane.setStyle("-fx-background-color: pink; -fx-border-color: gray");
    }

    @FXML
    private void onMousePressedListener(MouseEvent e) {
        this.startX = e.getX();
        this.startY = e.getY();

        if(drawtext) {
            drawtextBox();
        } else if(drawImage) {
            drawImage();
        }
    }

    @FXML
    private void FXMousePress(MouseEvent e) {
        this.startX = e.getX();
        this.startY = e.getY();
    }

    @FXML
    private void FXMouseDrag(MouseEvent e) {
        this.lastX = e.getX();
        this.lastY = e.getY();

        if(drawrectangle) {
            drawRectEffect();
        } else if(drawoval) {
            drawOvalEffect();
        }
    }

    @FXML
    private void FXMouseRelease(MouseEvent e) {
        if(drawrectangle) {
            drawRect();
        } else if(drawoval) {
            drawOval();
        }
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
    }

    @FXML
    private void onMouseExitedListener(MouseEvent event) {
        System.out.println("Out of canvas bounds");
    }

    private void setCurrentNode(Node node) {
        this.currentNode = node;
    }

    private void makeDraggable(Node node) {
        node.setOnMousePressed(e -> {
            originX = e.getSceneX() - node.getTranslateX();
            originY = e.getSceneY() - node.getTranslateY();

            setCurrentNode(node);
        });

        node.setOnMouseDragged(e -> {
            node.setTranslateX(e.getSceneX() - originX);
            node.setTranslateY(e.getSceneY() - originY);
        });
    }
    
    private void drawOval() {
        this.wh = (lastX - startX) / 2;
        this.hg = (lastY - startY) / 2;

        double centerX = (startX + lastX) / 2, centerY = (startY + lastY) / 2 ;

        Ellipse newOval = new Ellipse(centerX, centerY, wh, hg);
        newOval.setFill(Color.AQUAMARINE);
        newOval.toFront();
        makeDraggable(newOval);

        canvasPane.getChildren().add(newOval);
    }

    private void drawRect() {
        this.wh = lastX - startX;
        this.hg = lastY - startY;
        Rectangle newRect = new Rectangle(startX, startY, this.wh, this.hg);
        newRect.setFill(Color.BLACK);
        newRect.toFront();
        makeDraggable(newRect);

        canvasPane.getChildren().add(newRect);
    }

    private void drawtextBox() {
        Label textBox = new Label("text");
        textBox.setTextFill(Color.BLACK);
        textBox.setStyle("-fx-border-color: black");
        textBox.toFront();

        makeDraggable(textBox);

        textBox.relocate(this.startX, this.startY);
        canvasPane.getChildren().addAll(textBox);
    }

    private void drawImage() {
        ImageView img = new ImageView();
        ControllerHelper.imageChooser(nameLabel, img);
        img.toFront();

        makeDraggable(img);

        img.relocate(this.startX, this.startY);
        canvasPane.getChildren().addAll(img);
    }

    private void drawOvalEffect() {
        this.wh = lastX - startX;
        this.hg = lastY - startY;
        gcF.setLineWidth(10);

        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
        gcF.setFill(Color.AQUA);
        gcF.fillOval(startX, startY, this.wh, this.hg);
       }

    private void drawRectEffect() {
        this.wh = lastX - startX;
        this.hg = lastY - startY;
        gcF.setLineWidth(10);

        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
        gcF.setFill(Color.AQUA);
        gcF.fillRect(startX, startY, this.wh, this.hg);
    }

}
