import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import java.util.ArrayList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;

/**
 * Controls logic of DataViewer
 *
 * @author Ishab Ahmed
 * @version 2023.03.13
 */
public class DataViewerController extends Controller
{
    @FXML
    private BorderPane mainLayout;
    
    @FXML
    private StackPane mainPanel;
    
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
    
    // private ArrayList<CovidData> data;
    
    private Controller[] controllers;
    private int controllerIndex;

    @FXML
    public void initialize() {
        
        // CovidDataLoader dataLoader = new CovidDataLoader();
        // data = dataLoader.load();

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
        // make all columns equal width
        dataTable.setColumnResizePolicy(dataTable.CONSTRAINED_RESIZE_POLICY);
        
        try {
            controllers = new Controller[2];
            loadControllers();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        controllerIndex = 0;
    }
    
    /**
     * Loads the controllers of all of the panels.
     */
    public void loadControllers() throws Exception {
        
        FXMLLoader mapLoader = new FXMLLoader(getClass().getResource(
               "MapWindow.fxml"));
        mapLoader.load();
        Controller mapController = mapLoader.getController();
        
        controllers[0] = this;
        controllers[1] = mapController;
    }
    
    /**
     * When the date is changed, check if the date range is valid and display the data if so.
     */
    @FXML
    private void dateChanged(ActionEvent event) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        // sets the pickers of the current panel to the dates chosen
        controllers[controllerIndex].setDateRange(fromDate, toDate);
        
        if (isDateRangeValid(fromDate, toDate) == true) {
            if (isDataInDateRange(fromDate, toDate) == true) {
                populateTable(fromDate, toDate);
                dataTableInfoLabel.setText("Showing data from " + fromDate + " to " + toDate + ".");
            } else {
                dataTableInfoLabel.setText("There's no available data for the selected date range.");
            }
        } else {
            dataTableInfoLabel.setText("The 'from' date is before the 'to' date.");
        }
        
        setWelcomeState(false);
    }
    
    /**
     * @return whether the 'from' date if before the 'to' date 
     */
    private boolean isDateRangeValid(LocalDate from, LocalDate to) {
        boolean validRange = false;
        
        if (from != null && to != null) {
            if (from.isBefore(to)) {
                validRange = true;
            }
        }
        
        return validRange;
    }
    
    /**
     * Toggles the Welcome components.
     */
    private void setWelcomeState(boolean state) {
        leftButton.setDisable(state);
        rightButton.setDisable(state);
        welcomePane.setVisible(state);
        tablePane.setVisible(!state);
    }
    
    /**
     * Populates the table with the data in the range selected.
     */
    private void populateTable(LocalDate from, LocalDate to) {
        dataTable.getItems().clear();
        
        for (CovidData d : data) {
            LocalDate date = LocalDate.parse(d.getDate());
            if (date.isAfter(from) && date.isBefore(to))
                dataTable.getItems().add(d);
        }
    }
    
    /**
     * Changes the center panel to the next.
     */
    @FXML
    private void nextPanel(ActionEvent event) {
        controllerIndex++;
        controllerIndex = controllerIndex % controllers.length;
        
        // sets the date picker of the next panel to the dates chosen on the current panel
        controllers[controllerIndex].setDateRange(getFromDate(), getToDate());
        
        // switches the center of the BorderPane to the next panel
        mainLayout.setCenter(controllers[controllerIndex].getView());
    }
    
    /**
     * Changes the center panel to the previous.
     */
    @FXML
    private void previousPanel(ActionEvent event) {
        controllerIndex--;
        if (controllerIndex < 0) {
            controllerIndex = controllers.length - 1;
        }
        
        // sets the date picker of the previous panel to the dates chosen on the current panel
        controllers[controllerIndex].setDateRange(getFromDate(), getToDate());
        
        // switches the center of the BorderPane to the previous panel
        mainLayout.setCenter(controllers[controllerIndex].getView());
    }
    
    /**
     * @return the value in the fromDatePicker
     */
    public LocalDate getFromDate() {
        return fromDatePicker.getValue();
    }
    
    /**
     * @return the value in the toDatePicker
     */
    public LocalDate getToDate() {
        return toDatePicker.getValue();
    }
    
    /**
     * @return an array of all of the CovidData
     */
    public ArrayList<CovidData> getData() {
        return data;
    }
    
    protected void dateChanged(LocalDate from, LocalDate to) {;}
    
    /**
     * @return the main centre panel
     */
    protected Parent getView() {
        return mainPanel;
    }
}
