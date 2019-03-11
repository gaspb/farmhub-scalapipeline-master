package org.highjack.scalapipeline.web.rest.kafka;

import com.codahale.metrics.annotation.Timed;
import org.highjack.scalapipeline.web.rest.kafka.MessageModel;
import org.highjack.scalapipeline.web.rest.kafka.ProducerChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class ApiResource{

    private static MessageChannel staticChannel;

    private MessageChannel channel;

    public ApiResource(ProducerChannel channel) {
        this.channel = channel.messageChannel();
        this.setStaticChannel(this.channel);
    }

    @GetMapping("/greetings/{count}")
    @Timed
    public void produce(@PathVariable int count) {
        while(count > 0) {
            channel.send(MessageBuilder.withPayload(new MessageModel().setMessage("Hello world i'm Scala-MS!: " + count)).build());
            count--;
        }
    }
    @GetMapping("/greetings/str/{message}")
    @Timed
    public void produce(@PathVariable String message) {
        channel.send(MessageBuilder.withPayload(new MessageModel().setMessage(message)).build());

    }
    public static MessageChannel getStaticChannel() {
        return staticChannel;
    }

    public void setStaticChannel(MessageChannel staticChannel) {
        ApiResource.staticChannel = staticChannel;
    }
}
