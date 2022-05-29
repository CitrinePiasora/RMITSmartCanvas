package com.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;

import com.application.UserManagement;
import com.controllers.Helpers.ControllerHelper;
import com.dao.sqliteConnection;
import com.models.User;

public class SignUpController implements Initializable {
    
    @FXML private Label createdMsg;
    @FXML private ImageView profile_picture;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    @FXML private Rectangle pfpBorder;

    // initialize filepath variables as default profile picture filepath
    private String filepath = new File("default.jpg").getAbsolutePath();

    // initialize session variables
    private User currentUser = null;

    // Initialize controllerhelper
    private ControllerHelper controlHelper;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
    }

    // Initialization function for setting default profile image
    public void initializeImg(Image image, Stage stage) {
        this.profile_picture.setImage(image);
        this.controlHelper = new ControllerHelper(stage);
    }
    
    
    @FXML // FX Function, closes the signup page, if a user was made initializes canvas page, otherwise return to login page
    private void closeButton() throws IOException {
        if(this.currentUser != null) {
            controlHelper.initStage("Canvas", currentUser, 500, 500, false);
        } else {
            controlHelper.switchViews("Login", "welcome to SmartCanvas!");
        }
    }

    @FXML // FX Function that invokes imageChooser helper function to set profile picture
    void chooseImage(MouseEvent event) {
        this.filepath = controlHelper.imageChooser(profile_picture);
    }
    
    @FXML // FX Function that creates the user
    private void createUser() throws IOException {
        // Creates a buffered image to be stored in user object
        BufferedImage image = ImageIO.read(new File(filepath));

        // Calls the write to databse function from sqliteConnection
        boolean success = sqliteConnection.sqliteConnectWriteData(this.usernameField.getText(),
            this.firstNameField.getText(), this.lastNameField.getText(), UserManagement.hashingAlg(this.passwordField.getText()),
             filepath);

        // If the sql query was successful
        if(success) {
            // Create new user object
            this.currentUser = new User(this.usernameField.getText(), this.firstNameField.getText(), this.lastNameField.getText(),
            UserManagement.hashingAlg(this.passwordField.getText()), image);
            
            // Show that a user has been created
            createdMsg.setVisible(true);
            createdMsg.setText("User " + this.usernameField.getText() + " created");

        // If any of the required fields (all of em) are empty
        } else if(this.usernameField.getText().equals("") || this.firstNameField.getText().equals("") || this.lastNameField.getText().equals("") || this.passwordField.getText().equals("")){
            // Show error popup telling user to fill all fields
            controlHelper.initStage("ErrorPopup", "Please fill in all fields", null, null, "popup");
        } else {
            // Show error popup telling user that user already exists
            controlHelper.initStage("ErrorPopup", "User " + usernameField.getText() + " already exists", null, null, "popup");
        }
        
        
    }

}