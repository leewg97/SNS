package com.wg.sns.repository;

import com.wg.sns.model.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findAllByUserEntityId(Long userId, Pageable pageable);
}
