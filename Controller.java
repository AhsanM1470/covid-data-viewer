import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.Parent;

/**
 * Abstract class Controller - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Controller
{
    private LocalDate fromDate;
    private LocalDate toDate;
    
    @FXML
    protected DatePicker toDatePicker;

    @FXML
    protected DatePicker fromDatePicker;
    
    public void setDateRange(LocalDate from, LocalDate to) {
        fromDatePicker.setValue(from);
        toDatePicker.setValue(to);
        
        dateChanged(from, to);
    }
    
    abstract protected void dateChanged(LocalDate from, LocalDate to);
    
    abstract protected Parent getView();
}
