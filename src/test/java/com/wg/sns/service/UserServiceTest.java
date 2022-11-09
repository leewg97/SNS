package com.wg.sns.service;

import com.wg.sns.exception.ErrorCode;
import com.wg.sns.exception.SnsApplicationException;
import com.wg.sns.fixture.UserEntityFixture;
import com.wg.sns.model.entity.UserEntity;
import com.wg.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserEntityRepository userEntityRepository;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @DisplayName("회원가입이 정상 동작하는 경우")
    @Test
    void joinSuccess() {
        String username = "username";
        String password = "password";


        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(username, password));

        assertDoesNotThrow(() -> userService.join(username, password));
    }

    @DisplayName("이미 해당 이름으로 회원가입한 유저가 있어 회원가입이 정상 동작하지 않는 경우")
    @Test
    void joinFail() {
        String username = "username";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(username, password);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(fixture));
        when(passwordEncoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.join(username, password));
        assertEquals(ErrorCode.DUPLICATED_USERNAME, e.getErrorCode());

    }

    @DisplayName("로그인이 정상 동작하는 경우")
    @Test
    void loginSuccess() {
        String username = "username";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(username, password);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(fixture));
        when(passwordEncoder.matches(password, fixture.getPassword())).thenReturn(true);

        assertDoesNotThrow(() -> userService.login(username, password));
    }

    @DisplayName("username으로 회원가입한 유저가 없는 경우")
    @Test
    void loginFail1() {
        String username = "username";
        String password = "password";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(username, password));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @DisplayName("로그인시 패스워드가 틀린 경우")
    @Test
    void loginFail2() {
        String username = "username";
        String password = "password";
        String wrongPass = "wrongPass";

        UserEntity fixture = UserEntityFixture.get(username, password);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(fixture));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(username, wrongPass));
        assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }

}
