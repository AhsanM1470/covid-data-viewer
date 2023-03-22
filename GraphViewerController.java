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
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.control.DatePicker;

//necessary?
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
    private LineChart<?,?> chart;
    
    @FXML
    CategoryAxis xAxis = new CategoryAxis();
    
    @FXML
    NumberAxis yAxis = new NumberAxis();
    
    @FXML
    private DatePicker fromDatePicker;
    
    @FXML
    private DatePicker toDatePicker;
    
    private ArrayList<CovidData> data;
    
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
        series = new XYChart.Series();
        //Clears the previous chart before creating a new one
        series.setName("deaths in borough");
        chart.setCreateSymbols(false);
        chart.getData().clear();
        
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> totalDeathsArray = new ArrayList<>();
        for (CovidData d : data) {
            if(d.getBorough().equals(borough)){
                LocalDate date = LocalDate.parse(d.getDate());
                Integer totalDeaths = d.getTotalDeaths();
                if(date.isAfter(from.minusDays(1)) && date.isBefore(to.plusDays(1))){
                    if(totalDeaths != null){
                        dates.add(date.toString());
                        totalDeathsArray.add(totalDeaths);
                    }
                }   
            }
        }
        
        //Last index in the arraylist has the smallest total deaths
        //First index in the arryalist has the largest total deaths
        int lowerValue = Integer.valueOf(totalDeathsArray.get(totalDeathsArray.size() - 1));
        int upperValue = Integer.valueOf(totalDeathsArray.get(0));
        
        double lowerBound = (lowerValue/100)*100;
        double upperBound = ((upperValue + 99)/100)*100;
        
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(50);
        yAxis.setMinorTickVisible(false);
        
        for(int i = dates.size() - 1; i >= 0; i--){
            series.getData().add(new XYChart.Data(dates.get(i),totalDeathsArray.get(i)));
            final Label label = createDataThresholdLabel(dates.get(i), totalDeathsArray.get(i));
            // setOnMouseEntered(new EventHandler<MouseEvent>() {
                // @override
                // public void handle(MouseEvent event){
                    // getChildren().setAll(label);
                    // setCursor(Cursor.NONE);
                    // toFront();
                // }
            // });
            // setOnMouseExited(new EventHandler<MouseEvent>() {
                // @override
                // public void handle(MouseEvent event){
                    // getChildren().clear();
                    // setCursor(Cursor.CROSSHAIR);
                // }
            // });
        }
        
        chart.getData().add(series);
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
    
    /**
     * 
     */
    public void setData(ArrayList<CovidData> data){
        this.data = data;
    }
}