import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * Represents the data set that is loaded from a CovidDataLoader and processed. 
 * Implemented as a singleton, ensuring that only one instance of the class exists,
 * that all controllers have global access to.
 *
 * @author Ishab Ahmed
 * @version 2022.03.24
 */
public class Dataset {
    // singleton instance of the Dataset
    private static Dataset instance = null;
    
    // data to be served
    private ArrayList<CovidData> data;
    
    /**
     * Initialises its data field by loading the CovidData.
     * Private constructor used to prevent instantiation outside of this class.
     */
    private Dataset() {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();
        Collections.sort(data);
        Collections.reverse(data);
    }
    
    /**
     * Returns the instance of the singleton Dataset object.
     * 
     * @return the Dataset instance
     */
    public static synchronized Dataset getInstance() {
        // Create an instance of Dataset, if it does not exist yet
        if (instance == null) {
            instance = new Dataset();
        }
        
        return instance;
    }
    
    /**
     * Returns a list of all CovidData objects in the dataset.
     *
     * @return ArrayList containing all CovidData objects in the dataset
     */
    public ArrayList<CovidData> getData() {
        return data;
    }
    
    /**
     * Returns a list of CovidData objects that fall within the specified date range.
     *
     * @param fromDate The start date of the date range (inclusive)
     * @param toDate The end date of the date range (inclusive)
     * @return ArrayList of CovidData objects that fall within the specified date range
     */
    public ArrayList<CovidData> getDataInDateRange(LocalDate fromDate, LocalDate toDate) {
        List<CovidData> filteredData = data.stream()
            .filter((covidData) -> 
                isDateInRange(LocalDate.parse(covidData.getDate()), fromDate, toDate))
            .collect(Collectors.toList());
            
        return new ArrayList<>(filteredData);
    }
    
    /**
     * Returns a list of CovidData objects that fall within the specified date range
     * for a specified borough.
     * 
     * @param boroughName The name of the borough to filter CovidData objects by
     * @param fromDate The start date of the date range (inclusive)
     * @param toDate The end date of the date range (inclusive)
     * @return ArrayList of CovidData objects for the specified borough and date range
     */
    public ArrayList<CovidData> getBoroughData(String boroughName, LocalDate fromDate, LocalDate toDate) {
        List<CovidData> filteredData = getDataInDateRange(fromDate, toDate).stream()
            .filter(covidData -> covidData.getBorough().equals(boroughName))
            .collect(Collectors.toList());
        
        return new ArrayList<>(filteredData);
    }
    
    /**
     * Checks if a given date falls within a specified date range.
     *
     * @param date The date to check
     * @param fromDate The start date of the date range (inclusive)
     * @param toDate The end date of the date range (inclusive)
     * @return true if the date falls within the specified date range, false otherwise
     */
    public boolean isDateInRange(LocalDate date, LocalDate fromDate, LocalDate toDate) {
        return date.isEqual(fromDate) || date.isEqual(toDate) || (date.isAfter(fromDate) && date.isBefore(toDate));
    }
    
    /**
     * Checks if a given date range is valid.
     * 
     * @param from The start date of the range.
     * @param to   The end date of the range.
     * @return true if the range is valid (i.e. from is before or equal to to),
     *         false otherwise
     */
    public boolean isDateRangeValid(LocalDate fromDate, LocalDate toDate) {
        // if any of them are null, date range is automatically invalid
        if (fromDate == null || toDate == null) {
            return false;
        }
        return fromDate.isBefore(toDate) || fromDate.isEqual(toDate);
    }
}
