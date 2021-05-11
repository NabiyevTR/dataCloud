package ntr.datacloud.client.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import ntr.datacloud.client.stage.Dialog;

import java.net.URL;
import java.util.ResourceBundle;

import static ntr.datacloud.client.stage.Dialog.Type.TEXT_INPUT;

@NoArgsConstructor
public class DialogController implements Initializable {

    @FXML
    public Label dialogText;
    @FXML
    private HBox inputBox;
    @FXML
    private TextField dialogTextField;


    private Dialog.Type type;
    private String text;
    private String placeHolder;


    public DialogController(Dialog.Type type, String text, String placeHolder) {
        this.type = type;
        this.text = text;
        this.placeHolder = placeHolder;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dialogText.setText(text);
        dialogTextField.setText(placeHolder);

        dialogTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                closeStage();
            }
        });

        switch (type) {
            case ALERT:
                dialogText.setStyle("-fx-text-fill: #b13232;");
                dialogTextField.setVisible(false);
                dialogTextField.setManaged(false);
                break;
            case INFORMATION:
                dialogText.setStyle("-fx-text-fill: #1a1c2b;");
                dialogTextField.setVisible(false);
                dialogTextField.setManaged(false);
                break;
            case TEXT_INPUT:
                dialogText.setStyle(" -fx-text-fill: #1a1c2b;");
                dialogTextField.setManaged(true);
                dialogTextField.setVisible(true);
                dialogTextField.requestFocus();
                break;
        }
    }

    @FXML
    private void btnDialogClicked(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        if (type == TEXT_INPUT && dialogTextField.getText().isEmpty()) {
            return;
        }
        Stage stage = (Stage)dialogTextField.getScene().getWindow();
        stage.close();
    }

    public String getText() {
        return dialogTextField.getText();
    }

}