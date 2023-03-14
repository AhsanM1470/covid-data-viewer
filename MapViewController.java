import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Polygon;
import javafx.fxml.Initializable;

public class MapViewController{

    @FXML
    private Polygon barnetPolygon;

    @FXML
    private BorderPane bp;

    @FXML
    private Polygon enfieldPolygon;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private Polygon harrowPolygon;

    @FXML
    private AnchorPane map;

    @FXML
    private Polygon redbridgePolygon;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Polygon walthamPolygon;

    @FXML
    void dateChanged(ActionEvent event) {

    }

    @FXML
    void getData(MouseEvent event) {

    }

    @FXML
    void polygonClicked(MouseEvent event){
        System.out.println(event.getSource());
    }

}
