package com.wg.sns.controller;

import com.wg.sns.controller.request.UserJoinRequest;
import com.wg.sns.controller.request.UserLoginRequest;
import com.wg.sns.controller.response.NotificationResponse;
import com.wg.sns.controller.response.Response;
import com.wg.sns.controller.response.UserJoinResponse;
import com.wg.sns.controller.response.UserLoginResponse;
import com.wg.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        return Response.success(UserJoinResponse.fromUser(userService.join(request.getName(), request.getPassword())));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        return Response.success(new UserLoginResponse(userService.login(request.getName(), request.getPassword())));
    }

    @GetMapping("/notify")
    public Response<Page<NotificationResponse>> notificationList(Pageable pageable, Authentication authentication) {
        return Response.success(userService.notificationList(authentication.getName(), pageable).map(NotificationResponse::fromNotification));
    }

}
