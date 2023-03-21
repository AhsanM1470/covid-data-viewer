import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MapViewerController extends ViewerController implements Initializable {

    Paint hoveredPolygonDefaultBorderColor;
    Double hoveredPolygonDefaultStroke;

    @FXML
    private AnchorPane mapAnchorPane, polygonPane;

    @FXML
    private BorderPane viewPane;

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
    private ArrayList<CovidData> dataInDateRange;

    // starting size of our pane to be used when scaling map view
    private double initialPaneWidth, initialPaneHeight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialPaneWidth = viewPane.getPrefWidth();
        initialPaneHeight = viewPane.getPrefHeight();

        // adding window size change listeneres
        viewPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
            }
            ;
        });

        viewPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
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

    }

    /**
     * resizing the 
     * 
     * @param parentPane pane that is to be used to scale with
     */
    protected void resizeComponents(Region parentPane) {

        double ratioX = parentPane.getWidth() / initialPaneWidth;
        double ratioY = parentPane.getHeight() / initialPaneHeight;

        // check if ratios are valid. Cannot be nothing, and cannot be infinity
        if (Double.isInfinite(ratioX) || Double.isNaN(ratioX)) {
            return;
        }
        if (Double.isInfinite(ratioY) || Double.isNaN(ratioY)) {
            return;
        }

        // returns the border pane at the center which stores our map (polygonPane +
        // labels)

        ratioY = Math.max(Math.min(ratioY, 2), 1);
        ratioX = Math.max(Math.min(ratioX, 2), 1);

        Node toScale = viewPane.getCenter();

        // scale by same ratio ensuring scaling will still fit inside the window.
        double scale = Math.min(ratioX, ratioY);

        toScale.setScaleY(scale);
        toScale.setScaleX(scale);
    }

    /**
     * Executes set of instructions related to the date range selected
     */
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        // reset boroughs heat map measure information when date is changed
        resetBoroughHeatMapData();

        if (isDateRangeValid(fromDate, toDate)) {
            // reset base value each time date is changed
            heatMapBaseValue = 0;

            // filter all the data to select data from our selected range
            loadDataInDateRange(fromDate, toDate);

            checkToChangeTitleLabel();

            // get the heat map values for each borough to colour them
            loadBoroughHeatMapData();
        }

        assignBoroughsColor();
    }

    /**
     * Set the appropriate message depending on the data that is loaded in the
     * selected date range
     */
    private void checkToChangeTitleLabel() {
        if (dataInDateRange.size() > 1) {
            title.setText("Click on a borough to view more information");
        } else {
            title.setText("No data in the selected date range");
        }
    }

    /**
     * Creates an ArrayList containing data between date period selected filtered
     * from the ArrayList 'data' which was created upon initialisation
     * 
     * @param fromDate
     * @param toDate
     */
    private void loadDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        dataInDateRange = getDataInDateRange(fromDate, toDate);
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
     * 
     * Also calculate the heat map's base value within the range to avoid multiple
     * iterations
     * over potentially large data set
     * 
     * In this case, we're using deaths within the time period selected as the
     * measure. This is done by summing the number of new deaths on each day for
     * each borough in the time range
     */
    private void loadBoroughHeatMapData() {

        for (CovidData covidEntry : dataInDateRange) {
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
     * attempt to calculate which interpolated colour from green to red to assign to
     * each borough based on heat map alues within the date range relative to the
     * base heat map value
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
                double hueUpperBound = 135.0;

                double percentageOfHue = (hueUpperBound * boroughHeatMapMeasure / heatMapBaseValue);

                // subtracting from the upper bound gives us reversed scale.
                // green -> low deaths
                // red -> high deaths
                double hue = hueUpperBound - percentageOfHue;

                // HSB (hue (in degrees), saturation , brightness)
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

    /**
     * If a polygon is clicked, prints the name of the borough that the fxid is
     * mapped to
     * 
     * @param event
     * @throws IOException
     */
    @FXML
    void polygonClicked(MouseEvent event) throws IOException {
        Polygon poly = (Polygon) event.getSource();
        String name = boroughIdToName.get(poly.getId());
        // Integer boroughHeatMapMeasure = boroughHeatMapData.get(name);

        // Integer perc = null;
        // if (boroughHeatMapMeasure != null && heatMapBaseValue > 0) {
        //     double percentage = (double) (100.0 * boroughHeatMapMeasure / heatMapBaseValue);
        //     perc = (int) Math.round(percentage);
        // }
        // System.out.println(
        //         name + " total deaths within date range: " + boroughHeatMapData.get(name) + " | Heat map base value: "
        //                 + heatMapBaseValue + " | percentage: " + perc + "%");
        showData(name);
    }

    /**
     * create a pop up window for the borough data
     * @param boroughName
     * @throws IOException
     */
    private void showData(String boroughName) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoroughInfo.fxml"));
        Parent root = loader.load();
        BoroughInfoController controller = loader.getController();
        
        Scene scene = new Scene(root);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mapAnchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.setTitle(boroughName);
        controller.showData(getBoroughData(boroughName, fromDate, toDate), stage);
        stage.show();
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

        // double scaleTo = 1.15;

        // ObservableList<Node> workingCollection = FXCollections.observableArrayList(
        // polygonPane.getChildren()
        // );

        // // get which pane is at the top
        // Collections.sort(workingCollection, new ScaleComparator());
        // polygonPane.getChildren().setAll(workingCollection);

        // change label text
        title.setAlignment(Pos.CENTER);
        String name = boroughIdToName.get(poly.getId());
        setLabelText(selectedBoroughLabel, name, 15.0);

        // change properties to indicate hovered borough
        hoveredPolygonDefaultBorderColor = poly.getStroke();
        poly.setStrokeWidth(3);
        poly.setStroke(new Color(1, 1, 1, 1.0));

    }

    // private class ScaleComparator implements Comparator<Node> {
    // @Override
    // public int compare(Node o1, Node o2) {

    // double o1B = o1.getScaleX();
    // double o2B = o2.getScaleX();

    // return (o1B > o2B ? 1 : 0);
    // }
    // }

    /**
     * If a polygon was being hovered, and is no long being hovered,
     * reset its attributes to default polygon state
     * 
     * @param event
     */
    @FXML
    void polygonLeft(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();

        // poly.setScaleX(1.15);
        // poly.setScaleY(1.15);

        poly.setStrokeWidth(1);
        poly.setStroke(hoveredPolygonDefaultBorderColor);

        // remove text if no borough is selected
        setLabelText(selectedBoroughLabel, null, 0);
    }

    /**
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
        return viewPane;
    }

}