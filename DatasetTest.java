import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The test class DatasetTest.
 *
 * @author  Ishab Ahmed
 * @version 2023.03.24
 */
public class DatasetTest
{
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
        assertNotNull(dataset);
    }
    
    /**
     * Tests whether the getData() method returns the expected list of CovidData objects from the dataset.
     */
    @Test
    public void testGetData() {
        assertEquals(data, dataset.getData());
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

}
