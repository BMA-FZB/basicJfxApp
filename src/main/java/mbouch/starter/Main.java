//Main Class
package mbouch.starter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mbouch.database.DataBase;

public class Main extends Application {
    @Override
    public void start(Stage stage)  {
        FXMLLoader loader =new FXMLLoader();
        loader.setLocation(getClass().getResource("/mbouch/pages/page1.fxml"));
        Parent root=null;
        try {
            DataBase db=DataBase.getInstance();
            root=loader.load();
            Scene scene=new Scene(root);
            stage.setScene(scene);
            stage.show();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
