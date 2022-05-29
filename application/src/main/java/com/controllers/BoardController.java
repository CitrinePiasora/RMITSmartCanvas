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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import java.awt.image.RenderedImage;

import com.controllers.Helpers.CalculationsNConversions;
import com.controllers.Helpers.ControllerHelper;
import com.models.User;

public class BoardController implements Initializable {

    //#region: Initializes JavaFX nodes found in FXML
    @FXML private ImageView profilePicture;
    @FXML private Label nameLabel;
    @FXML private Slider zoomSlider;
    @FXML private Slider textHeight;
    @FXML private Slider textWidth;
    @FXML private Slider rotateSlider;
    @FXML private Label zoomPercent;
    @FXML private MenuItem saveButton;
    @FXML private MenuItem clearButton;
    @FXML private StackPane zoomPane;
    @FXML private Pane canvasPane;
    @FXML private Canvas FXCanvas;
    @FXML private ComboBox<String> FontDropdown;
    @FXML private ComboBox<String> backgroundDropdown;
    @FXML private ComboBox<String> bordColorDropdown;
    @FXML private ComboBox<String> textColorDropdown;
    @FXML private Button boldBtn;
    @FXML private Button italicsBtn;
    @FXML private Button leftBtn;
    @FXML private Button rightBtn;
    @FXML private Button centerBtn;
    @FXML private Label bordColor;
    @FXML private Label borderWidth;
    @FXML private Label backgroundLabel;
    @FXML private Label textAlignment;
    @FXML private Label textAttributes;
    @FXML private Label textContents;
    @FXML private Label textFont;
    @FXML private Label textHeightLabel;
    @FXML private Label textWidthLabel;
    @FXML private Label textRotateLabel;
    @FXML private Label textColor;
    @FXML private Label fontSizeLabel;
    @FXML private TextField borderWidthIn;
    @FXML private TextField textInput;
    @FXML private TextField fontSize;
    //#endregion

    //initialize session variables
    private User currentUser;
    
    // Initialize controllerhelper and calculationshelper
    private ControllerHelper controlHelper;
    private CalculationsNConversions calcHelper = new CalculationsNConversions();

    //#region: Initializes canvas related variables
    private GraphicsContext gcF;
    private double percentageCalc = 1.0, previousTextHeight = 0.0, previousTextWidth = 0.0, textDefHeight, textDefWidth;
    private int canvasH, canvasW;
    private boolean drawoval = false, drawrectangle = false, drawtext = false, drawImage = false, initChoices = false, canvasColor = false;
    private double startX, startY, lastX, lastY, originX, originY;
    private double wh, hg;
    private Group currentNode;

    // Initializes shapes used for selection boxes
    private Line top, left, right, bottom;
    private Rectangle topL, topR, botL, botR;
    private Circle rotCircle;

    // Initialize list of nodes to loop through to shorten code
    private List<Label> labels; List<Control> controls; List<Button> buttons;
    // Initializes list of colors for color based combo boxes
    private Map<String, Color> colors = new HashMap<String, Color>(); {
        colors.put("Aqua", Color.AQUA);
        colors.put("Black", Color.BLACK);
        colors.put("Beige", Color.BEIGE);
        colors.put("Brown", Color.BROWN);
        colors.put("Fuchsia", Color.FUCHSIA);
        colors.put("Gray", Color.GRAY);
        colors.put("Green", Color.GREEN);
        colors.put("Lime", Color.LIME);
        colors.put("Maroon", Color.MAROON);
        colors.put("Navy Blue", Color.NAVY);
        colors.put("Olive", Color.OLIVE);
        colors.put("Purple", Color.PURPLE);
        colors.put("Red", Color.RED);
        colors.put("Silver", Color.SILVER);
        colors.put("Teal", Color.TEAL);
        colors.put("White", Color.WHITE);
        colors.put("Yellow", Color.YELLOW);
        
    }

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
        this.textDefHeight = new Label("text").getPrefHeight();
        this.textDefWidth = new Label("text").getPrefWidth();

        labels = Arrays.asList(bordColor, borderWidth, backgroundLabel, textAlignment, fontSizeLabel, textAttributes, textContents, textFont, textHeightLabel, textWidthLabel, textRotateLabel, textColor);
        controls = Arrays.asList(bordColorDropdown, borderWidthIn, backgroundDropdown, fontSize, textInput, textHeight, textWidth, rotateSlider, textColorDropdown, FontDropdown);
        buttons = Arrays.asList(boldBtn, italicsBtn, leftBtn, rightBtn, centerBtn, leftBtn, rightBtn, centerBtn);
        //#endregion

