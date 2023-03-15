import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.net.URL;

/**
 * GUI to display.
 *
 * @author Ishab Ahmed, Saihan Marshall
 * @version 2023.03.13
 */
public class DataViewer extends Application
{
    public static void main(String[] args){
        launch(DataViewer.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL url = getClass().getResource("MapWindow.fxml");
        
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        
        stage.setTitle("Covid Data");
        stage.setScene(scene);
        stage.show();
    }

}
