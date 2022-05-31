package com.controllers;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import java.awt.image.RenderedImage;

import com.application.CanvasElements;
import com.controllers.Helpers.CalculationsNConversions;
import com.controllers.Helpers.ControllerHelper;
import com.models.User;

public class BoardController implements Initializable {

    //#region: Initializes JavaFX nodes found in FXML
    @FXML private ImageView profilePicture;
    @FXML private Label nameLabel;
    @FXML private Slider zoomSlider;
    @FXML private Label zoomPercent;
    @FXML private MenuItem saveButton;
    @FXML private MenuItem clearButton;
    @FXML private StackPane zoomPane;
    @FXML private Pane canvasPane;
    @FXML private Canvas FXCanvas;
    @FXML private ComboBox<String> FontDropdown;
    @FXML private Button boldBtn;
    @FXML private Button italicsBtn;
    @FXML private Button leftBtn;
    @FXML private Button rightBtn;
    @FXML private Button centerBtn;
    @FXML private Button changeImageBtn;
    @FXML private Label bordColor;
    @FXML private Label borderWidth;
    @FXML private Label backgroundLabel;
    @FXML private Label textAlignment;
    @FXML private Label textAttributes;
    @FXML private Label textContents;
    @FXML private Label textFont;
    @FXML private Label textColor;
    @FXML private Label fontSizeLabel;
    @FXML private TextField borderWidthIn;
    @FXML private TextField textInput;
    @FXML private TextField fontSize;
    @FXML private ColorPicker borderColorPicker;
    @FXML private ColorPicker bgColorPicker;
    @FXML private ColorPicker textColorPicker;
    //#endregion

    //initialize session variables
    private User currentUser;
    
    // Initialize controllerhelper and calculationshelper
    private ControllerHelper controlHelper;
    private CalculationsNConversions calcHelper = new CalculationsNConversions();

    //#region: Initializes canvas related variables
    private GraphicsContext gcF;
    private double percentageCalc = 1.0;
    private int canvasH, canvasW;
    private boolean drawoval = false, drawrectangle = false, drawtext = false, drawImage = false, initChoices = false, canvasColor = false;
    private double startX, startY, lastX, lastY;
    private double wh, hg;

    // Initializes shapes used for selection boxes
    private CanvasElements currentNode;

    // Initialize list of nodes to loop through to shorten code
    private List<Label> labels; List<Control> controls; List<Button> buttons;

