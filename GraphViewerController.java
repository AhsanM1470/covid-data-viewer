import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.control.DatePicker;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Write a description of class GraphViewerController here.
 *
 * @author (your name)
 * @version (a version number or a date)
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
    private LineChart<String,Number> chart;
    
    @FXML
    CategoryAxis xAxis = new CategoryAxis();
    
    @FXML
    NumberAxis yAxis = new NumberAxis();
    
    private LocalDate from, to;

    private XYChart.Series series;
    
    //remove all? or fix it
    private String[] boroughs = {"All", "Barking And Dagenham", "Barnet", "Bexley", "Brent", "Bromley", "Camden",
        "Croydon", "Ealing", "Enfield", "Greenwich", "Hackney", "Hammersmith And Fulham", "Haringey",
        "Harrow", "Havering", "Hillingdon", "Hounslow", "Kensington and Chelsea", "Kingston Upon Thames",
        "Lambeth", "Lewisham", "Merton", "Newham", "Redbridge", "Richmond Upon Thames", "Southwark",
        "Sutton", "Tower Hamlets", "Waltham Forest", "Wandsworth", "Westminster"};
        
    private String borough;
    
    /**
     * Add all the boroughs to the choice box and respond to the selection made
     * by the user
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){ 
        choiceBox.getItems().addAll(boroughs);
        choiceBox.setOnAction(this::selectBorough);
    }
    
    //
    @FXML
    public void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        from = fromDate;
        to = toDate;

        if(borough != null){
            constructChart(from, to);
        }
    }
    
    /**
     * Gets the selected borough from the choice box and creates a chart from it
     */
    public void selectBorough(ActionEvent event){
        borough = choiceBox.getValue();
        constructChart(from, to);
        //System.out.println(dataController.getFromDate());
    }
    
    /**
     * Creates a line chart for the total deaths vs date of a borough
     */
    public void constructChart(LocalDate from, LocalDate to){

        // clear the chart
        series = new XYChart.Series();
        chart.setCreateSymbols(false);
        chart.getData().clear();

        // check if date range is valid and update label text appropriately
        infoLabel.setText("Showing data between "+fromDate+" and "+toDate);
        if(!dataset.isDateRangeValid(fromDate, toDate)){
            infoLabel.setText("The selected 'from' date is after the 'to' date");
            return;
        }

        // only continue with plotting if valid date range is selected
        series.setName("deaths in borough");

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> totalDeathsArray = new ArrayList<>();

        ArrayList<CovidData> dataInDateRange = dataset.getDataInDateRange(from, to);
        ArrayList<CovidData> boroughData = dataset.getBoroughData(borough, from, to);

        for (CovidData d : boroughData) {
            if(d.getBorough().equals(borough) && dataset.isDateRangeValid(from, to)){
                LocalDate date = LocalDate.parse(d.getDate());
                Integer totalDeaths = d.getTotalDeaths();
                if(totalDeaths != null){
                    dates.add(date.toString());
                    totalDeathsArray.add(totalDeaths);
                }   
            }
        }
        // boroughData = getBoroughData(borough, from, to);
        // for(CovidData data : boroughData){
            // LocalDate date = LocalDate.parse
        // }
        
        for(int i = dates.size() - 1; i >= 0; i--){
            series.getData().add(new XYChart.Data(dates.get(i),totalDeathsArray.get(i)));
        }
        chart.getData().add(series);
        setBounds(totalDeathsArray);
        
        // for(final XYChart.Data<String, Number> data : series.getData()){
            // data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                // @Override
                // public void handle(MouseEvent event){
                    // Label label = new Label("Hi");
                // }
            // });
        // }
    }
    
    /**
     * Sets the upper and lower bound of the y-axis on the line chart
     */
    private void setBounds(ArrayList<Integer> deaths){
        //Last index in the arraylist has the smallest total deaths
        //First index in the arryalist has the largest total deaths
        int lowerValue = Integer.valueOf(deaths.get(deaths.size() - 1));
        int upperValue = Integer.valueOf(deaths.get(0));
        
        double lowerBound = (lowerValue/100)*100;
        double upperBound = ((upperValue + 99)/100)*100;
        
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(50);
        yAxis.setMinorTickVisible(false);
    }
    
    /**
     * Create a label for the nodes on the line chart
     */
    private Label createDataThresholdLabel(String date, int deaths){
        final Label label = new Label( "(" + date + ", " + deaths + ")" );
        label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return label;
    }
    
    /**
     * Returns the anchor pane of GraphView.fxml
     * @return graphPane
     */
    protected Parent getView(){
        return graphPane;
    }
}
