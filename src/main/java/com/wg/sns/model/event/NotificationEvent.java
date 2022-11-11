package com.wg.sns.model.event;

import com.wg.sns.model.NotificationArgs;
import com.wg.sns.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {

    private Long receiveUserId;
    private NotificationType notificationType;
    private NotificationArgs notificationArgs;

}
