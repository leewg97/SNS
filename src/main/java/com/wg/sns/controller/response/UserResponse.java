package com.wg.sns.controller.response;

import com.wg.sns.model.Role;
import com.wg.sns.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private Role role;

    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

}
