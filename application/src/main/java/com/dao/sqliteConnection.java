package com.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import com.application.UserManagement;
import com.models.User;

public class sqliteConnection {

    public static void sqliteConnectReadData(ArrayList<User> userList) {
        String path = new File("CanvasUserDB.db").getAbsolutePath();
        String jdbcURL = "jdbc:sqlite:/" + path;
        
        try {
            Connection connect = DriverManager.getConnection(jdbcURL);

            Statement statement = connect.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM UserData");

            while(res.next()) {
                BufferedImage image;
                
                try {
                    image = ImageIO.read(res.getBinaryStream("profile_picture"));
                    User newUser = new User(res.getString("username"), res.getString("firstName"),
                     res.getString("lastName"), res.getString("password"), image);
                    
                    userList.add(newUser);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch(SQLException e) {
            System.out.print("Error connecting to database");
            e.printStackTrace();
        }
    }

    public static boolean sqliteConnectWriteData(String username, String firstName, String lastName, String hashedPass, String filepath) {
        String path = new File("CanvasUserDB.db").getAbsolutePath();
        String jdbcURL = "jdbc:sqlite:/" + path;

        ArrayList<User> userList = new ArrayList<User>();
        sqliteConnectReadData(userList);

        if(username.equals("") || firstName.equals("") || lastName.equals("") || hashedPass.equals(UserManagement.hashingAlg(""))) {
            return false;
        }
        
        for(User user : userList) {
            if(username.equals(user.getUsername())) {
                return false;
            }
        }
        
        try {
            Connection connect = DriverManager.getConnection(jdbcURL);
            connect.setAutoCommit(false);

            StringBuilder sql = new StringBuilder()
                .append("INSERT INTO UserData (profile_picture, password, lastName, firstName, username) VALUES")
                .append(" (?, ?, ?, ?, ?)");

            File image = new File(filepath);
            FileInputStream fis = new FileInputStream(image);

            PreparedStatement preparedStmt = connect.prepareStatement(sql.toString());
            preparedStmt.setBinaryStream(1, fis, (int) image.length());
            preparedStmt.setString(2, hashedPass);
            preparedStmt.setString(3, lastName);
            preparedStmt.setString(4, firstName);
            preparedStmt.setString(5, username);
            
            preparedStmt.executeUpdate();
            connect.commit();

            preparedStmt.close();
            fis.close();
            
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sqliteConnectEditData(String filepath, String firstName, String lastName, User user) {
        String path = new File("CanvasUserDB.db").getAbsolutePath();
        String jdbcURL = "jdbc:sqlite:/" + path;
        
        try {
            Connection connect = DriverManager.getConnection(jdbcURL);
            connect.setAutoCommit(false);

            StringBuilder sql = new StringBuilder()
                .append("UPDATE UserData SET ")
                .append("firstName = " + "'" + firstName + "'")
                .append(", lastName = " + "'" + lastName + "'");

            if(!filepath.equals("")) {
                sql.append(", profile_picture = ? ")
                    .append("WHERE username = " + "'" + user.getUsername() + "'");

                File image = new File(filepath);
                FileInputStream fis = new FileInputStream(image);

                PreparedStatement preparedStmt = connect.prepareStatement(sql.toString());
                preparedStmt.setBinaryStream(1, fis, (int) image.length());

                preparedStmt.executeUpdate();
                connect.commit();
                preparedStmt.close();
                fis.close();
            } else {
                sql.append("WHERE username = " + "'" + user.getUsername() + "'");
                PreparedStatement preparedStmt = connect.prepareStatement(sql.toString());

                preparedStmt.executeUpdate();
                connect.commit();
                preparedStmt.close();
            }

            return true;

        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
