import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import java.util.ArrayList;

/**
 * Provides test cases for the singleton Dataset clas that provides access to the CovidData objects
 * for all FXML controllers.
 * 
 * @author Ishab Ahmed
 * @version 2023.03.24
 */
public class DatasetTest {
    Dataset dataset;
    LocalDate fromDate, toDate;
    ArrayList<CovidData> data;

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp() {
        dataset = Dataset.getInstance();
        fromDate = LocalDate.parse("2022-01-01");
        toDate = LocalDate.parse("2022-01-31");
        data = dataset.getData();
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown() {
        dataset = null;
        fromDate = null;
        toDate = null;
        data = null;
    }

    /**
     * Tests whether getInstance() returns a non-null instance of the Dataset singleton.
     */
    @Test
    public void testGetInstance() {
        // Check non-null after retrieving instance for first time
        assertNotNull(dataset);
    
        // Check that the retrieving the instance again gives the same object
        Dataset dataset2 = Dataset.getInstance();
        assertSame(dataset, dataset2);
    }

    /**
     * Tests whether the getData() method returns the expected list of CovidData objects from the dataset.
     */
    @Test
    public void testGetData() {
        assertNotNull(data);
        
        for (CovidData record : data) {
            assertTrue(record instanceof CovidData);
        }
    }

    /**
     * Tests whether the CovidData objects that are returned by getDataInRange() fall within the specified date range.
     */
    @Test
    public void testGetDataInDateRange() {
        ArrayList<CovidData> dataInRange = dataset.getDataInDateRange(fromDate, toDate);
        for (CovidData covidData : dataInRange) {
            assertTrue(dataset.isDateInRange(LocalDate.parse(covidData.getDate()), fromDate, toDate));
        }
    }

    /**
     * Tests whether CovidData objects returned by getBoroughData() fall within the given date range and belong to the specified borough.
     */
    @Test
    public void testGetBoroughData() {
        ArrayList<CovidData> boroughData = dataset.getBoroughData("Brent", fromDate, toDate);
        for (CovidData covidData : boroughData) {
            assertTrue(dataset.isDateInRange(LocalDate.parse(covidData.getDate()), fromDate, toDate));
            assertEquals("Brent", covidData.getBorough());
        }
    }
    
    /**
     * Tests that getMostRecentDataWithFilter() returns only the most recent records that match the given filter.
     */
    @Test
    public void testGetMostRecentDataWithFilter() {
    
        // Get the most recent data with the getTotalCases filter applied
        ArrayList<CovidData> mostRecentDataWithTotalCases = dataset.getMostRecentDataWithFilter(dataset.getDataInDateRange(fromDate, toDate), CovidData::getTotalCases);
    
        // Check that the data returned matches the filter and is the most recent available
        for (CovidData record : mostRecentDataWithTotalCases) {
            assertNotEquals(null, record.getTotalCases());
            assertNotEquals(0, record.getTotalCases());
            assertEquals(toDate, LocalDate.parse(record.getDate()));
        }
    
        // Get the most recent data with the getTotalDeaths filter applied
        ArrayList<CovidData> mostRecentDataWithTotalDeaths = dataset.getMostRecentDataWithFilter(dataset.getDataInDateRange(fromDate, toDate), CovidData::getTotalCases);
        
        // Check that the data returned matches the filter and is the most recent available
        for (CovidData record : mostRecentDataWithTotalDeaths) {
            assertNotEquals(null, record.getTotalCases());
            assertNotEquals(0, record.getTotalCases());
            assertEquals(toDate, LocalDate.parse(record.getDate()));
        }
    }

    
    /**
     * Tests whether getBoroughs() is returning the expected borough names or not.
     */
    @Test
    public void testGetBoroughs() {
        String[] expectedBoroughs = {"Barking And Dagenham", "Barnet", "Bexley", "Brent", "Bromley", "Camden",
                "City Of London", "Croydon", "Ealing", "Enfield", "Greenwich", "Hackney", "Hammersmith And Fulham", "Haringey",
                "Harrow", "Havering", "Hillingdon", "Hounslow", "Islington", "Kensington And Chelsea", "Kingston Upon Thames",
                "Lambeth", "Lewisham", "Merton", "Newham", "Redbridge", "Richmond Upon Thames", "Southwark",
                "Sutton", "Tower Hamlets", "Waltham Forest", "Wandsworth", "Westminster"};
        String[] boroughs = dataset.getBoroughs();
        assertEquals(expectedBoroughs.length, boroughs.length);
        for (int i = 0; i < expectedBoroughs.length; i++) {
            assertEquals(expectedBoroughs[i], boroughs[i]);
        }
    }

    /**
     * Tests whether isDateInRange() returns true when the input date is within the date range (inclusive), and false when it's outside the range.
     */
    @Test
    public void testIsDateInRange() {
        // Valid data
        LocalDate dateInRange = LocalDate.parse("2022-01-15");

        // Invalid data
        LocalDate dateBeforeRange = LocalDate.parse("2021-12-31");
        LocalDate dateAfterRange = LocalDate.parse("2022-06-30");

        // Edge cases
        LocalDate dateOnFrom = LocalDate.parse("2022-01-01");
        LocalDate dateOnTo = LocalDate.parse("2022-01-31");

        assertTrue(dataset.isDateInRange(dateInRange, fromDate, toDate));
        assertFalse(dataset.isDateInRange(dateBeforeRange, fromDate, toDate));
        assertFalse(dataset.isDateInRange(dateAfterRange, fromDate, toDate));
        assertTrue(dataset.isDateInRange(dateOnFrom, fromDate, toDate));
        assertTrue(dataset.isDateInRange(dateOnTo, fromDate, toDate));
    }

    /**
     * Tests whether isDateRangeValid() returns false when either date is null or the 'to' date is before the 'from' date,
     * and true otherwise.
     */
    @Test
    public void testIsDateRangeValid() {
        // Valid input
        assertTrue(dataset.isDateRangeValid(fromDate, toDate));

        // Invalid inputs: nulls
        assertFalse(dataset.isDateRangeValid(null, null));
        assertFalse(dataset.isDateRangeValid(null, toDate));
        assertFalse(dataset.isDateRangeValid(fromDate, null));
        // Invalid input: 'to' before 'from'
        assertFalse(dataset.isDateRangeValid(toDate, fromDate));
    }

    /**
     * Tests whether getAverage() calculates the correct mean average, even when nulls are passed in.
     */
    @Test
    public void testGetAverage() {
        ArrayList<Number> dataField = new ArrayList<>();

        // Test with all null values
        dataField.add(null);
        dataField.add(null);
        dataField.add(null);

        assertEquals(0.0, dataset.getAverage(dataField));

        dataField.clear();

        // Test with mixed values
        dataField.add(1);
        dataField.add(2.0);
        dataField.add(null);
        dataField.add(3);
        dataField.add(null);
        dataField.add(4.0);

        assertEquals(2.5, dataset.getAverage(dataField));

        dataField.clear();

        // Test with non-null values
        dataField.add(1);
        dataField.add(2);
        dataField.add(3);
        dataField.add(4);

        assertEquals(2.5, dataset.getAverage(dataField));
    }
}
