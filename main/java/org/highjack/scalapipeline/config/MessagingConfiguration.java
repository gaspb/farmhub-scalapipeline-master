package org.highjack.scalapipeline.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.highjack.scalapipeline.web.rest.kafka.ConsumerChannel;
import org.highjack.scalapipeline.web.rest.kafka.MessageModel;
import org.highjack.scalapipeline.web.rest.kafka.ProducerChannel;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.*;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

/**
 * Configures Spring Cloud Stream support.
 *
 * This works out-of-the-box if you use the Docker Compose configuration at "src/main/docker/kafka.yml".
 *
 * See http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/
 * for the official Spring Cloud Stream documentation.
 */
@EnableBinding(value = {ProducerChannel.class, ConsumerChannel.class})
public class MessagingConfiguration {

   /* *//**
     * This sends a test message at regular intervals.
     *
     * In order to see the test messages, you can use the Kafka command-line client:
     * "./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic-jhipster --from-beginning".
     */

    /*@Bean
    @InboundChannelAdapter(value = Sink.INPUT)
    public MessageSource<String> timerMessageSource() {

        return () -> new GenericMessage<>("Test message from JHipster sent at " +
            new SimpleDateFormat().format(new Date()));
    }*/
}
