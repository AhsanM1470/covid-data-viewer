import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.net.URL;

/**
 * Responsible for displaying the MainWindow GUI
 *
 * @author Ishab Ahmed, Saihan Marshall
 * @version 2023.03.13
 */
public class DataViewer extends Application
{

    @Override
    public void start(Stage stage) throws Exception {
        URL url = getClass().getResource("MainWindow.fxml");
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        
        stage.setTitle("Covid Data");

        // TODO: remove and show what the lads what it would look like without these constraints when window is resized too small
        // don't allow window to be resized smaller than this
        stage.setMinWidth(920);
        stage.setMinHeight(650 + 35); // + 30px for the top bar of OS windows.
        
        stage.setScene(scene);
        stage.show();
    }

}
