package org.highjack.scalapipeline.web.rest.kafka;

import akka.util.ByteString;

public class MessageModelWithPayload extends MessageModel {

    private ByteString payload;

    public MessageModelWithPayload() {
    }

    public ByteString getPayload() {
        return payload;
    }

    public MessageModelWithPayload setPayload(ByteString payload) {
        this.payload = payload;
        return this;
    }
}
