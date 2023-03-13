import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

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
    private TableView dataTable;
    
    private ArrayList<CovidData> data;
    
    public DataViewerController() {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn dateCol = new TableColumn("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("date"));
         
        TableColumn boroughCol = new TableColumn("Borough");
        boroughCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("borough"));
         
        TableColumn newCasesCol = new TableColumn("New Cases");
        newCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("newCases"));
        
        TableColumn totalCasesCol = new TableColumn("Total Cases");
        totalCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("totalCases"));
         
        TableColumn newDeathsCol = new TableColumn("New Deaths");
        newDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("newDeaths"));
    
        TableColumn totalDeathsCol = new TableColumn("Total Deaths");
        totalDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("totalDeaths"));
        
        dataTable.getColumns().addAll(dateCol, boroughCol, newCasesCol, totalCasesCol, newDeathsCol, totalDeathsCol);
    }
    
    @FXML
    private void dateChanged(ActionEvent event) {
        // leftButton.setDisable(true);
        // rightButton.setDisable(true);
        // welcomePane.setVisible(true);
        // tablePane.setVisible(false);
        setWelcomeState(true);
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        if (fromDate != null && toDate != null) {
            if (fromDate.isBefore(toDate)) {
                // leftButton.setDisable(false);
                // rightButton.setDisable(false);
                // welcomePane.setVisible(false);
                // tablePane.setVisible(true);
                populateTable(fromDate, toDate);
            }
        }
    }
    
    private void setWelcomeState(boolean state) {
        leftButton.setDisable(state);
        rightButton.setDisable(state);
        welcomePane.setVisible(state);
        tablePane.setVisible(!state);
    }
    
    private void populateTable(LocalDate from, LocalDate to) {
        dataTable.getItems().clear();
        
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yy-MM-dd");
        
        for (CovidData d : data) {
            LocalDate date = LocalDate.parse(d.getDate());
            if (date.isAfter(from) && date.isBefore(to))
                dataTable.getItems().add(d);
        }
        
        if (dataTable.getItems().isEmpty()) {
            setWelcomeState(true);
        } else {
            setWelcomeState(false);
        }
    }
}
