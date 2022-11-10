package com.wg.sns.service;

import com.wg.sns.exception.ErrorCode;
import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.model.Post;
import com.wg.sns.model.entity.LikeEntity;
import com.wg.sns.model.entity.PostEntity;
import com.wg.sns.model.entity.UserEntity;
import com.wg.sns.repository.LikeEntityRepository;
import com.wg.sns.repository.PostEntityRepository;
import com.wg.sns.repository.UserEntityRepository;
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

    public void create(String title, String body, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
        );
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    public Post modify(String title, String body, String username, Long postId) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
        );

        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
        );

        if (postEntity.getUserEntity() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    public void delete(String username, Long postId) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
        );

        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
        );

        if (postEntity.getUserEntity() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        postEntityRepository.delete(postEntity);
    }

    @Transactional(readOnly = true)
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<Post> myList(String username, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
        );

        return postEntityRepository.findAllByUserEntity(userEntity, pageable).map(Post::fromEntity);
    }

    public void like(Long postId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
        );

        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
        );

        likeEntityRepository.findByUserEntityAndPostEntity(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("username %s already like post %s", username, postId));
        });

        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
    }

    @Transactional(readOnly = true)
    public long likeCount(Long postId) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
        );

        return likeEntityRepository.countByPostEntity(postEntity);
    }

}
