package AI.PR.Reviewer.service;

import AI.PR.Reviewer.dto.CreateReviewRequest;
import AI.PR.Reviewer.entity.Review;
import AI.PR.Reviewer.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import AI.PR.Reviewer.entity.ReviewStatus;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repository;

    @InjectMocks
    private ReviewService service;

    private CreateReviewRequest request;
    private Review review;

    @BeforeEach
    void setUp() {
        request = new CreateReviewRequest();
        request.setPullRequestId(1L);
        request.setAgentType(ReviewAgentType.SECURITY);
        request.setSeverity(ReviewSeverity.HIGH);
        request.setMessage("Potential security issue");
        request.setFilePath("src/Main.java");
        request.setLineNumber(10);
        request.setStatus(ReviewStatus.OPEN);

        review = new Review();
        review.setPullRequestId(1L);
        review.setAgentType(ReviewAgentType.SECURITY);
        review.setSeverity(ReviewSeverity.HIGH);
        review.setMessage("Potential security issue");
        review.setFilePath("src/Main.java");
        review.setLineNumber(10);
        review.setStatus(ReviewStatus.OPEN);
    }

    @Test
    void saveReviewShouldSaveAndReturnReview() {
        when(repository.save(any(Review.class))).thenReturn(review);

        Review result = service.saveReview(request);

        assertNotNull(result);
        assertEquals(1L, result.getPullRequestId());
        assertEquals(ReviewAgentType.SECURITY, result.getAgentType());
        assertEquals(ReviewSeverity.HIGH, result.getSeverity());
        assertEquals("Potential security issue", result.getMessage());
        assertEquals("src/Main.java", result.getFilePath());
        assertEquals(10, result.getLineNumber());
        assertEquals(ReviewStatus.OPEN, result.getStatus());

        verify(repository).save(any(Review.class));
    }

    @Test
    void getAllReviewsShouldReturnAllReviews() {
        when(repository.findAll()).thenReturn(List.of(review));

        List<Review> result = service.getAllReviews();

        assertEquals(1, result.size());
        assertSame(review, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void getReviewsByPullRequestShouldReturnMatchingReviews() {
        when(repository.findByPullRequestId(1L))
                .thenReturn(List.of(review));

        List<Review> result = service.getReviewsByPullRequest(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPullRequestId());
        verify(repository).findByPullRequestId(1L);
    }
}