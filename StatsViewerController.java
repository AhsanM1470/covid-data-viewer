import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

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
     * Initialises "dateRangeData" which is used to
     * contain data inside a give data range.
     */
    private ArrayList<CovidData> dateRangeData = new ArrayList<>();


    /**
     * These contain the records with the latest total deaths or total cases
     *  values. These are used instead of using the last 33 values of
     *  dateRangeData to eliminate the chance of using null values.
     */
    private ArrayList<CovidData> finalDeathDataRecords = new ArrayList<>();
    private ArrayList<CovidData> finalCasesDataRecords = new ArrayList<>();


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
            // this updates the value of "dateRangeData" so that
            // it takes into account the most recent "fromDate"
            // and "toDate"
            dateRangeData = getDataInDateRange(fromDate, toDate);

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
                highestDeathDateLabel.setText("" + getFinalRecordWithCondition(dateRangeData, "deaths").get(0).getDate());
            }

        }

    }


    /**
     * This searches the data, starting with the most recent records, and checks for records that have non-zero
     *  or non-null values for the 'condition'. The 'condition' determines if you are looking for total deaths
     *  or total cases. The records are added to a global ArrayList which is updated and returned.
     *
     * @param cd is just dateRangeData. The most recent records are at the lowest indexes.
     * @param condition checks if the desired output is for recent deaths or for recent cases.
     * @return ArrayList containing the records that has the most recent total deaths value or total cases value.
     */
    private ArrayList<CovidData> getFinalRecordWithCondition(ArrayList<CovidData> cd, String condition){
        // both conditions add the final 33 records to the respective arraylist.

        if(condition.equals("deaths")){
            finalDeathDataRecords.clear();
            for(int i = 0; i < cd.size(); i++){
                if(cd.get(i).getTotalDeaths() != null && cd.get(i).getTotalDeaths() != 0){
                    finalDeathDataRecords.add(cd.get(i));
                }

                // this prevents more records than the last 33 from being added
                if(finalDeathDataRecords.size() >= 33){
                    break;
                }
            }
            return finalDeathDataRecords;
        }

        else if(condition.equals("cases")){
            finalCasesDataRecords.clear();
            for(int i = 0; i < cd.size(); i++){
                if(cd.get(i).getTotalCases() != null && cd.get(i).getTotalCases() != 0){
                    finalCasesDataRecords.add(cd.get(i));
                }

                if(finalCasesDataRecords.size() >= 33){
                    break;
                }
            }

            return finalCasesDataRecords;
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


        // this iterates through the records within the final date and
        // sums the total deaths
        getFinalRecordWithCondition(dateRangeData, "deaths");
        for (CovidData c : finalDeathDataRecords) {
            totalNumberOfDeaths += c.getTotalDeaths();

        }

        return totalNumberOfDeaths;
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


        getFinalRecordWithCondition(dateRangeData, "cases");
        // iterates and adds each case to number of cases
        for (CovidData c : finalCasesDataRecords) {
            totalCases += c.getTotalCases();
        }

        // calculates average if there is at least one
        // data value
        if (finalCasesDataRecords.size() > 0) {
            average = totalCases / finalCasesDataRecords.size();
            average = Math.round(average);
        }

        else {
            average = 0;
        }

        return average;

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
            average = sum / dateRangeData.size();
            average = Math.round(average);
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
            average = sum / dateRangeData.size();
            average = Math.round(average);
        }

        return average;
    }

}