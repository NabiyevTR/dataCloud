package ntr.datacloud.client.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ntr.datacloud.client.DataCloudClient;

public class Dialog {
    private Stage primaryStage;
    private Stage stage;
    protected Type type;
    private String headerText;
    private String labelText;
    private DialogController dialogController;


    public enum Type {
        ALERT,
        INFORMATION,
        TEXT_INPUT
    }

    @SneakyThrows
    public Dialog(Stage primaryStage, Type type, String headerText, String labelText) {
        this.primaryStage = primaryStage;
        this.type = type;
        this.headerText = headerText;
        this.labelText = labelText;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(
                DataCloudClient.class.getResource("dialog.fxml"));

        dialogController = new DialogController(type, headerText, labelText);
        loader.setController(dialogController);
        VBox root = loader.load();
        Scene scene = new Scene(root, 320, 150);
        stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        stage.setX(primaryStage.getX() + 200);
        stage.setY(primaryStage.getY() + 100);

        stage.showAndWait();
    }

    public String getText() {
        return dialogController.getText();
    }


}
