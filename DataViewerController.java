import javafx.fxml.FXML;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import java.util.ArrayList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 * Responsible for managing the GUI components of the application, including
 * the table that displays the CovidData information, the date pickers, and the
 * buttons that navigate through different panels.
 *
 * @author Ishab Ahmed
 * @version 2023.03.13
 */
public class DataViewerController extends Controller {
    @FXML
    private BorderPane mainLayout;

    @FXML
    private BorderPane centerBorderPane;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @FXML
    private VBox welcomePane;

    @FXML
    private Pane tablePane;

    @FXML
    private TableView<CovidData> dataTable;

    @FXML
    private Label dataTableInfoLabel;

    // true when transitioning between views.
    // if true, the 'stackPane' has more than one child node.
    private boolean inTransition = false;

    private Controller[] controllers;
    private int controllerIndex;

    private double initialPaneWidth, initialPaneHeight;

    protected PanelType currentPanelType = PanelType.MAIN;

    /**
     * Initializes the FXML controller class.
     * This method is called by the FXMLLoader when the corresponding FXML file is
     * loaded.
     */
    @FXML
    public void initialize() {
        initialPaneWidth = mainLayout.getPrefWidth();
        initialPaneHeight = mainLayout.getPrefHeight();

        // adding window size change listeneres
        mainLayout.widthProperty().addListener((obs, oldVal, newVal) -> {
            // resizing components only takes place when 'inTransition' is false meaning
            // that there's only one child node at index 0 within the main 'stackPane'
            if (scalePanels.contains(currentPanelType)) {
                resizeComponents(0);
            }
            ;
        });

        mainLayout.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (scalePanels.contains(currentPanelType)) {
                // resizing components only takes place when no transitions are happening
                // meaning there's only one child node at index 0 within the main 'stackPane'
                resizeComponents(0);
            }
            ;
        });
        // Create TableColumns for the TableView
        TableColumn<CovidData, String> dateCol = new TableColumn<CovidData, String>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("date"));

        TableColumn<CovidData, String> boroughCol = new TableColumn<CovidData, String>("Borough");
        boroughCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("borough"));

        TableColumn<CovidData, String> newCasesCol = new TableColumn<CovidData, String>("New Cases");
        newCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("newCases"));

        TableColumn<CovidData, String> totalCasesCol = new TableColumn<CovidData, String>("Total Cases");
        totalCasesCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("totalCases"));

        TableColumn<CovidData, String> newDeathsCol = new TableColumn<CovidData, String>("New Deaths");
        newDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("newDeaths"));

        TableColumn<CovidData, String> totalDeathsCol = new TableColumn<CovidData, String>("Total Deaths");
        totalDeathsCol.setCellValueFactory(new PropertyValueFactory<CovidData, String>("totalDeaths"));

        // Add the TableColumns to the TableView
        dataTable.getColumns().addAll(dateCol, boroughCol, newCasesCol, totalCasesCol, newDeathsCol, totalDeathsCol);

        // Make all columns equal width
        dataTable.setColumnResizePolicy(dataTable.CONSTRAINED_RESIZE_POLICY);

        // Current controller to be used is in 0th index
        controllerIndex = 0;

        // Try to load the controllers for all the panels
        try {
            controllers = new Controller[2];
            loadControllers();
        } catch (Exception e) {
            // Print the error message and stack trace to the console
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param index within the stack pane to resize.
     */
    public void resizeComponents(int index) {

        // if the window is currenly in the process of switching panels, don't resize
        // anything
        if (inTransition) {
            return;
        }

        double ratioX = mainLayout.getWidth() / initialPaneWidth;

        if (Double.isInfinite(ratioX) || Double.isNaN(ratioX)) {
            return;
        }

        double ratioY = mainLayout.getHeight() / initialPaneHeight;

        if (Double.isInfinite(ratioY) || Double.isNaN(ratioY)) {
            return;
        }

        ratioY = Math.max(Math.min(ratioY, 2), 1);
        ratioX = Math.max(Math.min(ratioX, 2), 1);

        BorderPane ct = (BorderPane) stackPane.getChildren().get(index);
        Node node = ct.getCenter();

        // scale by same ratio
        double scale = Math.min(ratioX, ratioY);
        node.setScaleY(scale);
        node.setScaleX(scale);

        // stackPane.getChildren().set(0, ct);

    }

    /**
     * Loads the controllers of all of the panels in the app.
     * 
     * @throws Exception if the FXMLLoader fails to load any FXML file.
     */
    public void loadControllers() throws Exception {
        FXMLLoader mapLoader = new FXMLLoader(getClass().getResource(
                "MapWindow.fxml"));
        mapLoader.load();
        Controller mapController = mapLoader.getController();

        controllers[1] = mapController;
        // Current controller responsible for first panel
        controllers[0] = this;
    }

    /**
     * Handles the event when the date picker is changed.
     * Once two dates are selected, check if its valid, and attempt to process data
     * within that date range
     * 
     * @param event The event triggered by changing the date picker.
     */
    @FXML
    @Override
    protected void dateChanged(ActionEvent event) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            return;
        }

        controllers[controllerIndex].processDataInDateRange(fromDate, toDate);
    }

    /**
     * Handles the event when the date picker is changed. Updates the data table
     * with the chosen date range if valid date chosen, otherwise shows an error
     * message to user.
     * 
     * @param event The event triggered by changing the date picker.
     */
    @FXML
    protected void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        allowPanelSwitching(isDateRangeValid(fromDate, toDate));

        // Clear any existing items from the table
        dataTable.getItems().clear();

        ArrayList<CovidData> rangedData = getDataInDateRange(fromDate, toDate);
        boolean dataExistsInDateRange = rangedData.size() > 0;

        if (isDateRangeValid(fromDate, toDate) == true) {
            if (dataExistsInDateRange) {
                populateTable(rangedData);
                dataTableInfoLabel.setText("Showing data from " + fromDate + " to " + toDate + ".");
            } else {
                dataTableInfoLabel.setText("There's no available data for the selected date range.");
            }
        } else {
            dataTableInfoLabel.setText("The 'from' date is before the 'to' date.");
        }

        setWelcomeState(false);
    }

    /**
     * Changes the state of the welcome pane.
     * 
     * @param state Bool indicating whether the welcome pane should be shown or
     *              hidden
     */
    private void setWelcomeState(boolean state) {
        welcomePane.setVisible(state);
        tablePane.setVisible(!state);
    }

    private void allowPanelSwitching(boolean state) {
        leftButton.setDisable(!state);
        rightButton.setDisable(!state);
    }

    /**
     * Populates the table with CovidData objects that fall within the given date
     * range.
     * 
     * @param dataToShow The data to populate the table with
     */
    private void populateTable(ArrayList<CovidData> dataToShow) {
        // Add all CovidData objects within the given date range to the table
        for (CovidData covidData : dataToShow) {
            dataTable.getItems().add(covidData);
        }
    }

    /**
     * Changes the center of the main layout to the next panel.
     * 
     * @param event The event triggered by clicking the next panel button
     */
    @FXML
    private void nextPanel(ActionEvent event) {
        if (inTransition) {
            return;
        }
        Controller oldController = controllers[controllerIndex];
        controllerIndex++;
        controllerIndex = controllerIndex % controllers.length;

        controllers[controllerIndex].setDateRange(getFromDate(), getToDate());

        Controller currentController = controllers[controllerIndex];

        transitionIntoNextPanel(oldController, currentController, event);

        // Switches the center of the main layout to the next panel
        // mainLayout.setCenter(currentController.getView());
    }

    /**
     * Changes the center of the main layout to the previous panel.
     * 
     * @param event The event triggered by clicking the next panel button
     */
    @FXML
    private void previousPanel(ActionEvent event) {
        if (inTransition) {
            return;
        }
        Controller previousController = controllers[controllerIndex];
        controllerIndex--;
        if (controllerIndex < 0) {
            controllerIndex = controllers.length - 1;
        }
        Controller currentController = controllers[controllerIndex];
        // Sets the date picker of the previous panel to the dates chosen on the current
        // panel
        controllers[controllerIndex].setDateRange(getFromDate(), getToDate());
        transitionIntoNextPanel(previousController, currentController, event);

    }

    /**
     * set the center of the screen (contents of stackPane) to new view
     * show a transition between the the old view and  new view 
     * 
     * @param previousController controller class of the view being switched out
     * @param currentController controller class of the view being switched in
     * @param event button that triggered the series of events.
     */
    private void transitionIntoNextPanel(Controller previousController, Controller currentController,
            ActionEvent event) {
        // when inTransition is true, don't allow certain actions such as switching
        // panes until animation finished

        // Sets the date picker of the next panel to the dates chosen on the current
        // panel
        currentPanelType = currentController.getPanelType();
        currentController.setDateRange(getFromDate(), getToDate());
        BorderPane nextPanel = (BorderPane) currentController.getView();
        BorderPane oldPanel = (BorderPane) previousController.getView();

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

        stackPane.getChildren().add(nextPanel);

        // if we're switching to a window which we want to re-scale, resize it to fit
        // the current screen before transitioning
        if (scalePanels.contains(currentPanelType)){
            resizeComponents(1);
        }
            
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
            stackPane.getChildren().remove(previousController.getView());
            inTransition = false;
        });

        // set 'inTransition' to true, disallowing some actions such as spamming
        // left/right buttons
        timeline.play();
        inTransition = true;

    }

    /**
     * Returns the list of CovidData objects loaded from the data source.
     * 
     * @return ArrayList of CovidData objects
     */
    public ArrayList<CovidData> getData() {
        return data;
    }

}