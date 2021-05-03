package ntr.datacloud.client.model;

import ntr.datacloud.common.messages.Message;

@FunctionalInterface
public interface Callback {

    void call(Message arg);
}

