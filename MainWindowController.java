import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import javafx.util.Duration;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Controls the main window of the application and initializes the necessary
 * data and controllers for the different panels.
 *
 * @author Ishab Ahmed, Harshraj Patel
 * @version 2023.03.20
 */
public class MainWindowController {
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

    // Instance of Dataset singleton
    protected Dataset dataset = Dataset.getInstance();

    private ArrayList<ViewController> controllers;
    private int controllerIndex;

    /**
     * 
     * Initialises the GUI with the necessary data and controllers for panels.
     * initialize() used instead of constructor so that the initialisation has
     * access to the injected @FXML components.
     * 
     * @param location The location of the FXML file.
     */
    @FXML
    public void initialize() {

        // Try to load the controllers for all the panels
        try {
            controllers = new ArrayList<ViewController>();
            loadControllers();
        } catch (Exception e) {
            // Print the error message and stack trace to the console
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // Current controller to be used is in 0th index
        controllerIndex = 0;

        // Load the first panel into the center of the screen
        stackPane.getChildren().add(controllers.get(controllerIndex).getView());

        applyDatePickLimit(fromDatePicker);
        applyDatePickLimit(toDatePicker);
    }

    /**
     * Loads the FXML files and controllers for all panels.
     * 
     * @throws Exception if the FXMLLoader fails to load any FXML file.
     */
    private void loadControllers() throws Exception {
        // FXML files of all panels
        String[] fxmlFiles = { "WelcomeView.fxml", "MapView.fxml", "StatsView.fxml", "GraphView.fxml" };

        for (int i = 0; i < fxmlFiles.length; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFiles[i]));
            loader.load();

            // get controller instance as set in FXML file
            ViewController controller = loader.getController();
            controllers.add(controller);
        }
    }

    /**
     * Applies a minimum and maximum date limit to the date picker,
     * based on the minimum and maximum dates of the datset.
     * 
     * @param datePicker The DatePicker to apply the date limit to
     */
    private void applyDatePickLimit(DatePicker datePicker) {

        // Calculate the minimum and maximum dates in the dataset
        LocalDate minDate = LocalDate.parse(Collections.min(dataset.getData()).getDate());
        LocalDate maxDate = LocalDate.parse(Collections.max(dataset.getData()).getDate());

        datePicker.setDayCellFactory((cell) -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean bool) {
                super.updateItem(date, bool);
                // Disables date cell if date is not in the range of dataset
                setDisable(date.isBefore(minDate) || date.isAfter(maxDate));
            }
        });

    }

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
     * Checks if the stack pane can switch to a different panel or not.
     * 
     * @return true if the stack pane can switch to a different panel, false
     *         otherwise
     */
    private boolean canSwitchPanels() {
        // If there is a panel already on the pane, then switching should be prevented
        return !(stackPane.getChildren().size() > 1);
    }

    /**
     * Changes the center of the main layout to the next panel.
     * 
     * @param event The event triggered by clicking the next panel button
     */
    @FXML
    private void nextPanel(ActionEvent event) {
        // prevents continuous clicking if currently in process of switching to next
        // panel
        if (canSwitchPanels()) {
            ViewController currentController = controllers.get(controllerIndex);

            // increment the panel controller (% to prevent indexOutOfBound)
            controllerIndex = (controllerIndex + 1) % controllers.size();
            ViewController nextController = controllers.get(controllerIndex);

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
        // prevents continuous clicking if currently in process of switching to next
        // panel
        if (canSwitchPanels()) {
            ViewController currentController = controllers.get(controllerIndex);

            controllerIndex--;
            // rolls index back to end of list if reaching -ve index
            if (controllerIndex < 0) {
                controllerIndex = controllers.size() - 1;
            }
            ViewController nextController = controllers.get(controllerIndex);

            // transition from current view to the previous view
            transitionIntoNextPanel(currentController, nextController, event);
        }
    }

    /**
     * Sets the center of the screen to next panel.
     * Shows a transition between the the current panel and next panel.
     * 
     * @param currentController The ViewController of the current panel
     * @param nextController    The ViewController of the next panel
     * @param event             Button click event that triggers the panel
     *                          transition
     */
    private void transitionIntoNextPanel(ViewController currentController, ViewController nextController,
            ActionEvent event) {

        // Updates the next panel with the necessary information given the dates
        // currently chosen in the datepickers
        nextController.updatePanelForDateRange(fromDatePicker.getValue(), toDatePicker.getValue());
        nextController.resizeComponents(mainLayout);

        Parent currentPanel = currentController.getView();
        Parent nextPanel = nextController.getView();

        // Will determine which direction the panels will move in.
        double currentPanelEndX;
        double newPanelStartX;

        double currentPanelStartX = 0;
        double newPanelEndX = 0;

        // If right button pressed, transition from right to left
        if (event.getSource() == rightButton) {
            currentPanelEndX = -mainLayout.getWidth();
            newPanelStartX = mainLayout.getWidth();
        } else {
            // If left button pressed, transition from left to right (inversed values)
            currentPanelEndX = mainLayout.getWidth();
            newPanelStartX = -mainLayout.getWidth();
        }

        currentPanel.translateXProperty().set(currentPanelStartX);
        nextPanel.translateXProperty().set(newPanelStartX);

        stackPane.getChildren().add(nextPanel);

        // Transition length between panes
        Duration transitionDuration = Duration.seconds(0.5);

        // Timeline that allows for the animations
        Timeline timeline = new Timeline();

        // key value -> (whatToAnimate + start value, it's end value, interpolation
        // type)
        KeyValue currentViewKV = new KeyValue(currentPanel.translateXProperty(), currentPanelEndX,
                new AnimationInterpolator());
        // key frame -> (animation length, what animation to do)
        KeyFrame currentViewKF = new KeyFrame(transitionDuration, currentViewKV);

        KeyValue newViewKV = new KeyValue(nextPanel.translateXProperty(), newPanelEndX, new AnimationInterpolator());
        KeyFrame newViewKF = new KeyFrame(transitionDuration, newViewKV);

        // Adding the two animations to the timeline to execute
        timeline.getKeyFrames().add(currentViewKF);
        timeline.getKeyFrames().add(newViewKF);

        timeline.setOnFinished(e -> {
            // Remove the previous view that was on teh stack pane
            stackPane.getChildren().remove(currentController.getView());
        });

        // Play the animation
        timeline.play();
    }

    /**
     * Handles the event when the date picker is changed.
     * Once two dates are selected, check if its valid, and attempt to process data
     * within that date range
     * 
     * @param event The event triggered by changing the date picker.
     */
    @FXML
    private void dateChanged(ActionEvent event) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (!(fromDate == null || toDate == null)) {
            // Update the current panel using the new date range
            controllers.get(controllerIndex).updatePanelForDateRange(fromDate, toDate);

            // If 'from' date is before 'to' date, allow panel switching
            if (fromDate.isBefore(toDate) || fromDate.isEqual(toDate)) {
                allowPanelSwitching(true);
            } else {
                allowPanelSwitching(false);
            }
        }
    }

    /**
     * Growth ratio to allow for fast -> slow animation
     * y = 1 - (2.6^(-6.8x))
     * Takes in a value between 0 and 1, returns a value between 0 and 1 determining
     * acceleration of movement during transition
     */
    private class AnimationInterpolator extends Interpolator {
        @Override
        protected double curve(double timePassedRatio) {
            return (timePassedRatio >= 1.0) ? 1.0 : 1 - Math.pow(2, -6.8 * timePassedRatio);
        }
    }
}
