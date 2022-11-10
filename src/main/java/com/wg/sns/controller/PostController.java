package com.wg.sns.controller;

import com.wg.sns.controller.request.PostCommentRequest;
import com.wg.sns.controller.request.PostCreateRequest;
import com.wg.sns.controller.request.PostModifyRequest;
import com.wg.sns.controller.response.CommentResponse;
import com.wg.sns.controller.response.PostResponse;
import com.wg.sns.controller.response.Response;
import com.wg.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> myList(Pageable pageable, Authentication authentication) {
        return Response.success(postService.myList(authentication.getName(), pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Long postId, Authentication authentication) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Long postId) {
        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Long postId, @RequestBody PostCommentRequest request, Authentication authentication) {
        postService.comment(postId, request.getComment(), authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> getComments(@PathVariable Long postId, Pageable pageable) {
        return Response.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }

}
