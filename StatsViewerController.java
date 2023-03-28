import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Defines multiple panes, that can be navigated between, for displaying different statistics,
 * and updates the text labels for the statistics panel based on the currently selected date range.
 *
 * @author Saihan Marshall
 * @version 2023.03.13
 */

public class StatsViewerController extends ViewerController {

    @FXML
    private BorderPane statsPane, viewPane;
    
    @FXML
    private VBox firstPane;

    @FXML
    private BorderPane secondPane, thirdPane, fourthPane;
        
    // Stores all panels and the current panel index
    private ArrayList<Pane> statsPanes;
    private int panelIndex;

    // Google mobility data statistics labels
    @FXML
    private Label gpGMRLabel, rrGMRLabel;

    // Government data statistics labels
    @FXML
    private Label sumTotalDeathLabel, averageCasesLabel, highestDeathDateLabel;

    // Stores the data within the date range selected
    private ArrayList<CovidData> dataInDateRange;
    
    // Allows for transition between statistics
    private FadeTransition fadeIn;
    private FadeTransition fadeOut;

    /**
     * Initialises list of panes to be shown.
     * Initialises fade transitions that later are used to switch panes.
     */
    @FXML
    protected void initialize() {
        super.initialize();
        
        // Initialises "statsPanes" ArrayList<Pane>
        statsPanes = new ArrayList<>(Arrays.asList(firstPane, secondPane, thirdPane, fourthPane));
        
        // Only show the first pane initially
        statsPanes.forEach(pane -> pane.setOpacity(0));
        statsPanes.get(0).setOpacity(1);
        
        // Start on first panel
        panelIndex = 0;
        
        // Sets up fade transitions
        fadeIn = createFadeTransition(1, 0, Duration.millis(150));
        fadeOut = createFadeTransition(0, 1, Duration.millis(150));

        // "fadeIn" plays as soon as "fadeOut is finished"
        fadeOut.setOnFinished(e -> fadeIn.play());
    }
    
    /**
     * Creates a FadeTransition to animate the opacity of each statistic from the start value 
     * to the end value over the specified duration.
     */
    private FadeTransition createFadeTransition(double toValue, double fromValue, Duration duration) {
        FadeTransition fade = new FadeTransition(duration);
        fade.setToValue(toValue);
        fade.setFromValue(fromValue);
        return fade;
    }

    /**
     * Processes the data within a given date range and updates the labels accordingly.
     * 
     * @param fromDate The start date of the date range (inclusive) to be processed.
     * @param toDate The end date of the date range (inclusive) to be processed.
     */
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        if (dataset.isDateRangeValid(fromDate, toDate)) {
            dataInDateRange = dataset.getDataInDateRange(fromDate, toDate);
        }

        refreshLabels();
    }

    /**
     * Hides the currently displayed pane and shows the next pane with a fade transition.
     * 
     * @param event The button clicked event that triggered this method
     */
    @FXML
    private void forwardButton(ActionEvent event) {
        // Increment to next pane
        int nextIndex = (panelIndex + 1) % statsPanes.size();

        // Set which pane to fade out from / in to
        fadeOut.setNode(statsPanes.get(panelIndex));
        fadeIn.setNode(statsPanes.get(nextIndex));
        
        fadeOut.play();
        
        panelIndex = nextIndex;
    }

    /**
     * Hides the currently displayed pane and shows the previous pane with a fade transition.
     * 
     * @param event The button clicked event that triggered this method
     */
    @FXML
    private void backwardButton(ActionEvent event) {
        // Decrement to previous pane
        int previousIndex = (panelIndex - 1 + statsPanes.size()) % statsPanes.size();
        
        // Set which pane to fade out from / in to
        fadeIn.setNode(statsPanes.get(previousIndex));
        fadeOut.setNode(statsPanes.get(panelIndex));
        
        fadeOut.play();
        
        panelIndex = previousIndex;
    }

    /**
     * Updates the text labels for the statistics panel based on the currently selected date range.
     */
    private void refreshLabels() {
        if(!dataset.isDateRangeValid(fromDate, toDate)){
            String invalidDateText = "The date field is not valid. (The 'to' date is before the 'from' date)";

            rrGMRLabel.setText(invalidDateText);
            gpGMRLabel.setText(invalidDateText);
            sumTotalDeathLabel.setText(invalidDateText);
            averageCasesLabel.setText(invalidDateText);
            highestDeathDateLabel.setText(invalidDateText);
        } else {
            rrGMRLabel.setText("The average retail and recreation GMR: " + getAverageRRGMR());
            gpGMRLabel.setText("The average grocery and pharmacy GMR: " + getAverageGPGMR());
            // + "" used to implicitly convert int to str
            sumTotalDeathLabel.setText(getTotalNumberOfDeaths() + "");
            averageCasesLabel.setText(getAverageTotalCases() + "");
            highestDeathDateLabel.setText(dataset.getMostRecentDataWithFilter(dataInDateRange, CovidData::getTotalDeaths).get(0).getDate() + "");
        }
    }

    /**
     * Calculates the total number of deaths for the most recent data within the date range.
     * 
     * @return sum of total deaths in all boroughs wuthin the date range
     */
    private int getTotalNumberOfDeaths() {
        int totalNumberOfDeaths = 0;

        // gets the most recent record of every borough that is non-null and non-zero in the total_deaths column
        ArrayList<CovidData> mostRecentDataWithTotalDeaths = dataset.getMostRecentDataWithFilter(dataInDateRange, CovidData::getTotalDeaths);

        for (CovidData record : mostRecentDataWithTotalDeaths) {
            totalNumberOfDeaths += record.getTotalDeaths();
        }

        return totalNumberOfDeaths;
    }

    /**
     * Returns the average total number of Covid cases for the most recent data within the date range.
     *
     * @return the average of total cases in all boroughs within the date range (to 2 d.p.)
     */
    private double getAverageTotalCases() {
        // gets the most recent record of every borough that is non-null and non-zero in the total_cases column
        ArrayList<CovidData> mostRecentDataWithTotalCases = dataset.getMostRecentDataWithFilter(dataInDateRange, CovidData::getTotalCases);

        List<Number> totalCasesData = mostRecentDataWithTotalCases.stream()
            .map(CovidData::getTotalCases)
            .collect(Collectors.toList());

        return dataset.getAverage(totalCasesData);
    }

    /**
     * Returns the average retail and recreation mobility for the Covid Data within the date range.
     * 
     * @return the average RRGMR for the Covid Data within the date range (to 2 d.p.)
     */
    private double getAverageRRGMR() {
        List<Number> retailRecreationData = dataInDateRange.stream()
            .map(CovidData::getRetailRecreationGMR)
            .collect(Collectors.toList());

        return dataset.getAverage(retailRecreationData);
    }

    /**
     * Returns the average grocery and pharmacy mobility for the Covid Data within the date range.
     * 
     * @return the average GPGMR for the Covid Data within the date range (to 2 d.p.)
     */
    private double getAverageGPGMR() {
        List<Number> groceryPharmacyData = dataInDateRange.stream()
            .map(CovidData::getGroceryPharmacyGMR)
            .collect(Collectors.toList());

        return dataset.getAverage(groceryPharmacyData);
    }

    /**
     * @return The view that this controller is associated with.
     */
    protected Parent getView() {
        return viewPane;
    }
}