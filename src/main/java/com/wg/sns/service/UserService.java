package com.wg.sns.service;

import com.wg.sns.exception.ErrorCode;
import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.model.Notification;
import com.wg.sns.model.User;
import com.wg.sns.model.entity.UserEntity;
import com.wg.sns.repository.NotificationEntityRepository;
import com.wg.sns.repository.UserEntityRepository;
import com.wg.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final NotificationEntityRepository notificationEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUsername(String username) {
        return userEntityRepository.findByUsername(username).map(User::fromEntity).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
        );
    }

    public User join(String username, String password) {
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", username));
        });

        UserEntity userEntity = userEntityRepository.save(UserEntity.of(username, passwordEncoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    public String login(String username, String password) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", username))
        );

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        return JwtTokenUtils.generateToken(username, secretKey, expiredTimeMs);
    }

    @Transactional(readOnly = true)
    public Page<Notification> notificationList(Long userId, Pageable pageable) {
        return notificationEntityRepository.findAllByUserEntityId(userId, pageable).map(Notification::fromEntity);
    }

}
