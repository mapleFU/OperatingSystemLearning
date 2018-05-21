package Views;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class Registration extends Application {
    @Override
    public void start(Stage stage) {
        //Label for name
        Text nameLabel = new Text("Name");

        //Text field for name
        TextField nameText = new TextField();

        //Label for date of birth
        Text dobLabel = new Text("Date of birth");

        //date picker to choose date
        DatePicker datePicker = new DatePicker();

        //Label for gender
        Text genderLabel = new Text("gender");

        //Toggle group of radio buttons
        ToggleGroup groupGender = new ToggleGroup();
        RadioButton maleRadio = new RadioButton("male");
        maleRadio.setToggleGroup(groupGender);
        RadioButton femaleRadio = new RadioButton("female");
        femaleRadio.setToggleGroup(groupGender);

        //Label for reservation
        Text reservationLabel = new Text("Reservation");

        //Toggle button for reservation
        ToggleButton Reservation = new ToggleButton();
        ToggleButton yes = new ToggleButton("Yes");
        ToggleButton no = new ToggleButton("No");
        ToggleGroup groupReservation = new ToggleGroup();
        yes.setToggleGroup(groupReservation);
        no.setToggleGroup(groupReservation);

        //Label for technologies known
        Text technologiesLabel = new Text("Technologies Known");

        //check box for education
        CheckBox javaCheckBox = new CheckBox("Java");
        javaCheckBox.setIndeterminate(false);

        //check box for education
        CheckBox dotnetCheckBox = new CheckBox("DotNet");
        javaCheckBox.setIndeterminate(false);

        //Label for education
        Text educationLabel = new Text("Educational qualification");

        //list View for educational qualification
        ObservableList<String> names = FXCollections.observableArrayList(
                "Engineering", "MCA", "MBA", "Graduation", "MTECH", "Mphil", "Phd");
        ListView<String> educationListView = new ListView<String>(names);

        //Label for location
        Text locationLabel = new Text("location");

        //Choice box for location
        ChoiceBox locationchoiceBox = new ChoiceBox();
        locationchoiceBox.getItems().addAll
                ("Hyderabad", "Chennai", "Delhi", "Mumbai", "Vishakhapatnam");

        //Label for register
        Button buttonRegister = new Button("Register");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(500, 500);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameText, 1, 0);

        gridPane.add(dobLabel, 0, 1);
        gridPane.add(datePicker, 1, 1);

        gridPane.add(genderLabel, 0, 2);
        gridPane.add(maleRadio, 1, 2);
        gridPane.add(femaleRadio, 2, 2);
        gridPane.add(reservationLabel, 0, 3);
        gridPane.add(yes, 1, 3);
        gridPane.add(no, 2, 3);

        gridPane.add(technologiesLabel, 0, 4);
        gridPane.add(javaCheckBox, 1, 4);
        gridPane.add(dotnetCheckBox, 2, 4);

        gridPane.add(educationLabel, 0, 5);
        gridPane.add(educationListView, 1, 5);

        gridPane.add(locationLabel, 0, 6);
        gridPane.add(locationchoiceBox, 1, 6);

        gridPane.add(buttonRegister, 2, 8);

        //Styling nodes
        buttonRegister.setStyle(
                "-fx-background-color: darkslateblue; -fx-textfill: white;");

        nameLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        dobLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        genderLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        reservationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        technologiesLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        educationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        locationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");

        //Setting the back ground color
        gridPane.setStyle("-fx-background-color: BEIGE;");

        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        stage.setTitle("Registration Form");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }
    public static void main(String args[]){
        launch(args);
    }
}