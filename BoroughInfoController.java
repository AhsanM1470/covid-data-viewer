import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

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
    // @SuppressWarnings("unchecked")
    public void initialize() {

        TableColumn<CovidData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<CovidData, Integer> retailRecreationGMR = new TableColumn<>("Retail Recreation GMR");
        retailRecreationGMR.setCellValueFactory(new PropertyValueFactory<>("retailRecreationGMR"));

        TableColumn<CovidData, Integer> groceryPharmacyGMR = new TableColumn<>("Grocery Pharmacy GMR");
        groceryPharmacyGMR.setCellValueFactory(new PropertyValueFactory<>("groceryPharmacyGMR"));

        TableColumn<CovidData, Integer> parksGMR = new TableColumn<>("Parks GMR");
        parksGMR.setCellValueFactory(new PropertyValueFactory<>("parksGMR"));

        TableColumn<CovidData, Integer> transitGMR = new TableColumn<>("Transit GMR");
        transitGMR.setCellValueFactory(new PropertyValueFactory<>("transitGMR"));

        TableColumn<CovidData, Integer> workplacesGMR = new TableColumn<>("WorkPlaces GMR");
        workplacesGMR.setCellValueFactory(new PropertyValueFactory<>("workplacesGMR"));

        TableColumn<CovidData, Integer> residentialGMR = new TableColumn<>("Resedential GMR");
        residentialGMR.setCellValueFactory(new PropertyValueFactory<>("residentialGMR"));

        TableColumn<CovidData, Integer> newCasesColumn = new TableColumn<>("New Covid Cases");
        newCasesColumn.setCellValueFactory(new PropertyValueFactory<>("newCases"));

        TableColumn<CovidData, Integer> totalCovidCases = new TableColumn<>("Total Covid Cases");
        totalCovidCases.setCellValueFactory(new PropertyValueFactory<>("totalCases"));

        TableColumn<CovidData, Integer> newCovidDeaths = new TableColumn<>("New Covid Deaths");
        newCovidDeaths.setCellValueFactory(new PropertyValueFactory<>("newDeaths"));

        boroughTable.getColumns().addAll(dateColumn, retailRecreationGMR, groceryPharmacyGMR, parksGMR, transitGMR,
                workplacesGMR, residentialGMR, newCasesColumn, totalCovidCases, newCovidDeaths);

        // add all the column names to the filtercombo box
        columnNames = boroughTable.getColumns().stream().map(e -> e.getText()).collect(Collectors.toList());
        filterComboBox.getItems().addAll(columnNames);

        // create the options for how the user can sort. Ascending or Descending
        orderStrings = new String[] { "Ascending", "Descending" };
        orderComboBox.getItems().setAll(orderStrings);

        String initialOrder = orderStrings[0];
        orderComboBox.setValue(initialOrder);

        // initially, set the parameters for sorting to be by ascending dates
        orderByAscending = true;
        filterSelected = columnNames.get(0);
        filterComboBox.setValue(filterSelected);

    }

    /**
     * show the actual data in the table is created
     * 
     * @param data
     */
    public void showData(ArrayList<CovidData> data) {
        ObservableList<CovidData> obsData = FXCollections.observableArrayList(data);
        boroughTable.getItems().setAll(obsData);

        // bind the height of the tableview to the height of the stage
        boroughTable.prefHeightProperty().bind(mainPane.heightProperty());

        sortTable();
    }

    /**
     * Action event for when an item from the combobox is selected
     * 
     * @param event
     */
    @FXML
    void filterSelected(ActionEvent event) {
        filterSelected = filterComboBox.getValue();
        sortTable();
    }

    /**
     * Sorting the table by a certain column
     */
    private void sortTable() {
        // TODO: ask lads whether to keep sorting or just replace it with a label of the borough name
        
        int colIndex = getColumnIndexOfStat(filterSelected);

        // column we're retrieving could be String or Integer, hence the '?'
        TableColumn<CovidData, ?> col = (TableColumn<CovidData, ?>) boroughTable.getColumns().get(colIndex);

        // determine whether the order should be ascending or descending
        col.setSortType(orderByAscending ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);

        boroughTable.getSortOrder().setAll(col);
        boroughTable.sort();
    }

    /**
     * Handling when the order button is click determining what text to display
     * along with
     * 
     * @param event
     */
    @FXML
    void orderButtonClick(ActionEvent event) {
        String order = (String) orderComboBox.getValue();
        orderByAscending = order.equals("Ascending");
        sortTable();
    }

    /**
     * get the index of the column that points to a specific stat selected
     * 
     * @param stat
     * @return the index of the stat column. if not found, return -1
     */
    private int getColumnIndexOfStat(String stat) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equals(stat)) {
                return i;
            }
        }
        return -1;
    }

}