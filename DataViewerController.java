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

/**
 * Controls logic of DataViewer
 *
 * @author Ishab Ahmed
 * @version 2023.03.13
 */
public class DataViewerController
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
    
    @FXML
    private void dateChanged(ActionEvent event) {
        leftButton.setDisable(true);
        rightButton.setDisable(true);
        welcomePane.setVisible(true);
        tablePane.setVisible(false);
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        if (fromDate != null && toDate != null) {
            if (fromDate.isBefore(toDate)) {
                leftButton.setDisable(false);
                rightButton.setDisable(false);
                welcomePane.setVisible(false);
                tablePane.setVisible(true);
                System.out.println("x");
                populateTable(fromDate, toDate);
                System.out.println("y");
            }
        }
    }
    
    private void populateTable(LocalDate from, LocalDate to) {
        System.out.println("a");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yy-MM-dd");
        
        System.out.println("i");
        //ArrayList<CovidData> filteredData = data.stream().filter(x -> LocalDate.parse(x.getDate(), dateFormat).isAfter(from)).filter(x -> LocalDate.parse(x.getDate(), dateFormat).isBefore(to)).collect(Collectors.toCollection(ArrayList::new));
        
        System.out.println("n");
        TableColumn dateCol = new TableColumn("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("date"));
         
        TableColumn boroughCol = new TableColumn("Borough");
        boroughCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("borough"));
         
        TableColumn newCasesCol = new TableColumn("New Cases");
        newCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("newCases"));
        System.out.println("f");
        TableColumn totalCasesCol = new TableColumn("Total Cases");
        totalCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("totalCases"));
         
        TableColumn newDeathsCol = new TableColumn("New Deaths");
        newDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("newDeaths"));
        System.out.println("j");
        TableColumn totalDeathsCol = new TableColumn("Total Deaths");
        totalDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData,String>("totalDeaths"));
        
        System.out.println("b");
        dataTable.getColumns().addAll(dateCol, boroughCol, newCasesCol, totalCasesCol, newDeathsCol, totalDeathsCol);
        System.out.println("c");
        for (CovidData d : data) {
            LocalDate date = LocalDate.parse(d.getDate());
            if (date.isAfter(from) && date.isBefore(to))
                dataTable.getItems().add(d);
        }
        System.out.println("d");
    }
}
