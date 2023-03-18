import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import java.util.ArrayList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;

/**
 * Responsible for managing the GUI components of the application, including 
 * the table that displays the CovidData information, the date pickers, and the
 * buttons that navigate through different panels.
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

    private Controller[] controllers;
    private int controllerIndex;

    /**
     * Initializes the FXML controller class.
     * This method is called by the FXMLLoader when the corresponding FXML file is loaded.
     */
    @FXML
    public void initialize() {

        // Create TableColumns for the TableView
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

        // Add the TableColumns to the TableView
        dataTable.getColumns().addAll(dateCol, boroughCol, newCasesCol, totalCasesCol, newDeathsCol, totalDeathsCol);

        // Make all columns equal width
        dataTable.setColumnResizePolicy(dataTable.CONSTRAINED_RESIZE_POLICY);

        // Try to load the controllers for all the panels
        try {
            controllers = new Controller[2];
            loadControllers();
        } catch (Exception e) {
            // Print the error message and stack trace to the console
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // Current controller to be used is in 0th index
        controllerIndex = 0;
    }

    /**
     * Loads the controllers of all of the panels in the app.
     * 
     * @throws Exception if the FXMLLoader fails to load any FXML file.
     */
    public void loadControllers() throws Exception {
        FXMLLoader mapLoader = new FXMLLoader(getClass().getResource(
                    "MapWindow.fxml"));
        mapLoader.load();
        Controller mapController = mapLoader.getController();
        
        controllers[1] = mapController;
        // Current controller responsible for first panel
        controllers[0] = this;
    }

    /**
     * Handles the event when the date picker is changed. Updates the data table 
     * with the chosen date range  if valid date chosen, otherwise shows an error message to user.
     * 
     * @param event The event triggered by changing the date picker.
     */
    @FXML
    private void dateChanged(ActionEvent event) {
        togglePanelSwitching(true);
        
        // Clear any existing items from the table
        dataTable.getItems().clear();
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        // sets the pickers of the current panel to the dates chosen
        controllers[controllerIndex].setDateRange(fromDate, toDate);

        if (isDateRangeValid(fromDate, toDate) == true) {
            if (isDataInDateRange(fromDate, toDate) == true) {
                populateTable(fromDate, toDate);
                togglePanelSwitching(false);
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
     * Checks if there is any CovidData objects within the date range provided.
     * 
     * @param from The start date of the range to be checked
     * @param to The end date of the range to be checked
     * @return bool indicating if there is CovidData within the date range
     */
    private boolean isDataInDateRange(LocalDate from, LocalDate to) {
        boolean dataInRange = false;

        for (CovidData row : data) {
            // date in CovidData is string, parse to LocalDate for easy comparison
            LocalDate date = LocalDate.parse(row.getDate());
            // check if date is between 'from' and 'to'
            dataInRange = isDateInRange(date, from, to);
            if (dataInRange) {
                break;
            } 
        }
        return dataInRange;
    }

    /**
     * Changes the state of the welcome pane.
     * 
     * @param state Bool indicating whether the welcome pane should be shown or hidden
     */
    private void setWelcomeState(boolean state) {
        welcomePane.setVisible(state);
        tablePane.setVisible(!state);
    }
    
    private void togglePanelSwitching(boolean state) {
        leftButton.setDisable(state);
        rightButton.setDisable(state);
    }

    /**
     * Populates the table with CovidData objects that fall within the given date range.
     * 
     * @param from The starting date of the date range
     * @param to The ending date of the date range
     */
    private void populateTable(LocalDate from, LocalDate to) {
        // Add all CovidData objects within the given date range to the table
        for (CovidData d : getDateRangeData(from, to)) {
            dataTable.getItems().add(d);
        }
    }

    /**
     * Changes the center of the main layout to the next panel.
     * 
     * @param event The event triggered by clicking the next panel button
     */
    @FXML
    private void nextPanel(ActionEvent event) {
        controllerIndex++;
        controllerIndex = controllerIndex % controllers.length;

        // Sets the date picker of the next panel to the dates chosen on the current panel
        controllers[controllerIndex].setDateRange(getFromDate(), getToDate());

        // Switches the center of the main layout to the next panel
        mainLayout.setCenter(controllers[controllerIndex].getView());
    }

    /**
     * Changes the center of the main layout to the previous panel.
     * 
     * @param event The event triggered by clicking the next panel button
     */
    @FXML
    private void previousPanel(ActionEvent event) {
        controllerIndex--;
        if (controllerIndex < 0) {
            controllerIndex = controllers.length - 1;
        }

        // Sets the date picker of the previous panel to the dates chosen on the current panel
        controllers[controllerIndex].setDateRange(getFromDate(), getToDate());

        // Switches the center of the main layout to the previous panel
        mainLayout.setCenter(controllers[controllerIndex].getView());
    }

    /**
     * @return the currently selected date in 'fromDatePicker'
     */
    public LocalDate getFromDate() {
        return fromDatePicker.getValue();
    }

    /**
     * @return the currently selected date in 'toDatePicker'
     */
    public LocalDate getToDate() {
        return toDatePicker.getValue();
    }

    /**
     * Returns the list of CovidData objects loaded from the data source.
     * 
     * @return ArrayList of CovidData objects
     */
    public ArrayList<CovidData> getData() {
        return data;
    }

    protected void dateChanged(LocalDate from, LocalDate to) {;}

    /**
     * Returns the center panel of this view.
     * 
     * @return the main center panel
     */
    protected Parent getView() {
        return mainPanel;
    }
}
