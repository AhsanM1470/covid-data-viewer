import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Polygon;
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
    private AnchorPane mapPane;

    @FXML
    private BorderPane bp;

    @FXML
    private Label selectedBoroughLabel;

    // @FXML
    // private DatePicker toDatePicker;

    // @FXML
    // private DatePicker fromDatePicker;

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

    private HashMap<String, String> boroughIdToName;

    private HashMap<String, Integer> boroughsTotalDeaths;

    private int highestDeathCount;

    private ArrayList<CovidData> dateRangeData;
    

    @FXML
    void initialize() {
        // an array of all the borough polygons which will be used when assigning
        // colours
        boroughPolygons = new Polygon[] { brentPolygon, bexleyPolygon, bromleyPolygon, camdenPolygon, cityPolygon,
                croydonPolygon, ealingPolygon, enfieldPolygon, greenwichPolygon, hackneyPolygon, hamletsPolygon,
                hammfullPolygon, haringeyPolygon, harrowPolygon, haveringPolygon, hillingdonPolygon, hounslowPolygon,
                islingtonPolygon, kensChelsPolygon, kingstonPolygon, lambethPolygon, lewishamPolygon, mertonPolygon,
                newhamPolygon, redbridgePolygon, richmondPolygon, southwarkPolygon, suttonPolygon,
                walthamPolygon, wandsworthPolygon, westminsterPolygon, barkDagPolygon, barnetPolygon };

        // load all covid data
        // CovidDataLoader dataLoader = new CovidDataLoader();
        // data = dataLoader.load();

        // load the mapping of polygon IDs to their respective borough names
        JsonReader jsonReader = new JsonReader();
        boroughIdToName = jsonReader.readJson("boroughIDs.json");

        resetTotalBoroughDeaths();
    }

    /**
     * collects dates from date picker for this window, checks if its valid, and if so, loads the
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
        // reset boroughs death count information when date is changed
        resetTotalBoroughDeaths();

        if (isDateRangeValid(from, to)) {
            // filter all the data to select data from our selected range
            loadDateRangeData(from, to);
            // get the deaths for each borough to colour them
            loadBoroughDeaths();

            assignBoroughsColor();
        }
    }

    /**
     * Creates an ArrayList containing data between date period selected filtered
     * from the ArrayList 'data' which was created upon initialisation
     * 
     * @param fromDate
     * @param toDate
     */
    public void loadDateRangeData(LocalDate fromDate, LocalDate toDate) {
        dateRangeData = super.getDateRangeData(fromDate, toDate);
    }

    /**
     * Sets default value for all boroughs' total deaths to null in the HashMap
     * 'boroughsTotalDeaths'
     */
    public void resetTotalBoroughDeaths() {
        boroughsTotalDeaths = new HashMap<>();
        for (CovidData covidRecord : data) {
            String recordBoroughName = covidRecord.getBorough();
            boroughsTotalDeaths.put(recordBoroughName, null);
        }
    }

    /**
     * Fills in the boroughsDeaths HashMap with total deaths during date range
     * selected
     */
    private void loadBoroughDeaths() {

        // reset borough deaths count when new date range is picked
        resetTotalBoroughDeaths();
        highestDeathCount = 0;

        for (CovidData covidRecord : dateRangeData) {
            String recordBoroughName = covidRecord.getBorough();
            Integer dataEntryDeaths = covidRecord.getTotalDeaths();
            Integer boroughDeaths = boroughsTotalDeaths.get(recordBoroughName);

            // if no total deaths, continue onto next iteration
            if (dataEntryDeaths == null) {
                continue;
            }

            // update highest death count if possible
            highestDeathCount = Math.max(highestDeathCount, dataEntryDeaths);

            // if there's an existing value stored for a borough, update it
            if (boroughDeaths != null) {
                boroughsTotalDeaths.put(recordBoroughName, Math.max(boroughDeaths, dataEntryDeaths));
            }
            else {
                // if the borough currently stores no total deaths, place cdDeaths
                boroughsTotalDeaths.put(recordBoroughName, dataEntryDeaths);
            }

        }

    }

    

    /**
     * // attempt to calculate which colour to assign to each borough based on total
     * deaths relative to the highest death count within the date range
     * 
     */
    private void assignBoroughsColor() {
        System.out.println("assigning borough colours");

        for (Polygon boroughPolygon : boroughPolygons) {
            String boroughName = boroughIdToName.get(boroughPolygon.getId());
            Color col;

            if (boroughsTotalDeaths.get(boroughName) != null) {
                // try to retrieve deaths of current borough as a proportion of the highest
                // death count
                double currentBoroughDeaths = boroughsTotalDeaths.get(boroughName);
                double percentage = currentBoroughDeaths * 100 / highestDeathCount;
                // we convert percentage into hsl (hue (in degrees), saturation 100%, brightness
                // 90%)
                col = Color.hsb(100 - percentage, 1, 0.7);

            } else {
                // color for if there's no total deaths for the borough in the selectime time
                // frame
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

        System.out.println(name + " total deaths: " + boroughsTotalDeaths.get(name) + " | highest death count: "
                + highestDeathCount);
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
     * 
     * @param label    label component to be customised
     * @param text     text to be displayed on the label
     * @param fontSize size of the text
     */
    private void setLabelText(Label label, String text, double fontSize) {
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font("Comic Sans MS",fontSize));
    }

    protected Parent getView() {
        return mapPane;
    }
}
