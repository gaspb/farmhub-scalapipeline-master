package org.highjack.scalapipeline.web.rest.kafka;

import akka.kafka.javadsl.Consumer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.scaladsl.Source;
import akka.stream.scaladsl.SourceQueueWithComplete;
import akka.stream.scaladsl.StreamConverters;
import akka.util.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ConsumerService {

    private final Logger log = LoggerFactory.getLogger(ConsumerService.class);

    static public SubscribableChannel consumerChannel;
    public static Source<String, ?> activeSource;
    public static Source<String, ?> activeSourceQueue;

    public ConsumerService(ConsumerChannel consumerChannel) {

       /* activeSource = Source.queue(5, OverflowStrategy.dropHead());
*/
      /*  Consumer.
        ConsumerService.consumerChannel = consumerChannel.subscribableChannel();
        ConsumerService.consumerChannel.subscribe(new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                log.info("Received message:  {} "+ message.getPayload());
            }
        });*/
    }

    @StreamListener(ConsumerChannel.CHANNEL)
    public void consume(MessageModel message) {
        log.info("Received message:  {} ", message.getMessage());
        /*if (ConsumerService.activeSource!=null) {
            log.info("Passing to queue ");
            ConsumerService.activeSource.offer(message.getMessage());
        }*/

    }


   /* @StreamListener(ConsumerChannel.CHANNEL)
    public void consume(Flux<MessageModel> strings)
    {
        log.info("consuming kafka stream flux ");
        ConsumerService.activeSource = Source.fromPublisher(strings);
    }*/
}
