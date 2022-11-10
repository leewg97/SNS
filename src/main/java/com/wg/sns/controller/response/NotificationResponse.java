package com.wg.sns.controller.response;

import com.wg.sns.model.Notification;
import com.wg.sns.model.NotificationArgs;
import com.wg.sns.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String text;
    private NotificationType notificationType;
    private NotificationArgs args;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static NotificationResponse fromNotification(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getNotificationType().getNotificationText(),
                notification.getNotificationType(),
                notification.getArgs(),
                notification.getRegisteredAt(),
                notification.getUpdatedAt(),
                notification.getDeletedAt()
        );
    }
}
