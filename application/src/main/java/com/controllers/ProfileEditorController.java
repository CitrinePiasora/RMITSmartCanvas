package com.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import com.dao.sqliteConnection;
import com.models.User;
import com.controllers.Helpers.ControllerHelper;

public class ProfileEditorController implements Initializable {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ImageView profile_picture;
    @FXML private Label usersName;
    @FXML private Rectangle pfpBorder;

    // Initialize session variables
    private User currentUser;
    private Stage currentSession;

    // Initialize filepath as blank
    private String filepath = "";

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    // Initializes current session with user credentials
    public void initializeSession(Image image, User user, Stage currentSession) {
        profile_picture.setImage(image);   
        this.currentUser = user; 
        this.currentSession = currentSession;

        usersName.setText(user.getUsername());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
    }

    @FXML // FX Function, when select profile picture label is clicked
    void chooseImage(MouseEvent event) {
        // Sets current filepath using the imagechooser helper function
        this.filepath = ControllerHelper.imageChooser(this.currentSession, profile_picture);
    }

    @FXML // FX Function, when ok button is pressed...
    void editProfile(ActionEvent event) {
        // Creates a stage object for current stage
        Stage thisStage = (Stage) usersName.getScene().getWindow();

        if(firstNameField.getText().equals("") || lastNameField.getText().equals("")) {
            // Shows error popup saying that required fields are empty
            ControllerHelper.initStage("ErrorPopup", "Please fill in all fields", thisStage, null, null, "popup");
        } else {
            // Sets current user variables to updated parameters
            this.currentUser.setFirstName(firstNameField.getText());
            this.currentUser.setLastName(lastNameField.getText());
            this.currentUser.setPFP(filepath);

            boolean success = sqliteConnection.sqliteConnectEditData(this.filepath, firstNameField.getText(), lastNameField.getText(), this.currentUser);
            if(success) {
                // Shows popup saying update has been completed
                ControllerHelper.initStage("ErrorPopup", "Your profile has been updated", thisStage, null, null, "popup");
                // Updates the main stage to reflect changes
                ControllerHelper.initStage("Canvas", this.currentUser, this.currentSession, 500, 500, false);
            } else {
                // Shows error popup saying some form of error has occured
                ControllerHelper.initStage("ErrorPopup", "An error occured, please try again", thisStage, null, null, "popup");
            }
        }
    }
}
