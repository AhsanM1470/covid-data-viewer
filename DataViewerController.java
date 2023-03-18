
import javafx.fxml.FXML;
import java.time.LocalDate;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import java.util.ArrayList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * Responsible for managing the GUI components of the application, including
 * the table that displays the CovidData information, the date pickers, and the
 * buttons that navigate through different panels.
 *
 * @author Ishab Ahmed
 * @version 2023.03.13
 */
public class DataViewerController extends Controller {

    @FXML
    private StackPane mainPanel;

    @FXML
    private VBox welcomePane;

    @FXML
    private Pane tablePane;

    @FXML
    private TableView<CovidData> dataTable;

    @FXML
    private Label dataTableInfoLabel;

    /**
     * Initializes the FXML controller class.
     * This method is called by the FXMLLoader when the corresponding FXML file is
     * loaded.
     */
    @FXML
    public void initialize() {

        mainLayout.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal.floatValue() != newVal.floatValue()) {
                newVal.doubleValue();
            }
            ;
        });

        mainLayout.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal.floatValue() != newVal.floatValue()) {
                newVal.doubleValue();
            }
            ;
        });

        // Create TableColumns for the TableView
        TableColumn<CovidData, String> dateCol = new TableColumn<CovidData, String>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("date"));

        TableColumn<CovidData, String> boroughCol = new TableColumn<CovidData, String>("Borough");
        boroughCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("borough"));

        TableColumn<CovidData, String> newCasesCol = new TableColumn<CovidData, String>("New Cases");
        newCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("newCases"));

        TableColumn<CovidData, String> totalCasesCol = new TableColumn<CovidData, String>("Total Cases");
        totalCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("totalCases"));

        TableColumn<CovidData, String> newDeathsCol = new TableColumn<CovidData, String>("New Deaths");
        newDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("newDeaths"));

        TableColumn<CovidData, String> totalDeathsCol = new TableColumn<CovidData, String>("Total Deaths");
        totalDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("totalDeaths"));

        // Add the TableColumns to the TableView
        dataTable.getColumns().addAll(dateCol, boroughCol, newCasesCol, totalCasesCol, newDeathsCol, totalDeathsCol);

        // Make all columns equal width
        dataTable.setColumnResizePolicy(dataTable.CONSTRAINED_RESIZE_POLICY);

        // Current controller to be used is in 0th index
        sceneIndex = 0;
    }

    /**
     * Updates the data table with the chosen date range if valid date chosen,
     * otherwise shows an error message to user.
     */
    protected void processDateRangeData(LocalDate fromDate, LocalDate toDate) {

        // Clear any existing items from the table
        dataTable.getItems().clear();

        // sets the pickers of the current panel to the dates chosen
        ArrayList<CovidData> rangedData = getDateRangeData(fromDate, toDate);
        boolean dataExistsInDateRange = rangedData.size() > 0;

        if (isDateRangeValid(fromDate, toDate) == true) {
            if (dataExistsInDateRange) {
                populateTable(rangedData);
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
     * Changes the state of the welcome pane.
     * 
     * @param state Bool indicating whether the welcome pane should be shown or
     *              hidden
     */
    private void setWelcomeState(boolean state) {
        welcomePane.setVisible(state);
        tablePane.setVisible(!state);
    }

    /**
     * Populates the table with CovidData objects that fall within the given date
     * range.
     * 
     * @param from The starting date of the date range
     * @param to   The ending date of the date range
     */
    private void populateTable(ArrayList<CovidData> dataToShow) {
        // Add all CovidData objects within the given date range to the table
        for (CovidData covidData : dataToShow) {
            dataTable.getItems().add(covidData);
        }
    }

    /**
     * Returns the list of CovidData objects loaded from the data source.
     * 
     * @return ArrayList of CovidData objects
     */
    public ArrayList<CovidData> getData() {
        return data;
    }

    /**
     * Returns the center panel of this view.
     * 
     * @return the main center panel
     */
    protected Parent getView() {
        return mainPanel;
    }
}