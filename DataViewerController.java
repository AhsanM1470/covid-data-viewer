import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDate;
import javafx.scene.control.Button;

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
    private void dateChanged(ActionEvent event) {
        rightButton.setDisable(true);
        
        DateFormat dateFormat = new SimpleDateFormat("yy-mm-dd");
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        if (fromDate != null && toDate != null) {
            if (fromDate.isBefore(toDate)) {
                rightButton.setDisable(false);
            }
        }
    }
}
