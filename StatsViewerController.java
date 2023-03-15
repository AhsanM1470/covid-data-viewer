import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import java.util.ArrayList;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controls logic of DataViewer
 *
 * @author Ishab Ahmed
 * @version 2023.03.13
 */
public class StatsViewerController
{
    @FXML
    private DatePicker fromDatePicker;
    
    @FXML
    private DatePicker toDatePicker;
    
    @FXML
    private Button leftButton;
    
    @FXML
    private Button rightButton;
    
    @FXML
    private BorderPane statsBP;
    
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
    
    //index for statsPanes
    private int i = 0;
    
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
    
    @FXML
    private void dateChanged(ActionEvent event) {
        rightButton.setDisable(true);
    
        DateFormat dateFormat = new SimpleDateFormat("yy-mm-dd");
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        if (fromDate != null && toDate != null) {
            if (fromDate.isBefore(toDate)) {
                rightButton.setDisable(false);
            }
        }
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



    }
    
    
    
}
