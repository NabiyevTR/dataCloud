package ntr.datacloud.client.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import ntr.datacloud.client.DataCloudClient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@NoArgsConstructor
public class DialogController implements Initializable {

    @FXML
    private Text dialogHeader;
    @FXML
    private HBox inputBox;
    @FXML
    private Label dialogLabel;
    @FXML
    private TextField dialogTextField;



    private Dialog.Type type;
    private String header;
    private String label;

    public DialogController(Dialog.Type type, String header, String label) {
        this.type = type;
        this.header = header;
        this.label = label;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dialogHeader.setText(header);
        dialogLabel.setText(label);

        switch (type) {
            case ALERT:
            case INFORMATION:
                inputBox.setVisible(false);
                break;
            case TEXT_INPUT:
                inputBox.setVisible(true);
                break;
        }
    }


    @FXML
    void btnDialogClicked(ActionEvent event) {
        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public String getText () {
        return  dialogTextField.getText();
    }

}