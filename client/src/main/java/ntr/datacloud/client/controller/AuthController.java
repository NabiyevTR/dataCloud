package ntr.datacloud.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.client.model.ClientProperties;
import ntr.datacloud.client.model.NettyNetwork;
import ntr.datacloud.client.stage.AuthStage;
import ntr.datacloud.client.stage.MainStage;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.service.*;

import java.io.IOException;


@Log4j
public class AuthController {

    private final NettyNetwork network = NettyNetwork.getInstance();
    private final ClientProperties properties = ClientProperties.getInstance();
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private VBox authPanel;

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


    @FXML
    private VBox logonPanel;
    @FXML
    private TextField logonLogin;
    @FXML
    private PasswordField logonPass;
    @FXML
    private Label logonError;

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

        String regularExpressionLogin = "^[a-z0-9_-]{3,16}$";

        if (!regLogin.getText().matches(regularExpressionLogin)) {
            regError.setText("Not valid login");
            regError.setVisible(true);
            return;
        }

        String regularExpressionPass = "^[a-z0-9_-]{6,16}$";

        if (!regPass.getText().matches(regularExpressionPass)) {
            regError.setText("Not valid login");
            regError.setVisible(true);
            return;
        }

        if (network.sendMsg(RegMessage
                .builder()
                .login(login)
                .password(password)
                .build())) {

            AuthMessage message = (AuthMessage) network.readMessage();
            handleAuthMessage(message);

        } else {
            showError("Cannot connect to server");
            network.terminate();
        }


    }

    @FXML
    private void logonUser(ActionEvent actionEvent) {


        if (logonLogin.getText().isEmpty() && logonPass.getText().isEmpty()) {
            logonError.setText("Login and pass field is empty");
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

    public void onExitClicked(MouseEvent mouseEvent) {
        try {
            AuthStage.getStage().close();
        } catch (IOException e) {
            log.error("Cannot close AuthStage: ", e);
        }
        try {
            MainStage.getStage().close();
        } catch (IOException e) {
            log.error("Cannot close MainStage: ", e);
        }
        System.exit(0);
    }

    public void onMouseDragged(MouseEvent event) {
        Stage stage = (Stage) authPanel.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }


    public void onMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }


    private void goToMainStage() {
        logonLogin.setText("");
        logonPass.setText("");
        regLogin.setText("");
        regPass.setText("");
        regPass.setText("");
        regPanel.setVisible(false);
        authPanel.setVisible(true);

        try {
            MainStage.getStage().show();
            AuthStage.getStage().hide();
        } catch (Exception e) {
            log.error("Error during changing window: ", e);
        }
    }


    private void resetFields() {
        regError.setVisible(false);
        regPass.setText("");
        regLogin.setText("");
        regPassRepeat.setText("");

        logonError.setVisible(false);
        logonLogin.setText("");
        logonPass.setText("");

    }

    private void handleAuthMessage(Message message) {

        if (message instanceof AuthMessage) {
            AuthMessage authMessage = (AuthMessage) message;

            if (authMessage.getStatus() == ServiceMessageStatus.OK) {

                // Save credentials after successful authentication in client properties
                properties.setLogin(authMessage.getLogin());
                properties.setPassword(authMessage.getPassword());

                // Get settings from server
                getSettingsFromServer();

                // Clear all fields
                resetFields();
                goToMainStage();

            } else {
                // show error
                showError(authMessage.getErrorText());
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

    private void getSettingsFromServer() {
        network.sendMsg(ConfigMessage.builder()
                .build());

        ConfigMessage message = (ConfigMessage) network.readMessage();
        properties.setMaxFileFrame(message.getMaxFileFrame());

    }

}
