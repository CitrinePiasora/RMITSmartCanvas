package com.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.controllers.Helpers.ControllerHelper;
import com.models.User;

public class NewCanvasController implements Initializable {

    @FXML private TextField widthField;
    @FXML private TextField heightField;

    // Initialize session variables
    private User currentUser;
    private Stage currentSession;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    // Function to initialize session variables
    public void initializeSession(User currentUser, Stage currentStage) {
        this.currentSession = currentStage;
        this.currentUser = currentUser;
    }

    @FXML // FX Function, when ok button is pressed...
    void createCanvas(ActionEvent event) {
        // Gets current popup stage
        Stage popupStage = (Stage) widthField.getScene().getWindow();
        try {
            // Tries to open the canvas with given dimensions, close this popup if successful
            ControllerHelper.initStage("Canvas", this.currentUser, this.currentSession, Integer.parseInt(heightField.getText()), Integer.parseInt(widthField.getText()), true);
            popupStage.close();
        } catch (Exception e) {
            // If an exception occurs, create an error popup
            ControllerHelper.initStage("ErrorPopup", "Error, invalid height and width", popupStage, null, null, "popup");
        }
    }
}
