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

    Paint hoveredPolygonInitialBorderColor;
    Double hoveredPolygonInitialStroke;

    @FXML
    private AnchorPane mapAnchorPane, polygonPane;

    @FXML
    private BorderPane viewPane;

    @FXML
    private AnchorPane hoverBox;

    @FXML
    private Label deathsHoverLabel, baseHoverTotal, percentLabel, hoverBoxBoroughLabel, selectedBoroughLabel;

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

    // used to map polygon IDs to String borough names
    private HashMap<String, String> boroughIdToName;

    // stores the data for each borough which will be used to calculate intensity on
    // heat map
    private HashMap<String, Integer> heatMapData;

    // the value which is used as a base to determine the intensity for other
    // boroughs on the heat map
    private Integer heatMapBaseValue;

    // store the mouse position realtive to the mapacnhor pane
    Double infoPaneX = -100.0;
    Double infoPaneY = -100.0;

    /* ================================== MAIN ================================== */
    
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
     * called when the date has been selected/changed to 
     */
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {

        resetHeatMapData();

        validateDateRange(fromDate, toDate);

        assignBoroughsColor();
    }

    /**
     * Set the appropriate message depending on date range selected, and load data
     * if a valid date range is selected
     */
    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (dataset.isDateRangeValid(fromDate, toDate)) {
            messageLabel.setText("Click on a borough to view more information");
            // get the heat map values for each borough to colour them
            loadHeatMapData(fromDate, toDate);
        } else {
            messageLabel.setText("The 'from date' is after the 'to date' ! ");
        }
    }

    /* --------------------------- RESIZE COMPONENTS ---------------------------- */

    /**
     * resizing the map pane
     * 
     * @param parentPane pane that is to be used to scale with
     */
    protected void resizeComponents(Region parentPane) {

        // calculate current width/height relative to its original width/height
        double ratioX = parentPane.getWidth() / parentPane.getPrefWidth();
        double ratioY = parentPane.getHeight() / parentPane.getPrefHeight();

        // calculate the ratio the window was scaled by, limiting the minimum ratio to 1
        // and the maximum to 2
        ratioY = Math.max(Math.min(ratioY, 2), 1);
        ratioX = Math.max(Math.min(ratioX, 2), 1);

        Node toScale = viewPane.getCenter();

        // scale by same ratio allowing aspect ratio to be maintained.
        double scale = Math.min(ratioX, ratioY);

        toScale.setScaleY(scale);
        toScale.setScaleX(scale);
    }

    /* ========================== HEAT MAP PROCESSING =========================== */

    /**
     * Fills in the heatMapData HashMap with our chosen measure for the heat
     * map.
     * 
     * Also calculate the heat map's base value within the range to avoid multiple
     * iterations over potentially large data set
     * 
     * In this case, we're using deaths within the time period selected as the
     * measure. This is done by summing the number of new deaths on each day for
     * each borough in the time range
     */
    private void loadHeatMapData(LocalDate fromDate, LocalDate toDate) {

        // TODO: discuss if i should leave for loop, or use (totalDeaths on end date) -
        // (total deaths on start date)
        for (CovidData covidEntry : dataset.getDataInDateRange(fromDate, toDate)) {
            String entryBoroughName = covidEntry.getBorough();
            Integer entryDeathsOnDay = covidEntry.getNewDeaths();
            Integer boroughCumulitiveDeaths = heatMapData.get(entryBoroughName);

            // if no new deaths on this entry, continue onto next iteration
            if (entryDeathsOnDay == null) {
                continue;
            }

            int compareValue = 0;
            // if there's an existing value stored for a borough, update it
            if (boroughCumulitiveDeaths != null) {
                int deathCountInDateRange = entryDeathsOnDay + boroughCumulitiveDeaths;
                compareValue = deathCountInDateRange;
                heatMapData.put(entryBoroughName, deathCountInDateRange);
            } else {
                // if the borough currently stores no new deaths, store the amount of new deaths
                // from the current entry
                heatMapData.put(entryBoroughName, entryDeathsOnDay);
                compareValue = entryDeathsOnDay;
            }

            // set the base to the max between the current and the value to be compared with
            heatMapBaseValue = Math.max(compareValue, heatMapBaseValue);
        }

    }

    /**
     * Sets default value for all boroughs' heat map measure to null in the HashMap
     * 'heatMapData'
     */
    private void resetHeatMapData() {
        // reset the base value
        heatMapBaseValue = 0;

        heatMapData = new HashMap<>();
        for (String boroughName : dataset.getBoroughs()) {
            heatMapData.put(boroughName, null);
        }
    }

    /**
     * Calculates what colour to assign to a borough depending on the ratio between
     * it's heat map value and the base heat map value.
     * 
     * Uses HSB values to determine the colour allowing for a nice transition from
     * red to green
     * 
     */
    private void assignBoroughsColor() {
        System.out.println("assigning borough colours");

        for (Polygon boroughPolygon : boroughPolygons) {
            String boroughName = boroughIdToName.get(boroughPolygon.getId());
            Integer boroughHeatMapValue = heatMapData.get(boroughName);
            Color col;

            // if the borough has data available within the date range, give it a valid
            // heatMap colour.
            if (boroughHeatMapValue != null && heatMapBaseValue > 0) {

                // calculate proportion of current borough data with the base value
                // in HSB hue is measured in degrees where: 0 -> 120 == red -> green.
                // converts the proportion of heat map values as a % of the hueUpperBound
                double hueUpperBound = 105.0;
                double percentageOfHue = (hueUpperBound * boroughHeatMapValue / heatMapBaseValue);

                // subtracting from the upper bound gives us reversed scale.
                // green -> low deaths
                // red -> high deaths
                double hue = hueUpperBound - percentageOfHue;

                // HSB (hue (in degrees), saturation 100%, brightness 80%)
                col = Color.hsb(hue, 1, 0.8);

            } else {
                // color for if there's no heat map indicator for the borough in the selected
                // time frame
                col = Color.rgb(171, 171, 171);
            }

            // assign the borough its appropriate colour
            boroughPolygon.setFill(col);
        }
    }

    /* ============================ POLYGON HOVERING ============================ */

    /**
     * When hovering over a polygon, change border(stroke) colour and width
     * also change the lable at the top to show the borough being hovered over.
     * 
     * Display a mini info box showing data related to the heat map
     * 
     * @param event
     */
    @FXML
    void mouseEnteredPolygon(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();

        String name = boroughIdToName.get(poly.getId());

        // set the coordinates of the info pane to mouse coords + padding
        determineInfoPaneCoordinates(event, poly);

        // Add an event handler to get the mouse coordinates when the user moves the
        // mosue within the polygon
        poly.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // calculate the x and y coordinates with additional padding
                determineInfoPaneCoordinates(event, poly);
                showBoroughHeatMapInfo(poly);
            }
        });

        // when hovering, show the heatmap information for the borough
        showBoroughHeatMapInfo(poly);

        // change selected borough label text
        selectedBoroughLabel.setText(name);

        // store the polygon's initial properties
        hoveredPolygonInitialBorderColor = poly.getStroke();

        // change to new properties
        poly.setStrokeWidth(3);
        poly.setStroke(new Color(1, 1, 1, 1.0));
    }

    /**
     * If a polygon was being hovered, and is no long being hovered,
     * reset its attributes to default polygon state
     * 
     * @param event
     */
    @FXML
    void mouseExitedPolygon(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();

        // if mouse leaves polygon, disable visiblity of hover box
        hoverBox.setVisible(false);

        poly.setStrokeWidth(1);
        poly.setStroke(hoveredPolygonInitialBorderColor);

        // remove text if no borough is selected
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
     * show the info box next to the cursor displaying the data which has determined
     * the
     * borough's heat map data
     * 
     * @param poly
     */
    private void showBoroughHeatMapInfo(Polygon poly) {
        // make the mini info box visible
        hoverBox.setVisible(true);

        String boroughName = boroughIdToName.get(poly.getId());

        // retrieve the heat map data and calculate the percentage
        Integer boroughHeatMapValue = heatMapData.get(boroughName);

        // calculate then borough's heat map value as a percentage of the base heat map
        // value
        Integer percentage = null;
        if (boroughHeatMapValue != null && heatMapBaseValue > 0) {
            percentage = (int) Math.round((100.0 * boroughHeatMapValue / heatMapBaseValue));

        }

        // changing the text on the labels to adjust for the current borough being
        // hovered over
        hoverBoxBoroughLabel.setText(boroughName);

        deathsHoverLabel.setText("Borough Deaths: " + heatMapData.get(boroughName) + "\n" + percentage
                + "% of highest deaths within date range");

        // position the hover pane
        hoverBox.setLayoutX(infoPaneX);
        hoverBox.setLayoutY(infoPaneY);
    }

    /* ---------------------------- POLYGON CLICKING ---------------------------- */

    /**
     * If a polygon is clicked, prints the name of the borough that the fxid is
     * mapped to
     * 
     * @param event
     * @throws IOException
     */
    @FXML
    void mousePressedPolygon(MouseEvent event) throws IOException {
        Polygon poly = (Polygon) event.getSource();
        String name = boroughIdToName.get(poly.getId());
        showBoroughData(name);
    }

    /**
     * create a pop up window to show the borough's data
     * 
     * @param boroughName
     * @throws IOException
     */
    private void showBoroughData(String boroughName) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoroughInfo.fxml"));
        Parent root = loader.load();
        BoroughInfoController controller = loader.getController();

        Scene scene = new Scene(root);
        // stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mapAnchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.setTitle(boroughName);
        controller.showData(dataset.getBoroughData(boroughName, fromDate, toDate));
        stage.show();
    }

    /* ---------------------------------- MISC ---------------------------------- */

    protected Parent getView() {
        return viewPane;
    }

}