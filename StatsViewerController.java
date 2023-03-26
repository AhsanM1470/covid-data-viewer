import javafx.fxml.FXML;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;

import java.util.List;
import java.util.ArrayList;

import java.time.LocalDate;

import java.util.stream.Collectors;

/**
 * Defines multiple panes, that can be navigated between, for displaying different statistics,
 * and updates the text labels for the statistics panel based on the currently selected date range.
 *
 * @author Saihan Marshall
 * @version 2023.03.13
 */

public class StatsViewerController extends ViewerController {

    @FXML
    private BorderPane statsPane;
    
    // first pane - setVisible(true) when injected
    @FXML
    private VBox firstPane;

    // setVisible(false) when injected
    @FXML
    private BorderPane secondPane, thirdPane, fourthPane;

    // Array of all panes
    private ArrayList<Pane> statsPanes;

    // Index of current panel
    private int panelIndex;

    // Google mobility data labels
    @FXML
    private Label gpGMRLabel, rrGMRLabel;

    // Total deaths in given range label
    @FXML
    private Label sumTotalDeathLabel;

    // Average of total cases label
    @FXML
    private Label averageCasesLabel;

    // Date of highest deaths label
    @FXML
    private Label highestDeathDateLabel;

    // Stores the data within the date range selected
    private ArrayList<CovidData> dataInDateRange;

    /**
     * Initialises list of panes to be shown.
     */
    @FXML
    protected void initialize() {
        statsPanes = new ArrayList<>();

        statsPanes.add(firstPane);
        statsPanes.add(secondPane);
        statsPanes.add(thirdPane);
        statsPanes.add(fourthPane);
        firstPane.setVisible(true);

        // Start on first panel
        panelIndex = 0;
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
     * Hides the currently displayed pane and shows the next pane.
     * 
     * @param event The button clicked event that triggered this method
     */
    @FXML
    private void forwardButton(ActionEvent event) {
        // Stop showing current pane
        statsPanes.get(panelIndex).setVisible(false);

        // Increment to next pane
        panelIndex = (panelIndex + 1) % statsPanes.size();

        // Show next pane
        statsPanes.get(panelIndex).setVisible(true);
    }

    /**
     * Hides the currently displayed pane and shows the previous pane.
     * 
     * @param event The button clicked event that triggered this method
     */
    @FXML
    private void backwardButton(ActionEvent event) {
        // Stop showing current pane
        statsPanes.get(panelIndex).setVisible(false);

        // Decrement to previous pane
        panelIndex--;
        // Roll around to last pane in array if index becomes -ve
        if (panelIndex < 0) {
            panelIndex = statsPanes.size() - 1;
        }

        // Show previous pane
        statsPanes.get(panelIndex).setVisible(true);
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
    public int getTotalNumberOfDeaths() {
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
        return statsPane;
    }
}