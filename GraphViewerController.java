import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.AnchorPane;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.time.LocalDate;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Responsible for managing the graph viewer of the program.
 * The user selects a date range, borough, and particular dataField.
 * A line chart is plotted of the dataFields vs xAxisValues for the particular borough.
 * The user can hover over points on the line chart for the exact values of the
 * plots.
 *
 * @author Muhammad Ahsan Mahfuz
 * @version 2023.03.26 
 */
public class GraphViewerController extends ViewerController {
    @FXML
    private AnchorPane graphPane;

    @FXML
    private BorderPane viewPane;

    @FXML
    private ComboBox<String> boroughComboBox, dataFieldComboBox;

    @FXML
    private Label infoLabel, hoverLabel;

    @FXML
    private LineChart<String, Integer> chart;

    @FXML
    private CategoryAxis xAxis = new CategoryAxis();

    @FXML
    private NumberAxis yAxis = new NumberAxis();

    private XYChart.Series<String, Integer> series;

    private double upperBound, lowerBound;

    private String borough, dataField;
    private String[] dataFields = { "Retail and Recreation Mobility", "Grocery and Pharmacy Mobility", "Parks Mobility",
            "Transit Stations Mobility", "Workplaces Mobility", "Residential Mobility", "New Cases", "Total Cases",
            "New Deaths", "Total Deaths" };

