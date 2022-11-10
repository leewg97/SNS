package com.wg.sns.fixture;

import com.wg.sns.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String username, String password, Long userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        return userEntity;
    }


}
