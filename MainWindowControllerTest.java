import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class MainWindowControllerTest {
    
    private MainWindowController mainController;

    @BeforeEach
    public void setUp() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        loader.load();
        mainController = loader.getController();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mainController = null;
    }

    @Test
    public void testInitialize() throws Exception {
        // Accessing the private fields using reflection
        Field dataField = MainWindowController.class.getDeclaredField("data");
        Field controllersField = MainWindowController.class.getDeclaredField("controllers");
        Field controllerIndexField = MainWindowController.class.getDeclaredField("controllerIndex");
        
        // Allow access to private methods
        dataField.setAccessible(true);
        controllersField.setAccessible(true);
        controllerIndexField.setAccessible(true);
        
        // Check the fields have been initialised with objects
        assertNotNull(dataField);
        assertNotNull(controllersField);
        
        // Check the controller index starts at 0
        assertEquals(0, ((int) controllerIndexField.get(mainController)));
    }

    @Test
    public void testLoadControllers() throws Exception {
        Field controllersField = MainWindowController.class.getDeclaredField("controllers");
        controllersField.setAccessible(true);
        ViewerController[] controllers = (ViewerController[]) controllersField.get(mainController);
        
        // Check that all the controllers have popualated in controllers array
        assertEquals(controllers.length, 4);
        assertNotNull(controllers[0]);
        assertNotNull(controllers[1]);
        assertNotNull(controllers[2]);
        assertNotNull(controllers[3]);
    }

    @Test
    public void testAllowPanelSwitching() throws Exception {
        Field leftButtonField = MainWindowController.class.getDeclaredField("leftButton");
        Field rightButtonField = MainWindowController.class.getDeclaredField("rightButton");
        
        // Accessing the private method using reflection
        Method allowPanelSwitchingMethod = MainWindowController.class.getDeclaredMethod("allowPanelSwitching", boolean.class);
        allowPanelSwitchingMethod.setAccessible(true);
        
        leftButtonField.setAccessible(true);
        rightButtonField.setAccessible(true);
        
        // Check that the buttons are disabled
        assertTrue(((Button) leftButtonField.get(mainController)).isDisable());
        assertTrue(((Button) rightButtonField.get(mainController)).isDisable());
        
        // Call allowPanelSwitching with true
        allowPanelSwitchingMethod.invoke(mainController, true);
        
        // Check that the buttons are now enabled
        assertFalse(((Button) leftButtonField.get(mainController)).isDisable());
        assertFalse(((Button) rightButtonField.get(mainController)).isDisable());
        
        // Call allowPanelSwitching with false
        allowPanelSwitchingMethod.invoke(mainController, false);
        
        // Check that the buttons are now disabled
        assertTrue(((Button) leftButtonField.get(mainController)).isDisable());
        assertTrue(((Button) rightButtonField.get(mainController)).isDisable());
    }
    
    @Test
    public void testNextPanel() throws Exception {
        Field controllerIndexField = MainWindowController.class.getDeclaredField("controllerIndex");
        controllerIndexField.setAccessible(true);
        
        // Ensure that controller index is 0 at start
        assertEquals(0, ((int) controllerIndexField.get(mainController)));

        Method nextPanelMethod = MainWindowController.class.getDeclaredMethod("nextPanel", ActionEvent.class);
        nextPanelMethod.setAccessible(true);
        // Call next panel method
        nextPanelMethod.invoke(mainController, new ActionEvent());
        
        // after nextPanel(), controller index should be 1
        assertEquals(1, ((int) controllerIndexField.get(mainController)));
    }
    
    @Test
    public void testPreviousPanel() throws Exception {
        // Ensure that controller index is 0 at start
        Field controllerIndexField = MainWindowController.class.getDeclaredField("controllerIndex");
        controllerIndexField.setAccessible(true);
        assertEquals(0, ((int) controllerIndexField.get(mainController)));
        
        Method previousPanelMethod = MainWindowController.class.getDeclaredMethod("previousPanel", ActionEvent.class);
        previousPanelMethod.setAccessible(true);
        // Call previous panel method
        previousPanelMethod.invoke(mainController, new ActionEvent());
        
        Field controllersField = MainWindowController.class.getDeclaredField("controllers");
        controllersField.setAccessible(true);
        ViewerController[] controllers = (ViewerController[]) controllersField.get(mainController);
        
        // after previousPanel(), controller index should be the number of controllers -1
        assertEquals(controllers.length - 1, ((int) controllerIndexField.get(mainController)));
    
    }
    
    @Test
    public void testTransitionIntoNextPanel() throws Exception {
        // Access the private method transitionIntoNextPanel
        Method transitionIntoNextPanelMethod = MainWindowController.class.getDeclaredMethod(
            "transitionIntoNextPanel", ViewerController.class, ViewerController.class, ActionEvent.class);
        transitionIntoNextPanelMethod.setAccessible(true);
        
        // Access the private field stackPane
        Field stackPaneField = MainWindowController.class.getDeclaredField("stackPane");
        stackPaneField.setAccessible(true);
        StackPane stackPane = (StackPane) stackPaneField.get(mainController);
        
        Field controllersField = MainWindowController.class.getDeclaredField("controllers");
        controllersField.setAccessible(true);
        ViewerController[] controllers = (ViewerController[]) controllersField.get(mainController);
        
        // Call the method with valid arguments
        ViewerController currentController = controllers[0];
        ViewerController nextController = controllers[1];
        ActionEvent event = new ActionEvent();
        transitionIntoNextPanelMethod.invoke(mainController, currentController, nextController, event);
        
        // Assert that the new controller's view has been added to the stackPane
        assertEquals(2, stackPane.getChildren().size());
        assertEquals(nextController.getView(), stackPane.getChildren().get(1));
    }
}