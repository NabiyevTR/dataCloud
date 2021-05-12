package ntr.datacloud.client.model;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.Message;
import java.net.Socket;
import java.util.concurrent.*;

@Log4j
public class NettyNetwork {

    private static NettyNetwork INSTANCE;

    private Socket socket;
    @Getter
    private boolean active = false;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;
    private final int MAX_MESSAGE_SIZE = 5 * 1024 * 1024;

    private final String host = System.getenv("HOST");
    private final int port = Integer.parseInt(System.getenv("PORT"));

    public static NettyNetwork getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NettyNetwork();
        }
        return INSTANCE;
    }

    private NettyNetwork() {
        try {
            socket = new Socket(host, port);
            out = new ObjectEncoderOutputStream(socket.getOutputStream(), MAX_MESSAGE_SIZE);
            in = new ObjectDecoderInputStream(socket.getInputStream(), MAX_MESSAGE_SIZE);
            active = true;
            log.info(
                    String.format("Client has connected to server %s/%d successfully", host, port)
            );
        } catch (Exception e) {
            terminate();
            log.error(
                    String.format("Cannot connect to server %s/%d: ", host, port), e
            );
        }
    }

    public void terminate() {
        try {
            out.close();
        } catch (Exception e) {
            log.debug("ObjectEncoderOutputStream was not closed.");
        }

        try {
            in.close();
        } catch (Exception e) {
            log.debug("ObjectDecoderInputStream was not closed.");
        }

        try {
            socket.close();
        } catch (Exception e) {
            log.debug("Socket was not closed.");
        }

        active = false;
        INSTANCE = null;
        log.info(
                String.format("Connection with server %s/%d was closed ", host, port)
        );

    }


    public boolean sendMsg(Message message) {
        try {
            out.writeObject(message);
            log.debug(
                    String.format("Client send message to server %s/%d: %s", host, port, message)
            );
            return true;
        } catch (Exception e) {
            log.error(
                    String.format("Client failed to send message to server %s/%d: ", host, port), e
            );
        }
        return false;
    }

    public Message readMessage() {

        // DOESN'T WORK WRONG MESSAGE ORDER

     /*   ExecutorService executor = Executors.newSingleThreadExecutor();
        RunnableFuture<Message> future = new FutureTask<>(() -> (Message) in.readObject());
        executor.execute(future);

        try {
            Message message = future.get(ClientProperties.getInstance().getTimeOut(), TimeUnit.MILLISECONDS);
            log.info(
                    String.format("Client receive message from server %s/%d: %s", host, port, message)
            );
            return message;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {

            log.error("No response from server: ", e);
            return Message.builder()
                    .errorText(e.getMessage())
                    .build();
        }*/


        try {
            Message message = (Message) in.readObject();
            log.debug(
                    String.format("Client receive message from server %s/%d: %s", host, port, message)
            );
            return message;

        } catch (Exception e) {
            log.error(
                    String.format("Client failed to receive message from server %s/%d: ", host, port), e
            );
            return Message.builder()
                    .errorText(e.getMessage())
                    .build();
        }
    }
}
