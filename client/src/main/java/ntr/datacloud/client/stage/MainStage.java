package ntr.datacloud.client.stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ntr.datacloud.client.DataCloudClient;

import java.io.IOException;


public class MainStage extends Stage {

    private static MainStage INSTANCE;

    public static MainStage getStage()  {
        if (INSTANCE == null) {
            INSTANCE = new MainStage();
        }
        return INSTANCE;
    }

    @SneakyThrows
    private MainStage() {
        FXMLLoader fxmlLoader = new FXMLLoader(DataCloudClient.class.getResource("primary.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        setScene(scene);
        getIcons().add(new Image("/images/cloud.png"));
        setTitle("DataCloud");
    }

    @Override
    public void close() {
        super.close();
        INSTANCE =null;
    }
}
