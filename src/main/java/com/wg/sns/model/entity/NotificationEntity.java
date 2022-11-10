package com.wg.sns.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.wg.sns.model.NotificationArgs;
import com.wg.sns.model.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "\"notification\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_entity_id")
})
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLDelete(sql = "UPDATE \"notification\" SET deleted_at = NOW() where id=?")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "post_entity_id")
    private PostEntity postEntity;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private NotificationArgs args;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static NotificationEntity of(UserEntity userEntity, NotificationType notificationType, NotificationArgs args) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setUserEntity(userEntity);
        notificationEntity.setNotificationType(notificationType);
        notificationEntity.setArgs(args);
        return notificationEntity;
    }

}
