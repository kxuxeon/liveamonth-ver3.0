package teamproject.lam_server.domain.interaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamproject.lam_server.domain.interaction.constants.InteractionType;
import teamproject.lam_server.domain.interaction.constants.ReactType;
import teamproject.lam_server.domain.interaction.dto.InteractionRequest;
import teamproject.lam_server.domain.interaction.dto.ReactedCommentResponse;
import teamproject.lam_server.domain.interaction.service.InteractionServiceFinder;
import teamproject.lam_server.global.dto.CustomResponse;

import javax.validation.Valid;
import java.util.List;

import static teamproject.lam_server.global.constants.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interactions")
public class InteractionApiController {

    private final InteractionServiceFinder interactionServiceFinder;

    @PostMapping("/like")
    public ResponseEntity<?> likeContent(
            @RequestParam InteractionType type,
            @RequestBody @Valid InteractionRequest request) {
        interactionServiceFinder.find(type).like(request);
        return CustomResponse.success(CREATE_INTERACTION);
    }

    @PostMapping("/like/cancel")
    public ResponseEntity<?> cancelLikeContent(
            @RequestParam InteractionType type,
            @RequestBody @Valid InteractionRequest request) {
        interactionServiceFinder.find(type).cancelLike(request);
        return CustomResponse.success(DELETE_INTERACTION);
    }

    @PostMapping("/comments/react")
    public ResponseEntity<?> reactComment(
            @RequestParam(name = "comment_type") InteractionType commentType,
            @RequestParam(name = "react_type") ReactType reactType,
            @RequestBody @Valid InteractionRequest request) {
        interactionServiceFinder.findComment(commentType).react(request, reactType);
        return CustomResponse.success(CREATE_INTERACTION);
    }

    @PostMapping("/comments/react/cancel")
    public ResponseEntity<?> cancelReactComment(
            @RequestParam(name = "comment_type") InteractionType commentType,
            @RequestBody @Valid InteractionRequest request) {
        interactionServiceFinder.findComment(commentType).cancelReact(request);
        return CustomResponse.success(DELETE_INTERACTION);
    }

    @GetMapping("/member/liked")
    public ResponseEntity<?> isMemberLikedContent(@RequestParam InteractionType type, InteractionRequest request) {
        boolean result = interactionServiceFinder.find(type).isLike(request);
        return CustomResponse.success(READ_INTERACTION, result);
    }

    @GetMapping("/member/{memberId}/reacted-comments")
    public ResponseEntity<?> getMemberReactedComments(
            @PathVariable Long memberId,
            @RequestParam InteractionType type,
            @RequestParam List<Long> ids) {
        List<ReactedCommentResponse> result = interactionServiceFinder.findComment(type).getReactedComments(memberId, ids);
        return CustomResponse.success(READ_INTERACTION, result);
    }
}
