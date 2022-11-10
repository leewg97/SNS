package com.wg.sns.model;

import com.wg.sns.model.entity.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Notification {

    private Long id;
    private NotificationType notificationType;
    private NotificationArgs args;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public String getNotificationText() {
        return notificationType.getNotificationText();
    }

    public static Notification fromEntity(NotificationEntity notificationEntity) {
        return new Notification(
                notificationEntity.getId(),
                notificationEntity.getNotificationType(),
                notificationEntity.getArgs(),
                notificationEntity.getRegisteredAt(),
                notificationEntity.getUpdatedAt(),
                notificationEntity.getDeletedAt()
        );
    }
}
