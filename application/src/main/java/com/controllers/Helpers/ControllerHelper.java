package com.controllers.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import com.application.Main;
import com.controllers.BoardController;
import com.controllers.ErrorController;
import com.controllers.NewCanvasController;
import com.controllers.ProfileEditorController;
import com.controllers.SignUpController;
import com.models.User;

public class ControllerHelper {
    
    private static Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    // Helper function to load specific views
    public static void switchViews(String fxml, String title, Stage stage) {
        try {
            // Loads FXML file for provided view
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();

            // Setting stage properties
            stage.setScene(new Scene(root));
            stage.sizeToScene();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth()  - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
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
                signUp.initializeImg(image, stage);   

            } else if(fxml.toLowerCase().equals("profileedit")) {
                ProfileEditorController profileEdit = loader.getController();
                profileEdit.initializeSession(image, currentUser, stage);
            } else if(fxml.toLowerCase().equals("newcanvas")) {
                NewCanvasController newCanvas = loader.getController();
                newCanvas.initializeSession(currentUser, stage);
            } else if(fxml.toLowerCase().equals("errorpopup")) {
                ErrorController errorCon = loader.getController();
                errorCon.setErrorMsg(title);
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
                stage.setX((screenBounds.getWidth()  - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
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
            stage.setX((screenBounds.getWidth()  - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper function that creates a filechooser window to load in image files
    public static String imageChooser(Stage stage, ImageView imageView) {
        // Initialize filechooser window with jpg and png extensions only
        FileChooser fileChooser = new FileChooser();
        ExtensionFilter filter = new FileChooser.ExtensionFilter("ALL", "*.png", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);

        // Gets file and filepath from file selected within filechooser
        File selectedFile = fileChooser.showOpenDialog(stage);
        String filepath = selectedFile.getAbsolutePath();
        
        // Initialize inputstream
        InputStream fileInputStream;

        try {
            // Processes selected file and sets the image of the given imageview with the image
            fileInputStream = new FileInputStream(selectedFile);
            imageView.setImage(new Image(fileInputStream));

            return filepath;
        } catch(IOException e) {
            e.printStackTrace();
            // Returns default image path should any of the steps fail
            return new File("default.jpg").getAbsolutePath();
        }
    }
}
