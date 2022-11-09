package com.wg.sns.controller;

import com.wg.sns.controller.request.UserJoinRequest;
import com.wg.sns.controller.request.UserLoginRequest;
import com.wg.sns.controller.response.Response;
import com.wg.sns.controller.response.UserJoinResponse;
import com.wg.sns.controller.response.UserLoginResponse;
import com.wg.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        return Response.success(UserJoinResponse.fromUser(userService.join(request.getUsername(), request.getPassword())));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        return Response.success(new UserLoginResponse(userService.login(request.getUsername(), request.getPassword())));
    }

}
