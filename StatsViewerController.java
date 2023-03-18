import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

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

/**
 * Controls logic of DataViewer
 *
 * @author Saihan Marshall
 * @version 2023.03.13
 */
public class StatsViewerController extends Controller
{
//    @FXML
//    private DatePicker fromDatePicker;
//
//    @FXML
//    private DatePicker toDatePicker;
    
    @FXML
    private Button leftButton;
    
    @FXML
    private Button rightButton;

    @FXML
    private BorderPane statsPane;
    
    @FXML
    /**
     * This is the first pane that the user sees.
     * This pane is automatically set to visible when
     *  "StatsViewer" is launched.
     *
     *  StackPanes are used so that the panes can be
     *   stacked on one another as they do not need to
     *   be positioned relative to one another, and
     *   only must be set as visible or invisible.
     */
    private StackPane firstPane;
    
    @FXML
    /**
     * This is the second pane that the user sees.
     * This pane is automatically set to invisible when
     *  "StatsViewer" is launched.
     */
    private BorderPane secondPane;
    
    @FXML
    /**
     * This is the third pane that the user sees.
     * This pane is automatically set to invisible when
     *  "StatsViewer" is launched.
     */
    private StackPane thirdPane;
    
    @FXML
    /**
     * This is the fourth pane that the user sees.
     * This pane is automatically set to invisible when
     *  "StatsViewer" is launched.
     */
    private StackPane fourthPane;
    
    
    private ArrayList<Pane> statsPanes = new ArrayList<>();

    @FXML
    /**
     * This label shows the sum of the total deaths in a
     *  given date range.
     */
    private Label sumTotalDeathLabel;

    //index for statsPanes
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

    @FXML
    /**
     * This creates an arraylist of all the panes,
     *  which allows the panes to be systematically
     *  selected.
     */
    public void initialize(){
        statsPanes.add(firstPane);
        statsPanes.add(secondPane);
        statsPanes.add(thirdPane);
        statsPanes.add(fourthPane);
        firstPane.setVisible(true);
    }

    /**
     *
     * @param from
     * @param to
     */
    protected void dateChanged(LocalDate from, LocalDate to) {;}

    /**
     * @return the main centre panel
     */
    protected Parent getView() {
        return statsPane;
    }

    @FXML
    /**
     * This is called whenever the dates at the top right are changed.
     * For now, this changes the values of "fromDate" and "toDate" and
     */
    protected void processDateRangeData(LocalDate fromDate, LocalDate toDate) {
        rightButton.setDisable(true);

        DateFormat dateFormat = new SimpleDateFormat("yy-mm-dd");

        // These are initialised at the start because
        // they are needed elsewhere
        fromDate = fromDatePicker.getValue();
        toDate = toDatePicker.getValue();

        if (fromDate != null && toDate != null) {
            if (fromDate.isBefore(toDate)) {
                rightButton.setDisable(false);
            }
        }

        // if the index is 1, then refreshes the label
        // showing sum of total deaths.
        refreshSumTotalDeathLabel();
    }

    @FXML
    /**
     * This allows the user to view the next pane.
     * It sets the current pane as invisible and the
     *  next pane as visible.
     */
    private void forwardButton(ActionEvent event){
        //sets the current pane as invisible
        statsPanes.get(i).setVisible(false);


        // if on last pane, sets the first pane as visible to loop around
        if(i == statsPanes.size() - 1){
            statsPanes.get(0).setVisible(true);
        }

        // if on any pane not last, sets next pane as visible
        else{
            statsPanes.get(i + 1).setVisible(true);
        }

        //increments index for the arraylist of stats panes
        if(i == 3){
            i = 0;
        }

        else{
            i++;
        }

        refreshSumTotalDeathLabel();

    }

    @FXML
    /**
     * This allows the user to view the previous pane.
     * It sets the current pane as invisible and the
     *  previous pane as visible
     */
    private void backwardButton(ActionEvent event){
        //sets the current pane as invisible
        statsPanes.get(i).setVisible(false);

        // if on first pane, sets the last pane as visible to loop around
        if(i == 0){
            statsPanes.get(statsPanes.size() - 1).setVisible(true);
        }

        // if on any pane not last, sets next pane as visible
        else{
            statsPanes.get(i - 1).setVisible(true);
        }

        //decrements index for the arraylist of stats panes
        if(i == 0){
            i = 3;
        }

        else{
            i--;
        }

        refreshSumTotalDeathLabel();
    }

    /**
     * This updates the label in the second pane to show
     *  the current sum of total deaths for the given date
     *  range.
     * This is called whenever index "i" changes to show the
     *  second pane, or if "i" is already 1 and the date changed.
     */
    private void refreshSumTotalDeathLabel(){
        if (i == 1){
            if(fromDate != null && toDate != null){
                // this updates the value of "dataRangeData" so that
                // it takes into account the most recent "fromDate"
                // and "toDate"
                dataRangeData = getDateRangeData(fromDate, toDate);

                if(fromDate.isBefore(toDate)){
                    sumTotalDeathLabel.setText("" + totalNumberOfTotalDeathsCount());
                }
            }

        }

    }

     /**
      * This returns the sum of the total deaths.
      */
     public int totalNumberOfTotalDeathsCount(){
         int totalNumberOfTotalDeaths = 0;

         // this iterates through the data within the date range and
         // sums the total deaths
         for(CovidData c : dataRangeData){
             // some CovidData strings actually have null for the total
             // deaths so a check is necessary
             if(!Objects.isNull(c.getTotalDeaths())){
                totalNumberOfTotalDeaths += c.getTotalDeaths();
             }

         }
         System.out.println(totalNumberOfTotalDeaths);

         return totalNumberOfTotalDeaths;
     }

}