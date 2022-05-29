package com.controllers.Helpers;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CalculationsNConversions {
    
    // Helper function to get the style sheet of the node and replace specific ones with new values
    public String cssReplacerAppender(Node temp, String toReplace, String value) {
        // Split stylesheet string into string array
        String[] cssStyle = temp.getStyle().split("; ");

        // Initialize variables for checking
        String style = "";
        boolean dupe = false;
        
        // If style array isn't of length 0
        if(cssStyle.length != 0) {
            // Loop through list
            for(String string : cssStyle) {
                // Separate string and extract the key from the value
                String parseString = string.split(" ")[0];
                // If the key is equal to the value aimed to be replaced
                if(parseString.equals(toReplace)) {
                    // Set dupe = true
                    dupe = true;
                // else add the string to the stylesheet
                } else {
                    style += string + "; ";
                }
            }
        }
        
        // If dupe found
        if(dupe) {
            // If bold, italic or a button, "toggle" the changes by removing the style if it's a dupe
            if(!value.equals("bold") && !value.equals("italic") && !temp.getClass().getName().equals("javafx.scene.control.Button")) {
                style += toReplace + " " + value + "; ";
            }
        // Else just add the style and value
        } else {
            style += toReplace + " " + value + "; ";
        }
        
        return style;
    }

    // Helper function to calculate angle from 0 - 360 degrees
    public double clockAngle (double x, double y, double px, double py) {
        double dx = x - px;
        double dy = y - py;

        // Calculate the angle
        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        // In case of dy being 0
        if(dy < 0) {
            angle = 360 - angle;
        }

        return angle;
    }

    // Helper function to help build lines of selection box
    public Line buildLine(double x1, double x2, double y1, double y2) {    
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.AQUA);
        line.setStrokeWidth(1);
        return line;
    }

    // Helper function that returns webformat version of color to use in css
    public String webFormatter(Color colorChosen) {
        String webFormat = String.format("#%02x%02x%02x",
                (int) (255 * colorChosen.getRed()),
                (int) (255 * colorChosen.getGreen()),
                (int) (255 * colorChosen.getBlue()));
        
        return webFormat;
    }
}
