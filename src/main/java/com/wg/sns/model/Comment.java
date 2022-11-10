package com.wg.sns.model;

import com.wg.sns.model.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Comment {

    private Long id;
    private Long postId;
    private String username;
    private String comment;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Comment fromEntity(CommentEntity commentEntity) {
        return new Comment(
                commentEntity.getId(),
                commentEntity.getPostEntity().getId(),
                commentEntity.getUserEntity().getUsername(),
                commentEntity.getComment(),
                commentEntity.getRegisteredAt(),
                commentEntity.getUpdatedAt(),
                commentEntity.getDeletedAt()
        );
    }
}