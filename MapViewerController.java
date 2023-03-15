import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.HashMap;

public class MapViewerController {

    Paint hoveredPolygonDefaultBorderColor;
    Double hoveredPolygonDefaultStroke;

    @FXML
    private AnchorPane mapPane;

    @FXML
    private BorderPane bp;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private Label selectedBoroughLabel;

    @FXML
    private Polygon brentPolygon, bexleyPolygon, bromleyPolygon, camdenPolygon, cityPolygon, croydonPolygon,
            ealingPolygon, enfieldPolygon, greenwichPolygon, hackneyPolygon, hamletsPolygon, hammfullPolygon,
            haringeyPolygon, harrowPolygon, haveringPolygon, hillingdonPolygon, hounslowPolygon, islingtonPolygon,
            kensChelsPolygon, kingstonPolygon, lambethPolygon, lewishamPolygon, mertonPolygon, newhamPolygon,
            redbridgePolygon, richmondPolygon, southwarkPolygon, suttonPolygon, thamesPolygon, walthamPolygon,
            wandsworthPolygon, westminsterPolygon;

    @FXML
    private Label title, hillingdonLabel;


    private HashMap<String, String> boroughIdToName;
    // private HashMap<String,Boolean> boroughVisited = new HashMap<>();

    @FXML
    void dateChanged(ActionEvent event) {

    }

    @FXML
    void polygonClicked(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();
        String name = boroughIdToName.get(poly.getId());
        System.out.println(name);
        
    }

    @FXML
    void polygonHovered(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();

        // change label text
        title.setAlignment(Pos.CENTER);
        String name = boroughIdToName.get(poly.getId());
        setLabelText(selectedBoroughLabel, name, 15.0);

        // change properties to indicate hovered borough
        hoveredPolygonDefaultBorderColor = poly.getStroke();
        poly.setStrokeWidth(3);
        poly.setStroke(new Color(0.2,0.2,0.8,1.0));
        
    }

    @FXML
    void polygonLeft(MouseEvent event) {
        Polygon poly = (Polygon) event.getSource();
        poly.setStrokeWidth(1);
        poly.setStroke(hoveredPolygonDefaultBorderColor);

        // remove text if no borough is selected
        setLabelText(selectedBoroughLabel, null, 0);
    }

    @FXML
    void initialize() {
        setLabelText(title, "Current Borough Selected:", 15.0);
        // load the mapping of polygon IDs to their respective borough names
        JsonReader jsonReader = new JsonReader();
        boroughIdToName = jsonReader.readJson("boroughIds.json");

    }

    /**
     * 
     * @param label label component to be customised
     * @param text text to be displayed on the label
     * @param fontSize size of the text
     */
    private void setLabelText(Label label, String text, double fontSize){
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font(fontSize));
    }

}

