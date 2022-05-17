package com.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.application.Main;
import com.models.User;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class ControllerHelper {
    
    // Helper function to load specific views
    public static void switchViews(String fxml, String title, Stage stage) {
        try {
            // Loads FXML file for provided view
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();

            // Setting stage properties
            stage.setScene(new Scene(root));
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    // Helper function to load sign up page and initialize its view
    public static void initStage(String fxml, String title, Stage stage, Image image, User currentUser, String type) {
        try {
            // Loads FXML file for the sign up page
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();

            if(fxml.toLowerCase().equals("signup")) {
                // Initializes sign up page 
                SignUpController signUp = loader.getController();
                signUp.initializeImg(image);   

            } else if(fxml.toLowerCase().equals("profileedit")) {
                ProfileEditorController profileEdit = loader.getController();
                profileEdit.initializeSession(image, currentUser, stage);
            } else if(fxml.toLowerCase().equals("newcanvas")) {
                NewCanvasController newCanvas = loader.getController();
                newCanvas.initializeSession(currentUser, stage);
            }

            if(type.equals("popup")) {
                // Setting popup stage properties
                Stage popUp = new Stage();
                popUp.initModality(Modality.APPLICATION_MODAL);
                popUp.setTitle(title);
                popUp.setResizable(false);
                popUp.setScene(new Scene(root));
                popUp.sizeToScene();
                popUp.show();
            } else if(type.equals("main")) {
                // Setting stage properties
                stage.setScene(new Scene(root));
                stage.sizeToScene();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    // Helper function to load canvas page and initialize its view
    public static void initStage(String title, User user, Stage stage, int canvasHeight, int canvasWidth, boolean canvasActive) {
        try {
            //Loads FXML file for the canvas page
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Board.fxml"));
            Parent root = loader.load();

            // Initializes main canvas UI with the properties
            BoardController board = loader.getController();
            board.initializeSession(user, stage, canvasHeight, canvasWidth, canvasActive);

            // Setting stage properties
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String imageChooser(Label label, ImageView imageView) {
        Stage stage = (Stage) label.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        

        ExtensionFilter filter = new FileChooser.ExtensionFilter("ALL", "*.png", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(stage);
        String filepath = selectedFile.getAbsolutePath();
        
        InputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(selectedFile);
            imageView.setImage(new Image(fileInputStream));

            return filepath;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return new File("default.jpg").getAbsolutePath();
    }

    public static void errorMessage(String error, Stage primaryStage) {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ErrorPopup.fxml"));
            Parent root = loader.load();

            ErrorController errorCon = loader.getController();
            errorCon.setErrorMsg(error);

            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
