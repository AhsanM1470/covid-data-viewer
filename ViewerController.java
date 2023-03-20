import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

/**
 * Serves as a template of common functionality for the Controllers of each
 * view. Predominantly ensures that date pickers contain the same value across
 * each controller, and accessing the dataset.
 *
 * @author Ishab Ahmed
 * @version 2023.03.16
 */
public abstract class ViewerController {

    @FXML
    protected StackPane stackPane;

    // stores all the covid data
    protected ArrayList<CovidData> data;

    @FXML
    protected StackPane parentPane;

    protected LocalDate fromDate;
    protected LocalDate toDate;

    static boolean inTransition = false;

    // -------------------------------- Getters -------------------------------- //

    /**
     * @return the currently selected date in 'fromDatePicker'
     */
    protected LocalDate getFromDate() {
        return fromDate;
    }

    /**
     * @return the currently selected date in 'toDatePicker'
     */
    protected LocalDate getToDate() {
        return toDate;
    }

    // -------------------------------- Setters -------------------------------- //

    protected void setData(ArrayList<CovidData> data) {
        this.data = data;
    }

    // ----------------------- Ranged Data Helper Methods ---------------------- //

    /**
     * Returns an ArrayList of CovidData that fall within the specified date range.
     * 
     * @param from   The start date of the date range.
     * @param toDate The end date of the date range.
     * @return An ArrayList of CovidData that fall within the specified date range.
     */
    protected ArrayList<CovidData> getDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        // filters data to only include data that falls within the specified date range
        return new ArrayList<CovidData>(data.stream()
                .filter((covidData) -> isDateInRange(LocalDate.parse(covidData.getDate()), fromDate, toDate))
                .collect(Collectors.toList()));
    }

    /**
     * Sets the date range of the date pickers to the given dates.
     * Calls the processDateRangeData() method which performs actions related to the
     * date picked on the current scene
     *
     * @param from   The starting date of the date range to be set.
     * @param toDate The ending date of the date range to be set.
     */
    public void setDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;

        // do what is specified to be done within the date range
        processDataInDateRange(fromDate, toDate);
    }

    // ---------------------------- Date Validation ---------------------------- //

    /**
     * Checks if a given date range is valid.
     * 
     * @param from The start date of the range.
     * @param to   The end date of the range.
     * @return true if the range is valid (i.e. from is before or equal to to),
     *         false otherwise
     */
    protected boolean isDateRangeValid(LocalDate fromDate, LocalDate toDate) {
        // if any of them are null, date range is automatically invalid
        if (fromDate == null || toDate == null) {
            return false;
        }
        return fromDate.isBefore(toDate) || fromDate.isEqual(toDate);
    }

    /**
     * 
     * @param date date to be checked
     * @param from starting date (inclusive)
     * @param to   ending date (inclusive)
     * @return whether the date is within an inclusive date range
     */
    protected boolean isDateInRange(LocalDate date, LocalDate fromDate, LocalDate toDate) {
        return (date.isAfter(fromDate) && date.isBefore(toDate)) || date.isEqual(fromDate) || date.isEqual(toDate);
    }

    /**
     * Resize certain components (not mandatory) in certain panels.
     * 
     * @param parentPane pane that is to be used to scale with
     */
    protected void resizeComponents(Region parentPane) {
    };

    // ---------------------------- Abstract Methods --------------------------- //

    /**
     * Called when either date picker is changed.
     * 
     * @param from the starting date of the range
     * @param to   the ending date of the range
     */
    abstract protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate);

    /**
     * @return the main pane that we've all added components onto.
     */
    abstract protected Parent getView();
}
