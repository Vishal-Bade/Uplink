package com.example.Uplink_Backend.modules.content.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.content.dto.CreateCommentRequest;
import com.example.Uplink_Backend.modules.content.dto.CreatePostRequest;
import com.example.Uplink_Backend.modules.content.dto.PostResponseDto;
import com.example.Uplink_Backend.modules.content.entity.Comment;
import com.example.Uplink_Backend.modules.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/content")
@Tag(name = "12. Content Module", description = "Endpoints for Social Feed, Blogs, Notices, Discussions, Comments, and Likes")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/posts")
    @Operation(summary = "Publish a new post, blog, or campus/recruiter notice")
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreatePostRequest request) {
        PostResponseDto created = contentService.createPost(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.created("Post published successfully", created));
    }

    @GetMapping("/feed")
    @Operation(summary = "Get community feed of posts, blogs, and notices (paginated)")
    public ResponseEntity<ApiResponse<Page<PostResponseDto>>> getFeed(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String type,
            Pageable pageable) {
        Page<PostResponseDto> feed = contentService.getFeed(userPrincipal.getId(), type, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Community feed retrieved", feed));
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get single post details with like and comment counts")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        PostResponseDto post = contentService.getPostById(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Post retrieved", post));
    }

    @PostMapping("/posts/{id}/comments")
    @Operation(summary = "Add comment to a post")
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id,
            @Valid @RequestBody CreateCommentRequest request) {
        Comment comment = contentService.addComment(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.created("Comment added", comment));
    }

    @GetMapping("/posts/{id}/comments")
    @Operation(summary = "Get comments thread for a post (paginated)")
    public ResponseEntity<ApiResponse<Page<Comment>>> getPostComments(@PathVariable UUID id, Pageable pageable) {
        Page<Comment> comments = contentService.getPostComments(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Comments retrieved", comments));
    }

    @PostMapping("/posts/{id}/like")
    @Operation(summary = "Toggle like/unlike on a post")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        boolean liked = contentService.toggleLike(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(liked ? "Post liked" : "Post unliked", Map.of("liked", liked)));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete post (Owner or Admin)")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        boolean isAdmin = userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN"));
        contentService.deletePost(id, userPrincipal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Post deleted successfully", null));
    }
}
