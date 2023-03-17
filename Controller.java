import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.Parent;

/**
 * Serves as a template of common functionality for the Controllers of each
 * view. Predominantly ensures that date pickers contain the same value across
 * each controller, and accessing the dataset.
 *
 * @author Ishab Ahmed
 * @version 2023.03.16
 */
public abstract class Controller {

    protected ArrayList<CovidData> data;

    @FXML
    protected DatePicker toDatePicker;

    @FXML
    protected DatePicker fromDatePicker;
    
    /**
     * Creates a new Controller object and initialises it with a list of 
     * CovidData objects loaded from the data source.
     */
    public Controller() {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();
    }
    
    /**
     * Sets the date range of the date pickers to the given dates.
     *
     * @param from The starting date of the date range to be set.
     * @param to   The ending date of the date range to be set.
     */
    public void setDateRange(LocalDate from, LocalDate to) {
        fromDatePicker.setValue(from);
        toDatePicker.setValue(to);

        dateChanged(from, to);
    }

    /**
     * Returns an ArrayList of CovidData that fall within the specified date range.
     * 
     * @param fromDate The start date of the date range.
     * @param toDate The end date of the date range.
     * @return An ArrayList of CovidData that fall within the specified date range.
     */
    protected ArrayList<CovidData> getDateRangeData(LocalDate fromDate, LocalDate toDate) {
        // filters data to only include data that falls within the specified date range
        return new ArrayList<CovidData>(data.stream().filter((cd) -> {
                LocalDate date = LocalDate.parse(cd.getDate());
                return (date.isAfter(fromDate)) && (date.isBefore(toDate));
            }).collect(Collectors.toList()));
    }

    /**
     * Checks if a given date range is valid.
     * 
     * @param fromDate The start date of the range.
     * @param toDate The end date of the range.
     * @return true if the range is valid (i.e. fromDate is before or equal to toDate), false otherwise
    */
    protected boolean isDateRangeValid(LocalDate fromDate, LocalDate toDate) {
        // if any of them are null, date range is automatically invalid
        if (fromDate == null || toDate == null) {
            return false;
        }
        return fromDate.isBefore(toDate) || fromDate.isEqual(toDate);
    }
    
    /**
     * Called when either date picker is changed.
     * 
     * @param from the starting date of the range
     * @param to the ending date of the range
     */
    abstract protected void dateChanged(LocalDate from, LocalDate to);
    
    /**
     * Returns the center panel of the controller.
     *
     * @return The center panel of the controller as an instance of Parent class.
     */
    abstract protected Parent getView();
}
