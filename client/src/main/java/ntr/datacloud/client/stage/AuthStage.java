package ntr.datacloud.client.stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import ntr.datacloud.client.DataCloudClient;

import java.io.IOException;


public class AuthStage extends Stage {

    private static AuthStage INSTANCE;

    public static AuthStage getStage()  {
        if (INSTANCE == null) {
            INSTANCE = new AuthStage();
        }
        return INSTANCE;
    }

    @SneakyThrows
    protected AuthStage() {
        FXMLLoader fxmlLoader = new FXMLLoader(DataCloudClient.class.getResource("auth.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        setScene(scene);
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);
        getIcons().add(new Image("/images/cloud.png"));
        setTitle("DataCloud");
    }

}
