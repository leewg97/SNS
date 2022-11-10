package com.wg.sns.service;

import com.wg.sns.exception.ErrorCode;
import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.fixture.PostEntityFixture;
import com.wg.sns.fixture.UserEntityFixture;
import com.wg.sns.model.entity.PostEntity;
import com.wg.sns.model.entity.UserEntity;
import com.wg.sns.repository.PostEntityRepository;
import com.wg.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @MockBean
    private PostEntityRepository postEntityRepository;
    @MockBean
    private UserEntityRepository userEntityRepository;

    @DisplayName("포스트 작성이 성공한 경우")
    @Test
    void postCreationSuccessful() {
        String title = "title";
        String body = "body";
        String username = "username";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        assertDoesNotThrow(() -> postService.create(title, body, username));
    }

    @DisplayName("포스트 작성시 요청한 유저가 존재하지 않는 경우")
    @Test
    void userDoesNotExistWhenPostCreation() {
        String title = "title";
        String body = "body";
        String username = "username";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.create(title, body, username));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @DisplayName("포스트 수정이 성공한 경우")
    @Test
    void postModificationSuccessful() {
        String title = "title";
        String body = "body";
        String username = "username";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1L);
        UserEntity userEntity = postEntity.getUserEntity();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        assertDoesNotThrow(() -> postService.modify(title, body, username, postId));
    }

    @DisplayName("포스트 수정시 포스트가 존재하지 않는 경우")
    @Test
    void thePostIdYouWantToModifyDoesNotExist() {
        String title = "title";
        String body = "body";
        String username = "username";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1L);
        UserEntity userEntity = postEntity.getUserEntity();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, username, postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @DisplayName("포스트 수정시 권한이 없는 경우")
    @Test
    void ifYouDoNotHavePermissionToModifyThePost() {
        String title = "title";
        String body = "body";
        String username = "username";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(username, postId,1L);
        UserEntity writer = UserEntityFixture.get("username1", "password1", 2L);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, username, postId));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @DisplayName("포스트 삭제가 성공한 경우")
    @Test
    void postDeletionSuccessful() {
        String username = "username";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1L);
        UserEntity userEntity = postEntity.getUserEntity();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        assertDoesNotThrow(() -> postService.delete(username, postId));
    }

    @DisplayName("포스트 삭제시 포스트가 존재하지 않는 경우")
    @Test
    void thePostIdYouWantToDeleteDoesNotExist() {
        String username = "username";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1L);
        UserEntity userEntity = postEntity.getUserEntity();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(username, postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @DisplayName("포스트 삭제시 권한이 없는 경우")
    @Test
    void ifYouDoNotHavePermissionToDeleteThePost() {
        String username = "username";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(username, postId,1L);
        UserEntity writer = UserEntityFixture.get("username1", "password1", 2L);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(username, postId));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @DisplayName("포스트 목록 요청 성공한 경우")
    @Test
    void requestPostListSuccessful() {
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        assertDoesNotThrow(() -> postService.list(pageable));
    }

    @DisplayName("내 포스트 목록 요청 성공한 경우")
    @Test
    void requestMyPostListSuccessful() {
        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findAllByUserEntity(any(), eq(pageable))).thenReturn(Page.empty());
        assertDoesNotThrow(() -> postService.myList(userEntity.getUsername(), pageable));
    }

}
