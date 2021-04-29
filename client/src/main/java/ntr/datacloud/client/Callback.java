package ntr.datacloud.client;

import ntr.datacloud.common.messages.Message;

@FunctionalInterface
public interface Callback {

    void call(Message arg);
}

