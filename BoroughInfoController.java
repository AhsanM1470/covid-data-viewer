import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

/**
 * Responsible for displaying CovidData objects in a TableView object for
 * a specified borough. Opens when a borough polygon is clicked on
 * MapViewController.
 * 
 * @author Harshraj Patel
 * @version 2023.03.28
 */
public class BoroughInfoController {

    @FXML
    private TableView<CovidData> boroughTable;

    @FXML
    private ComboBox<String> filterComboBox, orderComboBox;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label message;

    List<String> columnNames;
    String[] orderStrings;
    boolean orderByAscending;
    String filterSelected;

    @FXML
    public void initialize() {
        TableColumn<CovidData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<CovidData, Integer> retailRecreationGMRCol = new TableColumn<>("Retail and Recreation Mobility");
        retailRecreationGMRCol.setCellValueFactory(new PropertyValueFactory<>("retailRecreationGMR"));

        TableColumn<CovidData, Integer> groceryPharmacyGMRCol = new TableColumn<>("Grocery and Pharmacy Mobility");
        groceryPharmacyGMRCol.setCellValueFactory(new PropertyValueFactory<>("groceryPharmacyGMR"));

        TableColumn<CovidData, Integer> parksGMRCol = new TableColumn<>("Parks Mobility");
        parksGMRCol.setCellValueFactory(new PropertyValueFactory<>("parksGMR"));

        TableColumn<CovidData, Integer> transitGMRCol = new TableColumn<>("Transit Stations Mobility");
        transitGMRCol.setCellValueFactory(new PropertyValueFactory<>("transitGMR"));

        TableColumn<CovidData, Integer> workplacesGMRCol = new TableColumn<>("Workplaces Mobility");
        workplacesGMRCol.setCellValueFactory(new PropertyValueFactory<>("workplacesGMR"));

        TableColumn<CovidData, Integer> residentialGMRCol = new TableColumn<>("Residential Mobility");
        residentialGMRCol.setCellValueFactory(new PropertyValueFactory<>("residentialGMR"));

        TableColumn<CovidData, Integer> newCasesCol = new TableColumn<>("New Cases");
        newCasesCol.setCellValueFactory(new PropertyValueFactory<>("newCases"));

        TableColumn<CovidData, Integer> totalCasesCol = new TableColumn<>("Total Cases");
        totalCasesCol.setCellValueFactory(new PropertyValueFactory<>("totalCases"));

        TableColumn<CovidData, Integer> newDeathsCol = new TableColumn<>("New Deaths");
        newDeathsCol.setCellValueFactory(new PropertyValueFactory<>("newDeaths"));

        TableColumn<CovidData, Integer> totalDeathsCol = new TableColumn<>("Total Deaths");
        totalDeathsCol.setCellValueFactory(new PropertyValueFactory<>("totalDeaths"));

        boroughTable.getColumns().addAll(dateCol, retailRecreationGMRCol, groceryPharmacyGMRCol, parksGMRCol,
                transitGMRCol,
                workplacesGMRCol, residentialGMRCol, newCasesCol, totalCasesCol, newDeathsCol, totalDeathsCol);

        // Add all the column names to the filter combobox
        columnNames = boroughTable.getColumns().stream().map(e -> e.getText()).collect(Collectors.toList());
        filterComboBox.getItems().addAll(columnNames);

        // Bind the height of the tableview to the height of the stage
        boroughTable.prefHeightProperty().bind(mainPane.heightProperty());

        // Create the options for how the user can sort
        orderStrings = new String[] { "Ascending", "Descending" };
        orderComboBox.getItems().setAll(orderStrings);

        // Initially ordered by ascending
        String initialOrder = orderStrings[0];
        orderComboBox.setValue(initialOrder);
        orderByAscending = true;

        filterSelected = columnNames.get(0);
        filterComboBox.setValue(filterSelected);
    }

    /**
     * Filters the data displayed in boroughTable based on the selected option in
     * the filterComboBox
     * 
     * @param event ActionEvent triggered by the user selecting a filter option from
     *              the filterComboBox
     */
    @FXML
    void filterSelected(ActionEvent event) {
        filterSelected = filterComboBox.getValue();
        sortTable();
    }

    /**
     * Sets the order of sorting for the data displayed in boroughTable based on the
     * selected option in the orderComboBox
     * 
     * @param event ActionEvent triggered by the user changing the order type
     */
    @FXML
    void orderButtonClick(ActionEvent event) {
        String order = (String) orderComboBox.getValue();
        orderByAscending = order.equals("Ascending");
        sortTable();
    }

    /**
     * Displays CovidData objects in an ArrayList by adding them to the boroughTable
     * 
     * @param data ArrayList of CovidData objects to be displayed
     */
    public void showData(ArrayList<CovidData> data) {
        for (CovidData covidData : data) {
            boroughTable.getItems().add(covidData);
        }

        sortTable();
    }

    /**
     * Sorts the data displayed in the TableView by the selected filter and order
     * options.
     */
    private void sortTable() {
        // Get the column to sort
        int colIndex = getColumnIndexByName(filterSelected);

        // Column being retrieved could be String or Integer, hence the `?`
        TableColumn<CovidData, ?> col = (TableColumn<CovidData, ?>) boroughTable.getColumns().get(colIndex);

        // Determine whether the order should be ascending or descending
        col.setSortType(orderByAscending ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);

        boroughTable.getSortOrder().setAll(col);
        boroughTable.sort();
    }

    /**
     * Returns the index of a TableColumn in boroughTable by its name.
     * 
     * @param colName The name of the TableColumn to retrieve the index of
     * @return Index of the TableColumn, or -1 if the column is not found
     */
    private int getColumnIndexByName(String colName) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equals(colName)) {
                return i;
            }
        }
        return -1;
    }
}