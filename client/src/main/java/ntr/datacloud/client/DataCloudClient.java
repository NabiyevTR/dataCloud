package ntr.datacloud.client;

import javafx.application.Application;
import javafx.stage.Stage;
import ntr.datacloud.client.stage.AuthStage;

import java.io.IOException;

public class DataCloudClient extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AuthStage.getStage().show();
    }
}