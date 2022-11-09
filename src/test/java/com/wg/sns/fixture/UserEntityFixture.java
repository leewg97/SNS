package com.wg.sns.fixture;

import com.wg.sns.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String username, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        return userEntity;
    }


}
