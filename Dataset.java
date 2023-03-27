import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.function.Function;

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
    
    private String[] boroughs = {"Barking And Dagenham", "Barnet", "Bexley", "Brent", "Bromley", "Camden",
        "City Of London", "Croydon", "Ealing", "Enfield", "Greenwich", "Hackney", "Hammersmith And Fulham", "Haringey",
        "Harrow", "Havering", "Hillingdon", "Hounslow", "Islington", "Kensington And Chelsea", "Kingston Upon Thames",
        "Lambeth", "Lewisham", "Merton", "Newham", "Redbridge", "Richmond Upon Thames", "Southwark",
        "Sutton", "Tower Hamlets", "Waltham Forest", "Wandsworth", "Westminster"};
    
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
    
    // -------------------------------- Retrieving data -------------------------------- //
    
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
     * Returns an list of the most recent CovidData records for each borough, filtered by the given function.
     * 
     * @param covidData the ArrayList of CovidData records to be filtered
     * @param filterFunc the function to use as a filter (takes an argument of type CovidData and returns an Integer)
     * @return an ArrayList of the most recent CovidData records for each borough that pass the given filter
     */
    public ArrayList<CovidData> getMostRecentDataWithFilter(ArrayList<CovidData> covidData, Function<CovidData, Integer> filterFunc) {
        List<CovidData> result = new ArrayList<>();
    
        for (String boroughName : boroughs) {
            // Data is already sorted, so iterates from newest to oldest data
            for (CovidData record : covidData) {
                // Ensures that the result function being used is not null nor 0
                if (record.getBorough().equals(boroughName) &&
                        filterFunc.apply(record) != null) {
                    // Newest, non-null and non-zero record on filter function found
                    result.add(record);
                    break;
                }
            }
        }
    
        return new ArrayList<>(result);
    }
    
    /**
     * @return Array of strings representing the names of all boroughs in the dataset.
     */
    public String[] getBoroughs() {
        return boroughs;
    }
    
    // -------------------------------- Date validation -------------------------------- //
        
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
    
    // -------------------------------- Statistics calculations -------------------------------- //
    
    /**
     * Calculates the average of a list of numbers by adding up all the non-null values 
     * and dividing by the number of non-null values.
     * 
     * @param field List of numbers to calculate the average of
     * @return the average of the non-null values in the list (to 2 d.p.)
     */
    public double getAverage(List<Number> field) {
        double sum = 0;
        double average = 0;
        int count = 0;  // count of non-null values
        
        for (Number value : field) {
            // To stop the many null values in dataset
            if (value != null) {
                sum += value.doubleValue();
                count++;
            }
        }
        
        // Check to ensure no division by zero error
        if (count > 0) {
            average = sum / count;
            // Rounds to 2 decimal places
            average = Math.round(average * 100.0) / 100.0;
        }
        
        return average;
    }
}
