import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.event.EventHandler;

import java.time.LocalDate;

import java.util.HashMap;

import java.io.IOException;

public class MapViewerController extends ViewerController {

    @FXML
    private AnchorPane mapAnchorPane, polygonPane;

    @FXML
    private BorderPane viewPane;

    @FXML
    private Label deathsHoverLabel, baseHoverTotal, percentLabel, hoverBoxBoroughLabel, selectedBoroughLabel;
    
    // Small info box that is shown about a borough when it is hovered over
    @FXML
    private AnchorPane hoverBox;

    @FXML
    private Polygon brentPolygon, bexleyPolygon, bromleyPolygon, camdenPolygon, cityPolygon, croydonPolygon,
            ealingPolygon, enfieldPolygon, greenwichPolygon, hackneyPolygon, hamletsPolygon, hammfullPolygon,
            haringeyPolygon, harrowPolygon, haveringPolygon, hillingdonPolygon, hounslowPolygon, islingtonPolygon,
            kensChelsPolygon, kingstonPolygon, lambethPolygon, lewishamPolygon, mertonPolygon, newhamPolygon,
            redbridgePolygon, richmondPolygon, southwarkPolygon, suttonPolygon, thamesPolygon, walthamPolygon,
            wandsworthPolygon, westminsterPolygon, barkDagPolygon, barnetPolygon;

    private Polygon[] boroughPolygons;

    @FXML
    private Label messageLabel;

    // Used to map polygon IDs to String borough names
    private HashMap<String, String> boroughIdToName;

    // Stores the sum of new cases in the date range for each borough 
    private HashMap<String, Integer> boroughDeathsInDateRange;

    // The highest death sum in the date range
    private Integer highestDeathsInRange;
    
    // Initial attributes of the polygon, before hover changes
    private Paint hoveredPolygonInitialBorderColor;
    private Double hoveredPolygonInitialStroke;

    // Mouse position relative to the mapAnchorPane
    private Double infoPaneX = -100.0;
    private Double infoPaneY = -100.0;
    
