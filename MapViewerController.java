import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.Parent;

public class MapViewerController extends Controller {

    Paint hoveredPolygonDefaultBorderColor;
    Double hoveredPolygonDefaultStroke;

    @FXML
    private HBox mapPane;

    @FXML
    private AnchorPane mapAnchorPane;

    @FXML
    private BorderPane bp;

    @FXML
    private Label selectedBoroughLabel;

    @FXML
    private Polygon brentPolygon, bexleyPolygon, bromleyPolygon, camdenPolygon, cityPolygon, croydonPolygon,
            ealingPolygon, enfieldPolygon, greenwichPolygon, hackneyPolygon, hamletsPolygon, hammfullPolygon,
            haringeyPolygon, harrowPolygon, haveringPolygon, hillingdonPolygon, hounslowPolygon, islingtonPolygon,
            kensChelsPolygon, kingstonPolygon, lambethPolygon, lewishamPolygon, mertonPolygon, newhamPolygon,
            redbridgePolygon, richmondPolygon, southwarkPolygon, suttonPolygon, thamesPolygon, walthamPolygon,
            wandsworthPolygon, westminsterPolygon, barkDagPolygon, barnetPolygon;

    private Polygon[] boroughPolygons;

    @FXML
    private Label title, hillingdonLabel;

    // used to map polygon IDs to String borough names
    private HashMap<String, String> boroughIdToName;

    // stores the data for each borough which will be used to calculate intensity on
    // heat map
    private HashMap<String, Integer> boroughHeatMapData;

    // the value which is used as a base to determine the intensity for other
    // boroughs on the heat map
    private int heatMapBaseValue;

    // stores the data within the date range selected
    private ArrayList<CovidData> dateRangeData;

    // used to calculate % change in window size which is used to determine % change
    // in panel size
    private double initialPaneWidth;
    private double initialPaneHeight;

