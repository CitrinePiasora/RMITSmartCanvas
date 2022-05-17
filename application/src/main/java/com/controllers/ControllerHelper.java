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
    
    public static void switchViews(String fxml, String title, Label label, int height, int width) {
        Stage stage = (Stage) label.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();

            // stage.setHeight(height);
            // stage.setWidth(width);
            // stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void initStage(String title, Label label, int height, int width, Image image) {
        Stage stage = (Stage) label.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("SignUp.fxml"));
            Parent root = loader.load();

            SignUpController signUp = loader.getController();
            signUp.initializeImg(image);
            
            // stage.setTitle(title);
            // stage.setHeight(height);
            // stage.setWidth(width);
            stage.setScene(new Scene(root));
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void initStage(String title, int height, int width, User user, Stage stage, int canvasHeight, int canvasWidth, boolean canvasActive) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Board.fxml"));
            Parent root = loader.load();

            BoardController board = loader.getController();
            board.initializeSession(user, canvasHeight, canvasWidth, canvasActive);

            stage.setTitle(title);
            stage.setHeight(height);
            stage.setWidth(width);
            stage.setScene(new Scene(root));
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
