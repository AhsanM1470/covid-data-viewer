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

    private HashMap<String, String> boroughIdToName;

    private HashMap<String, Integer> boroughsTotalDeaths;

    private int highestDeathCount;

    private ArrayList<CovidData> dateRangeData;

    private double initialPaneWidth; 
    private double initialPaneHeight;
    

    @FXML
    void initialize() {
        bp.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            if(oldVal.floatValue()!=newVal.floatValue()) {resizeComponents();};
        });
       
       bp.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            if(oldVal.floatValue()!=newVal.floatValue()) {resizeComponents();};
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

        initialPaneWidth = bp.getPrefWidth();
        initialPaneHeight = bp.getPrefHeight();
        

        resetTotalBoroughDeaths();

    }

    public void resizeComponents(){
        var parentPane = bp;

        double ratioX = parentPane.getWidth()/ initialPaneWidth;
        double ratioY = parentPane.getHeight()/ initialPaneHeight;
        
        if (Double.isInfinite(ratioX) || Double.isInfinite(ratioX)){
            return;
        }

        if (Double.isNaN(ratioX) || Double.isNaN(ratioX)){
            return;
        }
        
        //TODO: Try to add aspect ratio scaling

        // make it so that the size of the map can't be smaller than its initially set size.
        ratioX = Math.max(Math.min(ratioX,2),1);
        ratioY =  Math.max(Math.min(ratioY,2),1);

        mapAnchorPane.setScaleX(ratioX);
        mapAnchorPane.setScaleY(ratioY);

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
    private void loadDateRangeData(LocalDate fromDate, LocalDate toDate) {
        dateRangeData = super.getDateRangeData(fromDate, toDate);
    }

    /**
     * Sets default value for all boroughs' total deaths to null in the HashMap
     * 'boroughsTotalDeaths'
     */
    private void resetTotalBoroughDeaths() {
        boroughsTotalDeaths = new HashMap<>();
        for (CovidData covidRecord : data) {
            String recordBoroughName = covidRecord.getBorough();
            boroughsTotalDeaths.put(recordBoroughName, null);
        }
    }

    private void getHighestDeathCount(){
        highestDeathCount = 0;
        for (CovidData cd: data){
            Integer totalDeaths = cd.getTotalDeaths();
            if (totalDeaths!=null){
                highestDeathCount = Math.max(highestDeathCount,totalDeaths);
            }
        }
    }

    /**
     * Fills in the boroughsDeaths HashMap with total deaths during date range
     * selected
     */
    private void loadBoroughDeaths() {

        // reset borough deaths count when new date range is picked
        resetTotalBoroughDeaths();
        
        for (CovidData covidRecord : dateRangeData) {
            String recordBoroughName = covidRecord.getBorough();
            Integer dataEntryDeaths = covidRecord.getNewDeaths();
            Integer boroughDeaths = boroughsTotalDeaths.get(recordBoroughName);

            // if no total deaths, continue onto next iteration
            if (dataEntryDeaths == null) {
                continue;
            }


            // if there's an existing value stored for a borough, update it
            if (boroughDeaths != null) {
                int deathCountInDateRange = boroughDeaths+dataEntryDeaths;
                highestDeathCount = Math.max(highestDeathCount,deathCountInDateRange);
                boroughsTotalDeaths.put(recordBoroughName, deathCountInDateRange);
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

        getHighestDeathCount();

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

        // svgPolygon1.setLayoutX(svgPolygon1.getLayoutX()*1.2);
        // svgPolygon1.setLayoutY(svgPolygon1.getLayoutY()*1.2);
        // svgPolygon1.setScaleX(svgPolygon1.getScaleX()*1.2);
        // svgPolygon1.setScaleY(svgPolygon1.getScaleY()*1.2);

        
        

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
