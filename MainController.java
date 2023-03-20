import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Write a description of class MainController here.
 *
 * @author Ishab Ahmed
 * @version 2023.03.20
 */
public class MainController implements Initializable
{
    @FXML
    private BorderPane mainLayout;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;
    
    @FXML
    protected DatePicker toDatePicker;

    @FXML
    protected DatePicker fromDatePicker;
    
    @FXML
    protected StackPane stackPane;
    
    private ArrayList<CovidData> data;
    
    private ViewerController[] controllers;
    private int controllerIndex;

    /**
     * 
     * Initialises the GUI with the necessary data and controllers for panels.
     * initialize() used instead of constructor so that the initialisation has
     * access to the injected @FXML components.
     * 
     * @param location The location of the FXML file.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();

        // Try to load the controllers for all the panels
        try {
            controllers = new ViewerController[3];
            loadControllers();
        } catch (Exception e) {
            // Print the error message and stack trace to the console
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        // Current controller to be used is in 0th index
        controllerIndex = 0;
        
        // Load the first panel into the center of the screen
        stackPane.getChildren().add(controllers[controllerIndex].getView());
    }
    
    /**
     * Loads the controllers of all of the panels in the app.
     * 
     * @throws Exception if the FXMLLoader fails to load any FXML file.
     */
    public void loadControllers() throws Exception {
        // FXML files of all panels
        String[] fxmlFiles = {"WelcomeView.fxml", "MapView.fxml", "StatsView.fxml"};
        
        for (int i = 0; i < fxmlFiles.length; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFiles[i]));
            loader.load();
            ViewerController controller = loader.getController();
            
            controller.setData(data);
            controllers[i] = controller;
        }
    }
    
    // ----------------------------- Panel Switching ---------------------------- //
    /**
     * Changes whether the panel switching buttons are interactable or not.
     * 
     * @param state The state the buttons should be (true=enabled, false=disabled)
     */
    private void allowPanelSwitching(boolean state) {
        leftButton.setDisable(!state);
        rightButton.setDisable(!state);
    }
    
    /**
     * Changes the center of the main layout to the next panel.
     * 
     * @param event The event triggered by clicking the next panel button
     */
    @FXML
    private void nextPanel(ActionEvent event) {
        // prevents continuous clicking if currently in process of switching to next panel
        if (ViewerController.inTransition == false) {
            ViewerController currentController = controllers[controllerIndex];
            
            // increment the panel controller (% to prevent indexOutOfBound)
            controllerIndex = (controllerIndex + 1) % controllers.length;
            ViewerController nextController = controllers[controllerIndex];
            
            // transition from current view to the next view
            transitionIntoNextPanel(currentController, nextController, event);
        }
    }
    
    /**
     * Changes the center of the main layout to the previous panel.
     * 
     * @param event The event triggered by clicking the next panel button
     */
    @FXML
    private void previousPanel(ActionEvent event) {
        // prevents continuous clicking if currently in process of switching to next panel
        if (ViewerController.inTransition == false) {
            ViewerController currentController = controllers[controllerIndex];
            
            controllerIndex--;
            // rolls index back to end of list if reaching -ve index
            if (controllerIndex < 0) {
                controllerIndex = controllers.length - 1;
            }
            ViewerController nextController = controllers[controllerIndex];
            
            // transition from  current view to the previous view
            transitionIntoNextPanel(currentController, nextController, event);
        }
        
    }

    // ----------------------- Transition Between Panels ----------------------- //
    
    /**
     * set the center of the screen (contents of stackPane) to new view
     * show a transition between the the old view and  new view 
     * 
     * @param previousController controller class of the view being switched out
     * @param currentController controller class of the view being switched in
     * @param event button that triggered the series of events.
     */
    private void transitionIntoNextPanel(ViewerController currentController, ViewerController nextController,
            ActionEvent event) {
                
        // when inTransition is true, don't allow certain actions such as switching
        // panes until animation finished

        nextController.updatePanelForDateRange(fromDatePicker.getValue(), toDatePicker.getValue());
        nextController.resizeComponents(mainLayout);

        // Sets the date picker of the next panel to the dates chosen on the current
        // panel
        nextController.updatePanelForDateRange(fromDatePicker.getValue(), toDatePicker.getValue());
        nextController.resizeComponents(mainLayout);

        Parent nextPanel = nextController.getView();
        Parent oldPanel = currentController.getView();

        // determine which direction the panes will move in depending on the button
        // pressed. 

        // animation to move panels from right to left.
        double oldPanelStartX = 0;
        double oldPanelEndX = -mainLayout.getWidth();
        double newPanelStartX = mainLayout.getWidth();
        double newPanelEndX = 0;


        // if instead we're moving from left to right, inverse values
        if (event.getSource() == leftButton) {
            oldPanelEndX *= -1;
            newPanelStartX *= -1;
        }

        oldPanel.translateXProperty().set(oldPanelStartX);
        nextPanel.translateXProperty().set(newPanelStartX);

        // add you held 
        stackPane.getChildren().add(nextPanel);

        // transitionig between panes
        Duration transitionDuration = Duration.seconds(1);

        // timline thread that allows for the animations
        Timeline timeline = new Timeline();

        // key value -> (whatToAnimate + start value, its end value, interpolation type)
        // key frame -> (how long above animation, animation to do)
        KeyValue oldViewKV = new KeyValue(oldPanel.translateXProperty(), oldPanelEndX, new AnimationInterpolator());
        KeyFrame oldViewKF = new KeyFrame(transitionDuration, oldViewKV);

        KeyValue newViewKV = new KeyValue(nextPanel.translateXProperty(), newPanelEndX, new AnimationInterpolator());
        KeyFrame newViewKF = new KeyFrame(transitionDuration, newViewKV);

        // adding the two animations to the timeline to execute
        timeline.getKeyFrames().add(oldViewKF);
        timeline.getKeyFrames().add(newViewKF);
        

        timeline.setOnFinished(e -> {
            // remove the previous view that was on teh stack pane
            stackPane.getChildren().remove(currentController.getView());
            ViewerController.inTransition = false;
        });

        // set 'Controller.inTransition' to true, disallowing some actions such as spamming
        // left/right buttons
        timeline.play();
        ViewerController.inTransition = true;

    }
    
    // ----------------------------- Date Changing ----------------------------- //

    /**
     * Handles the event when the date picker is changed.
     * Once two dates are selected, check if its valid, and attempt to process data
     * within that date range
     * 
     * @param event The event triggered by changing the date picker.
     */
    @FXML
    void dateChanged(ActionEvent event) {        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        if (!(fromDate == null || toDate == null)) {
            // Update the current panel using the new date range
            controllers[controllerIndex].updatePanelForDateRange(fromDate, toDate);
            
            // If 'from' date is before 'to' date, allow panel switching
            if (fromDate.isBefore(toDate) || fromDate.isEqual(toDate)) {
                allowPanelSwitching(true);
            } else {
                allowPanelSwitching(false);
            }
        }
    }
}