    /**
     * Add all the boroughs to the choice box and respond to the selection made
     * by the user.
     */
    @FXML
    public void initialize() {
        // Adding window size change listeners to resize map properly
        viewPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
            }
        });

        viewPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != newVal) {
                resizeComponents(viewPane);
            }
        });


        boroughComboBox.getItems().addAll(dataset.getBoroughs());
        dataFieldComboBox.getItems().addAll(dataFields);
    }

    /**
     * Called when either date picker is changed.
     * 
     * @param fromDate The starting date of the range
     * @param toDate The ending date of the range
     */
    @FXML
    public void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        // If the user changes the date range, but has a borough and field selected, construct the chart
        if (borough != null || dataField != null) {
            constructChart(fromDate, toDate);
        }
    }

    /**
     * Gets the selected borough from the choice box to use when creating the chart.
     * 
     * @param event The borough the user selected from the choice box.
     */
    @FXML
    private void selectBorough(ActionEvent event) {
        borough = boroughComboBox.getValue();
        // If the user has selected both a borough and field to graph, construct the chart
        if (dataFieldComboBox.getValue() != null) {
            constructChart(fromDate, toDate);
        }
    }

    /**
     * Gets the selected dataField from the choice box to use when creating the chart.
     * 
     * @param event The dataField the user selected from the choice box.
     */
    @FXML
    private void selectStat(ActionEvent event) {
        dataField = dataFieldComboBox.getValue();
        // If the user has selected both a borough and field to graph, construct the chart
        if (boroughComboBox.getValue() != null) {
            constructChart(fromDate, toDate);
        }
    }

    /**
     * Creates a line chart for the dates vs total deaths of a borough.
     * 
     * @param fromDate The start date of the date range.
     * @param toDate   The end date of the date range.
     */
    private void constructChart(LocalDate fromDate, LocalDate toDate) {
        // Clears the previous chart before creating a new one
        chart.getData().clear();
        
        series = new XYChart.Series<String, Integer>();
        series.setName(dataField + " in " + borough);

        // Display appropriate label if data range is invalid
        if (!dataset.isDateRangeValid(fromDate, toDate)) {
            infoLabel.setText("The selected 'from' date is after the 'to' date");
            return;
        }
        
        // Get the data to plot and split to x-axis data and y-axis data
        ArrayList<Object> dataToPlot = getDataToPlot(fromDate, toDate);
        ArrayList<String> xAxisValues = (ArrayList<String>) dataToPlot.get(0);
        ArrayList<Integer> yAxisValues = (ArrayList<Integer>) dataToPlot.get(1);
        
        // Display appropriate label if no data in date range
        if (xAxisValues.size() == 0) {
            infoLabel.setText("There is no data for this dataField and borough in the selected date range");
            return;
        }
        
        infoLabel.setText("Showing data between " + fromDate + " and " + toDate);

        // Populate series to be plotted
        for (int i = xAxisValues.size() - 1; i >= 0; i--) {
            series.getData().add(new XYChart.Data<String, Integer>(xAxisValues.get(i), yAxisValues.get(i)));
        }
        
        // Plot the series
        chart.getData().addAll(series);
        
        addTooltips();
        setBounds(yAxisValues);
        
        yAxis.setLabel(dataField);
    }

    /**
     * Set the ArrayLists 'date' and 'yAxisValues' to the X and Y values that
     * will be plotted on the linechart.
     * dates is the x-axis. yAxisValues is the y-axis.
     * 
     * @param fromDate The start date of the date range.
     * @param toDate   The end date of the date range.
     */
    private ArrayList<Object> getDataToPlot(LocalDate fromDate, LocalDate toDate) {
        ArrayList<String> xAxisValues = new ArrayList<>();
        ArrayList<Integer> yAxisValues = new ArrayList<>();
        
        // Get the data from the dataset and store dates as x-axis and the data as y-axis
        for (CovidData data : dataset.getBoroughData(borough, fromDate, toDate)) {
            LocalDate xValue = LocalDate.parse(data.getDate());
            Integer yValue = getDataField(data);
            if (xValue != null && yValue != null) {
                xAxisValues.add(xValue.toString());
                yAxisValues.add(yValue);
            }
        }
        
        ArrayList<Object> dataToPlot = new ArrayList<>();
        dataToPlot.add(xAxisValues);
        dataToPlot.add(yAxisValues);
        
        return dataToPlot;
    }

    /**
     * Calls the appripriate method to retrieve the dataFieldistic selected by the user.
     * 
     * @param data
     * @return
     */
    private Integer getDataField(CovidData data) {
        String dataFieldSelected = dataFieldComboBox.getValue();

        switch (dataFieldSelected) {
            case "Retail and Recreation Mobility":
                return data.getRetailRecreationGMR();

            case "Grocery and Pharmacy Mobility":
                return data.getGroceryPharmacyGMR();

            case "Parks Mobility":
                return data.getParksGMR();

            case "Transit Stations Mobility":
                return data.getTransitGMR();

            case "Workplaces Mobility":
                return data.getWorkplacesGMR();

            case "Residential Mobility":
                return data.getResidentialGMR();

            case "New Cases":
                return data.getNewCases();

            case "Total Cases":
                return data.getTotalCases();

            case "New Deaths":
                return data.getNewDeaths();

            case "Total Deaths":
                return data.getTotalDeaths();
                
            default:
                return null;
        }
    }

    /**
     * Sets the upper and lower bound of the y-axis on the line chart.
     * 
     * @param yValues Arraylist of all the y-axis valus in the specified borough and
     *                date range.
     */
    private void setBounds(ArrayList<Integer> yValues) {
        calculateBounds(yValues);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setMinorTickVisible(false);
    }

    /**
     * Calculates the lower and upper bounds of the y-axis.
     * 
     * @param yValues ArrayList of all the y-axis values in the specified borough
     *                and date range
     */
    private void calculateBounds(ArrayList<Integer> yValues) {
        // Get min and max value in data
        Integer lowerValue = Integer.valueOf(Collections.min(yValues));
        Integer upperValue = Integer.valueOf(Collections.max(yValues));

        // Add 10% padding around the lower and upper bouds
        double yPadding = 0.1 * (upperValue - lowerValue);
        
        lowerBound = (lowerValue - yPadding);
        upperBound = upperValue + yPadding;
    }

    /**
     * Adds a Tooltip to every node on the line chart which displays the exact date
     * and dataField value when hovered over with the mouse.
     */
    private void addTooltips() {
        for (XYChart.Data<String, Integer> data : series.getData()) {
            // Make each point smaller - more visually appealing
            data.getNode().setStyle("-fx-background-insets: 0, 1;" +
                    "-fx-background-radius: 1px;" +
                    "-fx-padding: 2px;");
                    
            // Shows the exact data when hovering over a point
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Tooltip tp = new Tooltip(
                            "Date: " + data.getXValue() + "\n" + dataField + ": " + data.getYValue().toString());
                    tp.setShowDelay(Duration.seconds(0.0));
                    Tooltip.install(data.getNode(), tp);
                    hoverLabel.setVisible(false);
                }
            });
        }
    }

    /**
     * Returns the anchor pane of GraphView.fxml
     * 
     * @return graphPane
     */
    protected Parent getView() {
        return viewPane;
    }
}
