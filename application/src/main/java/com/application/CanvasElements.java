package com.application;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import com.controllers.Helpers.CalculationsNConversions;
import com.controllers.Helpers.ControllerHelper;

public class CanvasElements extends Region {

    private enum Position {
        TopLeft, Top, TopRight, BottomRight, BottomLeft, Left;
    }

    private Rectangle tr, tl, br, bl;
    private Line top, left, right, bottom;
    private Label textBox = null;
    private Circle rotateCircle;
    private Rectangle rectangle = null;
    private Ellipse ellipse = null;
    private ImageView image = null;
    private double cornerSize = 10, x, y;
    private String shape;

    private ControllerHelper controlHelper;
    private CalculationsNConversions calcHelper = new CalculationsNConversions();

    private Rotate rotate = new Rotate(); {
        getTransforms().add(rotate);
        rotate.setPivotX(cornerSize);
        rotate.setPivotY(cornerSize);
    }

    public CanvasElements(double width, double height, String shape, Stage stage) {
        this.controlHelper = new ControllerHelper(stage);
        this.shape = shape;

        // Create circle that is used to rotate shape
        rotateCircle = new Circle(5);
        rotateCircle.setFill(Color.PINK);
        rotateCircle.setStroke(Color.rgb(0, 0, 0, 0.75));

        // Make the circle draggable
        rotateCircle.setOnMousePressed(e -> {
            setMouse(e.getSceneX(), e.getSceneY());
        });

        // When it's dragged rotate the box
        rotateCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // Used to get the scene position of the corner of the box
                Transform localToScene = getLocalToSceneTransform();

                double x1 = getMouseX();
                double y1 = getMouseY();

                double x2 = event.getSceneX();
                double y2 = event.getSceneY();

                double px = rotate.getPivotX() + localToScene.getTx();
                double py = rotate.getPivotY() + localToScene.getTy();

                // Work out the angle rotated
                double th1 = clockAngle(x1, y1, px, py);
                double th2 = clockAngle(x2, y2, px, py);

                double angle = rotate.getAngle();

                angle += th2 - th1;

                // Rotate the rectangle
                rotate.setAngle(angle);

                setMouse(event.getSceneX(), event.getSceneY());
            }
        });

        // Switch case to check what shape the selection box will be containing: rectangle, circle, image or text
        switch (shape) {
            case "rectangle":
                rectangle = new Rectangle();
                rectangle.setFill(Color.AQUA);
                rectangle.setStrokeType(StrokeType.INSIDE);
                rectangle.setStroke(Color.WHITE);
                makeDraggable(rectangle);
                break;
            case "circle":
                ellipse = new Ellipse();
                ellipse.setFill(Color.AQUA);
                ellipse.setStrokeType(StrokeType.INSIDE);
                ellipse.setStroke(Color.WHITE);
                makeDraggable(ellipse);
                break;
            case "image":
                image = new ImageView();
                controlHelper.imageChooser(image);
                makeDraggable(image);
                break;
            case "text":
                textBox = new Label("text");
                textBox.setTextFill(Color.BLACK);
                textBox.setStyle("-fx-border-color: " + calcHelper.webFormatter(Color.BLACK)
                        + "; -fx-background-color: " + calcHelper.webFormatter(Color.WHITE)
                        + "; -fx-border-width: 1; -fx-font-size: 12; -fx-font-family: " + "\"Arial\",sans-serif; ");
                makeDraggable(textBox);
                break;
        }

        // Build the corners
        tr = buildCorner(0, 0, Position.TopRight);
        tl = buildCorner(0, 0, Position.TopLeft);
        br = buildCorner(0, 0, Position.BottomRight);
        bl = buildCorner(0, 0, Position.BottomLeft);

        // Build the lines
        top = buildLine(0, 100, -100, 0);
        bottom = buildLine(0, 0, 0, 0);
        left = buildLine(0, 0, 0, 0);
        right = buildLine(0, 0, 0, 0);

        // Initialize the size of the selection box and the node
        setSize(width, height);
        getChildren().addAll(
                shape.equals("rectangle") ? rectangle
                        : shape.equals("circle") ? ellipse : shape.equals("image") ? image : textBox,
                top, bottom, left, right, tr, tl, br, bl, rotateCircle);
    }

    // Helper function to turn the shape draggable
    private void makeDraggable(Node node) {
        node.setOnMousePressed(e -> {
            setMouse(e.getSceneX(), e.getSceneY());
        });

        node.setOnMouseDragged(e -> {
            // Get the mouse deltas
            double dx = e.getSceneX() - getMouseX();
            double dy = e.getSceneY() - getMouseY();

            // Set save the current mouse value
            setMouse(e.getSceneX(), e.getSceneY());

            // Move the selection box and it's contents around based on mouse movement
            setTranslateX(getTranslateX() + dx);
            setTranslateY(getTranslateY() + dy);
        });
    }

    // Return the angle from 0 - 360 degrees
    public double clockAngle(double x, double y, double px, double py) {
        double dx = x - px;
        double dy = y - py;

        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        if (dy < 0) {
            angle = 360 - angle;
        }

        return angle;
    }

    // Set the size of the selection box
    public void setSize(double width, double height) {
        // Sets the x and y coordinates of the different resizing handlers
        tl.setX(0);
        tl.setY(0);
        tr.setX(width + cornerSize);
        tr.setY(0);
        bl.setX(0);
        bl.setY(height + cornerSize);
        br.setX(width + cornerSize);
        br.setY(height + cornerSize);

        // Switch case to check what the current shape is: rectangle, circle, text or image
        switch (this.shape) {
            case "rectangle":
                rectangle.setX(cornerSize); rectangle.setY(cornerSize);
                rectangle.setHeight(height); rectangle.setWidth(width);
                break;
            case "circle":
                ellipse.setCenterX((width + (2 * cornerSize)) / 2); ellipse.setCenterY((height + (2 * cornerSize)) / 2);
                ellipse.setRadiusX(width / 2); ellipse.setRadiusY(height / 2);
                break;
            case "image":
                image.setX(cornerSize); image.setY(cornerSize);
                image.setFitHeight(height); image.setFitWidth(width);
                break;
            case "text":
                textBox.relocate(cornerSize, cornerSize);
                textBox.setPrefHeight(height); textBox.setPrefWidth(width);
                textBox.setMinHeight(height); textBox.setMinWidth(width);
                break;
        }

        // Sets the new positions of the bounding box lines
        setLine(top, cornerSize, cornerSize, width + cornerSize, cornerSize);
        setLine(bottom, cornerSize, height + cornerSize, width + cornerSize, height + cornerSize);
        setLine(right, width + cornerSize, cornerSize, width + cornerSize, height + cornerSize);
        setLine(left, cornerSize, cornerSize, cornerSize, height + cornerSize);

        // Sets the cursor type of the lines
        top.setCursor(Cursor.V_RESIZE);
        bottom.setCursor(Cursor.V_RESIZE);
        left.setCursor(Cursor.H_RESIZE);
        right.setCursor(Cursor.H_RESIZE);

        // Sets the cursor type of the resizing handlers
        tr.setCursor(Cursor.CROSSHAIR);
        tl.setCursor(Cursor.CROSSHAIR);
        br.setCursor(Cursor.CROSSHAIR);
        bl.setCursor(Cursor.CROSSHAIR);

        // Sets new position of the rotation circle
        rotateCircle.setCenterX(width + 2 * cornerSize + rotateCircle.getRadius());
        rotateCircle.setCenterY(height + 2 * cornerSize + rotateCircle.getRadius());

    }

    // Set the start and end points of a line
    private void setLine(Line l, double x1, double y1, double x2, double y2) {
        l.setStartX(x1);
        l.setStartY(y1);
        l.setEndX(x2);
        l.setEndY(y2);
    }

    // Save mouse coordinates
    private void setMouse(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Gets mouse current position X
    private double getMouseX() {
        return x;
    }

    // Gets mouse current position X
    private double getMouseY() {
        return y;
    }

    // Calculates selection box width
    public double w() {
        return Math.abs(bottom.getEndX() - bottom.getStartX());
    }

    // Calculates selection box height
    public double h() {
        return Math.abs(right.getEndY() - right.getStartY());
    }

    // Build a corner of the selection box
    private Rectangle buildCorner(double x, double y, final Position pos) {

        // Create the rectangle
        Rectangle r = new Rectangle();
        r.setX(x);
        r.setY(y);
        r.setWidth(cornerSize);
        r.setHeight(cornerSize);
        r.setStroke(Color.rgb(0, 0, 0, 0.75));
        r.setFill(Color.rgb(0, 0, 0, 0.25));
        r.setStrokeWidth(1);

        r.setStrokeType(StrokeType.INSIDE);

        // Make it draggable
        r.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setMouse(event.getSceneX(), event.getSceneY());
            }
        });

        r.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // Get the mouse deltas
                double dx = event.getSceneX() - getMouseX();
                double dy = event.getSceneY() - getMouseY();

                // Set save the current mouse value
                setMouse(event.getSceneX(), event.getSceneY());

                // Get the rotation angle in radians
                double tau = -Math.toRadians(rotate.getAngle());

                // Create variables for the sin and cosine
                double sinTau = Math.sin(tau);
                double cosTau = Math.cos(tau);

                // Perform a rotation on dx and dy to the object co-ordinate frame
                double dx_ = dx * cosTau - dy * sinTau;
                double dy_ = dy * cosTau + dx * sinTau;

                // Create a variable for the change in height of the box
                double dh = Math.abs(right.getEndY() - right.getStartY());

                // Work out the new positions for the resize corners
                if (pos == Position.TopLeft) {
                    // Set the size based on the transformed dx and dy values
                    setSize(w() - dx_, Math.abs(right.getEndY() - right.getStartY()) - dy_);

                    // Move the shape
                    setTranslateX(getTranslateX() + dx);
                    setTranslateY(getTranslateY() + dy);
                } else if (pos == Position.TopRight) {

                    // This comes down to geometry - you need to know the
                    // amount the height of the shape has increased
                    setSize(w() + dx_, Math.abs(right.getEndY() - right.getStartY()) - dy_);

                    // Work out the delta height - that is then used to work out
                    // the correct translations
                    dh = Math.abs(right.getEndY() - right.getStartY()) - dh;

                    setTranslateX(getTranslateX() - dh * sinTau);
                    setTranslateY(getTranslateY() - dh * cosTau);
                } else if (pos == Position.BottomRight) {
                    setSize(w() + dx_, Math.abs(right.getEndY() - right.getStartY()) + dy_);
                } else if (pos == Position.BottomLeft) {

                    setSize(w() - dx_, Math.abs(right.getEndY() - right.getStartY()) + dy_);

                    dh = Math.abs(right.getEndY() - right.getStartY()) - dh;

                    setTranslateX(getTranslateX() + dx - dh * sinTau);
                    setTranslateY(getTranslateY() + dy - dh * cosTau);
                }
            }
        });

        return r;
    }

    // Build the line for selection box
    private Line buildLine(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);

        l.setStroke(Color.AQUA);
        l.setStrokeWidth(1);

        return l;
    }

    // Sets the color of the current shape
    public void setColor(Color color) {
        // Switch case to check what the current shape is: rectangle, circle or text
        switch (this.shape) {
            case "rectangle":
                rectangle.setFill(color);
                break;
            case "circle":
                ellipse.setFill(color);
                break;
            case "text":
                calcHelper.cssReplacerAppender(textBox, "-fx-background-color:", calcHelper.webFormatter(color));
                break;
        }
    }

    // Sets the border color of the current shape
    public void setBorderColor(Color color) {
        // Switch case to check what the current shape is: rectangle, circle or text
        switch (this.shape) {
            case "rectangle":
                rectangle.setStroke(color);
                break;
            case "circle":
                ellipse.setStroke(color);
                break;
            case "text":
                calcHelper.cssReplacerAppender(textBox, "-fx-border-color:", calcHelper.webFormatter(color));
                break;
        }
    }

    // Sets the width of the border
    public void setBorderWidth(double width) {
        // Switch case to check what the current shape is: rectangle, circle or text
        switch (this.shape) {
            case "rectangle":
                rectangle.setStrokeWidth(width);
                break;
            case "circle":
                ellipse.setStrokeWidth(width);
                break;
            case "text":
                calcHelper.cssReplacerAppender(textBox, "-fx-border-width:", Double.toString(width));
                break;
        }
    }

    // Sets the label's text
    public void setText(String text) {
        textBox.setText(text);
    }

    // Sets the label's font
    public void setFont(String input, String type) {
        // Switch case to check what the current type is: font or size
        switch(type) {
            case "font":
                calcHelper.cssReplacerAppender(textBox, "-fx-font-family:", input);
                break;
            case "size":
                calcHelper.cssReplacerAppender(textBox, "-fx-font-size:", input);
                break;
        }
    }

    // Sets the label's text color
    public void setTextColor(Color color) {
        textBox.setTextFill(color);
    }

    // Sets the style of the text, chosen between bold or italics
    public void setTextStyle(String style) {
        // Switch case to check what the current style is: bold or italic
        switch(style) {
            case "bold":
                calcHelper.cssReplacerAppender(textBox, "-fx-font-weight:", "bold");
                break;
            case "italic":
                calcHelper.cssReplacerAppender(textBox, "-fx-font-style:", "italic");
                break;
        }
    }

    // Sets the alignment of the text in the label
    public void setAlignment(Pos alignment) {
        textBox.setAlignment(alignment);
    }

    // Returns the current text alignment of the label
    public Pos getAlignment() {
        return textBox.getAlignment();
    }

    // Returns current shape
    public String getShapeType() {
        return this.shape;
    }

    // Returns stylesheet of textbox
    public String[] getTextBoxStylesheet() {
        return textBox.getStyle().split("; ");
    }

    // Returns color of the text in the text box
    public Paint getTextColor() {
        return textBox.getTextFill();
    }

    // Returns the color of the shape
    public Paint getShapeColor() {
        return shape.equals("rectangle") ? rectangle.getFill() : shape.equals("circle") ? ellipse.getFill() : null;
    }

    // Returns the text inside the text box
    public String getText() {
        return textBox.getText();
    }

    // Returns the border color of the shape
    public Paint getBorderColor() {
        switch(this.shape) {
            case "rectangle":
                return rectangle.getStroke();
            case "circle":
                return ellipse.getStroke();
            default:
                return null;
        }
    }

    // Return border width of shape
    public String getBorderWidth() {
        // Switch case to check what the current shape is: rectangle, circle or text
        switch (this.shape) {
            case "rectangle":
                return Integer.toString((int) rectangle.getStrokeWidth());
            case "circle":
                return Integer.toString((int) ellipse.getStrokeWidth());
            default:
                return "0";
        }
    }

    public void toggleBoundingBox(boolean toggle) {
        tr.setDisable(toggle); tr.setVisible(!toggle);
        tl.setDisable(toggle); tl.setVisible(!toggle);
        br.setDisable(toggle); br.setVisible(!toggle);
        bl.setDisable(toggle); bl.setVisible(!toggle);
        top.setDisable(toggle); top.setVisible(!toggle);
        bottom.setDisable(toggle); bottom.setVisible(!toggle);
        left.setDisable(toggle); left.setVisible(!toggle);
        right.setDisable(toggle); right.setVisible(!toggle);
        rotateCircle.setDisable(toggle); rotateCircle.setVisible(!toggle);
    }

    public void setContainedImage() {
        controlHelper.imageChooser(image);
    }
}
