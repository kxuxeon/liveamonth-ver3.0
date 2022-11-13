package teamproject.lam_server.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamproject.lam_server.domain.review.constants.ReviewCategory;
import teamproject.lam_server.domain.review.constants.ReviewSortType;
import teamproject.lam_server.domain.review.dto.condition.ReviewSearchCond;
import teamproject.lam_server.domain.review.dto.reqeust.ReviewCreate;
import teamproject.lam_server.domain.review.dto.reqeust.ReviewEdit;
import teamproject.lam_server.domain.review.dto.response.ReviewDetailResponse;
import teamproject.lam_server.domain.review.dto.response.ReviewListResponse;
import teamproject.lam_server.domain.review.dto.response.TagResponse;
import teamproject.lam_server.domain.review.entity.Review;
import teamproject.lam_server.domain.review.entity.ReviewEditor;
import teamproject.lam_server.domain.review.entity.ReviewTag;
import teamproject.lam_server.domain.review.entity.Tag;
import teamproject.lam_server.domain.review.repository.ReviewRepository;
import teamproject.lam_server.domain.review.repository.ReviewTagRepository;
import teamproject.lam_server.domain.review.repository.TagRepository;
import teamproject.lam_server.exception.notfound.ReviewNotFound;
import teamproject.lam_server.global.dto.response.PostIdResponse;
import teamproject.lam_server.global.service.SecurityContextFinder;
import teamproject.lam_server.paging.CustomPage;
import teamproject.lam_server.paging.DomainSpec;
import teamproject.lam_server.paging.PageableDTO;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final SecurityContextFinder finder;
    private final ReviewRepository reviewRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final TagRepository tagRepository;
    private final DomainSpec<ReviewSortType> spec = new DomainSpec<>(ReviewSortType.class);

    @Transactional
    public PostIdResponse write(String loginId, ReviewCreate request) {
        finder.checkLegalLoginId(loginId);
        Review save = reviewRepository.save(
                request.toEntity(finder.getLoggedInMember(), mapToReviewTags(request.getTags()))
        );
        return PostIdResponse.of(save.getId());
    }

    @Transactional
    public Tag findOrCreateTag(String tag) {
        return tagRepository.findByName(tag)
                .orElseGet(
                        () -> tagRepository.save(
                                Tag.builder().name(tag).build()
                        )
                );
    }

    @Transactional
    public void edit(Long id, ReviewEdit request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFound::new);
        finder.checkLegalWriterOfPost(review);

        // 리뷰 태그 삭제
        if (request.getRemovedTags() != null) {
            review.removeTags(reviewTagRepository.findByReviewIdAndTag(id, request.getRemovedTags()));
        }

        // 리뷰 태그 추가
        if (request.getAddedTags() != null) {
            for (ReviewTag addedTag : mapToReviewTags(request.getAddedTags())) {
                review.addTag(addedTag);
            }
        }

        // 나머지 내용 수정
        ReviewEditor.ReviewEditorBuilder editorBuilder = review.toEditor();
        ReviewEditor reviewEditor = editorBuilder.title(request.getTitle())
                .category(ReviewCategory.valueOf(request.getCategory()))
                .content(request.getContent())
                .build();

        review.edit(reviewEditor);
    }

    @Transactional
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFound::new);

        finder.checkLegalWriterOfPost(review);

        reviewRepository.delete(review);
    }

    public CustomPage<ReviewListResponse> search(ReviewSearchCond cond, PageableDTO pageableDTO) {
        List<Long> reviewTagIds = cond.getTags() != null
                ? reviewTagRepository.findReviewTagsByTags(cond.getTags())
                : Collections.emptyList();
        Pageable pageable = spec.getPageable(pageableDTO);
        Page<ReviewListResponse> page =
                reviewRepository
                        .search(cond, reviewTagIds, pageable)
                        .map(ReviewListResponse::of);

        return CustomPage.<ReviewListResponse>builder()
                .page(page)
                .build();
    }

    public List<ReviewListResponse> getReviewByMember(String loginId, Integer size, Long lastId) {
        finder.checkLegalLoginId(loginId);
        return reviewRepository.getReviewByMember(loginId, size, lastId).stream()
                .map(ReviewListResponse::of)
                .collect(Collectors.toList());

    }

    public List<TagResponse> getRecommendationTags() {
        return reviewRepository.getRecommendationTags().stream()
                .map(TagResponse::of)
                .collect(Collectors.toList());
    }

    public ReviewDetailResponse getReview(Long id) {
        return ReviewDetailResponse.of(
                reviewRepository.getReview(id)
                        .orElseThrow(ReviewNotFound::new),
                reviewTagRepository.findTagNamesById(id)
        );
    }

    @Transactional
    public void viewCountUp(Long id) {
        reviewRepository.viewCountUp(id);
    }

    private Set<ReviewTag> mapToReviewTags(Set<String> tags) {
        return tags.stream()
                .map(tag -> ReviewTag.createReviewTag(findOrCreateTag(tag)))
                .collect(Collectors.toSet());
    }
}
