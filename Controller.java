import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;

/**
 * Serves as a template of common functionality for the Controllers of each
 * view. Predominantly ensures that date pickers contain the same value across
 * each controller, and accessing the dataset.
 *
 * @author Ishab Ahmed
 * @version 2023.03.16
 */
public abstract class Controller {

    // stores all the covid data
    protected ArrayList<CovidData> data;

    // FXML attributes which all controllers will have
    @FXML
    protected DatePicker toDatePicker;

    @FXML
    protected DatePicker fromDatePicker;

    @FXML
    protected BorderPane mainLayout;

    @FXML
    protected Button leftButton;

    @FXML
    protected Button rightButton;

    // shared across all controllers to store current scene information
    public static int sceneIndex = 0;
    public static String[] scenes = new String[] { "MainWindow.fxml", "MapWindow.fxml" };

    /**
     * Creates a new Controller object and initialises it with a list of
     * CovidData objects loaded from the data source.
     */
    public Controller() {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();
    }

    // ------------------------ User Interaction Methods ----------------------- //

    /**
     * Changes the center of the main layout to the next panel.
     * 
     * @param event The event triggered by clicking the next panel button
     * @throws IOException
     */
    @FXML
    private void nextPanel(ActionEvent event) throws IOException {
        sceneIndex = (sceneIndex + 1) % scenes.length;
        switchPanel(event);
    }

    /**
     * Changes the center of the main layout to the previous panel.
     * 
     * @param event The event triggered by clicking the next panel button
     * @throws IOException
     */
    @FXML
    private void previousPanel(ActionEvent event) throws IOException {
        sceneIndex--;
        if (sceneIndex < 0) {
            sceneIndex = scenes.length - 1;
        }
        // controllerIndex = (controllerIndex - 1 + scenes.length) % scenes.length;
        switchPanel(event);
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

        allowPanelSwitching(isDateRangeValid(fromDate, toDate));
        processDateRangeData(fromDate, toDate);
    }

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
     * @param from The start date of the date range.
     * @param to   The end date of the date range.
     * @return An ArrayList of CovidData that fall within the specified date range.
     */
    protected ArrayList<CovidData> getDateRangeData(LocalDate from, LocalDate to) {
        // filters data to only include data that falls within the specified date range
        return new ArrayList<CovidData>(data.stream()
                .filter((covidData) -> isDateInRange(LocalDate.parse(covidData.getDate()), from, to))
                .collect(Collectors.toList()));
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
    protected boolean isDateRangeValid(LocalDate from, LocalDate to) {
        // if any of them are null, date range is automatically invalid
        if (from == null || to == null) {
            return false;
        }
        return from.isBefore(to) || from.isEqual(to) || to.isEqual(from);
    }

    /**
     * 
     * @param date date to be checked
     * @param from starting date (inclusive)
     * @param to ending date (inclusive)
     * @return whether the date is within an inclusive date range
     */
    protected boolean isDateInRange(LocalDate date, LocalDate from, LocalDate to) {
        return (date.isAfter(from) && date.isBefore(to)) || date.isEqual(from) || date.isEqual(to);
    }

    // ---------------------------- Panel Switching ---------------------------- //

    /**
     * @param state Determines whether the user can switch to a different panel from
     *              the current panel
     */
    protected void allowPanelSwitching(boolean state) {
        leftButton.setDisable(!state);
        rightButton.setDisable(!state);
    }

    /**
     * Sets the date range of the date pickers to the given dates.
     * Calls the processDateRangeData() method which performs actions related to the
     * date picked on the current scene
     *
     * @param from The starting date of the date range to be set.
     * @param to   The ending date of the date range to be set.
     */
    public void setDateRange(LocalDate from, LocalDate to) {
        fromDatePicker.setValue(from);
        toDatePicker.setValue(to);

        // do what is specified to be done with date range in current scene
        processDateRangeData(from, to);
        allowPanelSwitching(isDateRangeValid(from, to));
    }

    /**
     * Used when next or previous buttons are pressed. Uses the sceneIndex to select
     * next scene to be displayed from the array of scenes
     * 
     * @param event
     * @throws IOException
     */
    protected void switchPanel(ActionEvent event) throws IOException {
        FXMLLoader sceneLoader = new FXMLLoader(
                getClass().getResource(scenes[sceneIndex]));

        Parent root = sceneLoader.load();
        Controller sceneController = sceneLoader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        sceneController.setDateRange(getFromDate(), getToDate());

        Scene scene = new Scene(root);

        stage.setWidth(mainLayout.getWidth());

        // causes height to shrink by 28 for some reason so adding 28 cancels out
        stage.setHeight(mainLayout.getHeight() + 28);
        stage.setScene(scene);

    }

    // ---------------------------- Abstract Methods --------------------------- //

    /**
     * Called when either date picker is changed.
     * 
     * @param from the starting date of the range
     * @param to   the ending date of the range
     */
    abstract protected void processDateRangeData(LocalDate from, LocalDate to);

}
