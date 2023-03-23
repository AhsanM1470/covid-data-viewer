import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

import java.net.URL;
import java.util.ResourceBundle;

import java.time.LocalDate;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.Objects;

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
// waiting for jeffrey to respond, in meantime I will make a way to sum up deaths and cases
    // within the date range and not use totals.

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


    private String finalDateString;

    /**
     * Initialises "dataRangeData" which is used to
     * contain data inside a give data range.
     */
    private ArrayList<CovidData> dateRangeData = new ArrayList<>();

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

        refreshLabels();
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
            dateRangeData = getDataInDateRange(fromDate, toDate);

            // this generates a new ArrayList for the records for each borough on the last
            // date in the date range
            finalDateString = getFinalDateString(dateRangeData);


            // finalDateRecords will not have nulls for total deaths cases in most cases
            // finalDateRecords must be cleared every time this is called
            finalDateRecords.clear();
            for (CovidData d : dateRangeData) {
                if (d.getDate().equals(finalDateString)) {
                    finalDateRecords.add(d);
                }
            }

            if(!isDateRangeValid(fromDate, toDate)){
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
                highestDeathDateLabel.setText("" + getFinalRecordWithCondition(dateRangeData, "deaths").getDate());
            }

        }

    }

    /**
     * This returns the date of the final record which has non-null values
     *  for total deaths and total cases.
     * This takes in a reversed dataRangeData to check for the last dates first.
     * @param cd is dataRangeData. Not used directly because dataRangeData may change.
     * @return date as String of final record that has values for total deaths and total cases.
     */
    private String getFinalDateString(ArrayList<CovidData> cd){
        // datarangedata is newest at i == 0
        for(int i = 0; i < cd.size(); i++){
            if(cd.get(i).getTotalDeaths() != null && cd.get(i).getTotalCases() != null){
                return cd.get(i).getDate();
            }

        }

        System.out.println("error has occurred with finding final date string");
        return "";
    }

    /**
     * cd is a REVERSED arraylist
     * @param cd
     * @param condition
     * @return
     */
    private CovidData getFinalRecordWithCondition(ArrayList<CovidData> cd, String condition){
        if(condition.equals("deaths")){
            for(int i = 0; i < cd.size(); i++){
                if(cd.get(i).getTotalDeaths() != null && cd.get(i).getTotalDeaths() != 0){
                    return cd.get(i);
                }
            }
        }

        else if(condition.equals("cases")){
            for(int i = cd.size() - 1; i > 0; i--){
                if(cd.get(i).getTotalCases() != null && cd.get(i).getTotalCases() != 0){
                    return cd.get(i);
                }
            }
        }

        return null;
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

        System.out.println(finalDateRecords.size());


        // this iterates through the records within the final date and
        // sums the total deaths
        for (CovidData c : finalDateRecords) {
            // some records actually have null for the total
            // deaths so a check is necessary
            if(Objects.isNull(c.getTotalDeaths())){

            }



            if (!Objects.isNull(c.getTotalDeaths())) {
                totalNumberOfDeaths += c.getTotalDeaths();
            }

        }

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
                dateRangeData = getDataInDateRange(fromDate, toDate);

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
        float sum = 0;

        // iterates and adds each case to number of cases
        for (CovidData c : finalDateRecords) {
            // some records actually have null for the total
            // cases so a check is necessary
            if (!Objects.isNull(c.getTotalCases())) {
                totalCases += c.getTotalCases();
            }

        }

        System.out.println(totalCases);

        // calculates average if there is at least one
        // data value
        if (finalDateRecords.size() > 0) {
            // System.out.println(totalCases + " " + dataRangeData.size());
            average = totalCases / finalDateRecords.size();
        }

        else {
            average = 0;
        }



        for (CovidData c : dateRangeData){
            if (!Objects.isNull(c.getTotalCases())) {
                sum += c.getNewCases();
            }
        }
        if(dateRangeData.size() > 0){
//            average = sum / dataRangeData.size();
            average = sum / 32;
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
//    private void refreshHighestDeathLabel() {
//
//        System.out.println("poiuy");
//        if (i == 3) {
//            if (fromDate != null && toDate != null) {
//                // this updates the value of "dataRangeData" so that
//                // it takes into account the most recent "fromDate"
//                // and "toDate"
//                dataRangeData = getDataInDateRange(fromDate, toDate);
//
//                if (fromDate.isBefore(toDate)) {
//                    highestDeathDateLabel.setText("" + highestTotalDeathDate());
//                }
//
//                else {
//                    highestDeathDateLabel.setText("The date field is not valid.");
//                }
//            }
//
//        }
//    }


//    /**
//     *
//     */
//    private void refreshMobilityMeasureLabel() {
//        // System.out.println(i);
//
//        if (i == 0) {
//            System.out.println(fromDate + "   " + toDate);
//            if (fromDate != null && toDate != null) {
//                // this updates the value of "dataRangeData" so that
//                // it takes into account the most recent "fromDate"
//                // and "toDate"
//                dataRangeData = getDataInDateRange(fromDate, toDate);
//
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
//        }


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
        for (CovidData c : dateRangeData) {

            // many records have null for mobility measures
            // so a check is necessary
            if (!Objects.isNull(c.getRetailRecreationGMR())) {
                sum += c.getRetailRecreationGMR();
            }

        }

        // calculates average if there is at least onex
        // data value
        if (dateRangeData.size() > 0) {
            // System.out.println(sum + " " + dataRangeData.size());
            average = sum / dateRangeData.size();
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
        for (CovidData c : dateRangeData) {

            // many records have null for mobility measures
            // so a check is necessary
            if (!Objects.isNull(c.getGroceryPharmacyGMR())) {
                sum += c.getGroceryPharmacyGMR();
            }

        }

        // calculates average if there is at least onex
        // data value
        if (dateRangeData.size() > 0) {
            // System.out.println(sum + " " + dataRangeData.size());
            average = sum / dateRangeData.size();
        }

        return average;
    }

}