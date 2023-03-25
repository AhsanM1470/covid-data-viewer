import java.util.ArrayList;
import java.util.ResourceBundle;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;

import java.time.LocalDate;

/**
 * Responsible for displaying line charts of Covid Data for each boroughs in London.
 *  
 * @author Muhammad Ahsan Mahfuz
 * @version 2023.03.16
 */
public class GraphViewerController extends ViewerController implements Initializable
{
    @FXML
    private AnchorPane graphPane;
    
    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Label infoLabel;
    
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
        
    private String borough;
    
    /**
     * Add all the boroughs to the choice box and respond to the selection made
     * by the user.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){ 
        choiceBox.getItems().addAll(dataset.getBoroughs());
        choiceBox.setOnAction(this::selectBorough);
    }
    
    /**
     * Called when either date picker is changed.
     * 
     * @param fromDate The starting date of the range.
     * @param toDate The ending date of the range.
     */
    @FXML
    public void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        if(borough != null){
            constructChart(fromDate, toDate);
        }
    }
    
    /**
     * Gets the selected borough from the choice box and creates a chart from it.
     * @param event     The borough the user selected from the choice box .   
     */
    public void selectBorough(ActionEvent event){
        borough = choiceBox.getValue();
        constructChart(fromDate, toDate);
    }
    
    /**
     * Creates a line chart for the dates vs total deaths of a borough.
     * 
     * @param from The start date of the date range.
     * @param to The end date of the date range.
     */
    public void constructChart(LocalDate from, LocalDate to){
        series = new XYChart.Series<String, Integer>();
        series.setName("deaths in borough");
        //Clears the previous chart before creating a new one
        chart.getData().clear();

        // check if date range is valid and update label text appropriately
        // only continue with plotting if valid date range is selected
        infoLabel.setText("Showing data between "+fromDate+" and "+toDate);
        if(!dataset.isDateRangeValid(fromDate, toDate)){
            infoLabel.setText("The selected 'from' date is after the 'to' date");
            return;
        }

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> totalDeaths = new ArrayList<>();
        //Add data from the excel database to the arraylists
        for(CovidData data : dataset.getBoroughData(borough, from, to)){
            LocalDate date = LocalDate.parse(data.getDate());
            Integer deaths = data.getTotalDeaths();
            if(deaths != null || date != null){
                dates.add(date.toString());
                totalDeaths.add(deaths);
            }
        }
        
        //Creates a new point on the line chart using elements from the arraylists
        for(int i = dates.size() - 1; i >= 0; i--){
            series.getData().add(new XYChart.Data<String, Integer>(dates.get(i),totalDeaths.get(i)));
        }
        chart.getData().addAll(series);
        setBounds(totalDeaths);
        addTooltips();
    }
    
    /**
     * Sets the upper and lower bound of the y-axis on the line chart.
     * 
     * @param deaths Arraylist of all the deaths in the specified borough and date range.
     */
    private void setBounds(ArrayList<Integer> deaths){
        //The arraylist stores total deaths from most recent to oldest
        int lowerValue = Integer.valueOf(deaths.get(deaths.size() - 1));
        int upperValue = Integer.valueOf(deaths.get(0));
        
        //Rounding lowerValue down to the nearest 100 and and upperValue up to the nearest 100 
        double lowerBound = (lowerValue/100)*100;
        double upperBound = ((upperValue + 99)/100)*100;
        
        //Setting the y-axis
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(50);
        yAxis.setMinorTickVisible(false);
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
                    Tooltip.install(data.getNode(), new Tooltip("Date: " + data.getXValue() + "\nTotal Deaths: " + data.getYValue().toString()));
                }
            });
        }
    }
    
    /**
     * @return The view that this controller is associated with.
     */
    protected Parent getView(){
        return graphPane;
    }
}
