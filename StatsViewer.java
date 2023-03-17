import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.util.ArrayList;
import java.net.URL;

/**
 * This is the GUI for the window that
 *  shows statistics information.
 *
 * @author (Saihan Marshall)
 * @version (13/03/23)
 */
public class StatsViewer extends Application
{
    public static void main(String[] args){
        launch(StatsViewer.class);
    }

    private ArrayList<CovidData> data;
    
    @Override
    public void init() throws Exception {
        CovidDataLoader dataLoader = new CovidDataLoader();
        data = dataLoader.load();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        URL url = getClass().getResource("StatsWindow.fxml");
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        
        stage.setTitle("Covid Data");
        stage.setScene(scene);
        stage.show();
        
        
        
    }
    
    
    

}