        //#region: Initialize Canvas Properties
        this.canvasH = canvasHeight; this.canvasW = canvasWidth;
        this.canvasPane.setPrefHeight(canvasHeight); this.canvasPane.setPrefWidth(canvasWidth);
        this.FXCanvas.setHeight(canvasHeight); this.FXCanvas.setWidth(canvasWidth);
        this.canvasPane.setStyle("-fx-border-color: gray; -fx-background-color: " + calcHelper.webFormatter(colors.get("White")) + "; ");
        this.gcF = this.FXCanvas.getGraphicsContext2D();
        
        // Initialize state of menu items and canvas
        this.clearButton.setDisable(!canvasActive);
        this.saveButton.setDisable(!canvasActive);
        this.canvasPane.setVisible(canvasActive);

        // Initialize session
        this.currentUser = user; 
        //#endregion

        // Initialize border color dropdown event
        EventHandler<ActionEvent> bordColorEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if(!initChoices) {
                    Color chosen = colors.get(bordColorDropdown.getValue());
                    String webFormat = calcHelper.webFormatter(chosen);

                    if(canvasColor) {
                        String style = calcHelper.cssReplacerAppender(canvasPane, "-fx-background-color:", webFormat);
                        canvasPane.setStyle(style);
                    } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Rectangle")) {
                        Rectangle temp = (Rectangle) currentNode.getChildren().get(0);
                        temp.setStroke(chosen);
                        currentNode.getChildren().set(0, temp);

                    } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Ellipse")){
                        Ellipse temp = (Ellipse) currentNode.getChildren().get(0);
                        temp.setStroke(chosen);
                        currentNode.getChildren().set(0, temp);

                    } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.control.Label")) {
                        Label temp = (Label) currentNode.getChildren().get(0);
                        String style = calcHelper.cssReplacerAppender(temp, "-fx-border-color:", webFormat);
                        temp.setStyle(style);
                        currentNode.getChildren().set(0, temp);
                    }
                }
            }
        };
        this.bordColorDropdown.setOnAction(bordColorEvent);
        this.bordColorDropdown.setItems(FXCollections.observableArrayList(colors.keySet()).sorted());

        // Initialize background color dropdown event 1
        EventHandler<ActionEvent> bgColorEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if(!initChoices) {
                    Color chosen = colors.get(backgroundDropdown.getValue());
                    String webFormat = calcHelper.webFormatter(chosen);

                    if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Rectangle")) {
                        Rectangle temp = (Rectangle) currentNode.getChildren().get(0);
                        temp.setFill(chosen);
                        currentNode.getChildren().set(0, temp);

                    } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Ellipse")){
                        Ellipse temp = (Ellipse) currentNode.getChildren().get(0);
                        temp.setFill(chosen);
                        currentNode.getChildren().set(0, temp);

                    } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.control.Label")) {
                        Label temp = (Label) currentNode.getChildren().get(0);
                        String style = calcHelper.cssReplacerAppender(temp, "-fx-background-color:", webFormat);
                        temp.setStyle(style);
                        currentNode.getChildren().set(0, temp);
                    }
                }
            }
        };
        this.backgroundDropdown.setOnAction(bgColorEvent);
        this.backgroundDropdown.setItems(FXCollections.observableArrayList(colors.keySet()).sorted());

        // Initializes text color dropdown event
        EventHandler<ActionEvent> textColorEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                if(!initChoices) {
                    Color chosen = colors.get(textColorDropdown.getValue());
                    Label temp = (Label) currentNode.getChildren().get(0);
                    temp.setTextFill(chosen);
                    currentNode.getChildren().set(0, temp);
                }
            }
        };
        this.textColorDropdown.setOnAction(textColorEvent);
        this.textColorDropdown.setItems(FXCollections.observableArrayList(colors.keySet()).sorted());

        // Initializes font dropdown event
        EventHandler<ActionEvent> fontChangeEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                if(!initChoices) {
                    Label temp = (Label) currentNode.getChildren().get(0);
                    String style = calcHelper.cssReplacerAppender(temp, "-fx-font-family:", fontFamilies.get(FontDropdown.getValue()));
                    temp.setStyle(style);
                    currentNode.getChildren().set(0, temp);
                }
            }
            
        };
        this.FontDropdown.setOnAction(fontChangeEvent);
        this.FontDropdown.setItems(FXCollections.observableArrayList(fontFamilies.keySet()).sorted());

        //#region: Initialize slider actions
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

        // Adds a listener to the slider in order to change the height of the textbox
        textHeight.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                if(!initChoices) {
                    double sliderValue = (double) textHeight.getValue();
                    Label temp = (Label) currentNode.getChildren().get(0);
                    if(sliderValue > previousTextHeight) {
                        temp.setPrefHeight(textDefHeight + (sliderValue * 2));
                    } else if(sliderValue < previousTextHeight) {
                        temp.setPrefHeight(temp.getHeight() - (sliderValue * 2));
                    }
                    
                    currentNode.getChildren().set(0, temp);
                    previousTextHeight = sliderValue;
                }
            }
        });

        // Adds a listener to the slider in order to change the width of the textbox
        textWidth.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                if(!initChoices) {
                    double sliderValue = (double) textWidth.getValue();
                    Label temp = (Label) currentNode.getChildren().get(0);
                    if(sliderValue > previousTextWidth) {
                        temp.setPrefWidth(textDefWidth + (sliderValue * 2));
                    } else if(sliderValue < previousTextWidth) {
                        temp.setPrefWidth(temp.getPrefWidth() - (sliderValue * 2));
                    }
                    
                    currentNode.getChildren().set(0, temp);
                    previousTextWidth = sliderValue;
                }
            }
        });
        
        // Adds a listener to the slider in order to change the angle of rotation of textbox
        rotateSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                if(!initChoices) {
                    double sliderValue = rotateSlider.getValue();
                    currentNode.setRotate(360 - sliderValue);
                }
            }
            
        });
        //#endregion
    }

    //#region: FXML Functions
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
                for(String item : colors.keySet()) {
                    if(calcHelper.webFormatter(colors.get(item)).equals(parseString[1])) {
                        SingleSelectionModel<String> options = bordColorDropdown.getSelectionModel();
                        options.select(item);
                        bordColorDropdown.setSelectionModel(options);
                        break;
                    }
                }
                break;
            }
        }

        this.initChoices = false;
        canvasColor = true;
    }

    @FXML // FX Function, when user clicks mouse anywhere within canvaspane
    private void onMousePressedListener(MouseEvent e) {
        // Sets values for startx and starty
        this.startX = e.getX();
        this.startY = e.getY();

        // If text brush is active
        if(drawtext) {
            // Invokes the draw text function
            drawTextBox();
        // Else if image brush is active
        }
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

        // If rectangle brush is active
        if(drawrectangle) {
            // Invokes the draw rectangle effect function
            drawRectEffect();
        // Else if circle brush is active
        } else if(drawoval) {
            // Invokes the draw circle effect function
            drawOvalEffect();
        } else if(drawImage) {
            // Invokes the draw image function
            drawRectEffect();
        }
    }

    @FXML // FX Function, when mouse is released on fxcanvas (invisible layer on top of canvaspane)
    private void FXMouseRelease(MouseEvent e) {
        // If rectangle brush is active
        if(drawrectangle) {
            // Invokes the draw rectangle function
            drawRect();
        // Else if circle brush is active
        } else if(drawoval) {
            // Invokes the draw circle function
            drawOval();
        } else if(drawImage) {
            // Invokes the draw image function
            drawImage();
        }

        // Clears the graphicscontext for fxcanvas 
        gcF.clearRect(0, 0, this.canvasW, this.canvasH);
    }

    @FXML
    void textBold(ActionEvent event) {
        Label temp = (Label) currentNode.getChildren().get(0);
        String style = calcHelper.cssReplacerAppender(temp, "-fx-font-weight:", "bold");
        String buttonStyle = calcHelper.cssReplacerAppender(boldBtn, "-fx-background-color:", "gray");
        boldBtn.setStyle(buttonStyle);
        temp.setStyle(style);
        currentNode.getChildren().set(0, temp);
    }

    @FXML
    void textItalics(ActionEvent event) {
        Label temp = (Label) currentNode.getChildren().get(0);
        String style = calcHelper.cssReplacerAppender(temp, "-fx-font-style:", "italic");
        String buttonStyle = calcHelper.cssReplacerAppender(italicsBtn, "-fx-background-color:", "gray");
        italicsBtn.setStyle(buttonStyle);
        temp.setStyle(style);
        currentNode.getChildren().set(0, temp);
    }
    
    @FXML // FX Function to change text alignment to left alignment
    void leftAlign(ActionEvent event) {
        Label temp = (Label) currentNode.getChildren().get(0);

        // Changes button color if not currently selected
        if(temp.getAlignment() != Pos.CENTER_LEFT) {
            leftBtn.setStyle("-fx-background-color: gray");
            centerBtn.setStyle("");
            rightBtn.setStyle("");
        }

        // Set text alignment
        temp.setAlignment(Pos.CENTER_LEFT);
        currentNode.getChildren().set(0, temp);
    }

    @FXML // FX Function to change text alignment to right alignment
    void rightAlign(ActionEvent event) {
        Label temp = (Label) currentNode.getChildren().get(0);

        // Changes button color if not currently selected
        if(temp.getAlignment() != Pos.CENTER_RIGHT) {
            rightBtn.setStyle("-fx-background-color: gray");
            centerBtn.setStyle("");
            leftBtn.setStyle("");
        }

        // Set text alignment
        temp.setAlignment(Pos.CENTER_RIGHT);
        currentNode.getChildren().set(0, temp);
    }

    @FXML // FX Function to change text alignment to center alignment
    void centerAlign(ActionEvent event) {
        Label temp = (Label) currentNode.getChildren().get(0);

        // Changes button color if not currently selected
        if(temp.getAlignment() != Pos.CENTER) {
            centerBtn.setStyle("-fx-background-color: gray");
            leftBtn.setStyle("");
            rightBtn.setStyle("");
        }

        // Set text alignment
        temp.setAlignment(Pos.CENTER);
        currentNode.getChildren().set(0, temp);
    }

    @FXML // FX Function to change border of node
    void borderChange(ActionEvent event) {
        // Try to change width of border
        try {
            double newBorderWidth = Double.parseDouble(borderWidthIn.getText());

            // Change width of border based on class of node
            if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Ellipse")) {
                Ellipse temp = (Ellipse) currentNode.getChildren().get(0);
                temp.setStrokeWidth(newBorderWidth);
                currentNode.getChildren().set(0, temp);
            } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Rectangle")) {
                Rectangle temp = (Rectangle) currentNode.getChildren().get(0);
                temp.setStrokeWidth(newBorderWidth);
                currentNode.getChildren().set(0, temp);
            } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.control.Label")) {
                Label temp = (Label) currentNode.getChildren().get(0);
                String style = calcHelper.cssReplacerAppender(temp, "-fx-border-width:", borderWidthIn.getText());
                temp.setStyle(style);
                currentNode.getChildren().set(0, temp);
            }
        // If calculations failed (due to invalid input), send error message
        } catch (Exception e) {
            controlHelper.initStage("ErrorPopup", "Error, invalid border width", null, null, "popup");
        }
    }

    @FXML // FX Function to change the text of textbox element
    void setLabelText(ActionEvent event) {
        Label temp = (Label) currentNode.getChildren().get(0);
        temp.setText(textInput.getText());
        currentNode.getChildren().set(0, temp);
    }   

    @FXML // FX Function to change font size of textbox element
    void setFontSize(ActionEvent event) {
        // Tries to change current font size
        try {
            Integer.parseInt(fontSize.getText());
            Label temp = (Label) currentNode.getChildren().get(0);
            String style = calcHelper.cssReplacerAppender(temp, "-fx-font-size:", fontSize.getText());
            temp.setStyle(style);
            currentNode.getChildren().set(0, temp);
        // If the input is invalid, sends an error
        } catch (Exception e) {
            controlHelper.initStage("ErrorPopup", "Error, invalid border width", null, null, "popup");
        }
    }    
    //#endregion

    //#region: helper functions
    // Helper function that sets current node
    private void setCurrentNode(Group node) {
        this.initChoices = true;
        // If the current node isn't null and not a label or image view
        if(this.currentNode != null && !this.currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.control.Label")) {
            // Set all non shape nodes off and invisible
            for(int i = 1; i < this.currentNode.getChildren().size(); i++) {
                this.currentNode.getChildren().get(i).setDisable(true);
                this.currentNode.getChildren().get(i).setVisible(false);
            }
        }

        // Set the current group
        this.currentNode = node;
        
        // If node isn't null and isn't a label
        if(node != null && !node.getChildren().get(0).getClass().getName().equals("javafx.scene.control.Label")) {
            // Enable the bounding box
            for(int i = 1; i < this.currentNode.getChildren().size(); i++) {
                this.currentNode.getChildren().get(i).setDisable(false);
                this.currentNode.getChildren().get(i).setVisible(true);
            }

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

            // If the current node's first child is rectangle, get stroke width
            if(this.currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Rectangle")) {
                Rectangle temp = (Rectangle) this.currentNode.getChildren().get(0);
                borderWidthIn.setText(Character.toString(Double.toString(temp.getStrokeWidth()).charAt(0)));

                // Set dropdown value according to the shape's parameters
                for(String item : colors.keySet()) {
                    if(colors.get(item) == temp.getStroke()) {
                        SingleSelectionModel<String> options = bordColorDropdown.getSelectionModel();
                        options.select(item);
                        bordColorDropdown.setSelectionModel(options);
                        break;
                    }
                }
                for(String item : colors.keySet()) {
                    if(colors.get(item) == temp.getFill()) {
                        SingleSelectionModel<String> options = backgroundDropdown.getSelectionModel();
                        options.select(item);
                        backgroundDropdown.setSelectionModel(options);
                        break;
                    }
                }
            // If the current node's first child is Ellipse, get stroke width
            } else if(this.currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Ellipse")) {
                Ellipse temp = (Ellipse) this.currentNode.getChildren().get(0);
                borderWidthIn.setText(Character.toString(Double.toString(temp.getStrokeWidth()).charAt(0)));
                
                // Set dropdown value according to the shape's parameters
                for(String item : colors.keySet()) {
                    if(colors.get(item) == temp.getStroke()) {
                        SingleSelectionModel<String> options = bordColorDropdown.getSelectionModel();
                        options.select(item);
                        bordColorDropdown.setSelectionModel(options);
                    }
                }
                for(String item : colors.keySet()) {
                    if(colors.get(item) == temp.getFill()) {
                        SingleSelectionModel<String> options = backgroundDropdown.getSelectionModel();
                        options.select(item);
                        backgroundDropdown.setSelectionModel(options);
                        break;
                    }
                }
            }
            
            canvasColor = false;
        // If node being set isn't null
        } else if(node != null){
            // Loops to enable all buttons, controls and labels
            for(Button input : buttons) {
                input.setVisible(true); input.setDisable(false);
            }
            for(Node input : controls) {
                input.setVisible(true); input.setDisable(false);
            }
            for(int i = 0; i < labels.size(); i++) {
                if(i == 0) {
                    labels.get(i).setVisible(true); labels.get(i).setText("Border Color");
                } else {
                    labels.get(i).setVisible(true);
                }
            }

            // Initialize variables for looping through the CSS Styles
            Label temp = (Label) this.currentNode.getChildren().get(0);
            String[] cssStyle = temp.getStyle().split("; ");
            boolean italicSet = false, boldSet = false;

            // Loop through the stylesheet
            for(String string : cssStyle) {
                String[] parseString = string.split(" ");
                // Checks for border width and set input textfield to current border width
                if(parseString[0].equals("-fx-border-width:")) {
                    borderWidthIn.setText(parseString[1]);
                
                // Checks for background color and set dropdown selection to said color
                } else if(parseString[0].equals("-fx-background-color:")) {
                    for(String item : colors.keySet()) {
                        if(calcHelper.webFormatter(colors.get(item)).equals(parseString[1])) {
                            SingleSelectionModel<String> options = backgroundDropdown.getSelectionModel();
                            options.select(item);
                            backgroundDropdown.setSelectionModel(options);
                        }
                    }
                // Checks for border color and set dropdown selection to said color
                } else if(parseString[0].equals("-fx-border-color:")) {
                    for(String item : colors.keySet()) {
                        if(calcHelper.webFormatter(colors.get(item)).equals(parseString[1])) {
                            SingleSelectionModel<String> options = bordColorDropdown.getSelectionModel();
                            options.select(item);
                            bordColorDropdown.setSelectionModel(options);
                        }
                    }
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
            for(String item: colors.keySet()) {
                if(temp.getTextFill() == colors.get(item)) {
                    SingleSelectionModel<String> options = textColorDropdown.getSelectionModel();
                    options.select(item);
                    textColorDropdown.setSelectionModel(options);
                    break;
                }
            }

            // If not bold, reset style of bold button
            if(!boldSet) {
                boldBtn.setStyle("");
            }
            // If not italic, reset style of italic button
            if(!italicSet) {
                italicsBtn.setStyle("");
            }

            // Set text input field value as label text
            textInput.setText(temp.getText());

            //#region: Initialize slider values
            rotateSlider.setValue(360 - currentNode.getRotate());
            textHeight.setValue(temp.getPrefHeight() - textDefHeight <= 0 ? 0 : temp.getPrefHeight() - textDefHeight);
            textWidth.setValue(temp.getPrefWidth() - textDefWidth <= 0 ? 0 : temp.getPrefWidth() - textDefWidth);
            this.previousTextHeight = textHeight.getValue() - 1;
            this.previousTextWidth = textWidth.getValue() - 1;
            //#endregion

            // Changes button colors based on alignment
            if(temp.getAlignment() == Pos.CENTER) {
                centerBtn.setStyle("-fx-background-color: gray");
                leftBtn.setStyle("");
                rightBtn.setStyle("");
            } else if(temp.getAlignment() == Pos.CENTER_RIGHT) {
                rightBtn.setStyle("-fx-background-color: gray");
                centerBtn.setStyle("");
                leftBtn.setStyle("");
            } else {
                leftBtn.setStyle("-fx-background-color: gray");
                rightBtn.setStyle("");
                centerBtn.setStyle("");
            }
            canvasColor = false;
        }
        this.initChoices = false;
    }

    // Helper function to make the first node (which is always the shape) draggable
    private void makeDraggable(Group node) {
        // Initialize the node by getting index 0 from group children
        Node node1 = node.getChildren().get(0);

        // On clicked, set originx, originy, disable all brushes and set group as current group
        node1.setOnMousePressed(e -> {
            originX = e.getSceneX() - node.getTranslateX();
            originY = e.getSceneY() - node.getTranslateY();

            // Turn off all brushes
            drawImage = false;
            drawoval = false;
            drawrectangle = false;
            drawtext = false;

            // Set group as current group
            setCurrentNode(node);
        });

        // Make node draggable
        node1.setOnMouseDragged(e -> {
            node.setTranslateX(e.getSceneX() - originX);
            node.setTranslateY(e.getSceneY() - originY);
        });
    }

    // TODO: fix resizing random breaking
    // Build a corner of the rectangle
    private Rectangle buildCorner (double x, double y) {

        // Create the rectangle
        Rectangle r = new Rectangle();
        r.setX(x);
        r.setY(y);
        r.setWidth(10);
        r.setHeight(10);
        r.setStroke(Color.rgb(0,0,0,0.75));
        r.setFill(Color.LIGHTGRAY);
        r.setStrokeWidth(1);
        r.setStrokeType(StrokeType.INSIDE);
        r.setCursor(Cursor.CROSSHAIR);

        // Initialize originx and originy on being clicked
        r.setOnMousePressed(e -> {
            originX = e.getSceneX() - r.getTranslateX();
            originY = e.getSceneY() - r.getTranslateY();

            drawImage = false;
            drawoval = false;
            drawrectangle = false;
            drawtext = false;
        });

        r.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - originX;
            double newY  = e.getSceneY() - originY;

            // Initialize temporary rectangle to test for bounds so resize handlers don't go through each other
            Rectangle tempRect = new Rectangle();
            tempRect.setX(r.getX()); tempRect.setY(r.getY()); tempRect.setWidth(10); tempRect.setHeight(10);
            tempRect.setTranslateX(newX); tempRect.setTranslateY(newY);

            // If current handler is top left handler
            if(r == topL) {  
                // If statements to make sure handler is within its bounds of movement
                if(topR.getBoundsInParent().getMinX() - 15 >= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    botL.setTranslateX(newX);
                }
                if(botL.getBoundsInParent().getMinY() - 15 >= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    topR.setTranslateY(newY);
                } 

                // Calculate the new positions of the lines using the bounds of the 4 handlers
                Bounds rectangleInParent = r.getBoundsInParent();
                top.setStartX(rectangleInParent.getMinX() + 5); top.setStartY(rectangleInParent.getMaxY() - 5); top.setEndY(rectangleInParent.getMaxY() - 5);
                bottom.setStartX(rectangleInParent.getMinX() + 5);
                left.setStartX(rectangleInParent.getMinX() + 5); left.setEndX(rectangleInParent.getMinX() + 5); left.setStartY(rectangleInParent.getMaxY());
                right.setStartY(rectangleInParent.getMaxY());

            } else if(r == topR) {
                // If statements to make sure handler is within its bounds of movement
                if(topL.getBoundsInParent().getMinX() + 15 <= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    botR.setTranslateX(newX);
                }
                if(botR.getBoundsInParent().getMinY() - 15 >= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    topL.setTranslateY(newY);
                } 

                // Calculate the new positions of the lines using the bounds of the 4 handlers
                Bounds rectangleInParent = r.getBoundsInParent();
                top.setEndX(rectangleInParent.getMinX() + 5); top.setStartY(rectangleInParent.getMaxY() - 5); top.setEndY(rectangleInParent.getMaxY() - 5);
                bottom.setEndX(rectangleInParent.getMinX() + 5);
                right.setStartX(rectangleInParent.getMinX() + 5); right.setEndX(rectangleInParent.getMinX() + 5); right.setStartY(rectangleInParent.getMaxY());
                left.setStartY(rectangleInParent.getMaxY());

            } else if(r == botL) {
                // If statements to make sure handler is within its bounds of movement
                if(botR.getBoundsInParent().getMinX() - 15 >= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    topL.setTranslateX(newX);
                }
                if(topL.getBoundsInParent().getMinY() + 15 <= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    botR.setTranslateY(newY);
                } 

                // Calculate the new positions of the lines using the bounds of the 4 handlers
                Bounds rectangleInParent = r.getBoundsInParent();
                top.setStartX(rectangleInParent.getMinX() + 5);
                bottom.setStartX(rectangleInParent.getMinX() + 5); bottom.setEndY(rectangleInParent.getMinY() + 5); bottom.setStartY(rectangleInParent.getMinY() + 5);
                left.setStartX(rectangleInParent.getMinX() + 5); left.setEndX(rectangleInParent.getMinX() + 5); left.setEndY(rectangleInParent.getMaxY());
                right.setEndY(rectangleInParent.getMaxY());

            } else if(r == botR) {
                // If statements to make sure handler is within its bounds of movement
                if(botL.getBoundsInParent().getMinX() + 15 <= tempRect.getBoundsInParent().getMinX()) {
                    r.setTranslateX(newX);
                    topR.setTranslateX(newX);
                }
                if(topR.getBoundsInParent().getMinY() + 20 <= tempRect.getBoundsInParent().getMinY()) {
                    r.setTranslateY(newY);
                    botL.setTranslateY(newY);
                } 

                // Calculate the new positions of the lines using the bounds of the 4 handlers
                Bounds rectangleInParent = r.getBoundsInParent();
                top.setEndX(rectangleInParent.getMinX() + 5);
                bottom.setEndX(rectangleInParent.getMinX() + 5); bottom.setStartY(rectangleInParent.getMinY() + 5); bottom.setEndY(rectangleInParent.getMinY() + 5);
                right.setStartX(rectangleInParent.getMinX() + 5); right.setEndX(rectangleInParent.getMinX() + 5); right.setEndY(rectangleInParent.getMaxY());
                left.setEndY(rectangleInParent.getMaxY());
                
            }
            
            // Sets startx and starty to minx + 5, miny + 5 and endx, endy to maxx + 5, maxy + 5
            startX = topL.getBoundsInParent().getMinX() + 5; lastX = topR.getBoundsInParent().getMinX() + 5;
            startY = topL.getBoundsInParent().getMinY() + 5; lastY = botR.getBoundsInParent().getMinY() + 5; 

            // Calculate the new height and width of the node
            wh = lastX - startX;
            hg = lastY - startY;

            // Checks what type of node the node is and does calculations to change the hight width and current position of the nodes
            if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Rectangle")) {
                Rectangle temp = (Rectangle) currentNode.getChildren().get(0);
                temp.setHeight(hg); temp.setWidth(wh);
                temp.setX(startX); temp.setY(startY);
                currentNode.getChildren().set(0, temp);

            } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.shape.Ellipse")){
                Ellipse temp = (Ellipse) currentNode.getChildren().get(0);
                temp.setRadiusX(wh/2); temp.setRadiusY(hg/2);
                temp.setCenterX((startX + lastX)/ 2); temp.setCenterY((startY + lastY)/2);
                currentNode.getChildren().set(0, temp);

            } else if(currentNode.getChildren().get(0).getClass().getName().equals("javafx.scene.image.ImageView")) {
                ImageView temp = (ImageView) currentNode.getChildren().get(0);
                temp.setFitHeight(hg); temp.setFitWidth(wh);
                temp.relocate(startX, startY);
                currentNode.getChildren().set(0, temp);

            }

            // Update rotate handler position
            Circle tempCirc = (Circle) currentNode.getChildren().get(currentNode.getChildren().size() - 1);
            tempCirc.setCenterX((lastX + startX) / 2);
            tempCirc.setCenterY(startY);
        });

        return r;
    }

    // Helper function to help build the rotation handler
    private Circle rotationHandler(Group node) {
        // Create rotation circle handler + add it to the group
        Rotate rotate = new Rotate();
        node.getTransforms().add(rotate);

        // Get the bounds of the node within parent to initialize rotation pivot in the center of the group
        Bounds nodeBounds = node.getBoundsInParent();
        rotate.setPivotX((nodeBounds.getMinX() + nodeBounds.getMaxX() + 10)/2);
        rotate.setPivotY((nodeBounds.getMinY() + nodeBounds.getMaxY() + 10)/ 2);

        // Initialize the visuals of the rotation handler
        Circle handler = new Circle(5);
        handler.setCenterX((nodeBounds.getMinX() + nodeBounds.getMaxX() + 10)/2);
        handler.setCenterY(nodeBounds.getMinY() + 5);
        handler.setFill(Color.LIGHTGRAY);
        handler.setStroke(Color.rgb(0,0,0,0.75));
        handler.setCursor(Cursor.HAND);
        
        // Initialize originx and originy on being clicked
        handler.setOnMousePressed(e -> {
            originX = e.getSceneX() - handler.getTranslateX();
            originY = e.getSceneY() - handler.getTranslateY();
        });

        // When it's dragged rotate the selected group
        handler.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {

                // Used to get the scene position of the corner of the box
                Transform localToScene = node.getLocalToParentTransform();

                // Initialize x1, x2, y1, y2
                double x1 = originX, y1 = originY, x2 = e.getSceneX(), y2 = e.getSceneY();

                // Initialize px value to calculate dx
                double px = rotate.getPivotX() + localToScene.getTx();
                double py = rotate.getPivotY() + localToScene.getTy();

                // Work out the angle rotated, theta1 being angle from origin and theta2 being angle at current mouse position
                double th1 = calcHelper.clockAngle(x1, y1, px, py);
                double th2 = calcHelper.clockAngle(x2, y2, px, py);
                double angle = rotate.getAngle();
                angle += th2 - th1;

                // Rotate the node
                rotate.setAngle(angle);

                originX = e.getSceneX() - handler.getTranslateX();
                originY = e.getSceneY() - handler.getTranslateY();
            }
        });

        return handler;
    }

    // Helper function that draws the circle based on the area selected
    private void drawOval() {
        // Calculates radiusx and radiusy of circle
        this.wh = (lastX - startX) / 2;
        this.hg = (lastY - startY) / 2;

        // Calculates the centerx and centery of circle (for placement)
        double centerX = (startX + lastX) / 2, centerY = (startY + lastY) / 2 ;

        // Initializes new circle
        Ellipse newOval = new Ellipse(centerX, centerY, wh, hg);
        newOval.setStroke(colors.get("White"));
        newOval.setFill(colors.get("Aqua"));
        newOval.setStrokeType(StrokeType.INSIDE);

        // Create the node group with the controls for resizing and rotation
        Group newGroup = createNodeGroup(newOval);

        // Adds new circle to children list of canvaspane
        canvasPane.getChildren().add(newGroup);
    }

    // Helper function that draws the rectangle based on the area selected
    private void drawRect() {
        // Calculates width and height of rectangle
        this.wh = lastX - startX;
        this.hg = lastY - startY;

        // Initializes new rectangle
        Rectangle newRect = new Rectangle(startX, startY, this.wh, this.hg);
        newRect.setFill(colors.get("Aqua"));
        newRect.setStrokeType(StrokeType.INSIDE);
        newRect.setStroke(colors.get("White"));

        // Create the node group with the controls for resizing and rotation
        Group newGroup = createNodeGroup(newRect);
        
        // Adds new rectangle (and its handlers) to children list of canvaspane
        canvasPane.getChildren().add(newGroup);
    }

    // Helper funtion that places a textbox on clicked area
    private void drawTextBox() {
        // Initialzes new textbox
        Label textBox = new Label("text");
        textBox.setTextFill(Color.BLACK);
        textBox.setStyle("-fx-border-color: " + calcHelper.webFormatter(colors.get("Black")) + "; -fx-background-color: " + calcHelper.webFormatter(colors.get("White")) + "; -fx-border-width: 1; -fx-font-size: 12; -fx-font-family: " + fontFamilies.get("Arial") + "; ");
        textBox.toFront();

        // Moves new textbox to clicked location
        textBox.relocate(this.startX, this.startY);

        // Creates textbox group
        Group textGroup = new Group();
        textGroup.getChildren().add(textBox);
        makeDraggable(textGroup);
        setCurrentNode(textGroup);

        // Adds new textbox to children list of canvaspane
        canvasPane.getChildren().addAll(textGroup);
    }

    // Helper function that places an image on clicked area
    private void drawImage() {
        // Initializes imageview
        ImageView img = new ImageView();

        // Calls helper function to set the imageview as selected image
        controlHelper.imageChooser(img);
        
        // Moves new image to clicked location
        img.setFitHeight(this.hg);
        img.setFitWidth(this.wh);
        img.relocate(this.startX, this.startY);

        // Create the node group with the controls for resizing and rotation
        Group newGroup = createNodeGroup(img);

        // Adds new imageview (and its handlers) to children list of canvaspane
        canvasPane.getChildren().addAll(newGroup);

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

    // Helper function to initialize node group of the node + its resize rotate handlers
    private Group createNodeGroup(Node node) {
        Group newGroup = new Group();

        // Get node bounds to set the proper paramaters to initialize the selection box tied to each node
        Bounds nodeBounds = node.getBoundsInParent();
        bottom = calcHelper.buildLine(nodeBounds.getMinX(), nodeBounds.getMaxX(), nodeBounds.getMaxY(), nodeBounds.getMaxY());
        top = calcHelper.buildLine(nodeBounds.getMinX(), nodeBounds.getMaxX(), nodeBounds.getMinY(), nodeBounds.getMinY());
        left = calcHelper.buildLine(nodeBounds.getMinX(), nodeBounds.getMinX(), nodeBounds.getMinY(), nodeBounds.getMaxY());
        right = calcHelper.buildLine(nodeBounds.getMaxX(), nodeBounds.getMaxX(), nodeBounds.getMinY(), nodeBounds.getMaxY());

        botL = buildCorner(nodeBounds.getMinX() - 5, nodeBounds.getMaxY() - 5);
        botR = buildCorner(nodeBounds.getMaxX() - 5, nodeBounds.getMaxY() - 5);
        topL = buildCorner(nodeBounds.getMinX() - 5, nodeBounds.getMinY() - 5);
        topR = buildCorner(nodeBounds.getMaxX() - 5, nodeBounds.getMinY() - 5);

        // Add all nodes to the new group
        newGroup.getChildren().addAll(node, bottom, top, left, right, botL, botR, topL, topR);

        // Create the rotation handler for the new group, add it to the group and initialize the group to make it draggable and current node
        rotCircle = rotationHandler(newGroup);
        newGroup.getChildren().add(rotCircle);
        makeDraggable(newGroup);
        setCurrentNode(newGroup);

        return newGroup;
    }
    //#endregion
}
