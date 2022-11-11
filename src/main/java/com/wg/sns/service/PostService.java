package com.wg.sns.service;

import com.wg.sns.exception.ErrorCode;
import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.model.Comment;
import com.wg.sns.model.NotificationArgs;
import com.wg.sns.model.NotificationType;
import com.wg.sns.model.Post;
import com.wg.sns.model.entity.*;
import com.wg.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final NotificationEntityRepository notificationEntityRepository;
    private final NotificationService notificationService;

    public void create(String title, String body, String username) {
        UserEntity userEntity = getUserEntity(username);
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    public Post modify(String title, String body, String username, Long postId) {
        UserEntity userEntity = getUserEntity(username);
        PostEntity postEntity = getPostEntity(postId);

        if (postEntity.getUserEntity() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    public void delete(String username, Long postId) {
        UserEntity userEntity = getUserEntity(username);
        PostEntity postEntity = getPostEntity(postId);

        if (postEntity.getUserEntity() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    @Transactional(readOnly = true)
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<Post> myList(String username, Pageable pageable) {
        UserEntity userEntity = getUserEntity(username);
        return postEntityRepository.findAllByUserEntity(userEntity, pageable).map(Post::fromEntity);
    }

    public void like(Long postId, String username) {
        UserEntity userEntity = getUserEntity(username);
        PostEntity postEntity = getPostEntity(postId);

        likeEntityRepository.findByUserEntityAndPostEntity(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("username %s already like post %s", username, postId));
        });

        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
        NotificationEntity notificationEntity = notificationEntityRepository.save(NotificationEntity.of(postEntity.getUserEntity(), NotificationType.NEW_LIKE_ON_POST, new NotificationArgs(userEntity.getId(), postEntity.getId())));
        notificationService.send(notificationEntity.getId(), postEntity.getId());
    }

    @Transactional(readOnly = true)
    public long likeCount(Long postId) {
        PostEntity postEntity = getPostEntity(postId);
        return likeEntityRepository.countByPostEntity(postEntity);
    }

    public void comment(Long postId, String comment, String username) {
        UserEntity userEntity = getUserEntity(username);
        PostEntity postEntity = getPostEntity(postId);
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        NotificationEntity notificationEntity = notificationEntityRepository.save(NotificationEntity.of(postEntity.getUserEntity(), NotificationType.NEW_COMMENT_ON_POST, new NotificationArgs(userEntity.getId(), postEntity.getId())));
        notificationService.send(notificationEntity.getId(), postEntity.getId());
    }

    @Transactional(readOnly = true)
    public Page<Comment> getComments(Long postId, Pageable pageable) {
        PostEntity postEntity = getPostEntity(postId);
        return commentEntityRepository.findAllByPostEntity(postEntity, pageable).map(Comment::fromEntity);
    }

    private UserEntity getUserEntity(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
        );
    }

    private PostEntity getPostEntity(Long postId) {
        return postEntityRepository.findById(postId).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
        );
    }

}
