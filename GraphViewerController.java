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
import javafx.scene.input.MouseEvent;

/**
 * Write a description of class GraphViewerController here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GraphViewerController implements Initializable
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
    
    private DataViewerController dataController;
    
    //does this line work
    private XYChart.Series series;
    
    //remove all? or fix it
    private String[] boroughs = {"All", "Barking And Dagenham", "Barnet", "Bexley", "Brent", "Bromley", "Camden",
        "Croydon", "Ealing", "Enfield", "Greenwich", "Hackney", "Hammersmith And Fulham", "Haringey",
        "Harrow", "Havering", "Hillingdon", "Hounslow", "Kensington and Chelsea", "Kingston Upon Thames",
        "Lambeth", "Lewisham", "Merton", "Newham", "Redbridge", "Richmond Upon Thames", "Southwark",
        "Sutton", "Tower Hamlets", "Waltham Forest", "Wandsworth", "Westminster"};
        
    private String borough;
    
    /**
     * Constructor for objects of class GraphViewerController
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){ 
        choiceBox.getItems().addAll(boroughs);
        choiceBox.setOnAction(this::selectBorough);
    }
    
    @FXML
    public void dateChanged(ActionEvent event) {
        System.out.println("graph");
    }
    
    public void selectBorough(ActionEvent event){
        borough = choiceBox.getValue();
        series = new XYChart.Series();
        chart.getData().clear();
        constructChart(dataController.getFromDate(), dataController.getToDate());
        chart.getData().add(series);
        //System.out.println(dataController.getFromDate());
    }
    
    public void constructChart(LocalDate from, LocalDate to){
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> totalDeaths = new ArrayList<>();
        for (CovidData d : data) {
            if(d.getBorough().equals(borough)){
                LocalDate date = LocalDate.parse(d.getDate());
                if(date.isAfter(from.minusDays(1)) && date.isBefore(to.plusDays(1))){
                    dates.add(date.toString());
                    totalDeaths.add(d.getTotalDeaths());
                }   
            }
        }
        
        //Last index in the arraylist has the smallest total deaths
        //First index in the arryalist has the largest total deaths
        
        int index = totalDeaths.size() - 1;
        int lowerValue = Integer.valueOf(totalDeaths.get(index));
        int upperValue = Integer.valueOf(totalDeaths.get(0));
        System.out.println(index);
        
        double lowerBound = (lowerValue/100)*100;
        double upperBound = ((upperValue + 99)/100)*100;
        
        //System.out.println(upperBound);
        
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(50);
        yAxis.setMinorTickVisible(false);
        
        for(int i = dates.size() - 1; i >= 0; i--){
            series.getData().add(new XYChart.Data(dates.get(i),totalDeaths.get(i)));
        }
    }
    
    public Parent getGraphPane(){
        return graphPane;
    }
    
    public void setDataController(DataViewerController controller) {
        dataController = controller;
    }
    
    public void setData(ArrayList<CovidData> data){
        this.data = data;
    }
}
