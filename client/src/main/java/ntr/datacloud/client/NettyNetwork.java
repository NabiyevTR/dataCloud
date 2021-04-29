package ntr.datacloud.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.Message;

import java.io.IOException;
import java.net.Socket;

@Log4j
public class NettyNetwork {

    private static NettyNetwork INSTANCE;

    private Socket socket;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    //todo configure somewhere



    private String host = "localhost";
    private int port = 8189;


    public static NettyNetwork getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NettyNetwork();
        }
        return INSTANCE;
    }

    private NettyNetwork() {
        try {
            socket = new Socket(host, port);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
            log.info(
                    String.format("Client has connected to server %s/%d successfully", host, port)
            );
        } catch (Exception e) {
            INSTANCE = null;
            log.error(
                    String.format("Cannot connect to server %s/%d: ", host, port), e
            );
        }
    }

    public void terminate() {
        try {
            out.close();
            log.debug(out.getClass().getSimpleName() + " was closed successfully.");
            in.close();
            log.debug(in.getClass().getSimpleName() + " was closed successfully.");
            socket.close();
            log.debug(socket.getClass().getSimpleName() + " was closed successfully.");
            INSTANCE = null;
            log.info(
                    String.format("Connection with server %s/%d was closed successfully", host, port)
            );
        } catch (Exception e) {
            log.error(
                    String.format("Client failed to close connection with server %s/%d: ", host, port), e
            );
        }
    }

    public boolean sendMsg(Message message) {
        try {
            out.writeObject(message);
            log.debug(
                    String.format("Client send message to server %s/%d: %s", host, port, message)
            );
            //todo override toString() in Message
            return true;
        } catch (IOException e) {
            log.error(
                    String.format("Client failed to send message to server %s/%d: ", host, port), e
            );
        }
        return false;
    }

    public Message readMessage() {
        try {
            return (Message) in.readObject();
        } catch (Exception e) {
            log.error(
                    String.format("Client failed to receive message from server %s/%d: ", host, port), e
            );
        }
        return null;
    }
}
