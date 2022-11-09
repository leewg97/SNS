package com.wg.sns.service;

import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.fixture.UserEntityFixture;
import com.wg.sns.model.entity.UserEntity;
import com.wg.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserEntityRepository userEntityRepository;

    @DisplayName("회원가입이 정상 동작하는 경우")
    @Test
    void joinSuccess() {
        String username = "username";
        String password = "password";


        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userEntityRepository.save(any())).thenReturn(Optional.of(UserEntityFixture.get(username, password)));

        assertDoesNotThrow(() -> userService.join(username, password));
    }

    @DisplayName("이미 해당 이름으로 회원가입한 유저가 있어 회원가입이 정상 동작하지 않는 경우")
    @Test
    void joinFail() {
        String username = "username";
        String password = "password";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(UserEntityFixture.get(username, password)));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        assertThrows(SnsApplicationException.class, () -> userService.join(username, password));
    }

    @DisplayName("로그인이 정상 동작하는 경우")
    @Test
    void loginSuccess() {
        String username = "username";
        String password = "password";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(UserEntityFixture.get(username, password)));
        assertDoesNotThrow(() -> userService.login(username, password));
    }

    @DisplayName("username으로 회원가입한 유저가 없는 경우")
    @Test
    void loginFail1() {
        String username = "username";
        String password = "password";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(SnsApplicationException.class, () -> userService.login(username, password));
    }

    @DisplayName("로그인시 패스워드가 틀린 경우")
    @Test
    void loginFail2() {
        String username = "username";
        String password = "password";
        String wrongPass = "wrongPass";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(UserEntityFixture.get(username, password)));

        assertThrows(SnsApplicationException.class, () -> userService.login(username, wrongPass));
    }

}
