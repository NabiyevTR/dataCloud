package ntr.datacloud.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.client.DataCloudClient;
import ntr.datacloud.client.model.ClientProperties;
import ntr.datacloud.client.model.NettyNetwork;
import ntr.datacloud.common.messages.service.LogonMessage;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.service.RegMessage;
import ntr.datacloud.common.messages.service.ServiceMessageStatus;


@Log4j
public class AuthController  {


    private NettyNetwork network = NettyNetwork.getInstance();
    private ClientProperties properties = ClientProperties.getInstance();
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


    public void backToLogonPanel(ActionEvent actionEvent) {
        regPanel.setVisible(false);
        logonPanel.setVisible(true);
    }

    public void toRegPanel(ActionEvent actionEvent) {
        regPanel.setVisible(true);
        logonPanel.setVisible(false);
    }

    public void regUser(ActionEvent actionEvent) {
        String login = regLogin.getText().toLowerCase().trim();
        String password = regPass.getText().trim();
        String passwordRepeat = regPassRepeat.getText().trim();

        //toDo check credentials

        network.sendMsg(RegMessage
                .builder()
                .login(login)
                .password(password)
                .build());

        //todo set timeout
        Message message = network.readMessage();
        if (message instanceof RegMessage) {

            RegMessage regMessage = (RegMessage) message;

            if (regMessage.getStatus() == ServiceMessageStatus.OK) {
                regError.setVisible(false);

                //duplicated code (in Logon user)
                // Save credentials after successful authentication in client properties
                properties.setLogin(message.getLogin());
                properties.setPassword(message.getPassword());

                goToMainAppWindow(actionEvent);

            } else {
                regError.setText(regMessage.getStatus().getStatusText());
                regError.setVisible(true);
            }
        }
    }

    public void logonUser(ActionEvent actionEvent) {
        String login = logonLogin.getText().toLowerCase().trim();
        String password = logonPass.getText().trim();

        //toDo check credentials

        network.sendMsg(LogonMessage
                .builder()
                .login(login)
                .password(password)
                .build());

        //todo set timeout
        Message message = network.readMessage();

        if (message instanceof LogonMessage) {
            LogonMessage logonMessage = (LogonMessage) message;
            if (logonMessage.getStatus() == ServiceMessageStatus.OK) {
                logonError.setVisible(false);

                // Save credentials after successful authentication in client properties
                properties.setLogin(message.getLogin());
                properties.setPassword(message.getPassword());

                goToMainAppWindow(actionEvent);

            } else {
                logonError.setText(logonMessage.getStatus().getStatusText());
                logonError.setVisible(true);
            }
        }


    }

    private void goToMainAppWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(
                    DataCloudClient.class.getResource("primary.fxml"));
            VBox root = loader.load();

            Stage authStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            authStage.hide();

            Stage mainStage = new Stage();
            Scene mainAppScene = new Scene(root, 1000, 600);

            mainStage.setResizable(true);
            mainStage.getIcons().add(new Image("/images/cloud.png"));
            mainStage.setScene(mainAppScene);

            mainStage.show();
        } catch (Exception e) {
            log.error("Error during changing window: ", e);
        }


    }

    public void onExitClicked(MouseEvent mouseEvent) {
        System.exit(0);
    }


    public void onMouseDragged(MouseEvent event) {
        Stage  stage = (Stage)authPanel.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
        }


    public void onMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
}
