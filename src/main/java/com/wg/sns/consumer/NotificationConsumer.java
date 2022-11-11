package com.wg.sns.consumer;

import com.wg.sns.model.event.NotificationEvent;
import com.wg.sns.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.topic.notification}")
    public void consumeNotification(NotificationEvent notificationEvent, Acknowledgment ack) {
        log.info("Consume the event {}", notificationEvent);
        notificationService.send(notificationEvent.getNotificationType(), notificationEvent.getNotificationArgs(), notificationEvent.getReceiveUserId());
        ack.acknowledge();
    }

}
