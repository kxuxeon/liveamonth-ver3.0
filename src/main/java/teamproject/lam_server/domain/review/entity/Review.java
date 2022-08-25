package teamproject.lam_server.domain.review.entity;

import lombok.*;
import org.hibernate.annotations.Formula;
import teamproject.lam_server.domain.comment.entity.ReviewComment;
import teamproject.lam_server.domain.interaction.entity.review.ReviewLike;
import teamproject.lam_server.domain.member.entity.Member;
import teamproject.lam_server.domain.review.constants.ReviewCategory;
import teamproject.lam_server.domain.review.entity.editor.ReviewEditor;
import teamproject.lam_server.global.entity.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @OneToMany(mappedBy = "review")
    private final List<ReviewComment> reviewComments = new ArrayList<>();
    @OneToMany(mappedBy = "to")
    @ToString.Exclude
    private final Set<ReviewLike> likes = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private ReviewCategory reviewCategory;
    private String title;
    @Lob
    private String content;
    private Long viewCount;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Formula("(select count(1) from review_like rl where rl.to_review_id = review_id)")
    private int likeCount;

    @Formula("(select count(1) from review_comment rc where rc.review_id = review_id)")
    private int commentCount;

    @Builder
    public Review(ReviewCategory reviewCategory, String title, String content, Member member) {
        this.reviewCategory = reviewCategory;
        this.title = title;
        this.content = content;
        this.viewCount = 0L;
        setUpMember(member);
    }

    private void setUpMember(Member member) {
        this.member = member;
        member.getReviews().add(this);
    }
//
//    public ReviewEditor.ReviewEditorBuilder toEditor(){
//        return ReviewEditor.builder()
//                .title(title)
//                .content(content)
//                .reviewCategory(reviewCategory);
//    }

    public void edit(ReviewEditor reviewEditor) {
        this.title = reviewEditor.getTitle();
        this.reviewCategory = reviewEditor.getReviewCategory();
        this.content = reviewEditor.getContent();
    }
}
