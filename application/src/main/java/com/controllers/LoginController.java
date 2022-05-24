package com.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.application.UserManagement;
import com.controllers.Helpers.ControllerHelper;
import com.dao.sqliteConnection;
import com.models.User;

public class LoginController {
    
    public static ArrayList<User> userList = new ArrayList<User>();

    @FXML private Label newUser;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML // FX Function, when the new user label is clicked...
    private void switchToSignUp() throws IOException {
        // Initializes signup page
        Stage stage = (Stage) newUser.getScene().getWindow();
        Image image = SwingFXUtils.toFXImage(ImageIO.read(new File("default.jpg")), null);
        ControllerHelper.initStage("SignUp", "Create a user", stage, image, null, "main");
    }

    @FXML // FX Function, when login is pressed...
    private void signIntoCanvas() throws IOException {
        // Get current stage variable
        Stage stage = (Stage) newUser.getScene().getWindow();

        // Get username and hashed password from both fields
        String username = this.usernameField.getText();
        String hashedPassword = UserManagement.hashingAlg(this.passwordField.getText());
        
        User currentUser = LoginChecker(username, hashedPassword);
        // If login checker helper function returns true
        if(currentUser != null) {
            // Initializes canvas page
            ControllerHelper.initStage("Canvas", currentUser, stage, 500, 500, false);
        } else {
            // Creates an error pop up saying that the credentials are wrong
            ControllerHelper.initStage("ErrorPopup", "Error, invalid login credentials", stage, null, null, "popup");
        }
    }

    // Helper function that returns the correct user when user credentials match
    private static User LoginChecker(String Username, String hashedPassword) {
        // Invoke data read function from sqliteconnection to initialize userlist
        sqliteConnection.sqliteConnectReadData(userList);

        // For loop to check...
        for(int i = 0; i < userList.size(); i++) {
            // If the username matches inputted username and password matches any of the users
            if(userList.get(i).getUsername().equals(Username) && userList.get(i).getHashedPassword().equals(hashedPassword)) {
                // If they do match, return the user object
                return userList.get(i);
            }
        }

        // Otherwise returns null
        return null;
    }

    @FXML // FX Function that closes the app on pressing the close button
    void closeApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
