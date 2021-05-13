package ntr.datacloud.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.client.model.ClientProperties;
import ntr.datacloud.client.model.NettyNetwork;
import ntr.datacloud.client.stage.AuthStage;
import ntr.datacloud.client.stage.Dialog;
import ntr.datacloud.client.stage.MainStage;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.service.*;
import java.net.URL;
import java.util.ResourceBundle;


@Log4j
public class AuthController implements Initializable {

    private NettyNetwork network;
    private ClientProperties properties;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private VBox authPanel;

    //Registration pane
    @FXML
    private VBox regPanel;
    @FXML
    private TextField regLogin;
    @FXML
    private PasswordField regPass;
    @FXML
    private PasswordField regPassRepeat;
    @FXML
    private Label regError;

    //Logon pane
    @FXML
    private VBox logonPanel;
    @FXML
    private TextField logonLogin;
    @FXML
    private PasswordField logonPass;
    @FXML
    private Label logonError;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        network = NettyNetwork.getInstance();
        properties = ClientProperties.getInstance();

        network.sendMsg(ConfigMessage.builder()
                .build());

        if (network.isActive()) {
            ConfigMessage message = (ConfigMessage) network.readMessage();
            properties.setMaxFileFrame(message.getMaxFileFrame());
            properties.setLoginRegex(message.getRegexLogin());
            properties.setPassRegex(message.getRegexPass());
        } else {
            alert("Cannot connect to server");
            Platform.exit();
            System.exit(0);
        }
    }


    @FXML
    private void backToLogonPanel(ActionEvent actionEvent) {
        regPanel.setVisible(false);
        logonPanel.setVisible(true);
        resetFields();
    }

    @FXML
    private void toRegPanel(ActionEvent actionEvent) {
        regPanel.setVisible(true);
        logonPanel.setVisible(false);
        resetFields();
    }

    @FXML
    private void regUser(ActionEvent actionEvent) {
        String login = regLogin.getText().toLowerCase().trim();
        String password = regPass.getText().trim();
        String passwordRepeat = regPassRepeat.getText().trim();

        if (!password.equals(passwordRepeat)) {
            regError.setText("Password mismatch");
            regError.setVisible(true);
            return;
        }

        if (!regLogin.getText().matches(properties.getLoginRegex())) {
            regError.setText("Not valid login");
            regError.setVisible(true);
            return;
        }

        if (!regPass.getText().matches(properties.getPassRegex())) {
            regError.setText("Not valid password");
            regError.setVisible(true);
            return;
        }

        if (network.sendMsg(RegMessage
                .builder()
                .login(login)
                .password(password)
                .build())) {

            AuthMessage message = (AuthMessage) network.readMessage();
            handleAuthMessage(message, "Registration completed successfully");

        } else {
            showError("Cannot connect to server");
            network.terminate();
        }
    }

    @FXML
    private void logonUser(ActionEvent actionEvent) {

        if (logonLogin.getText().isEmpty() && logonPass.getText().isEmpty()) {
            logonError.setText("Login and pass field are empty");
            logonError.setVisible(true);
            return;
        }

        if (logonLogin.getText().isEmpty()) {
            logonError.setText("Login field is empty");
            logonError.setVisible(true);
            return;
        }

        if (logonPass.getText().isEmpty()) {
            logonError.setText("Pass field is empty");
            logonError.setVisible(true);
            return;
        }

        String login = logonLogin.getText().toLowerCase().trim();
        String password = logonPass.getText().trim();

        if (network.sendMsg(LogonMessage
                .builder()
                .login(login)
                .password(password)
                .build())) {
            Message message = network.readMessage();
            handleAuthMessage(message);
        } else {
            showError("Cannot connect to server");
            network.terminate();
        }
    }

    @FXML
    private void onExitClicked(MouseEvent mouseEvent) {
        exit();
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        Stage stage = (Stage) authPanel.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void goToMainStage() {
        resetFields();
        MainStage.getStage().show();
        AuthStage.getStage().hide();
    }

    private void exit() {
        AuthStage.getStage().close();
        network.terminate();
        Platform.exit();
        System.exit(0);
    }

    private void resetFields() {
        regPass.setText("");
        regLogin.setText("");
        regPassRepeat.setText("");
        logonLogin.setText("");
        logonPass.setText("");
        hideError();
    }

    private void handleAuthMessage(Message message) {
        handleAuthMessage(message, null);
    }

    private void handleAuthMessage(Message message, String text) {

        if (message instanceof AuthMessage) {
            AuthMessage authMessage = (AuthMessage) message;

            if (authMessage.getStatus() == ServiceMessageStatus.OK) {

                // Save credentials after successful authentication in client properties
                properties.setLogin(authMessage.getLogin());
                properties.setPassword(authMessage.getPassword());

                if (text != null) {
                    new Dialog(
                            (Stage) authPanel.getScene().getWindow(),
                            Dialog.Type.INFORMATION,
                            text
                    );
                }

                // Clear all fields
                resetFields();
                goToMainStage();

            } else {
                showError(authMessage.getStatus().getStatusText());
            }
        } else {
            showError(message.getErrorText());
        }
    }

    private void showError(String errorText) {
        logonError.setText(errorText);
        logonError.setVisible(true);
        regError.setText(errorText);
        regError.setVisible(true);
    }

    private void hideError() {
        logonError.setText("");
        logonError.setVisible(false);
        regError.setText("");
        regError.setVisible(false);
    }

    private void alert(String contentText) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("DataCloud");
        alert.setHeaderText("Results:");
        alert.setContentText(contentText);


        DialogPane dialogPane = alert.getDialogPane();
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image("/images/cloud.png"));

        alert.showAndWait();
    }
}
