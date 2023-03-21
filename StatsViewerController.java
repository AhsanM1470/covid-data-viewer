import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import java.net.URL;
import java.util.ResourceBundle;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.Objects;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javafx.fxml.Initializable;

import javax.swing.*;

/**
 * Controls logic of DataViewer.
 *
 * The ArrayList<Pane> contains each Pane that StatsView contains.
 * StatsView switches between these nested panes and checks
 * conditions within the panes by using the index i of the
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
    /**
     * This is the first pane that the user sees.
     * This pane is automatically set to visible when
     * "StatsViewer" is launched.
     *
     * StackPanes are used so that the panes can be
     * stacked on one another as they do not need to
     * be positioned relative to one another, and
     * only must be set as visible or invisible.
     */
    private VBox firstPane;

    @FXML
    /**
     * These are the second, third, and fourth panes that
     * the user sees.
     * These panes are automatically set to invisible when
     * "StatsViewer" is launched.
     */
    private BorderPane secondPane, thirdPane, fourthPane;

    /**
     * This is an ArrayList that contains all the nested panes
     * of "StatsViewer". Its index is used to switch between
     * and refer to certain panes.
     * i = 0, 1, 2, 3 for first, second, third, and fourth
     * panes respectively.
     */
    private ArrayList<Pane> statsPanes = new ArrayList<>();

    @FXML
    /**
     * These labels show average mobility statistics.
     * They are retail recreation GMR and grocery
     * pharmacy GMR.
     */
    private Label gpGMRLabel;

    @FXML
    private Label rrGMRLabel;

    @FXML
    /**
     * This label shows the sum of the total deaths in a
     * given date range.
     */
    private Label sumTotalDeathLabel;

    @FXML
    /**
     * This label shows the average of the total cases for
     * each data value in the date range.
     */
    private Label averageCasesLabel;

    @FXML
    /**
     * This label shows the date of the case which has the
     * highest death in the date range.
     */
    private Label highestDeathDateLabel;

    // index for statsPanes
    private int i = 0;

    /**
     *
     */
    private LocalDate fromDate;
    private LocalDate toDate;

    /**
     * Initialises "dataRangeData" which is used to
     * contain data inside a give data range.
     */
    private ArrayList<CovidData> dataRangeData = new ArrayList<>();

    /**
     * This creates an arraylist of all the panes,
     * which allows the panes to be systematically
     * selected.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statsPanes.add(firstPane);
        statsPanes.add(secondPane);
        statsPanes.add(thirdPane);
        statsPanes.add(fourthPane);
        firstPane.setVisible(true);
    }

    /**
     *
     */
    private void loadDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        dataRangeData = getDataInDateRange(fromDate, toDate);
        for (CovidData c : dataRangeData) {
            ;
        }
    }

    /**
     *
     * @param fromDate
     * @param toDate
     */
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;

        if (isDateRangeValid(fromDate, toDate)) {
            getDataInDateRange(fromDate, toDate);
        }

        // if the index is 1, then refreshes the label
        // showing sum of total deaths.
        refreshSumTotalDeathLabel();

        //
        //
        refreshAverage();

        //
        //
        refreshHighestDeathLabel();

        System.out.println(fromDate + "   " + toDate);
        //
        refreshMobilityMeasureLabel();
        System.out.println("982378416982628796");
    }

    /**
     * @return the main centre panel
     */
    protected Parent getView() {
        return statsPane;
    }

    @FXML
    /**
     * This allows the user to view the next pane.
     * It sets the current pane as invisible and the
     * next pane as visible.
     */
    private void forwardButton(ActionEvent event) {
        // sets the current pane as invisible
        statsPanes.get(i).setVisible(false);

        // if on last pane, sets the first pane as visible to loop around
        if (i == statsPanes.size() - 1) {
            statsPanes.get(0).setVisible(true);
        }

        // if on any pane not last, sets next pane as visible
        else {
            statsPanes.get(i + 1).setVisible(true);
        }

        // increments index for the arraylist of stats panes
        if (i == 3) {
            i = 0;
        }

        else {
            i++;
        }

        refreshSumTotalDeathLabel();
        refreshAverage();
        refreshHighestDeathLabel();
        refreshMobilityMeasureLabel();

    }

    @FXML
    /**
     * This allows the user to view the previous pane.
     * It sets the current pane as invisible and the
     * previous pane as visible
     */
    private void backwardButton(ActionEvent event) {
        // sets the current pane as invisible
        statsPanes.get(i).setVisible(false);

        // if on first pane, sets the last pane as visible to loop around
        if (i == 0) {
            statsPanes.get(statsPanes.size() - 1).setVisible(true);
        }

        // if on any pane not last, sets next pane as visible
        else {
            statsPanes.get(i - 1).setVisible(true);
        }

        // decrements index for the arraylist of stats panes
        if (i == 0) {
            i = 3;
        }

        else {
            i--;
        }

        refreshSumTotalDeathLabel();
        refreshAverage();
        refreshHighestDeathLabel();
        refreshMobilityMeasureLabel();
    }

    /**
     * This updates the label in the second pane to show
     * the current sum of total deaths for the given date
     * range.
     * This is called whenever index "i" changes to show the
     * second pane, or if "i" is already 1 and the date changed.
     */
    private void refreshSumTotalDeathLabel() {
        if (i == 1) {
            if (fromDate != null && toDate != null) {
                // this updates the value of "dataRangeData" so that
                // it takes into account the most recent "fromDate"
                // and "toDate"
                dataRangeData = getDataInDateRange(fromDate, toDate);

                if (fromDate.isBefore(toDate)) {
                    sumTotalDeathLabel.setText("" + totalNumberOfTotalDeathsCount());
                }

                else {
                    sumTotalDeathLabel.setText("The date field is not valid.");
                }
            }

        }

    }

    /**
     * This returns the sum of the total deaths.
     */
    public int totalNumberOfTotalDeathsCount() {
        int totalNumberOfTotalDeaths = 0;

        // this iterates through the data within the date range and
        // sums the total deaths
        for (CovidData c : dataRangeData) {
            // some records actually have null for the total
            // deaths so a check is necessary
            if (!Objects.isNull(c.getTotalDeaths())) {
                totalNumberOfTotalDeaths += c.getTotalDeaths();
                // System.out.println(c);
            }

        }
        // System.out.println(totalNumberOfTotalDeaths);

        return totalNumberOfTotalDeaths;
    }

    /**
     * This updates the label in the third pane to show
     * the average of total cases in the given date range.
     * This is called whenever index "i" changes to show the
     * third pane, or if "i" is already 2 and the date changed.
     */
    private void refreshAverage() {
        if (i == 2) {
            if (fromDate != null && toDate != null) {
                // this updates the value of "dataRangeData" so that
                // it takes into account the most recent "fromDate"
                // and "toDate"
                dataRangeData = getDataInDateRange(fromDate, toDate);

                if (fromDate.isBefore(toDate)) {
                    averageCasesLabel.setText("" + averageOfTotalCases());
                }

                else {
                    averageCasesLabel.setText("The date field is not valid.");
                }
            }

        }
    }

    /**
     * This returns a decimal average of the total cases
     * of all records in the specified data range.
     * 
     * @return average of total cases
     */
    private float averageOfTotalCases() {
        float totalCases = 0;
        float average = 0;

        // iterates and adds each case to number of cases
        for (CovidData c : dataRangeData) {

            // some records actually have null for the total
            // cases so a check is necessary
            if (!Objects.isNull(c.getTotalCases())) {
                totalCases += c.getTotalCases();
            }

        }

        // calculates average if there is at least onex
        // data value
        if (dataRangeData.size() > 0) {
            // System.out.println(totalCases + " " + dataRangeData.size());
            average = totalCases / dataRangeData.size();
        }

        else {
            average = 0;
        }

        return average;

    }

    /**
     * This updates the label in the fourth pane to show
     * the date with the highest number of deaths in the
     * given date range.
     * This is called whenever index "i" changes to show the
     * fourth pane, or if "i" is already 3 and the date changed.
     */
    private void refreshHighestDeathLabel() {

        System.out.println("poiuy");
        if (i == 3) {
            if (fromDate != null && toDate != null) {
                // this updates the value of "dataRangeData" so that
                // it takes into account the most recent "fromDate"
                // and "toDate"
                dataRangeData = getDataInDateRange(fromDate, toDate);

                if (fromDate.isBefore(toDate)) {
                    highestDeathDateLabel.setText("" + highestTotalDeathDate());
                }

                else {
                    highestDeathDateLabel.setText("The date field is not valid.");
                }
            }

        }
    }

    /**
     * This returns the date with the highest total death
     * as a String.
     *
     * @return date with highest total death.
     */
    private String highestTotalDeathDate() {
        int totalDeath = 0;
        String highestDeathDate = "";
        System.out.println("xxxxxxxxxxxxx");

        // iterates through all records in date range
        for (CovidData c : dataRangeData) {

            if (!Objects.isNull(c)) {
                return "No records ";
            }

            // some records actually have null for the total
            // deaths so a check is necessary
            if (!Objects.isNull(c.getTotalCases())) {

                if (c.getTotalDeaths() > totalDeath) {
                    totalDeath = c.getTotalDeaths();
                    highestDeathDate = c.getDate();
                    // System.out.println(c);
                }

            }

        }

        return highestDeathDate;

    }

    /**
     *
     */
    private void refreshMobilityMeasureLabel() {
        // System.out.println(i);

        if (i == 0) {
            System.out.println(fromDate + "   " + toDate);
            if (fromDate != null && toDate != null) {
                // this updates the value of "dataRangeData" so that
                // it takes into account the most recent "fromDate"
                // and "toDate"
                dataRangeData = getDataInDateRange(fromDate, toDate);

                if (fromDate.isBefore(toDate)) {
                    rrGMRLabel.setText("The average retail recreational GMR: " + getAverageRRGMR());
                    gpGMRLabel.setText("The average grocery pharmacy GMR: " + getAverageGPGMR());
                }

                else {
                    rrGMRLabel.setText("The date field is not valid.");
                    gpGMRLabel.setText("The date field is not valid.");
                }
            }

        }
    }

    /**
     * This computes the average for retail recreation
     * GMR over a given data range.
     * 
     * @return average of retail recreation GMR.
     */
    private float getAverageRRGMR() {
        float sum = 0;
        float average = 0;

        // iterates through all records in date range
        for (CovidData c : dataRangeData) {

            // many records have null for mobility measures
            // so a check is necessary
            if (!Objects.isNull(c.getRetailRecreationGMR())) {
                sum += c.getRetailRecreationGMR();
            }

        }

        // calculates average if there is at least onex
        // data value
        if (dataRangeData.size() > 0) {
            // System.out.println(sum + " " + dataRangeData.size());
            average = sum / dataRangeData.size();
        }

        return average;
    }

    /**
     * This computes the average for grocery pharmacy
     * GMR over a given data range.
     * 
     * @return average of grocery pharmacy GMR.
     */
    private float getAverageGPGMR() {
        float sum = 0;
        float average = 0;

        // iterates through all records in date range
        for (CovidData c : dataRangeData) {

            // many records have null for mobility measures
            // so a check is necessary
            if (!Objects.isNull(c.getGroceryPharmacyGMR())) {
                sum += c.getGroceryPharmacyGMR();
            }

        }

        // calculates average if there is at least onex
        // data value
        if (dataRangeData.size() > 0) {
            // System.out.println(sum + " " + dataRangeData.size());
            average = sum / dataRangeData.size();
        }

        return average;
    }

}