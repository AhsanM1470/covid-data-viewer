import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import java.util.List;

import java.lang.Math;
import java.net.URL;
import java.util.ResourceBundle;

import java.time.LocalDate;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.Objects;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.stream.Collectors;

import javafx.fxml.Initializable;

/**
 * Controls logic of DataViewer.
 *
 * The ArrayList<Pane> contains each Pane that StatsView contains.
 * StatsView switches between these nested panes and checks
 * conditions within the panes by using the panelIndex of the
 * ArrayList. One condition could lead to the average of the
 * total cases being refreshed and shown.
 *
 * @author Saihan Marshall
 * @version 2023.03.13
 */

public class StatsViewerController extends ViewerController implements Initializable {

    @FXML
    private BorderPane statsPane;

    @FXML
    // first pane - setVisible(true) when injected
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    @FXML
    /**
     * This allows the user to view the next pane.
     * It sets the current pane as invisible and the
     * next pane as visible.
     */
    private void forwardButton(ActionEvent event) {
        // sets the current pane as invisible
        statsPanes.get(panelIndex).setVisible(false);

        // if on last pane, sets the first pane as visible to loop around
        if (panelIndex == statsPanes.size() - 1) {
            statsPanes.get(0).setVisible(true);
        }

        // if on any pane not last, sets next pane as visible
        else {
            statsPanes.get(panelIndex + 1).setVisible(true);
        }

        // increments index for the arraylist of stats panes
        if (panelIndex == 3) {
            panelIndex = 0;
        }

        else {
            panelIndex++;
        }

        refreshLabels();

    }

    @FXML
    /**
     * This allows the user to view the previous pane.
     * It sets the current pane as invisible and the
     * previous pane as visible
     */
    private void backwardButton(ActionEvent event) {
        // sets the current pane as invisible
        statsPanes.get(panelIndex).setVisible(false);

        // if on first pane, sets the last pane as visible to loop around
        if (panelIndex == 0) {
            statsPanes.get(statsPanes.size() - 1).setVisible(true);
        }

        // if on any pane not last, sets next pane as visible
        else {
            statsPanes.get(panelIndex - 1).setVisible(true);
        }

        // decrements index for the arraylist of stats panes
        if (panelIndex == 0) {
            panelIndex = 3;
        }

        else {
            panelIndex--;
        }

        refreshLabels();
    }

    /**
     * This updates the label in the second pane to show
     * the current sum of total deaths for the given date
     * range.
     * This is called whenever index "i" changes to show the
     * second pane, or if "i" is already 1 and the date changed.
     *
     * This updates all labels when they are currently being shown.
     *  This is the case when the buttons have been clicked such
     *  that "i" is equal to the index of the buttons.
     */
    private void refreshLabels() {
        if (fromDate != null && toDate != null) {
            if(!dataset.isDateRangeValid(fromDate, toDate)){
                rrGMRLabel.setText("The date field is not valid.");
                gpGMRLabel.setText("The date field is not valid.");
                sumTotalDeathLabel.setText("The date field is not valid.");
                averageCasesLabel.setText("The date field is not valid.");
                highestDeathDateLabel.setText("The date field is not valid.");
            }

            else if(panelIndex == 0){
                rrGMRLabel.setText("The average retail recreational GMR: " + getAverageRRGMR());
                gpGMRLabel.setText("The average grocery pharmacy GMR: " + getAverageGPGMR());
            }
            else if(panelIndex == 1){
                sumTotalDeathLabel.setText("" + totalNumberOfDeaths());
            }
            else if(panelIndex == 2){
                averageCasesLabel.setText("" + averageOfTotalCases());
            }
            else if(panelIndex == 3){
                highestDeathDateLabel.setText("" + dataset.getMostRecentDataWithTotalDeaths(dataInDateRange).get(0).getDate());
            }

        }

    }

    /**
     * Calculates the total number of deaths for the most recent data within the date range.
     * 
     * @return sum of total deaths in all boroughs wuthin the date range
     */
    public int totalNumberOfDeaths() {
        int totalNumberOfDeaths = 0;

        ArrayList<CovidData> mostRecentDataWithTotalDeaths = dataset.getMostRecentDataWithTotalDeaths(dataInDateRange);
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
    private double averageOfTotalCases() {
        ArrayList<CovidData> mostRecentDataWithTotalCases = dataset.getMostRecentDataWithTotalCases(dataInDateRange);
        List<Number> totalCasesList = mostRecentDataWithTotalCases.stream()
            .map(CovidData::getTotalCases)
            .collect(Collectors.toList());
            
        return dataset.getAverage(totalCasesList);
    }

    /**
     * Returns the average retail and recreation mobility relative to baseline
     * for the Covid Data within the date range.
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
     * Returns the average grocery and pharmacy mobility relative to baseline
     * for the Covid Data within the date range.
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
     * @return the main centre panel
     */
    protected Parent getView() {
        return statsPane;
    }
}