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

    private String filepath = new File("default.jpg").getAbsolutePath();
    private User currentUser = null;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
    }

    public void initializeImg(Image image) {
        profile_picture.setImage(image);
    }
    
    
    @FXML
    private void closeButton() throws IOException {
        if(this.currentUser != null) {
            Stage stage = (Stage) createdMsg.getScene().getWindow();
            ControllerHelper.initStage("Canvas", 720, 1000, currentUser, stage, 500, 500, false);
        } else {
            ControllerHelper.switchViews("Login", "welcome to SmartCanvas!", createdMsg, 295, 531);
        }
    }

    @FXML
    void chooseImage(MouseEvent event) {
        this.filepath = ControllerHelper.imageChooser(createdMsg, profile_picture);
    }
    
    @FXML
    private void createUser() throws IOException {
        BufferedImage image = ImageIO.read(new File(filepath));

        boolean success = sqliteConnection.sqliteConnectWriteData(this.usernameField.getText(),
            this.firstNameField.getText(), this.lastNameField.getText(), UserManagement.hashingAlg(this.passwordField.getText()),
             filepath);

        if(success) {
            this.currentUser = new User(this.usernameField.getText(), this.firstNameField.getText(), this.lastNameField.getText(),
            UserManagement.hashingAlg(this.passwordField.getText()), image);
            createdMsg.setVisible(true);
            createdMsg.setText("User " + this.usernameField.getText() + " created");
        } else if(this.usernameField.getText().equals("") || this.firstNameField.getText().equals("") || this.lastNameField.getText().equals("") || this.passwordField.getText().equals("")){
            createdMsg.setVisible(true);
            createdMsg.setText("Error, please fill in all fields to proceed");
        } else {
            createdMsg.setVisible(true);
            createdMsg.setText("Error, user " + this.usernameField.getText() + " has already been created, please change usernames to continue");
        }
        
        
    }

}