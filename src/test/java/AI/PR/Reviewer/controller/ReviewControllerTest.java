package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.dto.CreateReviewRequest;
import AI.PR.Reviewer.entity.Review;
import AI.PR.Reviewer.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService service;

    @InjectMocks
    private ReviewController controller;

    @Test
    void createReview_shouldReturnSavedReview() {
        CreateReviewRequest request = mock(CreateReviewRequest.class);
        Review savedReview = new Review();

        when(service.saveReview(request)).thenReturn(savedReview);

        Review result = controller.createReview(request);

        assertSame(savedReview, result);
        verify(service).saveReview(request);
    }

    @Test
    void getAllReviews_shouldReturnReviews() {
        List<Review> reviews =
                List.of(new Review(), new Review());

        when(service.getAllReviews()).thenReturn(reviews);

        assertEquals(reviews, controller.getAllReviews());
        verify(service).getAllReviews();
    }

    @Test
    void getReviewsByPullRequest_shouldReturnMatchingReviews() {
        List<Review> reviews = List.of(new Review());

        when(service.getReviewsByPullRequest(1L)).thenReturn(reviews);

        assertEquals(reviews, controller.getReviewsByPullRequest(1L));
        verify(service).getReviewsByPullRequest(1L);
    }

    @Test
    void createReview_shouldHaveCreatedStatus() throws Exception {
        ResponseStatus annotation = ReviewController.class
                .getMethod("createReview", CreateReviewRequest.class)
                .getAnnotation(ResponseStatus.class);

        assertNotNull(annotation);
        assertEquals(HttpStatus.CREATED, annotation.value());
    }
}