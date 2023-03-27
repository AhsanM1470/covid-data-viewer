import javafx.animation.FadeTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import javafx.util.Duration;

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

    private FadeTransition fadeIn;
    private FadeTransition fadeOut;




    @FXML
    private BorderPane statsPane, viewPane;
    
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

    // Date of the highest deaths label
    @FXML
    private Label highestDeathDateLabel;

    // Stores the data within the date range selected
    private ArrayList<CovidData> dataInDateRange;

    /**
     * Initialises list of panes to be shown.
     * Initialises fade transitions that later are used to switch panes.
     */
    @FXML
    protected void initialize() {
        super.initialize();
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
        statsPanes = new ArrayList<>();

        // First pane is the only visible pane
        firstPane.setOpacity(1);
        secondPane.setOpacity(0.0);
        thirdPane.setOpacity(0.0);
        fourthPane.setOpacity(0.0);

        // Initialises "statsPanes" ArrayList<Pane>
        statsPanes.add(firstPane);
        statsPanes.add(secondPane);
        statsPanes.add(thirdPane);
        statsPanes.add(fourthPane);

        // Start on first panel
        panelIndex = 0;

        // Sets up fade transitions
        fadeIn = new FadeTransition();
        fadeIn.setDuration(Duration.millis(150));
        fadeIn.setToValue(1);
        fadeIn.setFromValue(0);

        fadeOut = new FadeTransition();
        fadeOut.setDuration(Duration.millis(150));
        fadeOut.setToValue(0);
        fadeOut.setFromValue(1);

        // "fadeIn" plays as soon as "fadeOut is finished"
        fadeOut.setOnFinished(e -> fadeIn.play());

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
        // Sets up nodes for fade transitions
        fadeOut.setNode(statsPanes.get((panelIndex) % statsPanes.size()));
        fadeIn.setNode(statsPanes.get((panelIndex + 1) % statsPanes.size()));

        // Increment to next pane
        panelIndex = (panelIndex + 1) % statsPanes.size();

        // Show next panel
        fadeOut.play();

    }

    /**
     * Hides the currently displayed pane and shows the previous pane.
     * 
     * @param event The button clicked event that triggered this method
     */
    @FXML
    private void backwardButton(ActionEvent event) {
        // Decrement to previous pane
        panelIndex--;

        // Roll around to last pane in array if index becomes -ve
        if (panelIndex < 0) {
            panelIndex = statsPanes.size() - 1;
        }

        // Nodes are set up after to prevent negative indexes
        fadeIn.setNode(statsPanes.get((panelIndex) % statsPanes.size()));
        fadeOut.setNode(statsPanes.get((panelIndex + 1) % statsPanes.size()));

        // Show previous panel
        fadeOut.play();
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