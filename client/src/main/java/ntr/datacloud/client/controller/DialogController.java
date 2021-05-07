package ntr.datacloud.client.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import ntr.datacloud.client.stage.Dialog;

import java.net.URL;
import java.util.ResourceBundle;

@NoArgsConstructor
public class DialogController implements Initializable {

    @FXML
    private Text dialogText;
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

    public String getText() {
        return dialogTextField.getText();
    }


}