package mbouch.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Page2Controller implements Initializable {
    private Stage stage;

    @FXML
    private Button switchButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        switchButton.setOnAction(e->{
            goToSecondPage(e);
        });
    }

    private void goToSecondPage(ActionEvent e) {
        FXMLLoader loader =new FXMLLoader(getClass().getResource("/mbouch/pages/page1.fxml"));

        Parent root=null;
        try {
            root=loader.load();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        Scene scene=new Scene(root);
        stage= (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }
}
