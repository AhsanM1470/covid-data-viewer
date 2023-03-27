import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.Parent;
import java.util.Collections;
import javafx.fxml.Initializable;

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
    protected BorderPane viewPane;

    @FXML
    protected StackPane parentPane;

    @FXML
    protected StackPane stackPane;

    protected LocalDate fromDate, toDate;

    @FXML
    protected void initialize(){
        // Adding window size change listeners to resize map properly
        viewPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
            }
        });

        viewPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
            }
        });
    }

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

    // -------------------------------- Resize Components -------------------------------- //

    /**
     * Resizes the map relative to the size of the parentPane (MainWindow).
     * 
     * @param parentPane The parentPane that is being resized.
     */
    protected void resizeComponents(Region parentPane) {
        // Calculate the ratio of the current width/height relative to the original width/height
        double ratioX = parentPane.getWidth() / parentPane.getPrefWidth();
        double ratioY = parentPane.getHeight() / parentPane.getPrefHeight();

        // Calculate the scale factor (s.f.) the window was scaled by (limited to s.f. of 1, and max s.f. of 2)
        ratioX = Math.max(Math.min(ratioX, 2), 1);
        ratioY = Math.max(Math.min(ratioY, 2), 1);
        
        // Scale by same ratio allowing aspect ratio to be maintained
        double scale = Math.min(ratioX, ratioY);
        
        // Get the centre of the MainWindow and scale relative to the current MainWindow size
        Node toScale = viewPane.getCenter();
        
        toScale.setScaleY(scale);
        toScale.setScaleX(scale);
    }

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
