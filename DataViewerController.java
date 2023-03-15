import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import java.util.ArrayList;
// import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Label;

/**
 * Controls logic of DataViewer
 *
 * @author Ishab Ahmed
 * @version 2023.03.13
 */
public class DataViewerController implements Initializable
{
    @FXML
    private DatePicker fromDatePicker;
    
    @FXML
    private DatePicker toDatePicker;
    
    @FXML
    private Button leftButton;
    
    @FXML
    private Button rightButton;
    
    @FXML
    private VBox welcomePane;
    
    @FXML
    private Pane tablePane;
    
    @FXML
    private TableView<CovidData> dataTable;
    
    @FXML
    private Label dataTableInfoLabel;
    
    private ArrayList<CovidData> data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();

        TableColumn<CovidData,String> dateCol = new TableColumn<CovidData,String>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("date"));
         
        TableColumn<CovidData,String> boroughCol = new TableColumn<CovidData,String>("Borough");
        boroughCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("borough"));
         
        TableColumn<CovidData,String> newCasesCol = new TableColumn<CovidData,String>("New Cases");
        newCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("newCases"));
        
        TableColumn<CovidData,String> totalCasesCol = new TableColumn<CovidData,String>("Total Cases");
        totalCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("totalCases"));
         
        TableColumn<CovidData,String> newDeathsCol = new TableColumn<CovidData,String>("New Deaths");
        newDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("newDeaths"));
    
        TableColumn<CovidData,String> totalDeathsCol = new TableColumn<CovidData,String>("Total Deaths");
        totalDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("totalDeaths"));
        
        dataTable.getColumns().addAll(dateCol, boroughCol, newCasesCol, totalCasesCol, newDeathsCol, totalDeathsCol);
    }
    
    @FXML
    private void dateChanged(ActionEvent event) {
        setWelcomeState(true);
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        if (validDateRangeChosen(fromDate, toDate)) {
            populateTable(fromDate, toDate);
        } else {
            dataTableInfoLabel.setText("The 'from' date is after the 'to' date.");
            setWelcomeState(false);
        }
    }
    
    private boolean validDateRangeChosen(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            if (from.isBefore(to)) {
                return true;
            }
        }
        return false;
    }
    
    private void setWelcomeState(boolean state) {
        leftButton.setDisable(state);
        rightButton.setDisable(state);
        welcomePane.setVisible(state);
        tablePane.setVisible(!state);
    }
    
    private void populateTable(LocalDate from, LocalDate to) {
        dataTable.getItems().clear();
        
        // DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yy-MM-dd");
        
        for (CovidData d : data) {
            LocalDate date = LocalDate.parse(d.getDate());
            if (date.isAfter(from) && date.isBefore(to))
                dataTable.getItems().add(d);
        }
        
        checkNoDataInRange(from, to);
        setWelcomeState(false);
    }
    
    private void checkNoDataInRange(LocalDate from, LocalDate to) {
        if (dataTable.getItems().isEmpty()) {
            dataTableInfoLabel.setText("There's no available data for the selected date range.");
        } else {
            dataTableInfoLabel.setText("Showing data from " + from + " to " + to + ".");
        }
    }
}
