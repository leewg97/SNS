package com.wg.sns.service;

import com.wg.sns.exception.ErrorCode;
import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.model.NotificationArgs;
import com.wg.sns.model.NotificationType;
import com.wg.sns.model.entity.NotificationEntity;
import com.wg.sns.model.entity.UserEntity;
import com.wg.sns.repository.EmitterRepository;
import com.wg.sns.repository.NotificationEntityRepository;
import com.wg.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String NOTIFICATION_NAME = "notify";
    private final EmitterRepository emitterRepository;
    private final NotificationEntityRepository notificationEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public SseEmitter connectNotify(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
        }

        return sseEmitter;
    }

    public void send(NotificationType notificationType, NotificationArgs notificationArgs, Long receiverUserId) {
        UserEntity userEntity = userEntityRepository.findById(receiverUserId).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND)
        );

        NotificationEntity notificationEntity = notificationEntityRepository.save(NotificationEntity.of(userEntity, notificationType, notificationArgs));

        emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(notificationEntity.getId().toString()).name(NOTIFICATION_NAME).data("new notification"));
            } catch (IOException e) {
                emitterRepository.delete(receiverUserId);
                throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

}
