import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    CovidDataLoader dataLoader = new CovidDataLoader();
    protected ArrayList<CovidData> data = dataLoader.load();

    
    @FXML
    protected DatePicker toDatePicker;

    @FXML
    protected DatePicker fromDatePicker;
    
    public void setDateRange(LocalDate from, LocalDate to) {
        fromDatePicker.setValue(from);
        toDatePicker.setValue(to);
        
        dateChanged(from, to);
    }
    
    /**
     * Creates an ArrayList containing data between date period selected filtered
     * from the ArrayList 'data' which was created upon initialisation
     * 
     * @param fromDate
     * @param toDate
     */
    protected ArrayList<CovidData> getDateRangeData(LocalDate fromDate, LocalDate toDate) {
        return new ArrayList<CovidData>(data.stream().filter((cd) -> {
            LocalDate date = LocalDate.parse(cd.getDate());
            return (date.isAfter(fromDate)) && (date.isBefore(toDate));
        }).collect(Collectors.toList()));

    }

    abstract protected void dateChanged(LocalDate from, LocalDate to);
    
    abstract protected Parent getView();
}
