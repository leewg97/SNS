package com.wg.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationArgs {

    private Long fromUserId;    // 알림을 발생시킨 사람
    private Long targetId;

}
