import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;

/**
 * Write a description of class Controller here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class DataViewerController
{
    private DatePicker toDatePicker;
    private DatePicker fromDatePicker;
    
    @FXML
    private void dateChanged(ActionEvent event) {
        System.out.println(toDatePicker.getValue());
        System.out.println(fromDatePicker.getValue());
    }
}
