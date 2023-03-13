import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.util.ArrayList;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * GUI to display.
 *
 * @author Ishab Ahmed, Saihan Marshall
 * @version 2023.03.13
 */
public class DataViewer extends Application
{
    private ArrayList<CovidData> data;
    
    @Override
    public void init() throws Exception {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        URL url = getClass().getResource("MainWindow.fxml");
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        
        stage.setTitle("Covid Data");
        stage.setScene(scene);
        stage.show();
    }

}
