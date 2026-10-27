package com.example.Uplink_Backend.modules.content.dto;

import com.example.Uplink_Backend.modules.content.entity.Post;

public class PostResponseDto {
    private Post post;
    private long likeCount;
    private long commentCount;
    private boolean likedByCurrentUser;

    public PostResponseDto() {}

    public PostResponseDto(Post post, long likeCount, long commentCount, boolean likedByCurrentUser) {
        this.post = post;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
}
