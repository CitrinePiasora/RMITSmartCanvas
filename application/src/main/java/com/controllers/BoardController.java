package com.controllers;

import java.awt.*;
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
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import com.application.Main;
import com.models.User;

public class BoardController implements Initializable {

    @FXML private ImageView profilePicture;
    @FXML private Label nameLabel;
    @FXML private Canvas canvas;
    @FXML private Canvas FXCanvas;
    @FXML private Rectangle canvasBackground;
    @FXML private Slider zoomSlider;
    @FXML private Label zoomPercent;
    @FXML private MenuItem saveButton;
    @FXML private MenuItem clearButton;
    @FXML private StackPane zoomPane;
    @FXML private BorderPane borderPain;
    
    private User currentUser;
    private double percentageCalc;
    private int canvasH, canvasW;
    private GraphicsContext gcB, gcF;
    private boolean drawoval = false, drawrectangle = false, drawtext = false;
    double startX, startY, lastX, lastY, originX, originY;
    double wh, hg;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    public void initializeSession(User user, int canvasHeight, int canvasWidth, boolean canvasActive) {
        profilePicture.setImage(user.getImage());
        nameLabel.setText(user.getFullName());
        
        // Initialize Canvas Properties
        this.canvas.setHeight(canvasHeight);
        this.canvas.setWidth(canvasWidth);
        this.FXCanvas.setHeight(canvasHeight);
        this.FXCanvas.setWidth(canvasWidth);
        this.canvasBackground.setHeight(canvasHeight);
        this.canvasBackground.setWidth(canvasWidth);
        this.canvasBackground.setVisible(canvasActive);
        this.canvasH = canvasHeight;
        this.canvasW = canvasWidth;
        this.gcB = canvas.getGraphicsContext2D();
        this.gcF = FXCanvas.getGraphicsContext2D();

        // Initialize state of menu items
        this.clearButton.setDisable(!canvasActive);
        this.saveButton.setDisable(!canvasActive);

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
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
        gcB.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    void deleteElement(ActionEvent event) {

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
                ObservableList<Node> canvasElements = zoomPane.getChildren();

                for(Node element : canvasElements) {
                    element.setScaleX(1);
                    element.setScaleY(1);
                }

                // WritableImage exporting = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                // canvas.snapshot(null, exporting);
                // RenderedImage exportedFile = SwingFXUtils.fromFXImage(exporting, null);
                // ImageIO.write(exportedFile, "png", file);

                WritableImage exporting = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                borderPain.getCenter().snapshot(null, exporting);
                RenderedImage exportedFile = SwingFXUtils.fromFXImage(exporting, null);
                ImageIO.write(exportedFile, "png", file);

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
    private void onMousePressedListener(MouseEvent e) {
        this.startX = e.getX();
        this.startY = e.getY();

        if(drawtext) {
            drawtextBox();
        }
    }

    @FXML
    private void onMouseDraggedListener(MouseEvent e) {
        this.lastX = e.getX();
        this.lastY = e.getY();

        if(drawrectangle) {
            drawRectEffect();
        } else if(drawoval) {
            drawOvalEffect();
        }
    }

    @FXML
    private void onMouseReleaseListener(MouseEvent e) {
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

    private void drawOval() {
        gcB.setLineWidth(10);

        gcB.setFill(Color.BLACK);
        gcB.fillOval(startX, startY, this.wh, this.hg);
    }

    private void drawRect() {
        gcB.setLineWidth(10);

        gcB.setFill(Color.GRAY);
        gcB.fillRect(startX, startY, this.wh, this.hg);
    }

    private void drawtextBox() {
        Label textBox = new Label("text");
        textBox.setTextFill(Color.BLACK);
        textBox.setStyle("-fx-border-color: black");
        textBox.toFront();

        textBox.setOnMousePressed(e -> {
            originX = e.getSceneX() - textBox.getTranslateX();
            originY = e.getSceneY() - textBox.getTranslateY();

            textBox.toFront();
        });

        textBox.setOnMouseDragged(e -> {
            textBox.setTranslateX(e.getSceneX() - originX);
            textBox.setTranslateY(e.getSceneY() - originY);

            textBox.setText(textBox.getTranslateX() + " " + textBox.getTranslateY());
            textBox.toFront();
        });

        textBox.relocate(this.startX, this.startY);
        this.drawtext = false;

        zoomPane.getChildren().addAll(textBox);
    }

    private void drawOvalEffect() {
        this.wh = lastX - startX;
        this.hg = lastY - startY;
        gcF.setLineWidth(10);

        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
        gcF.setFill(Color.BLACK);
        gcF.fillOval(startX, startY, this.wh, this.hg);
       }

    private void drawRectEffect() {
        this.wh = lastX - startX;
        this.hg = lastY - startY;
        gcF.setLineWidth(10);

        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
        gcF.setFill(Color.GRAY);
        gcF.fillRect(startX, startY, this.wh, this.hg);
    }

}
