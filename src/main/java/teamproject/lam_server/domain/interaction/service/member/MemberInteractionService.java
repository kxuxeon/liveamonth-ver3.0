package teamproject.lam_server.domain.interaction.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamproject.lam_server.auth.jwt.JwtTokenProvider;
import teamproject.lam_server.domain.interaction.constants.InteractionType;
import teamproject.lam_server.domain.interaction.dto.InteractionRequest;
import teamproject.lam_server.domain.interaction.repository.member.FollowRepository;
import teamproject.lam_server.domain.interaction.service.InteractionService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInteractionService implements InteractionService {
    private final FollowRepository followRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public InteractionType getType() {
        return InteractionType.MEMBER;
    }

    @Override
    @Transactional
    public void like(InteractionRequest request) {
        followRepository.follow(request);
    }

    @Override
    @Transactional
    public void cancelLike(InteractionRequest request) {
        followRepository.unFollow(request);
    }

    @Override
    public boolean isLike(String accessToken, Long contentId) {
        String loginId = jwtTokenProvider.getAuthentication(accessToken).getName();
        return false;
    }
}
