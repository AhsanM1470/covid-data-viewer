import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import java.util.ArrayList;

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
            }
        }
    }
    
    private void loadTable() {
        
    }
}
