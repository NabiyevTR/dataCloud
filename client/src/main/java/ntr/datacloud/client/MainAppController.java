package ntr.datacloud.client;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.MessageType;


public class MainAppController  {


    @FXML
    public ListView listView;
    @FXML
    public Button send;
    @FXML
    public TextField text;
    private NettyNetwork network;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;



    /*    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Socket socket = new Socket("localhost", 8189);
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());

        Thread service = new Thread(() -> {
            try {
                while (true) {
                    Message message = (Message) is.readObject();
                    String s = message.getText();
                    Platform.runLater(() -> listView.getItems().add(s));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        service.setDaemon(true);
        service.start();
    }*/

    public void send(ActionEvent actionEvent) throws IOException {

    }


}

