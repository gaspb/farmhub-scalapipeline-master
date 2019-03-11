package org.highjack.scalapipeline.web.rest.kafka;

import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;
import scala.Function1;

@Service
public class ConsumerService {

    private final Logger log = LoggerFactory.getLogger(ConsumerService.class);

    private SubscribableChannel channel;
    public static ActorRef sourceActor;
    public static Function1<String,?> handler;

    public ConsumerService(ConsumerChannel consumerChannel) {
        this.channel = consumerChannel.subscribableChannel();
    }

    @StreamListener(ConsumerChannel.CHANNEL)
    public void consume(MessageModel message) {
        log.info("-Recieved message:  {} ",message.getMessage());

        if (ConsumerService.handler!=null) {
            log.info("-flow is not null");
            handler.apply(message.getMessage());
        }

    }
}