    /**
     * Initialises the controller with attributes used to render the map as required.
     */
    @FXML
    protected void initialize() {
        // Adding window size change listeners to resize map properly
        viewPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
            }
        });

        viewPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
            }
        });

        // Array of all the borough polygons which will be used when assigning colours
        boroughPolygons = new Polygon[] { brentPolygon, bexleyPolygon, bromleyPolygon, camdenPolygon, cityPolygon,
                croydonPolygon, ealingPolygon, enfieldPolygon, greenwichPolygon, hackneyPolygon, hamletsPolygon,
                hammfullPolygon, haringeyPolygon, harrowPolygon, haveringPolygon, hillingdonPolygon, hounslowPolygon,
                islingtonPolygon, kensChelsPolygon, kingstonPolygon, lambethPolygon, lewishamPolygon, mertonPolygon,
                newhamPolygon, redbridgePolygon, richmondPolygon, southwarkPolygon, suttonPolygon,
                walthamPolygon, wandsworthPolygon, westminsterPolygon, barkDagPolygon, barnetPolygon };

        // boroughIDs.json maps every polygon ID to the corresponding borough name
        JsonReader jsonReader = new JsonReader();
        boroughIdToName = jsonReader.readJson("boroughIDs.json");
    }

    /**
     * Processes the data within the given date range by resetting the heat map data, 
     * validating the date range, and assigning colors to the boroughs.
     * 
     * @param fromDate The start date of the date range (inclusive) to be processed.
     * @param toDate The end date of the date range (inclusive) to be processed.
     */
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);
        assignBoroughsColor();
    }

    /**
     * Displays appropriate message to user depending on if the data is valid or not.
     * 
     * @param fromDate The start date of the date range (inclusive) to validate.
     * @param toDate The end date of the date range (inclusive) to validate.
     */
    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (dataset.isDateRangeValid(fromDate, toDate)) {
            messageLabel.setText("Click on a borough to view more information");
            // Calculate the number of deaths in the borough for the date range
            calculateBoroughDeathsInDateRange(fromDate, toDate);
        } else {
            // Reason the date range chosen is invalid
            messageLabel.setText("The 'from' date is after the 'to' date!");
        }
    }

    // -------------------------------- Resize Components -------------------------------- //

    /**
     * Resizes the map relative to the size of the parentPane (MainWindow).
     * 
     * @param parentPane The parentPane that is being resized.
     */
    protected void resizeComponents(Region parentPane) {
        // Calculate the ratio of the current width/height relative to the original width/height
        double ratioX = parentPane.getWidth() / parentPane.getPrefWidth();
        double ratioY = parentPane.getHeight() / parentPane.getPrefHeight();

        // Calculate the scale factor (s.f.) the window was scaled by (limited to s.f. of 1, and max s.f. of 2)
        ratioX = Math.max(Math.min(ratioX, 2), 1);
        ratioY = Math.max(Math.min(ratioY, 2), 1);
        
        // Scale by same ratio allowing aspect ratio to be maintained
        double scale = Math.min(ratioX, ratioY);
        
        // Get the centre of the MainWindow and scale relative to the current MainWindow size
        Node toScale = viewPane.getCenter();
        
        toScale.setScaleY(scale);
        toScale.setScaleX(scale);
    }

    // -------------------------------- Heat Map Processing -------------------------------- //
    
    /**
     * Resets the the sum of new deaths for all boroughs in the previous date range, ready to be loaded with new data for new date range.
     */
    private void resetBoroughDeathsInDateRange() {
        // Reset the maximum value
        highestDeathsInRange = 0;
        
        // Clear values of map
        boroughDeathsInDateRange = new HashMap<>();
        for (String boroughName : dataset.getBoroughs()) {
            boroughDeathsInDateRange.put(boroughName, null);
        }
    }

    /**
     * Calculates the number of deaths in the given date range for each borough in the dataset
     * by summing all of the new deaths on each day for each borough.
     * 
     * Also calculates the maximum number of deaths in the date range, by checking if the updated
     * value is larger than the current maximum. This is to avoid multiple iterations over the 
     * relatively large dataset.
     * 
     * @param fromDate The start date of the date range (inclusive) to calculate deaths in
     * @param toDate The end date of the date range (inclusive) to calculate deaths in
     */
    private void calculateBoroughDeathsInDateRange(LocalDate fromDate, LocalDate toDate) {
        resetBoroughDeathsInDateRange();

        for (CovidData record : dataset.getDataInDateRange(fromDate, toDate)) {
            String boroughName = record.getBorough();
            
            Integer deathsOnDay = record.getNewDeaths();
            Integer deathsInDateRange = boroughDeathsInDateRange.get(boroughName);

            // If no new deaths on this day, continue onto the next record
            if (deathsOnDay == null) {
                continue;
            }
            
            // If this is the first record for the borough checked, set initial value
            if (deathsInDateRange == null) {
                deathsInDateRange = deathsOnDay;
            } else {
                // Update cumulative count of deaths for borough
                deathsInDateRange += deathsOnDay;
            }
            
            boroughDeathsInDateRange.put(boroughName, deathsInDateRange);

            // Check if value calculated is larger than the maximum deaths in the range for all boroughs.
            highestDeathsInRange = Math.max(deathsInDateRange, highestDeathsInRange);
        }

    }

    /**
     * Assigns colours to borough polygons based on the number of deaths in the given date range.
     * The colour of a borough is determined relative to the maximum number of deaths in the date range.
     * If a borough has no data within the date range, it is assigned a grey colour.
     */
    private void assignBoroughsColor() {
        for (Polygon boroughPolygon : boroughPolygons) {
            String boroughName = boroughIdToName.get(boroughPolygon.getId());
            // the colour the borough is assigned is based on the number of deaths for the borough in the date range
            Integer deathsInDateRangeForBorough = boroughDeathsInDateRange.get(boroughName);
            Color col;  // colour to assign the borough

            // If the borough has data within the date range, give it a colour.
            if (deathsInDateRangeForBorough != null && highestDeathsInRange > 0) {
                // Calculate the colour of the borough based on its deaths relative to the maximum deaths in the date range
                // In HSB, hue is measured in degrees where 0 -> 120 == red -> green.
                double hueUpperBound = 105.0;
                double percentageOfHue = (hueUpperBound * deathsInDateRangeForBorough / highestDeathsInRange);

                // Subtracting from the upper bound gives us reversed scale
                // The more red, the closer to the maximum deaths value
                double hue = hueUpperBound - percentageOfHue;

                // HSB value for borough (hue (in degrees), saturation=100%, brightness=80%)
                col = Color.hsb(hue, 1, 0.8);
            } else {
                // If no data within the range, assign borough to grey.
                col = Color.rgb(171, 171, 171);
            }

            // Set the colour of the borough.
            boroughPolygon.setFill(col);
        }
    }

    // -------------------------------- Polygon Hovering -------------------------------- //

    /**
     * Displays an info box on the borough being hovered over, as well as changing the
     * visual attributes of the borough polygon to give a visual cue to the user which
     * polygon they are hovering over.
     * 
     * @param event The mouse event that triggered this method
     */
    @FXML
    void mouseEnteredPolygon(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();
        String name = boroughIdToName.get(poly.getId());

        // Set the coordinates of the info pane to mouse coords + padding
        determineInfoPaneCoordinates(event, poly);

        // Add an event handler to get the mouse coordinates when the user moves the
        // mouse within the polygon
        poly.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // calculate the x and y coordinates with additional padding
                determineInfoPaneCoordinates(event, poly);
                showBoroughInfoOnHover(poly);
            }
        });

        // When hovering, show a box of information about the borough
        showBoroughInfoOnHover(poly);

        // Change label to hovered borough's name
        selectedBoroughLabel.setText(name);

        // Store the polygon's initial properties
        hoveredPolygonInitialBorderColor = poly.getStroke();

        // Add stroke around polygon to show it is being hovered over
        poly.setStrokeWidth(3);
        poly.setStroke(new Color(1, 1, 1, 1.0));
    }

    /**
     * If the mouse leaves the polygon, the info box is hidden and the visual
     * attributes of the polygon are reset.
     * 
     * @param event The mouse event that triggered this method
     */
    @FXML
    void mouseExitedPolygon(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();

        // If mouse leaves polygon, disable visiblity of hover box
        hoverBox.setVisible(false);
        
        // Reset visual attributes
        poly.setStrokeWidth(1);
        poly.setStroke(hoveredPolygonInitialBorderColor);

        // Remove text if no borough is selected
        selectedBoroughLabel.setText("");
    }

    /**
     * calculate the x and y coordinates for info box pane whilst hovering over a
     * polygon relative to the polygonPane to get coordinates on the window
     * 
     * @param event mouse event
     * @param poly  polygon that's being hovered over
     */
    private void determineInfoPaneCoordinates(MouseEvent event, Polygon poly) {
        // polygonPane
        infoPaneX = event.getX() + poly.getLayoutX() + polygonPane.getLayoutX() + 40;
        infoPaneY = event.getY() + poly.getLayoutY() + polygonPane.getLayoutY() - 10;
    }

    /**
     * Displays a box with information about the specified borough when hovering over
     * its corresponding polygon.
     * 
     * @param poly The borough polygon that was hovered over
     */
    private void showBoroughInfoOnHover(Polygon poly) {
        // Show the info box
        hoverBox.setVisible(true);

        String boroughName = boroughIdToName.get(poly.getId());

        // Retrieve the deaths in the date range for that borough
        Integer deathsInDateRangeForBorough = boroughDeathsInDateRange.get(boroughName);

        // Calculate the value as a percentage of the highest death count in the date range
        Integer percentage = null;
        if (deathsInDateRangeForBorough != null && highestDeathsInRange > 0) {
            percentage = (int) Math.round((100.0 * deathsInDateRangeForBorough / highestDeathsInRange));
        }

        hoverBoxBoroughLabel.setText(boroughName);
        deathsHoverLabel.setText("Borough Deaths: " + boroughDeathsInDateRange.get(boroughName) + "\n" + percentage
                + "% of highest deaths within date range");

        // Position the hover box
        hoverBox.setLayoutX(infoPaneX);
        hoverBox.setLayoutY(infoPaneY);
    }

    // -------------------------------- Polygon Clicking -------------------------------- //

    /**
     * Displays the data for the borough polygon clicked in a new window.
     * 
     * @param event MouseEvent of the polygon being clicked
     * @throws IOException if an error occurs while loading the FXML file
     */
    @FXML
    void mousePressedPolygon(MouseEvent event) throws IOException {
        // Get which borough was clicked
        Polygon poly = (Polygon) event.getSource();
        String borough = boroughIdToName.get(poly.getId());
        
        // Show data for that borough
        showBoroughData(borough);
    }

     /**
     * Displays a pop-up window with information about the specified borough.
     * 
     * @param boroughName The name of the borough that data is being displayed
     * @throws IOException if there is an error loading the FXML file
     */
    private void showBoroughData(String boroughName) throws IOException {
        // Load and stage the FXML file
        Stage stage = new Stage();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoroughInfo.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        stage.setTitle(boroughName);
        stage.setScene(scene);
        
        // Sets owner of pop-up to the MainWindow
        stage.initOwner(mapAnchorPane.getScene().getWindow());
        
        BoroughInfoController controller = loader.getController();
        // Load data into pop-up controller
        controller.showData(dataset.getBoroughData(boroughName, fromDate, toDate));
        
        stage.show();
    }
    
    // -------------------------------- Misc -------------------------------- //
    
    /**
     * @return The view that this controller is associated with.
     */
    protected Parent getView() {
        return viewPane;
    }
}