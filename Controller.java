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
public abstract class Controller {

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
     * @param fromDate
     * @param toDate
     * @return an ArrayList containing data between date period selected, filtered
     *         from the ArrayList 'data'
     */
    protected ArrayList<CovidData> getDateRangeData(LocalDate fromDate, LocalDate toDate) {
        return new ArrayList<CovidData>(data.stream().filter((cd) -> {
            LocalDate date = LocalDate.parse(cd.getDate());
            return (date.isAfter(fromDate)) && (date.isBefore(toDate));
        }).collect(Collectors.toList()));

    }

    /**
     * determines
     * 
     * @param fromDate
     * @param toDate
     * @return whether a selected date range is valid (from is before to)
     */
    protected boolean isDateRangeValid(LocalDate fromDate, LocalDate toDate) {
        // if any of them are null, date range is automatically invalid
        if (fromDate == null || toDate == null) {
            return false;
        }
        return fromDate.isBefore(toDate);

    }

    abstract protected void dateChanged(LocalDate from, LocalDate to);

    abstract protected Parent getView();
}
