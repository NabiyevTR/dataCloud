package ntr.datacloud.client.stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ntr.datacloud.client.DataCloudClient;

import java.io.IOException;


public class AuthStage extends Stage {

    private static AuthStage INSTANCE;

    public static AuthStage getStage() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new AuthStage();
        }
        return INSTANCE;
    }

    protected AuthStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DataCloudClient.class.getResource("auth.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        setScene(scene);
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);
            }

}