    // Initialize list of fonts
    private Map<String, String> fontFamilies = new HashMap<String, String>(); {
        fontFamilies.put("Aclonica", "Aclonica"); 
        fontFamilies.put("Aldrich", "Aldrich");
        fontFamilies.put("Allura", "Allura");
        fontFamilies.put("Arial", "\"Arial\",sans-serif");
        fontFamilies.put("Brush Script MT", "\"Brush Script MT\",cursive");
        fontFamilies.put("Courier New", "\"Courier New\", monospace"); 
        fontFamilies.put("Garamond", "\"Garamond\", serif");
        fontFamilies.put("Georgia", "\"Georgia\", serif");
        fontFamilies.put("Sofia", "Sofia");
        fontFamilies.put("Times New Roman", "\"Times New Roman\", serif");
        fontFamilies.put("Trebuchet MS", "\"Trebuchet MS\", cursive");
    }
    //#endregion
         
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    // Function to initalize the board's variables
    public void initializeSession(User user, Stage currentSession, int canvasHeight, int canvasWidth, boolean canvasActive) {
        //#region: Initialize variables
        profilePicture.setImage(user.getImage());
        nameLabel.setText(user.getFullName());
        this.controlHelper = new ControllerHelper(currentSession);

        labels = Arrays.asList(bordColor, borderWidth, backgroundLabel, textAlignment, fontSizeLabel, textAttributes, textContents, textFont, textColor);
        controls = Arrays.asList(borderColorPicker, borderWidthIn, bgColorPicker, fontSize, textInput, textColorPicker, FontDropdown);
        buttons = Arrays.asList(boldBtn, italicsBtn, leftBtn, rightBtn, centerBtn, leftBtn, rightBtn, centerBtn);
        //#endregion

        //#region: Initialize Canvas Properties
        this.canvasH = canvasHeight; this.canvasW = canvasWidth;
        this.canvasPane.setPrefHeight(canvasHeight); this.canvasPane.setPrefWidth(canvasWidth);
        this.FXCanvas.setHeight(canvasHeight); this.FXCanvas.setWidth(canvasWidth);
        this.canvasPane.setStyle("-fx-border-color: gray; -fx-background-color: " + calcHelper.webFormatter(Color.WHITE) + "; ");
        this.gcF = this.FXCanvas.getGraphicsContext2D();
        
        // Initialize state of menu items and canvas
        this.clearButton.setDisable(!canvasActive);
        this.saveButton.setDisable(!canvasActive);
        this.canvasPane.setVisible(canvasActive);

        // Initialize session
        this.currentUser = user; 
        //#endregion

        //#region: Initialize actions that couldn't be initialized using fxml
        // Initializes font dropdown items
        this.FontDropdown.setItems(FXCollections.observableArrayList(fontFamilies.keySet()).sorted());
        
        // Adds a listener to the slider in order to change zoom level of canvas
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                double sliderValue = (double) zoomSlider.getValue();

                // Logic that calculates the current zoom %
                if(sliderValue > 0.0) {
                    percentageCalc = 1.0 + (sliderValue / 100.0);
                    zoomPercent.setText("Zoom: " + Integer.toString((int) sliderValue) + "%");
                } else if(sliderValue < 0.0){
                    percentageCalc = (100.0 + sliderValue) / 100.0;
                    zoomPercent.setText("Zoom: " + Integer.toString((int) sliderValue) + "%");
                } else if(sliderValue == 0.0 || 100.0 + sliderValue == 0.0){
                    percentageCalc = 1.0;
                    zoomPercent.setText("Zoom: 0%");
                }

                // Code that resizes elements based on the calculated zoom %
                ObservableList<Node> canvasElements = zoomPane.getChildren();
                for(Node element : canvasElements) {
                    element.setScaleX(percentageCalc);
                    element.setScaleY(percentageCalc);
                }
            }
            
        });
        //#endregion
    }

    //#region: FXML Functions
    @FXML // FX Function to change the background color based on the color picker value
    public void changeBGColor(ActionEvent event) {
        if(!initChoices) {
            Color chosen = bgColorPicker.getValue();
            if(canvasColor) {
                calcHelper.cssReplacerAppender(canvasPane, "-fx-background-color:", calcHelper.webFormatter(chosen));
            } else {
                currentNode.setColor(chosen);
            }
        }
    }

    @FXML // FX Function to change color border based on color picker value
    public void changeBorderColor(ActionEvent event) {
        if(!initChoices) {
            Color chosen = borderColorPicker.getValue();
            if(canvasColor) {
                calcHelper.cssReplacerAppender(canvasPane, "-fx-background-color:", calcHelper.webFormatter(chosen));
            } else {
                currentNode.setBorderColor(chosen);
            }
        }
    }

    @FXML // FX Function to change the text color
    public void changeTextColor(ActionEvent event) {
        if(!initChoices) {
            Color chosen = textColorPicker.getValue();
            currentNode.setTextColor(chosen);
        }
    }

    @FXML // FX function to change the font of the textbox
    public void changeFont(ActionEvent event) {
        if(!initChoices) {
            currentNode.setFont(fontFamilies.get(FontDropdown.getValue()), "font");
        }
    }

    @FXML // FX Function, when profile button is pressed...
    public void profilePopUp(ActionEvent event) {
        // Creates a pop up to open up the profile management menu
        controlHelper.initStage("ProfileEdit", "Profile Management", profilePicture.getImage(), currentUser, "popup");
    }

    @FXML // FX Function, when new canvas is pressed...
    void NewCanvas(ActionEvent event) {
        // Create a pop up to initialize dimensions of canvas
        controlHelper.initStage("NewCanvas", "Canvas Properties", profilePicture.getImage(), currentUser, "popup");
    }

    @FXML // FX Function, when clear canvas is pressed...
    void clearCanvas(ActionEvent event) {
        // Clears all nodes on the canvaspane, wiping the canvas
        canvasPane.getChildren().clear();
        setCurrentNode(null);
    }

    @FXML // FX Function, when delete element is pressed...
    void deleteElement(ActionEvent event) {
        // Delete the currently selected Node
        canvasPane.getChildren().remove(this.currentNode);
        setCurrentNode(null);
    }

    @FXML // FX Function, when save as is pressed...
    void saveImage(ActionEvent event) {
        // initialize filechooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG files (*.png)", "*.png"));
        
        // Initialize file to write using filechooser save dialog
        File file = fileChooser.showSaveDialog(controlHelper.getStage());

        if(file != null) {
            try {
                
                // stores current slider value, sets slider value to 0 to export image properly
                int currentZoom = (int) zoomSlider.getValue();
                zoomSlider.setValue(0);

                WritableImage exporting = new WritableImage(canvasW, canvasH);
                canvasPane.snapshot(null, exporting);
                RenderedImage exportedFile = SwingFXUtils.fromFXImage(exporting, null);
                ImageIO.write(exportedFile, "png", file);

                // restores old zoom value so the user doesn't see that the zoom had changed while saving
                zoomSlider.setValue(currentZoom);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML // FX Function, when log out button is pressed...
    void logOut() {
        // Switches current scene to login screen
        controlHelper.switchViews("Login", "Welcome to SmartCanvas");
    }

    @FXML // FX Function, when the circle button is pressed...
    void circleBrush(ActionEvent event) {
        // If current brush is circle
        if(drawoval) {
            // Sets current brush as nothing
            drawoval = false;

        // Else set current brush as circle brush
        } else {
            drawoval = true;
            drawrectangle = false;
            drawtext = false;
            drawImage = false;
        }
    }
    
    @FXML // FX Function, when the rectangle button is pressed...
    void rectBrush(ActionEvent event) {
        // If current brush is rectangle
        if(drawrectangle) {
            // Sets current brush as nothing
            drawrectangle = false;
        // Else set current brush as rectangle brush
        } else {
            drawoval = false;
            drawrectangle = true;
            drawtext = false;
            drawImage = false;
        }
    }

    @FXML // FX Function, when the text button is pressed...
    void insertTextBox(ActionEvent event) {
        // If current brush is text
        if(drawtext) {
            // Sets current brush as nothing
            drawtext = false;
        // Else set current brush as text brush
        } else {
            drawoval = false;
            drawrectangle = false;
            drawtext = true;
            drawImage = false;
        }
    }

    @FXML // FX Function, when the image button is pressed...
    private void addImage() {
        // If current brush is image
        if(drawImage) {
            // Sets current brush as nothing
            drawImage = false;
        // Else set current brush as image brush
        } else {
            drawoval = false;
            drawrectangle = false;
            drawtext = false;
            drawImage = true;
        }
    }
    
    @FXML // FX Function, when the canvas button is pressed...
    void canvasColor(ActionEvent Event) {
        // Placeholder function to set canvas color to pink
        setCurrentNode(null);

        this.initChoices = true;
        // Set all buttons to off, and turn on only the first label and control node
        for(Button button : buttons) {
            button.setDisable(true); button.setVisible(false);
        }
        for(int i = 0; i < controls.size(); i++) {
            if(i == 0) {
                controls.get(i).setDisable(false); controls.get(i).setVisible(true);
            } else {
                controls.get(i).setDisable(true); controls.get(i).setVisible(false);
            }
        }
        for(int i = 0; i < labels.size(); i++) {
            if(i == 0) {
                labels.get(i).setVisible(true); labels.get(i).setText("Canvas Color");
            } else {
                labels.get(i).setVisible(false);
            }
        }
        
        // Initialize variables for looping through the CSS Styles
        String[] cssStyle = canvasPane.getStyle().split("; ");

        // Loop through the stylesheet
        for(String string : cssStyle) {
            String[] parseString = string.split(" ");
            // Checks for background color and set dropdown selection to said color
            if(parseString[0].equals("-fx-background-color:")) {
                borderColorPicker.setValue(Color.valueOf(parseString[1]));
                break;
            }
        }

        // Turn off change image button
        changeImageBtn.setVisible(false); changeImageBtn.setDisable(true);

        this.initChoices = false;
        canvasColor = true;
    }

    @FXML // FX Function, when user clicks on fxcanvas (invisible layer on top of canvaspane)
    private void FXMousePress(MouseEvent e) {
        // Sets values for startx and starty
        this.startX = e.getX();
        this.startY = e.getY();
    }

    @FXML // FX Function, when drags the mouse on fxcanvas (invisible layer on top of canvaspane)
    private void FXMouseDrag(MouseEvent e) {
        // Sets values for lastx and lasty
        this.lastX = e.getX();
        this.lastY = e.getY();

        // If any non oval brush is selected, call the function to start the effect of drawing those elements
        if(drawrectangle || drawImage || drawtext) {
            drawRectEffect();
        // Else call the function to visualize drawing the circle
        } else if(drawoval) {
            drawOvalEffect();
        }
    }

    @FXML // FX Function, when mouse is released on fxcanvas (invisible layer on top of canvaspane)
    private void FXMouseRelease(MouseEvent e) {
        this.wh = lastX - startX + 20;
        this.hg = lastY - startY + 20;
        
        // Invokes the appropriate function to create the new element
        if(drawrectangle) {
            drawRect();
        } else if(drawoval) {
            drawOval();
        } else if(drawImage) {
            drawImage();
        } else if(drawtext) {
            drawTextBox();
        }

        // Clears the graphicscontext for fxcanvas 
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
    }

    @FXML
    void textBold(ActionEvent event) {
        currentNode.setTextStyle("bold");
        calcHelper.cssReplacerAppender(boldBtn, "-fx-background-color:", "gray");
    }

    @FXML
    void textItalics(ActionEvent event) {
        currentNode.setTextStyle("italic");
        calcHelper.cssReplacerAppender(italicsBtn, "-fx-background-color:", "gray");
    }
    
    @FXML // FX Function to change text alignment to left alignment
    void leftAlign(ActionEvent event) {
        // Changes button color if not currently selected
        if(currentNode.getAlignment() != Pos.CENTER_LEFT) {
            leftBtn.setStyle("-fx-background-color: gray");
            centerBtn.setStyle("");
            rightBtn.setStyle("");
        }

        // Set text alignment
        currentNode.setAlignment(Pos.CENTER_LEFT);
    }

    @FXML // FX Function to change text alignment to right alignment
    void rightAlign(ActionEvent event) {
        // Changes button color if not currently selected
        if(currentNode.getAlignment() != Pos.CENTER_RIGHT) {
            rightBtn.setStyle("-fx-background-color: gray");
            centerBtn.setStyle("");
            leftBtn.setStyle("");
        }

        // Set text alignment
        currentNode.setAlignment(Pos.CENTER_RIGHT);
    }

    @FXML // FX Function to change text alignment to center alignment
    void centerAlign(ActionEvent event) {
        // Changes button color if not currently selected
        if(currentNode.getAlignment() != Pos.CENTER) {
            centerBtn.setStyle("-fx-background-color: gray");
            leftBtn.setStyle("");
            rightBtn.setStyle("");
        }

        // Set text alignment
        currentNode.setAlignment(Pos.CENTER);
    }

    @FXML // FX Function to change border width of node
    void borderChange(ActionEvent event) {
        // Try to change width of border
        try {
            double newBorderWidth = Double.parseDouble(borderWidthIn.getText());
            currentNode.setBorderWidth(newBorderWidth);
        // If calculations failed (due to invalid input), send error message
        } catch (Exception e) {
            controlHelper.initStage("ErrorPopup", "Error, invalid border width", null, null, "popup");
        }
    }

    @FXML // FX Function to change the text of textbox element
    void setLabelText(ActionEvent event) {
        currentNode.setText(textInput.getText());
    }   

    @FXML // FX Function to change font size of textbox element
    void setFontSize(ActionEvent event) {
        // Tries to change current font size
        try {
            Integer.parseInt(fontSize.getText());
            currentNode.setFont(fontSize.getText(), "size");
        // If the input is invalid, sends an error
        } catch (Exception e) {
            controlHelper.initStage("ErrorPopup", "Error, invalid border width", null, null, "popup");
        }
    }    
    
    @FXML
    void changeImage(ActionEvent event) {
        currentNode.setContainedImage();
    }
    //#endregion

    //#region: helper functions\

    private void toggleSelectionBox(CanvasElements node) {
        node.setOnMouseClicked(e -> {
            setCurrentNode(node);

            // Enables bounding box
            node.toggleBoundingBox(false);
        });

        node.setOnMouseDragged(e -> {
            setCurrentNode(node);

            // Enables bounding box
            node.toggleBoundingBox(false);
        });
    }

    // Helper function to toggle the visibility and usability of options on the side panel
    private void setOptionsSidePanel(String type) {
        // Sets intichoices to true as we don't want the values of the colors and other attributes to change while this happens
        this.initChoices = true;
        
        // Switch case that checks for specific type: text, image, circle, rectangle or canvas
        switch(type) {
            case "rectangle":
            case "circle":
                // Disable all buttons
                for(Button input : buttons) {
                    input.setVisible(false); input.setDisable(true);
                }

                // Enable first 3 control related nodes, disable the rest
                for(int i = 0; i < controls.size(); i++) {
                    if(i < 3) {
                        controls.get(i).setDisable(false); controls.get(i).setVisible(true);
                    } else {
                        controls.get(i).setDisable(true); controls.get(i).setVisible(false);
                    }
                }

                // Enable first 3 labels, disable the rest
                for(int i = 0; i < labels.size(); i++) {
                    if(i == 0) {
                        labels.get(i).setVisible(true); labels.get(i).setText("Border Color");
                    } else if(i > 0 && i < 3) {
                        labels.get(i).setVisible(true);
                    } else {
                        labels.get(i).setVisible(false);
                    }
                }

                // Set the border width in input box
                borderWidthIn.setText(this.currentNode.getBorderWidth());
            
                // Set background and border color picker current colors to colors of current node
                borderColorPicker.setValue((Color) this.currentNode.getBorderColor());
                bgColorPicker.setValue((Color) this.currentNode.getShapeColor());

                // Turn off change image button
                changeImageBtn.setVisible(false); changeImageBtn.setDisable(true);
                break;
            case "text":
                // Enable all buttons and controls
                for(Button input : buttons) {
                    input.setVisible(true); input.setDisable(false);
                }
                for(Control input : controls) {
                    input.setVisible(true); input.setDisable(false);
                }

                // Enable first 3 labels, disable the rest
                for(int i = 0; i < labels.size(); i++) {
                    if(i == 0) {
                        labels.get(i).setVisible(true); labels.get(i).setText("Border Color");
                    } else {
                        labels.get(i).setVisible(true);
                    }
                }

                // Turn off change image button
                changeImageBtn.setVisible(false); changeImageBtn.setDisable(true);
            
                // Initialize variables for looping through the CSS Styles
                String[] cssStyle = this.currentNode.getTextBoxStylesheet();
                boolean italicSet = false, boldSet = false;

                // Loop through the stylesheet
                for(String string : cssStyle) {
                    String[] parseString = string.split(" ");
                    // Checks for border width and set input textfield to current border width
                    if(parseString[0].equals("-fx-border-width:")) {
                        borderWidthIn.setText(parseString[1]);
                    // Checks for background color and set dropdown selection to said color
                    } else if(parseString[0].equals("-fx-background-color:")) {
                        bgColorPicker.setValue(Color.valueOf(parseString[1]));
                    // Checks for border color and set dropdown selection to said color
                    } else if(parseString[0].equals("-fx-border-color:")) {
                        borderColorPicker.setValue(Color.valueOf(parseString[1]));
                    // Checks for font family and set the dropdown selection to said font
                    } else if(parseString[0].equals("-fx-font-family:")) {
                        for(String item : fontFamilies.keySet()) {
                            if(fontFamilies.get(item).equals(string.substring(17))) {
                                SingleSelectionModel<String> options = FontDropdown.getSelectionModel();
                                options.select(item);
                                FontDropdown.setSelectionModel(options);
                            }
                        }
                    // Checks for font style tag as it's only used for italics in this application and set italic button as gray
                    } else if(parseString[0].equals("-fx-font-style:")) {
                        italicsBtn.setStyle("-fx-background-color: gray; ");
                        italicSet = true;
                    // Checks for font weight as it's only used for bold and set the bold button gray
                    } else if(parseString[0].equals("-fx-font-weight:")) {
                        boldBtn.setStyle("-fx-background-color: gray; ");
                        boldSet = true;
                    // Checks for font size and set the font size input to current font size
                    } else if(parseString[0].equals("-fx-font-size:")) {
                        fontSize.setText(parseString[1]);
                    }
                }

                // Checks for current textfill and sets it as current text fill in dropdown
                textColorPicker.setValue((Color) this.currentNode.getTextColor());

                // If not bold, reset style of bold button
                if(!boldSet) {
                    boldBtn.setStyle("");
                }
                // If not italic, reset style of italic button
                if(!italicSet) {
                    italicsBtn.setStyle("");
                }

                // Set text input field value as label text
                textInput.setText(this.currentNode.getText());

                // Changes button colors based on alignment
                switch(this.currentNode.getAlignment()) {
                    case CENTER:
                        centerBtn.setStyle("-fx-background-color: gray");
                        leftBtn.setStyle("");
                        rightBtn.setStyle("");
                        break;
                    case CENTER_RIGHT:
                        rightBtn.setStyle("-fx-background-color: gray");
                        centerBtn.setStyle("");
                        leftBtn.setStyle("");
                        break;
                    case CENTER_LEFT:
                        leftBtn.setStyle("-fx-background-color: gray");
                        rightBtn.setStyle("");
                        centerBtn.setStyle("");
                        break;
                    default:
                        break;
                }
                break;
            case "image":
                // Set all buttons to off, and turn on only the first label and control node
                for(Button button : buttons) {
                    button.setDisable(true); button.setVisible(false);
                }
                for(int i = 0; i < controls.size(); i++) {
                    if(i == 0) {
                        controls.get(i).setDisable(false); controls.get(i).setVisible(true);
                    } else {
                        controls.get(i).setDisable(true); controls.get(i).setVisible(false);
                    }
                }
                for(int i = 0; i < labels.size(); i++) {
                    if(i == 0) {
                        labels.get(i).setVisible(true); labels.get(i).setText("Canvas Color");
                    } else {
                        labels.get(i).setVisible(false);
                    }
                }

                // Turn on change image button
                changeImageBtn.setVisible(true); changeImageBtn.setDisable(false);
                break;
        }
        
        this.initChoices = false;
    }

    // Helper function that sets current node
    private void setCurrentNode(CanvasElements node) {
        // Disables the bounding box of the current node if current isn't null
        if(this.currentNode != null) {
            this.currentNode.toggleBoundingBox(true);
        }

        // Sets the current node
        this.currentNode = node;

        // If the node isn't null then toggle the pannel
        if(node != null) {
            setOptionsSidePanel(this.currentNode.getShapeType());
        }
        
        // Sets canvas color to false so non canvas color operations can function
        canvasColor = false;
    }

    // Helper function that draws the circle based on the area selected
    private void drawOval() {
        CanvasElements newCircle = new CanvasElements(this.wh, this.hg, "circle", controlHelper.getStage());
        newCircle.setTranslateX(startX - 10); newCircle.setTranslateY(startY - 10);

        // Sets current circle as current node
        setCurrentNode(newCircle);
        toggleSelectionBox(newCircle);

        // Adds new imageview (and its handlers) to children list of canvaspane
        canvasPane.getChildren().add(newCircle);
    }

    // Helper function that draws the rectangle based on the area selected
    private void drawRect() {
        // Initializes the selection box containing the rectangle
        CanvasElements newRect = new CanvasElements(this.wh, this.hg, "rectangle", controlHelper.getStage());
        newRect.setTranslateX(startX - 10); newRect.setTranslateY(startY - 10);
        
        // Sets current rectangle as current node
        setCurrentNode(newRect);
        toggleSelectionBox(newRect);

        // Adds new textbox to children list of canvaspane
        canvasPane.getChildren().add(newRect);
    }

    // Helper funtion that places a textbox on clicked area
    private void drawTextBox() {
        // Initializes the selection box containing the textbox
        CanvasElements newText = new CanvasElements(this.wh, this.hg, "text", controlHelper.getStage());
        newText.setTranslateX(startX - 10); newText.setTranslateY(startY - 10);
        
        // Sets current textbox as current node
        setCurrentNode(newText);
        toggleSelectionBox(newText);

        // Adds new textbox to children list of canvaspane
        canvasPane.getChildren().add(newText);
    }

    // Helper function that places an image on clicked area
    private void drawImage() {
        CanvasElements newImg = new CanvasElements(this.wh, this.hg, "image", controlHelper.getStage());
        newImg.setTranslateX(startX - 10); newImg.setTranslateY(startY - 10);

        // Sets current image as current node
        setCurrentNode(newImg);
        toggleSelectionBox(newImg);

        // Adds new imageview (and its handlers) to children list of canvaspane
        canvasPane.getChildren().add(newImg);

        // Sets drawimage to false so the user can click on other elements without turning the brush off
        this.drawImage = false;
    }

    // Helper function that displays an effect of the circle being drawn on fx canvas (invisible layer on top of canvaspane)
    private void drawOvalEffect() {
        // Calculates width and height of circle
        this.wh = lastX - startX;
        this.hg = lastY - startY;

        // Sets line width of graphicscontext
        gcF.setLineWidth(10);

        // Clears graphiccontext
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);

        // Start drawing the circle effect
        gcF.setFill(Color.AQUA);
        gcF.fillOval(startX, startY, this.wh, this.hg);
       }

    // Helper function that displays an effect of the rectangle being drawn on fx canvas (invisible layer on top of canvaspane)
    private void drawRectEffect() {
        // Calculates width and height of rectangle
        this.wh = lastX - startX;
        this.hg = lastY - startY;

        // Sets line width of graphicscontext
        gcF.setLineWidth(10);

        // Clears graphiccontext
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);

        // Start drawing rectangle effect
        gcF.setFill(Color.AQUA);
        gcF.fillRect(startX, startY, this.wh, this.hg);
    }
    //#endregion
}
