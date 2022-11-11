package com.wg.sns.service;

import com.wg.sns.exception.ErrorCode;
import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.repository.EmitterRepository;
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

    public void send(Long notificationId, Long userId) {
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(notificationId.toString()).name(NOTIFICATION_NAME).data("new notification"));
            } catch (IOException e) {
                emitterRepository.delete(userId);
                throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

}
