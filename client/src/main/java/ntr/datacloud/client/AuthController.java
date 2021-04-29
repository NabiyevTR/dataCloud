package ntr.datacloud.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ntr.datacloud.common.messages.LogonMessage;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.RegMessage;

public class AuthController {


    private NettyNetwork network = NettyNetwork.getInstance();


    @FXML
    private VBox regPanel;
    @FXML
    private TextField regLogin;
    @FXML
    private PasswordField regPass;
    @FXML
    private PasswordField regPassRepeat;


    @FXML
    private VBox logonPanel;
    @FXML
    private TextField logonLogin;
    @FXML
    private PasswordField logonPass;


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

        //todo get response
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

//todo get response
    }
}
