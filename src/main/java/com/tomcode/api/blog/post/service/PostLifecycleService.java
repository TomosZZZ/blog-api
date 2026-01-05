package com.tomcode.api.blog.post.service;

import com.tomcode.api.blog.common.exception.ForbiddenException;
import com.tomcode.api.blog.common.exception.PostNotFoundException;
import com.tomcode.api.blog.post.dto.ChangeStatusDTO;
import com.tomcode.api.blog.post.entity.Post;
import com.tomcode.api.blog.post.entity.PostStatus;
import com.tomcode.api.blog.post.repository.PostRepository;
import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.entity.UserRole;
import com.tomcode.api.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostLifecycleService {
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public void changeStatus(ChangeStatusDTO dto, String userEmail, UUID postId) {
        User user = userService.findByEmail(userEmail);

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        boolean isAdmin = user.hasRole(UserRole.ADMIN);
        boolean isAuthor = post.getAuthor().getId().equals(user.getId());

        PostStatus from = post.getStatus();
        PostStatus to = dto.status();

        if(!isStatusChangeAllowed(from,to,isAdmin,isAuthor)) {
            throw new ForbiddenException("Invalid status change");
        }

        if (to == PostStatus.CHANGES_REQ) {
            if (dto.comment() == null || dto.comment().isBlank()) {
                throw new ForbiddenException("Review comment is required");
            }
            post.setReviewComment(dto.comment());
        }else{
            post.setReviewComment(null);
        }

        post.setStatus(to);
    }


    private boolean isStatusChangeAllowed(PostStatus from, PostStatus to, boolean isAdmin, boolean isAuthor) {
        return switch (from){
            case DRAFT -> isAuthor && to==PostStatus.IN_REVIEW;
            case IN_REVIEW -> isAdmin && (to==PostStatus.APPROVED || to==PostStatus.CHANGES_REQ);
            case APPROVED -> (isAdmin || isAuthor) && to == PostStatus.PUBLISHED;
            case CHANGES_REQ -> isAuthor && (to==PostStatus.DRAFT || to==PostStatus.IN_REVIEW);
            case PUBLISHED -> false;
        };
    }
}
