package com.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ErrorController implements Initializable {

    @FXML private Label errorLabel;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    public void setErrorMsg(String errorMessage) {
        errorLabel.setText(errorMessage);
    }
}
