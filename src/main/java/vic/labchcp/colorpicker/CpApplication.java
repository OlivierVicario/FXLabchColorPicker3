package vic.labchcp.colorpicker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CpApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CpApplication.class.getResource("cp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("LABCH Colorpicker 0.0.3");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}