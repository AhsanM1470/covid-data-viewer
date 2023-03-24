import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import java.util.Collections;

/**
 * Serves as a template of common functionality for the Controllers of each
 * view. Predominantly ensures that date pickers contain the same value across
 * each controller, and accessing the dataset.
 *
 * @author Ishab Ahmed
 * @version 2023.03.16
 */
public abstract class ViewerController {

    // Instance of Dataset singleton
    protected Dataset dataset = Dataset.getInstance();

    @FXML
    protected StackPane parentPane;

    @FXML
    protected StackPane stackPane;

    protected LocalDate fromDate, toDate;

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

    /**
     * Sets the date range of the date pickers to the given dates.
     *
     * @param fromDate The starting date of the date range to be set.
     * @param toDate The ending date of the date range to be set.
     */
    public void setDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Updates the current panel for the given date range.
     * 
     * @param fromDate The start date of the date range.
     * @param toDate The end date of the date range.
     */
    protected void updatePanelForDateRange(LocalDate fromDate, LocalDate toDate) {
        setDateRange(fromDate, toDate);
        processDataInDateRange(fromDate, toDate);
    }

    /**
     * Resize certain components (not mandatory) in certain panels.
     * 
     * @param parentPane pane that is to be used to scale with
     */
    protected void resizeComponents(Region parentPane) {};

    // ---------------------------- Abstract Methods --------------------------- //

    /**
     * Called when either date picker is changed.
     * 
     * @param from The starting date of the range
     * @param to The ending date of the range
     */
    abstract protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate);

    /**
     * @return The view that the controller is associated with.
     */
    abstract protected Parent getView();
}