    @FXML
    void initialize() {

        // adding window size change listeneres
        mapPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal.floatValue() != newVal.floatValue()) {
                resizeComponents();
            }
            ;
        });

        mapPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal.floatValue() != newVal.floatValue()) {
                resizeComponents();
            }
            ;
        });

        // an array of all the borough polygons which will be used when assigning
        // colours
        boroughPolygons = new Polygon[] { brentPolygon, bexleyPolygon, bromleyPolygon, camdenPolygon, cityPolygon,
                croydonPolygon, ealingPolygon, enfieldPolygon, greenwichPolygon, hackneyPolygon, hamletsPolygon,
                hammfullPolygon, haringeyPolygon, harrowPolygon, haveringPolygon, hillingdonPolygon, hounslowPolygon,
                islingtonPolygon, kensChelsPolygon, kingstonPolygon, lambethPolygon, lewishamPolygon, mertonPolygon,
                newhamPolygon, redbridgePolygon, richmondPolygon, southwarkPolygon, suttonPolygon,
                walthamPolygon, wandsworthPolygon, westminsterPolygon, barkDagPolygon, barnetPolygon };

        // load the mapping of polygon IDs to their respective borough names
        JsonReader jsonReader = new JsonReader();
        boroughIdToName = jsonReader.readJson("boroughIDs.json");

        // store the initial size of window
        initialPaneWidth = bp.getPrefWidth();
        initialPaneHeight = bp.getPrefHeight();

        calculateHeatMapBase();
        resetBoroughHeatMapData();

    }

    public void resizeComponents() {
        var parentPane = bp;

        double ratioX = parentPane.getWidth() / initialPaneWidth;
        double ratioY = parentPane.getHeight() / initialPaneHeight;

        if (Double.isInfinite(ratioX) || Double.isInfinite(ratioX)) {
            return;
        }

        if (Double.isNaN(ratioX) || Double.isNaN(ratioX)) {
            return;
        }

        // make it so that the size of the map can't be smaller than its initially set
        // size.
        ratioX = Math.max(Math.min(ratioX, 2), 1);
        ratioY = Math.max(Math.min(ratioY, 2), 1);

        mapAnchorPane.setScaleX(ratioX);
        mapAnchorPane.setScaleY(ratioY);

    }

    /**
     * collects dates from date picker for this window, checks if its valid, and if
     * so, loads the
     * data for that range
     * 
     * @param event
     */
    @FXML
    void datePicked(ActionEvent event) {

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        dateChanged(fromDate, toDate);
    }

    /**
     * Execues set of instructions related to the date range selected
     */
    protected void dateChanged(LocalDate from, LocalDate to) {
        // reset boroughs heat map measure information when date is changed
        resetBoroughHeatMapData();

        if (isDateRangeValid(from, to)) {
            heatMapBaseValue = 0;
            // filter all the data to select data from our selected range
            loadDateRangeData(from, to);

            // get the heat map values for each borough to colour them
            loadBoroughHeatMapData();
        }

        assignBoroughsColor();
    }

    /**
     * Creates an ArrayList containing data between date period selected filtered
     * from the ArrayList 'data' which was created upon initialisation
     * 
     * @param fromDate
     * @param toDate
     */
    private void loadDateRangeData(LocalDate fromDate, LocalDate toDate) {
        dateRangeData = getDateRangeData(fromDate, toDate);
    }

    /**
     * Sets default value for all boroughs' heat map measure to null in the HashMap
     * 'boroughHeatMapData'
     */
    private void resetBoroughHeatMapData() {
        boroughHeatMapData = new HashMap<>();
        for (CovidData covidRecord : data) {
            String recordBoroughName = covidRecord.getBorough();
            boroughHeatMapData.put(recordBoroughName, null);
        }
    }

    /**
     * Fills in the boroughHeatMapData HashMap with our chosen measure for the heat
     * map.
     * In this case, we're using deaths within the time period selected as the
     * measure. This is done by summing the number of new deaths on each day for
     * each borough in the time range
     */
    private void loadBoroughHeatMapData() {
        for (CovidData covidEntry : dateRangeData) {
            String entryBoroughName = covidEntry.getBorough();
            Integer entryDeathsOnDay = covidEntry.getNewDeaths();
            Integer boroughCumulitiveDeaths = boroughHeatMapData.get(entryBoroughName);

            // if no new deaths on this entry, continue onto next iteration
            if (entryDeathsOnDay == null) {
                continue;
            }

            // if there's an existing value stored for a borough, update it
            int compareValue = 0;
            if (boroughCumulitiveDeaths != null) {
                int deathCountInDateRange = entryDeathsOnDay + boroughCumulitiveDeaths;
                compareValue = deathCountInDateRange;
                boroughHeatMapData.put(entryBoroughName, deathCountInDateRange);
            } else {
                // if the borough currently stores no new deaths, place deaths on this day
                boroughHeatMapData.put(entryBoroughName, entryDeathsOnDay);
                compareValue = entryDeathsOnDay;
            }
            heatMapBaseValue = Math.max(compareValue, heatMapBaseValue);
            

        }

    }

    /**
     * calculates the highest magnitude of the data value which will be used as the
     * base value to compare against in heat map intensity
     */
    private void calculateHeatMapBase() {
        for (CovidData cd : data) {
            Integer totalDeaths = cd.getTotalDeaths();
            if (totalDeaths != null) {
                // heatMapBaseValue = Math.max(heatMapBaseValue, totalDeaths);
            }
        }
    }

    /**
     * attempt to calculate which colour to assign to each borough based on heat map
     * values within the date range relative to the base heat map value
     * 
     */
    private void assignBoroughsColor() {
        System.out.println("assigning borough colours");

        for (Polygon boroughPolygon : boroughPolygons) {
            String boroughName = boroughIdToName.get(boroughPolygon.getId());
            Integer boroughHeatMapMeasure = boroughHeatMapData.get(boroughName);
            Color col;

            // if the borough has data available within the date range, give it a valid
            // heatMap colour.
            if (boroughHeatMapMeasure != null && heatMapBaseValue > 0) {

                // calculate proportion of current borough data with the base value
                // in HSB hue is measured in degrees where: 0 -> 120 == red -> green.
                // converts the proportion of heat map values as a % of the hueUpperBound
                int hueUpperBound = 140;
                double percentageOfHue = (hueUpperBound * boroughHeatMapMeasure / heatMapBaseValue);

                // subtracting from the upper bound gives us reversed scale.
                // green -> low deaths
                // red -> high deaths
                double hue = hueUpperBound - percentageOfHue;

                // HSB (hue (in degrees), saturation , brightness)
                col = Color.hsb(hue, 1, 0.85);

            } else {
                // color for if there's no heat map indicator for the borough in the selected
                // time frame
                col = Color.rgb(171, 171, 171);
            }

            // assign the borough its appropriate colour
            boroughPolygon.setFill(col);
        }
    }

    /**
     * If a polygon is clicked, prints the name of the borough that the fxid is
     * mapped to
     * 
     * @param event
     */
    @FXML
    void polygonClicked(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();
        String name = boroughIdToName.get(poly.getId());

        System.out.println(
                name + " total deaths within date range: " + boroughHeatMapData.get(name) + " | Heat map base value: "
                        + heatMapBaseValue);
    }

    /**
     * When hovering over a polygon, change border(stroke) colour and width
     * also change the lable at the top to show the borough being hovered over
     * 
     * @param event
     */
    @FXML
    void polygonHovered(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();

        // change label text
        title.setAlignment(Pos.CENTER);
        String name = boroughIdToName.get(poly.getId());
        setLabelText(selectedBoroughLabel, name, 15.0);

        // change properties to indicate hovered borough
        hoveredPolygonDefaultBorderColor = poly.getStroke();
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
    void polygonLeft(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();
        poly.setStrokeWidth(1);
        poly.setStroke(hoveredPolygonDefaultBorderColor);

        // remove text if no borough is selected
        setLabelText(selectedBoroughLabel, null, 0);
    }

    /**
     * q
     * 
     * @param label    label component to be customised
     * @param text     text to be displayed on the label
     * @param fontSize size of the text
     */
    private void setLabelText(Label label, String text, double fontSize) {
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font("Comic Sans MS", fontSize));
    }

    protected Parent getView() {
        return mapPane;
    }
}
