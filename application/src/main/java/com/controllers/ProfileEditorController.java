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

    // Initialize filepath as blank
    private String filepath = "";

    // Initialize controllerhelper
    private ControllerHelper controlHelper;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    // Initializes current session with user credentials
    public void initializeSession(Image image, User user, Stage currentSession) {
        profile_picture.setImage(image);   
        this.currentUser = user; 
        controlHelper = new ControllerHelper(currentSession);

        usersName.setText(user.getUsername());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
    }

    @FXML // FX Function, when select profile picture label is clicked
    void chooseImage(MouseEvent event) {
        // Sets current filepath using the imagechooser helper function
        this.filepath = controlHelper.imageChooser(profile_picture);
    }

    @FXML // FX Function, when ok button is pressed...
    void editProfile(ActionEvent event) {
        if(firstNameField.getText().equals("") || lastNameField.getText().equals("")) {
            // Shows error popup saying that required fields are empty
            controlHelper.initStage("ErrorPopup", "Please fill in all fields", null, null, "popup");
        } else {
            // Sets current user variables to updated parameters
            this.currentUser.setFirstName(firstNameField.getText());
            this.currentUser.setLastName(lastNameField.getText());
            this.currentUser.setPFP(filepath);

            boolean success = sqliteConnection.sqliteConnectEditData(this.filepath, firstNameField.getText(), lastNameField.getText(), this.currentUser);
            if(success) {
                // Shows popup saying update has been completed
                controlHelper.initStage("ErrorPopup", "Your profile has been updated", null, null, "popup");
                // Updates the main stage to reflect changes
                controlHelper.initStage("Canvas", this.currentUser, 500, 500, false);
            } else {
                // Shows error popup saying some form of error has occured
                controlHelper.initStage("ErrorPopup", "An error occured, please try again", null, null, "popup");
            }
        }
    }
}
