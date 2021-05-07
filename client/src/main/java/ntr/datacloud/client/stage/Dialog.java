package ntr.datacloud.client.stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ntr.datacloud.client.DataCloudClient;
import ntr.datacloud.client.controller.DialogController;

public class Dialog {
    private Stage primaryStage;
    private Stage stage;
    protected Type type;
    private String headerText;
    private String placeHolder;
    private DialogController dialogController;


    public enum Type {
        ALERT,
        INFORMATION,
        TEXT_INPUT
    }

    @SneakyThrows
    public Dialog(Stage primaryStage, Type type, String headerText, String placeHolder) {
        this.primaryStage = primaryStage;
        this.type = type;
        this.headerText = headerText;
        this.placeHolder = placeHolder;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(
                DataCloudClient.class.getResource("dialog.fxml"));

        dialogController = new DialogController(type, headerText, placeHolder);
        loader.setController(dialogController);
        VBox root = loader.load();
        Scene scene = new Scene(root, 320, 150);
        stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        stage.initOwner(primaryStage);
        stage.getIcons().add(new Image("/images/cloud.png"));
        stage.setTitle("DataCloud");

        // Set position of second window, related to primary window.
        stage.setX(primaryStage.getX() + 200);
        stage.setY(primaryStage.getY() + 100);

        stage.showAndWait();
    }

    public String getText() {
        return dialogController.getText();
    }


}
