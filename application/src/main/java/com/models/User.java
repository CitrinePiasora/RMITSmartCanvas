package com.models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class User {
    private String username = "";
    private String firstName = "";
    private String lastName = "";
    private String hashedPassword = "";
    private BufferedImage image;

    public User(String username, String firstName, String lastName, String hashedPassword, BufferedImage image) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hashedPassword = hashedPassword;
        this.image = image;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    public Image getImage() {
        return SwingFXUtils.toFXImage(this.image, null);
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public void setFirstName(String FirstName) {
        this.firstName = FirstName;
    }

    public void setLastName(String LastName) {
        this.lastName = LastName;
    }

    public void setPFP(String filepath) {
        try {
            this.image = ImageIO.read(new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
