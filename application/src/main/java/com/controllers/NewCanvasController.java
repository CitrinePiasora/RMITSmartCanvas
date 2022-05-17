package com.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.models.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewCanvasController implements Initializable {

    @FXML private TextField widthField;
    @FXML private TextField heightField;

    private User currentUser;
    private Stage currentSession;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    public void initializeSession(User currentUser, Stage currentStage) {
        this.currentSession = currentStage;
        this.currentUser = currentUser;
    }

    @FXML
    void createCanvas(ActionEvent event) {
        Stage primaryStage = (Stage) widthField.getScene().getWindow();
        try {
            ControllerHelper.initStage("Canvas", 720, 1000, this.currentUser, this.currentSession, Integer.parseInt(heightField.getText()), Integer.parseInt(widthField.getText()), true);
            primaryStage.close();
        } catch (Exception e) {
            ControllerHelper.errorMessage("Error, invalid height and width", primaryStage);
        }
    }
}
