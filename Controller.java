import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
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
public abstract class Controller {

    protected enum PanelType {
        MAIN, MAP, STATS, GRAPH;
    }

    @FXML
    protected StackPane stackPane;

    // stores all the covid data
    protected ArrayList<CovidData> data;

    // FXML attributes which all controllers will have
    @FXML
    protected DatePicker toDatePicker;

    @FXML
    protected DatePicker fromDatePicker;

    @FXML
    protected BorderPane viewPane;

    @FXML
    protected StackPane parentPane;

    protected PanelType currentPanelType;

    protected List<PanelType> scalePanels = Arrays.asList(new PanelType[]{PanelType.MAP});

    static boolean inTransition = false;

    /**
     * Creates a new Controller object and initialises it with a list of
     * CovidData objects loaded from the data source.
     */
    public Controller() {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();
    }

    /**
     * Handles the event when the date picker is changed.
     * Once two dates are selected, check if its valid, and attempt to process data
     * within that date range
     * 
     * @param event The event triggered by changing the date picker.
     */
    @FXML
    protected void dateChanged(ActionEvent event) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            return;
        }

        // allowPanelSwitching(isDateRangeValid(fromDate, toDate));
        processDataInDateRange(fromDate, toDate);
    }

    protected PanelType getPanelType(){
        return currentPanelType;
    }

    /**
     * @return the main pane that we've all added components onto.
     */
    protected Parent getView() {
        return viewPane;
    };

    // -------------------------------- Getters -------------------------------- //

    /**
     * @return the currently selected date in 'fromDatePicker'
     */
    protected LocalDate getFromDate() {
        return fromDatePicker.getValue();
    }

    /**
     * @return the currently selected date in 'toDatePicker'
     */
    protected LocalDate getToDate() {
        return toDatePicker.getValue();
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
        fromDatePicker.setValue(fromDate);
        toDatePicker.setValue(toDate);

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

    // ---------------------------- Abstract Methods --------------------------- //

    /**
     * Called when either date picker is changed.
     * 
     * @param from the starting date of the range
     * @param to   the ending date of the range
     */
    abstract protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate);

}
