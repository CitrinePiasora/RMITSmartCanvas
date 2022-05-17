package com.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.dao.sqliteConnection;
import com.models.User;

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

public class ProfileEditorController implements Initializable {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ImageView profile_picture;
    @FXML private Label usersName;
    @FXML private Rectangle pfpBorder;

    private User currentUser;
    private Stage currentSession;
    private String filepath = "";

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    public void initializeSession(Image image, User user, Stage currentSession) {
        profile_picture.setImage(image);   
        this.currentUser = user; 
        this.currentSession = currentSession;

        usersName.setText(user.getUsername());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
    }

    @FXML
    void chooseImage(MouseEvent event) {
        this.filepath = ControllerHelper.imageChooser(usersName, profile_picture);
    }

    @FXML
    void editProfile(ActionEvent event) {
        this.currentUser.setFirstName(firstNameField.getText());
        this.currentUser.setLastName(lastNameField.getText());
        this.currentUser.setPFP(filepath);

        boolean success = sqliteConnection.sqliteConnectEditData(this.filepath, firstNameField.getText(), lastNameField.getText(), this.currentUser);
            Stage thisStage = (Stage) usersName.getScene().getWindow();
        if(success) {
            ControllerHelper.errorMessage("Your profile has been updated", thisStage);
            ControllerHelper.initStage("Canvas", 720, 1000, this.currentUser, this.currentSession, 500, 500, false);
        } else {
            ControllerHelper.errorMessage("An error occured, please try again", thisStage);
        }
        
    }
}
