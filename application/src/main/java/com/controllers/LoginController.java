package com.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import com.application.UserManagement;
import com.dao.sqliteConnection;
import com.models.User;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginController {
    
    public static ArrayList<User> userList = new ArrayList<User>();

    @FXML private Label newUser;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void switchToSignUp() throws IOException {
        Stage stage = (Stage) newUser.getScene().getWindow();
        Image image = SwingFXUtils.toFXImage(ImageIO.read(new File("default.jpg")), null);
        ControllerHelper.initStage("SignUp", "Create a user", stage, image, null, "main");
    }

    @FXML
    private void signIntoCanvas() throws IOException {
        String username = this.usernameField.getText();
        String hashedPassword = UserManagement.hashingAlg(this.passwordField.getText());
        
        if(LoginChecker(username, hashedPassword)) {
            for(int i = 0; i < userList.size(); i++) {
                if(userList.get(i).getUsername().equals(username)) {
                    Stage stage = (Stage) newUser.getScene().getWindow();
                    ControllerHelper.initStage("Canvas", userList.get(i), stage, 500, 500, false);

                }
            }
        }
    }

    private static Boolean LoginChecker(String Username, String hashedPassword) {
        Boolean correct = false;
        sqliteConnection.sqliteConnectReadData(userList);

        for(int i = 0; i < userList.size(); i++) {
            if(userList.get(i).getUsername().equals(Username)) {
                if(userList.get(i).getHashedPassword().equals(hashedPassword)) {
                    correct = true;
                } else {
                    break;
                }
            }
        }

        return correct;
    }

    @FXML
    void closeApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
