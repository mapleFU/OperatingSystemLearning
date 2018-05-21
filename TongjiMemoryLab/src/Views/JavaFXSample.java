package Views;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * Life style
 *
 */
public class JavaFXSample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        //Drawing a Rectangle
        Rectangle rectangle = new Rectangle(50, 50, 100, 75);

        //Setting the color of the rectangle
        rectangle.setFill(Color.BURLYWOOD);

        //Setting the stroke color of the rectangle
        rectangle.setStroke(Color.BLACK);

        //creating the rotation transformation
        Rotate rotate = new Rotate();

        //Setting the angle for the rotation
        rotate.setAngle(20);

        //Setting pivot points for the rotation
        rotate.setPivotX(150);
        rotate.setPivotY(225);

        //Creating the scale transformation
        Scale scale = new Scale();

        //Setting the dimensions for the transformation
        scale.setX(1.5);
        scale.setY(1.5);

        //Setting the pivot point for the transformation
        scale.setPivotX(300);
        scale.setPivotY(135);

        //Creating the translation transformation
        Translate translate = new Translate();

        //Setting the X,Y,Z coordinates to apply the translation
        translate.setX(250);
        translate.setY(0);
        translate.setZ(0);

        //Adding all the transformations to the rectangle
        rectangle.getTransforms().addAll(rotate, scale, translate);

        //Creating a Group object
        Group root = new Group(rectangle);

        //Creating a scene object
        Scene scene = new Scene(root, 600, 300);

        //Setting title to the Stage
        stage.setTitle("Multiple transformations");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }
}
