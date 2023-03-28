import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Responsible for managing the GUI components of the application, including
 * the table that displays the CovidData information, the date pickers, and the
 * buttons that navigate through different panels.
 *
 * @author Ishab Ahmed
 * @version 2023.03.13
 */
public class WelcomeViewController extends ViewController {

    @FXML
    private VBox welcomePane;

    @FXML
    protected BorderPane viewPane;

    @FXML
    private Pane tablePane;

    @FXML
    private TableView<CovidData> dataTable;

    @FXML
    private Label dataTableInfoLabel;

    @FXML
    private Label validDataRangeLabel;

    /**
     * Initializes the FXML controller class.
     * This method is called by the FXMLLoader when the corresponding FXML file is
     * loaded.
     * 
     * Creates a new Controller object and initialises it with a list of
     * CovidData objects loaded from the data source.
     */
    @FXML
    protected void initialize() {
        super.initialize();

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
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        validDataRangeLabel.setText("The dataset shows data from " + Collections.min(dataset.getData()).getDate()
                + " to " + Collections.max(dataset.getData()).getDate()
                + " so you can only select dates from that period.");
    }

    /**
     * Handles the event when the date picker is changed. Updates the data table
     * with the chosen date range if valid date chosen, otherwise shows an error
     * message to user.
     * 
     * @param event The event triggered by changing the date picker.
     */
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        // Clear any existing items from the table
        dataTable.getItems().clear();

        ArrayList<CovidData> rangedData = dataset.getDataInDateRange(fromDate, toDate);
        boolean dataExistsInDateRange = rangedData.size() > 0;

        if (dataset.isDateRangeValid(fromDate, toDate) == true) {
            if (dataExistsInDateRange) {
                populateTable(rangedData);
                dataTableInfoLabel.setText("Showing data from " + fromDate + " to " + toDate + ".");
            } else {
                dataTableInfoLabel.setText("There's no available data for the selected date range.");
            }
        } else {
            dataTableInfoLabel.setText("The 'from' date is after the 'to' date.");
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
     * @param dataToShow The data to populate the table with
     */
    private void populateTable(ArrayList<CovidData> dataToShow) {
        // Add all CovidData objects within the given date range to the table
        for (CovidData covidData : dataToShow) {
            dataTable.getItems().add(covidData);
        }

    }

    public Parent getView() {
        return viewPane;
    }

}