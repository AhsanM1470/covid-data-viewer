import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
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
    public void start(Stage stage) throws IOException {
        // load the first scene
        URL url = getClass().getResource(Controller.scenes[0]);
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        
        stage.setTitle("Covid Data");
        stage.setScene(scene);
        stage.show();
    }

}
