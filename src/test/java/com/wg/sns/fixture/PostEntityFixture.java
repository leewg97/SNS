package com.wg.sns.fixture;

import com.wg.sns.model.entity.PostEntity;
import com.wg.sns.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String username, Long postId, Long userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername(username);

        PostEntity postEntity = new PostEntity();
        postEntity.setUserEntity(userEntity);
        postEntity.setId(postId);
        return postEntity;
    }


}
