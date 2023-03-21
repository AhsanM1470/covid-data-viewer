import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class BoroughInfoController {

    @FXML
    private TableView<CovidData> boroughTable;

    @FXML
    private ComboBox<String> filter;

    @FXML
    private Label message;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize(){
        
        TableColumn<CovidData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<CovidData, Integer> retailRecreationGMR = new TableColumn<>("Retail Recreation GMR");
        retailRecreationGMR.setCellValueFactory(new PropertyValueFactory<>("retailRecreationGMR"));
        
        TableColumn<CovidData, Integer> groceryPharmacyGMR = new TableColumn<>("Grocery Pharmacy GMR");
        groceryPharmacyGMR.setCellValueFactory(new PropertyValueFactory<>("groceryPharmacyGMR"));
        
        TableColumn<CovidData, Integer> parksGMR = new TableColumn<>("parksGMR");
        parksGMR.setCellValueFactory(new PropertyValueFactory<>("parksGMR"));
        
        TableColumn<CovidData, Integer> transitGMR = new TableColumn<>("transitGMR");
        transitGMR.setCellValueFactory(new PropertyValueFactory<>("transitGMR"));
        
        TableColumn<CovidData, Integer> workplacesGMR = new TableColumn<>("workplacesGMR");
        workplacesGMR.setCellValueFactory(new PropertyValueFactory<>("workplacesGMR"));

        TableColumn<CovidData, Integer> residentialGMR = new TableColumn<>("residentialGMR");
        residentialGMR.setCellValueFactory(new PropertyValueFactory<>("residentialGMR"));

        TableColumn<CovidData, Integer> newCasesColumn = new TableColumn<>("New Covid Cases");
        newCasesColumn.setCellValueFactory(new PropertyValueFactory<>("newCases"));

        TableColumn<CovidData, Integer> totalCovidCases = new TableColumn<>("Total Covid Cases");
        totalCovidCases.setCellValueFactory(new PropertyValueFactory<>("totalCases"));

        TableColumn<CovidData, Integer> newCovidDeaths = new TableColumn<>("New Covid Deaths");
        newCovidDeaths.setCellValueFactory(new PropertyValueFactory<>("newDeaths"));

        boroughTable.getColumns().addAll(dateColumn, retailRecreationGMR, groceryPharmacyGMR,parksGMR,transitGMR,workplacesGMR, residentialGMR, newCasesColumn, totalCovidCases, newCovidDeaths);
    
    
    }

    public void showData(ArrayList<CovidData> data, Stage stage){
        ObservableList<CovidData> obsData = FXCollections.observableArrayList(data);
        boroughTable.getItems().setAll(obsData);
        // System.out.println(obsData);

        System.out.println(stage.widthProperty()+" "+boroughTable.getPrefWidth());
        boroughTable.prefHeightProperty().bind(stage.heightProperty());
        // boroughTable.prefWidthProperty().bind(stage.widthProperty());
        
    }

}      