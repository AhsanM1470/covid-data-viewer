import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ResourceBundle;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javafx.fxml.Initializable;

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

// TODO
// does "average cases" refer to an average over all records in date range OR an average over all boroughs
// fix average cases


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
     * This contains the records that have the latest date in dataRangeData.
     * This is used for total cases and total deaths so different methods don't
     *  have to iterate through all the records in dataRangeData.
     */
    private ArrayList<CovidData> finalDateRecords = new ArrayList<>();

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

//    /**
//     *
//     */
//    private void loadDataInDateRange(LocalDate fromDate, LocalDate toDate) {
//        dataRangeData = getDataInDateRange(fromDate, toDate);
//        for (CovidData c : dataRangeData) {
//            ;
//        }
//    }

    /**
     *
     * @param fromDate
     * @param toDate
     */
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;

        // // TODO: Go over with saihan over how the averages are caluclated using this: sum of new cases/number of days
        // long daysBetween = ChronoUnit.DAYS.between(fromDate, toDate);
        // System.out.println(daysBetween);

        if (isDateRangeValid(fromDate, toDate)) {
            getDataInDateRange(fromDate, toDate);
        }

        // if the index is 1, then refreshes the label
        // showing sum of total deaths.
        refreshLabels();

//        //
//        //
//        refreshAverage();
//
//        //
//        //
//        refreshHighestDeathLabel();
//
//        System.out.println(fromDate + "   " + toDate);
//        //
//        refreshMobilityMeasureLabel();
//        System.out.println("982378416982628796");
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

        refreshLabels();
//        refreshAverage();
//        refreshHighestDeathLabel();
//        refreshMobilityMeasureLabel();

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

        refreshLabels();
//        refreshAverage();
//        refreshHighestDeathLabel();
//        refreshMobilityMeasureLabel();
    }

    /**
     * This updates the label in the second pane to show
     * the current sum of total deaths for the given date
     * range.
     * This is called whenever index "i" changes to show the
     * second pane, or if "i" is already 1 and the date changed.
     */
    private void refreshLabels() {
        if (fromDate != null && toDate != null) {
            // this updates the value of "dataRangeData" so that
            // it takes into account the most recent "fromDate"
            // and "toDate"
            dataRangeData = getDataInDateRange(fromDate, toDate);

            // this generates a new ArrayList for the records for each borough on the last
            // date in the date range
            // could also compare the boroughs on final date to make sure they're unique
            String recordsOnFinalDate = dataRangeData.get(dataRangeData.size() - 1).getDate();
            // this assumes you can't have multiple records on the same day
            // finalDateRecords = new ArrayList<>(dataRangeData.stream()
                    // .filter(i -> {
                        // return i.getDate().equals(recordsOnFinalDate) && i.;
                    // })
                    // .collect(Collectors.toList()) );


            if(!fromDate.isBefore(toDate)){
                rrGMRLabel.setText("The date field is not valid.");
                gpGMRLabel.setText("The date field is not valid.");
                sumTotalDeathLabel.setText("The date field is not valid.");
                averageCasesLabel.setText("The date field is not valid.");
                highestDeathDateLabel.setText("The date field is not valid.");
            }

            else if(i == 0){
                rrGMRLabel.setText("The average retail recreational GMR: " + getAverageRRGMR());
                gpGMRLabel.setText("The average grocery pharmacy GMR: " + getAverageGPGMR());
            }
            else if(i == 1){
                sumTotalDeathLabel.setText("" + totalNumberOfDeaths());
            }
            else if(i == 2){
                averageCasesLabel.setText("" + averageOfTotalCases());
            }
            else if(i == 3){
                highestDeathDateLabel.setText("" + highestTotalDeathDate());
            }




//            if(i == 0){
//                if (fromDate.isBefore(toDate)) {
//                    rrGMRLabel.setText("The average retail recreational GMR: " + getAverageRRGMR());
//                    gpGMRLabel.setText("The average grocery pharmacy GMR: " + getAverageGPGMR());
//                }
//
//                else {
//                    rrGMRLabel.setText("The date field is not valid.");
//                    gpGMRLabel.setText("The date field is not valid.");
//                }
//            }
//
//            else if (i == 1) {
//                if(fromDate.isBefore(toDate)) {
//                    sumTotalDeathLabel.setText("" + totalNumberOfTotalDeathsCount());
//                }
//
//                else {
//                    sumTotalDeathLabel.setText("The date field is not valid.");
//                }
//            }
//
//            else if(i == 2){
//                if (fromDate.isBefore(toDate)) {
//                    averageCasesLabel.setText("" + averageOfTotalCases());
//                }
//
//                else {
//                    averageCasesLabel.setText("The date field is not valid.");
//                }
//            }
//
//            else if(i == 3){
//                if (fromDate.isBefore(toDate)) {
//                    highestDeathDateLabel.setText("" + highestTotalDeathDate());
//                }
//
//                else {
//                    highestDeathDateLabel.setText("The date field is not valid.");
//                }
//            }

        }

    }

    /**
     * This returns the sum of the total deaths of all boroughs in the given date
     *  range.
     * The sum of the total deaths is the value of "total deaths" for each borough
     *  on the last date in the date range.
     *
     *  Every borough only appears in data only when every other borough is shown
     *   for that date.
     * @return sum of total deaths in all boroughs in date range.
     */
    public int totalNumberOfDeaths() {
        int totalNumberOfDeaths = 0;

//        int numberOfBoroughsLeft = 32;
//        for(int i = dataRangeData.size() - 1; numberOfBoroughsLeft > 0; i--){
//            System.out.println(dataRangeData.get(i).getBorough());
//            numberOfBoroughsLeft--;
//        }


        // this iterates through the records within the final date and
        // sums the total deaths
        for (CovidData c : finalDateRecords) {
            // some records actually have null for the total
            // deaths so a check is necessary
            if (!Objects.isNull(c.getTotalDeaths())) {
                totalNumberOfDeaths += c.getTotalDeaths();
                // System.out.println(c);
            }

        }
        // System.out.println(totalNumberOfDeaths);

        return totalNumberOfDeaths;
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
     * @return average of total cases over total records
     */
    private float averageOfTotalCases() {
        float totalCases = 0;
        // this must be declared here
        float average;

        // iterates and adds each case to number of cases
        for (CovidData c : finalDateRecords) {
                System.out.println(c.toString());

            // some records actually have null for the total
            // cases so a check is necessary
            if (!Objects.isNull(c.getTotalCases())) {
                totalCases += c.getTotalCases();
            }

        }

        // calculates average if there is at least one
        // data value
        if (finalDateRecords.size() > 0) {
            // System.out.println(totalCases + " " + dataRangeData.size());
            average = totalCases / finalDateRecords.size();
//            average = totalCases / 33;
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
        int newDeaths = 0;
        String highestDeathDate = "";

        // iterates through all records in date range
        for (CovidData c : dataRangeData) {
            // some records actually have null for the total
            // deaths so a check is necessary
            if (!Objects.isNull(c.getNewDeaths())) {

                if (c.getNewDeaths() > newDeaths) {
                    newDeaths = c.getNewDeaths();
                    highestDeathDate = c.getDate();
                    // System.out.println(c);
                }

            }

        }

        // in the case that there is no highest death date
        if(highestDeathDate.equals("")){
            return "Not applicable.";
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