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
    
    @FXML
    private DatePicker fromDatePicker, toDatePicker;
    
    private LocalDate fromDate, toDate;

    private XYChart.Series<String, Integer> series;
    
    private double upperBound, lowerBound;
    
    private String borough, stat;
    
    //tk remove All or keep it
    private String[] boroughs = {"All", "Barking And Dagenham", "Barnet", "Bexley", "Brent", "Bromley", "Camden",
        "Croydon", "Ealing", "Enfield", "Greenwich", "Hackney", "Hammersmith And Fulham", "Haringey",
        "Harrow", "Havering", "Hillingdon", "Hounslow", "Kensington And Chelsea", "Kingston Upon Thames",
        "Lambeth", "Lewisham", "Merton", "Newham", "Redbridge", "Richmond Upon Thames", "Southwark",
        "Sutton", "Tower Hamlets", "Waltham Forest", "Wandsworth", "Westminster"};
        
    private String[] stats = {"New Cases", "Total Cases", "New Deaths", "Total Deaths"};
    
    private ArrayList<String> dates = new ArrayList<>();
    
    private ArrayList<Integer> yAxisValues = new ArrayList<>();
    
    /**
     * Add all the boroughs to the choice box and respond to the selection made
     * by the user.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){ 
        boroughChoiceBox.getItems().addAll(boroughs);
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
     * Gets the selected borough from the choice box and creates a chart from it.
     * @param event     The borough the user selected from the choice box .   
     */
    private void selectBorough(ActionEvent event){
        borough = boroughChoiceBox.getValue();
        if(statChoiceBox.getValue() != null){
            constructChart(fromDate, toDate);
        }
    }
    
    private void selectStat(ActionEvent event){
        stat = statChoiceBox.getValue();
        if(boroughChoiceBox.getValue() != null){
            constructChart(fromDate, toDate);
        }
    }
    
    /**
     * Creates a line chart for the dates vs total deaths of a borough.
     * @param from      The start date of the date range.
     * @param to        The end date of the date range.
     */
    private void constructChart(LocalDate from, LocalDate to){
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

        setChartXYValues(from, to);
        
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
     */
    private void setChartXYValues(LocalDate from, LocalDate to){
        // Add data from the excel database to the arraylists
        for(CovidData data : dataset.getBoroughData(borough, from, to)){
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
    
    private void calculateAllBoroughs(String borough, LocalDate from, LocalDate to){
        ArrayList<CovidData> boroughs = new ArrayList<>();
        boroughs = dataset.getBoroughData(borough, from, to);
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
        // 20 evenly spaced ticks on the y-axis
        yAxis.setTickUnit((upperBound - lowerBound)/20);
        yAxis.setMinorTickVisible(false);
    }
    
    /**
     * Calculating the upper and lower bounds of the y-axis
     * An upperValue less than or equal to 10 will set the lower and upper bounds to 0 and 10 respectively.
     * An upperValue in the thousands, hundreds, or tens will set bounds to the nearest hundred.
     * An upperValue in the ten thousands will set bounds to the nearest thousand. 
     * An upperValue in the hundred thousands will set bounds to the nearest ten thousand.
     * 
     * @param yValues   Arraylist of all the y-axis valus in the specified borough and date range.
     */
    private void calculateBounds(ArrayList<Integer> yValues){
        int lowerValue = 9999999;
        int upperValue = 0;
        // for the total deaths and cases, the last value in the arraylist will be the smallest
        // and the first value in the arraylist will be the largest
        if(stat.contains("Total")){
            lowerValue = Integer.valueOf(yValues.get(yValues.size() - 1));
            upperValue = Integer.valueOf(yValues.get(0));
        }else{
            // for the new deaths and cases, search through each value to find the smallest and largest
            for(int value : yValues){
                if(value < lowerValue){
                    lowerValue = value;
                } else if(value > upperValue){
                    upperValue = value;
                }
            }
        }
        
        // adder and multiplier are used when calculating the lower and upper bounds of the y-axis
        // by default their values will be used to round to the nearest 100
        int adder = 99;
        int multiplier = 100;
        if(upperValue <= 10){
            lowerBound = 0;
            upperBound = 10;
            return;
        // to round to nearest thousand
        }else if(upperValue > 9999){
            adder = 999;
            multiplier = 1000;
        // to round to nearest ten thousand
        }else if(upperValue > 99999){
            adder = 9999;
            multiplier = 10000;
        }

        // rounding lowerValue down and upperValue up to the nearest 100/1000/10,000
        lowerBound = (lowerValue/multiplier)*multiplier;
        upperBound = ((upperValue + adder)/multiplier)*multiplier;
    }
    
    /**
     * Adds a Tooltip to every node on the line chart which displays the exact date
     * and total deaths when hovered over with the mouse
     */
    private void addTooltips(){
        for(XYChart.Data<String, Integer> data : series.getData()){
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event){
                    Tooltip tp = new Tooltip("Date: " + data.getXValue() + "\nTotal Deaths: " + data.getYValue().toString());
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
