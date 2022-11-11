package com.wg.sns.producer;

import com.wg.sns.model.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<Long, NotificationEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private String topic;

    public void send(NotificationEvent notificationEvent) {
        kafkaTemplate.send(topic, notificationEvent.getReceiveUserId(), notificationEvent);
        log.info("Send to Kafka finished");
    }


}
