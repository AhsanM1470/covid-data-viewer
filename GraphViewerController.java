import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.util.Duration;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import java.util.Collections;

/**
 * Responsible for managing the graph viewer of the program.
 * The user selects a date range, borough, and particular stat.
 * A line chart is plotted of the stats vs dates for the particular borough.
 * The user can hover over points on the line chart for the exact values of the plots.
 *
 * @author Muhammad Ahsan Mahfuz
 * @version 2023.03.26 (yyyy.mm.dd)
 */
public class GraphViewerController extends ViewerController implements Initializable
{
    @FXML
    private AnchorPane graphPane;
    
    @FXML
    private ChoiceBox<String> boroughChoiceBox, statChoiceBox;

    @FXML
    private Label infoLabel, hoverLabel;
    
    @FXML
    private LineChart<String, Integer> chart;
    
    @FXML
    private CategoryAxis xAxis = new CategoryAxis();
    
    @FXML
    private NumberAxis yAxis = new NumberAxis();

    private XYChart.Series<String, Integer> series;
    
    private double upperBound, lowerBound;
    
    private String borough, stat;
    private String[] stats = {"New Cases", "Total Cases", "New Deaths", "Total Deaths"};
    
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<Integer> yAxisValues = new ArrayList<>();
    
    /**
     * Add all the boroughs to the choice box and respond to the selection made
     * by the user.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){ 
        boroughChoiceBox.getItems().addAll(dataset.getBoroughs());
        statChoiceBox.getItems().addAll(stats);
        boroughChoiceBox.setOnAction(this::selectBorough);
        statChoiceBox.setOnAction(this::selectStat);
    }
    
    /**
     * Called when either date picker is changed.
     * 
     * @param fromDate      The starting date of the range.
     * @param toDate        The ending date of the range.
     */
    @FXML
    public void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        if(borough != null || stat != null){
            constructChart(fromDate, toDate);
        }
    }
    
    /**
     * Gets the selected borough from the choice box to use when creating the chart.
     * @param event     The borough the user selected from the choice box.   
     */
    private void selectBorough(ActionEvent event){
        borough = boroughChoiceBox.getValue();
        if(statChoiceBox.getValue() != null){
            constructChart(fromDate, toDate);
        }
    }
    
    /**
     * Gets the selected stat from the choice box to use when creating the chart.
     * @param event     The stat the user selected from the choice box.   
     */
    private void selectStat(ActionEvent event){
        stat = statChoiceBox.getValue();
        if(boroughChoiceBox.getValue() != null){
            constructChart(fromDate, toDate);
        }
    }
    
    /**
     * Creates a line chart for the dates vs total deaths of a borough.
     * @param fromDate      The start date of the date range.
     * @param toDate        The end date of the date range.
     */
    private void constructChart(LocalDate fromDate, LocalDate toDate){
        series = new XYChart.Series<String, Integer>();
        series.setName(stat.toLowerCase() + " in borough");
        // Clears the previous chart before creating a new one
        chart.getData().clear();

        // check if date range is valid and update label text appropriately
        infoLabel.setText("Showing data between "+fromDate+" and "+toDate);
        if(!dataset.isDateRangeValid(fromDate, toDate)){
            infoLabel.setText("The selected 'from' date is after the 'to' date");
            return;
        }

        setChartXYValues(fromDate, toDate);
        
        if(dates.size() == 0){
            infoLabel.setText("There is no data for this stat and borough in the selected date range");
            return;
        }
        
        //Creates a new point on the line chart using elements from the arraylists
        for(int i = dates.size() - 1; i >= 0; i--){
            series.getData().add(new XYChart.Data<String, Integer>(dates.get(i),yAxisValues.get(i)));
        }
        chart.getData().addAll(series);
        yAxis.setLabel(stat);
        setBounds(yAxisValues);
        addTooltips();
        
        dates.clear();
        yAxisValues.clear();
    }
    
    /**
     * Set the ArrayLists 'date' and 'yAxisValues' to the X and Y values that
     * will be plotted on the linechart.
     * dates is the x-axis. yAxisValues is the y-axis.
     * 
     * @param fromDate      The start date of the date range.
     * @param toDate        The end date of the date range.
     */
    private void setChartXYValues(LocalDate fromDate, LocalDate toDate){
        // Add data from the excel database to the arraylists
        for(CovidData data : dataset.getBoroughData(borough, fromDate, toDate)){
            LocalDate date = LocalDate.parse(data.getDate());
            Integer yValue = null;
            switch(stat){
                case "New Cases":
                    yValue = data.getNewCases();
                    break;
                case "Total Cases":
                    yValue = data.getTotalCases();
                    break;
                case "New Deaths":
                    yValue = data.getNewDeaths();
                    break;
                case "Total Deaths":
                    yValue = data.getTotalDeaths();
                    break;
                default:
                    break;
            }
            if(date != null && yValue != null){
                dates.add(date.toString());
                yAxisValues.add(yValue);
            }
        }
    }
    
    /**
     * Sets the upper and lower bound of the y-axis on the line chart.
     * @param yValues   Arraylist of all the y-axis valus in the specified borough and date range.
     */
    private void setBounds(ArrayList<Integer> yValues){
        calculateBounds(yValues);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        // 10 evenly spaced ticks on the y-axis
        yAxis.setTickUnit((upperBound - lowerBound)/10);
        yAxis.setMinorTickVisible(false);
    }
    
    /**
     * Calculates the lower and upper bounds of the y-axis.
     * 
     * @param yValues ArrayList of all the y-axis values in the specified borough and date range
     */
    private void calculateBounds(ArrayList<Integer> yValues) {
        int lowerValue = Collections.min(yValues);
        int upperValue = Collections.max(yValues);
        
        // Set the multiplier based on the upperValue
        int multiplier = 100;
        if (upperValue <= 90) {
            multiplier = 10;
        } else if (upperValue > 9999) {
            multiplier = 1000;
        } else if (upperValue > 99999) {
            multiplier = 10000;
        }
        
        // The lower bound is rounded down to the nearest multiple of the multiplier
        // The upper bound is rounded up to the nearest multiple of the multiplier
        lowerBound = (lowerValue / multiplier) * multiplier;
        upperBound = ((upperValue + multiplier - 1) / multiplier) * multiplier;
    }
    
    /**
     * Adds a Tooltip to every node on the line chart which displays the exact date
     * and stat value when hovered over with the mouse.
     */
    private void addTooltips(){
        for(XYChart.Data<String, Integer> data : series.getData()){
            // make the nodes smaller
            data.getNode().setStyle("-fx-background-insets: 0, 1;" +
                                    "-fx-background-radius: 1px;" +
                                    "-fx-padding: 2px;");
            // add the feature to hover over them
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event){
                    Tooltip tp = new Tooltip("Date: " + data.getXValue() + "\n" + stat + ": " + data.getYValue().toString());
                    tp.setShowDelay(Duration.seconds(0.0));
                    Tooltip.install(data.getNode(), tp);
                    hoverLabel.setVisible(false);
                }
            });
        }
    }
    
    /**
     * Returns the anchor pane of GraphView.fxml
     * @return graphPane
     */
    protected Parent getView(){
        return graphPane;
    }
}
