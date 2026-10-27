package com.example.Uplink_Backend.modules.content.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.content.dto.CreateCommentRequest;
import com.example.Uplink_Backend.modules.content.dto.CreatePostRequest;
import com.example.Uplink_Backend.modules.content.dto.PostResponseDto;
import com.example.Uplink_Backend.modules.content.entity.Comment;
import com.example.Uplink_Backend.modules.content.entity.Post;
import com.example.Uplink_Backend.modules.content.entity.PostLike;
import com.example.Uplink_Backend.modules.content.repository.CommentRepository;
import com.example.Uplink_Backend.modules.content.repository.PostLikeRepository;
import com.example.Uplink_Backend.modules.content.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    public ContentService(PostRepository postRepository,
                          CommentRepository commentRepository,
                          PostLikeRepository postLikeRepository,
                          UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostResponseDto createPost(UUID userId, CreatePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Post post = new Post(user, request.getContent(), request.getType());
        Post savedPost = postRepository.save(post);

        return new PostResponseDto(savedPost, 0, 0, false);
    }

    public Page<PostResponseDto> getFeed(UUID currentUserId, String type, Pageable pageable) {
        Page<Post> postsPage = type != null && !type.isBlank()
                ? postRepository.findByTypeOrderByCreatedAtDesc(type.toUpperCase(), pageable)
                : postRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<PostResponseDto> dtoList = postsPage.getContent().stream().map(post -> {
            long likes = postLikeRepository.countByPostId(post.getId());
            long comments = commentRepository.countByPostId(post.getId());
            boolean liked = currentUserId != null && postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
            return new PostResponseDto(post, likes, comments, liked);
        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, postsPage.getTotalElements());
    }

    public PostResponseDto getPostById(UUID postId, UUID currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        long likes = postLikeRepository.countByPostId(post.getId());
        long comments = commentRepository.countByPostId(post.getId());
        boolean liked = currentUserId != null && postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);

        return new PostResponseDto(post, likes, comments, liked);
    }

    @Transactional
    public Comment addComment(UUID userId, UUID postId, CreateCommentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment comment = new Comment(post, user, request.getContent());
        return commentRepository.save(comment);
    }

    public Page<Comment> getPostComments(UUID postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);
    }

    @Transactional
    public boolean toggleLike(UUID userId, UUID postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            return false; // unliked
        } else {
            postLikeRepository.save(new PostLike(post, user));
            return true; // liked
        }
    }

    @Transactional
    public void deletePost(UUID postId, UUID userId, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (isAdmin || post.getUser().getId().equals(userId)) {
            postRepository.delete(post);
        }
    }
}
