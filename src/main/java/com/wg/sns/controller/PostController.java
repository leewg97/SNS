package com.wg.sns.controller;

import com.wg.sns.controller.request.PostCreateRequest;
import com.wg.sns.controller.request.PostModifyRequest;
import com.wg.sns.controller.response.PostResponse;
import com.wg.sns.controller.response.Response;
import com.wg.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@RequestBody PostModifyRequest request, @PathVariable Long postId, Authentication authentication) {
        return Response.success(PostResponse.fromPost(postService.modify(request.getTitle(), request.getBody(), authentication.getName(), postId)));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Long postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

}
