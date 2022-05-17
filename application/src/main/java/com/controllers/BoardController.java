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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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

import com.application.Main;
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
    private GraphicsContext gcF;
    private double percentageCalc = 1.0;
    private int canvasH, canvasW;
    private boolean drawoval = false, drawrectangle = false, drawtext = false;
    double startX, startY, lastX, lastY, originX, originY;
    double wh, hg;

    private Node currentNode;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    public void initializeSession(User user, int canvasHeight, int canvasWidth, boolean canvasActive) {
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
        

        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                double sliderValue = (double) zoomSlider.getValue();

                // logic that calculates the current zoom %
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

                // code that resizes elements based on the calculated zoom %
                ObservableList<Node> canvasElements = zoomPane.getChildren();
                for(Node element : canvasElements) {
                    element.setScaleX(percentageCalc);
                    element.setScaleY(percentageCalc);
                }
            }
            
        });
    }

    @FXML
    public void profilePopUp(ActionEvent event) {
        try {
            Stage popUp = new Stage();
            Stage currentStage = (Stage) profilePicture.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProfileEdit.fxml"));
            Parent root = loader.load();

            ProfileEditorController profileEdit = loader.getController();
            profileEdit.initializeSession(profilePicture.getImage(), this.currentUser, currentStage);

            popUp.initModality(Modality.APPLICATION_MODAL);
            popUp.setTitle("Profile Management");
            popUp.setResizable(false);
            popUp.setScene(new Scene(root));
            popUp.sizeToScene();

            popUp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    @FXML
    void NewCanvas(ActionEvent event) {
        try {
            Stage popUp = new Stage();
            Stage currentStage = (Stage) profilePicture.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("NewCanvas.fxml"));
            Parent root = loader.load();

            NewCanvasController newCanvas = loader.getController();
            newCanvas.initializeSession(this.currentUser, currentStage);

            popUp.initModality(Modality.APPLICATION_MODAL);
            popUp.setTitle("New Canvas");
            popUp.setResizable(false);
            popUp.setScene(new Scene(root));

            popUp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Stage stage = (Stage) nameLabel.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG files (*.png)", "*.png"));
        
        File file = fileChooser.showSaveDialog(stage);

        if(file != null) {
            try {
                ObservableList<Node> canvasElements = canvasPane.getChildren();

                canvasPane.setScaleX(1);
                canvasPane.setScaleY(1);
                for(Node element : canvasElements) {
                    element.setScaleX(1);
                    element.setScaleY(1);
                }

                WritableImage exporting = new WritableImage(canvasW, canvasH);
                canvasPane.snapshot(null, exporting);
                RenderedImage exportedFile = SwingFXUtils.fromFXImage(exporting, null);
                ImageIO.write(exportedFile, "png", file);


                canvasPane.setScaleX(percentageCalc);
                canvasPane.setScaleY(percentageCalc);
                for(Node element : canvasElements) {
                    element.setScaleX(percentageCalc);
                    element.setScaleY(percentageCalc);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void circleBrush(ActionEvent event) {
        drawoval = true;
        drawrectangle = false;
        drawtext = false;
    }
    
    @FXML
    void rectBrush(ActionEvent event) {
        drawoval = false;
        drawrectangle = true;
        drawtext = false;
    }

    @FXML
    void insertTextBox(ActionEvent event) {
        drawoval = false;
        drawrectangle = false;
        drawtext = true;
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
