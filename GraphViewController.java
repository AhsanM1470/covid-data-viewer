import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * Responsible for displaying the graphs of the Covid Data.
 *
 * @author Muhammad Ahsan Mahfuz
 * @version 2023.03.26
 */
public class GraphViewController extends ViewController {
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

    private String borough, dataField;

    /**
     * Add all the boroughs to the choice box and respond to the selection made
     * by the user.
     */
    @FXML
    public void initialize() {
        super.initialize();

        boroughComboBox.getItems().addAll(dataset.getBoroughs());
        dataFieldComboBox.getItems().addAll(new String[] { "Retail and Recreation Mobility",
                "Grocery and Pharmacy Mobility", "Parks Mobility",
                "Transit Stations Mobility", "Workplaces Mobility", "Residential Mobility", "New Cases", "Total Cases",
                "New Deaths", "Total Deaths" });
    }

    /**
     * Called when either date picker is changed.
     * 
     * @param fromDate The starting date of the range
     * @param toDate   The ending date of the range
     */
    @FXML
    public void processDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        // If the user changes the date range, but has a borough and field selected,
        // construct the chart
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
    private void boroughSelected(ActionEvent event) {
        borough = boroughComboBox.getValue();
        // If the user has selected both a borough and field to graph,
        // construct the chart
        if (dataFieldComboBox.getValue() != null) {
            constructChart(fromDate, toDate);
        }
    }

    /**
     * Gets the selected dataField from the choice box to use when creating the
     * chart.
     * 
     * @param event The dataField the user selected from the choice box.
     */
    @FXML
    private void dataFieldSelected(ActionEvent event) {
        dataField = dataFieldComboBox.getValue();
        // If the user has selected both a borough and field to graph, construct the
        // chart
        if (boroughComboBox.getValue() != null) {
            constructChart(fromDate, toDate);
        }
    }

    /**
     * Constructs a chart for the specified data range, borough selected, and data
     * field selected.
     * 
     * @param fromDate The start date of the date range (inclusive)
     * @param toDate   The end date of the date range (inclusive)
     */
    private void constructChart(LocalDate fromDate, LocalDate toDate) {
        // Clears the previous chart before creating a new one
        chart.getData().clear();

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
        if (xAxisValues.isEmpty()) {
            infoLabel.setText("There is no data for this field and borough in the selected date range");
            return;
        }

        infoLabel.setText("Showing data between " + fromDate + " and " + toDate);

        // Populate series to be plotted
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName(dataField + " in " + borough);
        for (int i = xAxisValues.size() - 1; i >= 0; i--) {
            series.getData().add(new XYChart.Data<>(xAxisValues.get(i), yAxisValues.get(i)));
        }

        // Plot the series
        chart.getData().addAll(series);

        setYAxisBounds(yAxisValues);
        yAxis.setLabel(dataField);

        addTooltips(series);
    }

    /**
     * Retrieves the data from the dataset for a given borough within a specified
     * date range, and returns the data in a format that can be plotted on a chart.
     * 
     * @param fromDate The start date of the date range (inclusive)
     * @param toDate   The end date of the date range (inclusive)
     * @return ArrayList of the x-axis and y-axis values to be plotted on the chart
     */
    private ArrayList<Object> getDataToPlot(LocalDate fromDate, LocalDate toDate) {
        ArrayList<String> xAxisValues = new ArrayList<>();
        ArrayList<Integer> yAxisValues = new ArrayList<>();

        // Get the data from the dataset and store dates as x-axis and the data as
        // y-axis
        for (CovidData data : dataset.getBoroughData(borough, fromDate, toDate)) {
            LocalDate date = LocalDate.parse(data.getDate());
            Integer value = getDataField(data);

            if (date != null && value != null) {
                xAxisValues.add(date.toString());
                yAxisValues.add(value);
            }
        }

        ArrayList<Object> dataToPlot = new ArrayList<>();
        dataToPlot.add(xAxisValues);
        dataToPlot.add(yAxisValues);

        return dataToPlot;
    }

    /**
     * Returns the selected data field from the CovidData object.
     * 
     * @param data the CovidData object to retrieve the data field from
     * @return the selected data field value from the CovidData object, or null if
     *         no data field has been selected
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
     * @param yValues ArrayList of integers representing the y-axis data
     */
    private void setYAxisBounds(ArrayList<Integer> yValues) {
        // Get min and max value in data
        Integer lowerValue = Integer.valueOf(Collections.min(yValues));
        Integer upperValue = Integer.valueOf(Collections.max(yValues));

        // Add 10% padding around the lower and upper bouds
        double padding = 0.1 * (upperValue - lowerValue);
        double lowerBound = lowerValue - padding;
        double upperBound = upperValue + padding;

        // Set the axis bounds
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setMinorTickVisible(false);
    }

    /**
     * Adds a Tooltip to every point on the line chart which displays the exact date
     * and data field value when the point is hovered over.
     * 
     * @param series the XY chart series to add tooltips to
     */
    private void addTooltips(XYChart.Series<String, Integer> series) {
        for (XYChart.Data<String, Integer> data : series.getData()) {
            // Make each point smaller - more visually appealing
            data.getNode().setStyle("-fx-background-insets: 0, 1;"
                    + "-fx-background-radius: 1px;"
                    + "-fx-padding: 2px;");

            // Show the exact data when hovering over a point
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                // Make a tooltip showing the date and data value of the point
                String dateValue = data.getXValue();
                String dataFieldValue = dataField + ": " + data.getYValue().toString();
                Tooltip tooltip = new Tooltip("Date: " + dateValue + "\n" + dataFieldValue);

                // Show tooltip instantly after hovering over point
                tooltip.setShowDelay(Duration.seconds(0.0));

                // Add the tooltip to the point
                Tooltip.install(data.getNode(), tooltip);

                hoverLabel.setVisible(false);
            });
        }
    }

    /**
     * @return The view that this controller is associated with.
     */
    protected Parent getView() {
        return viewPane;
    }
}
