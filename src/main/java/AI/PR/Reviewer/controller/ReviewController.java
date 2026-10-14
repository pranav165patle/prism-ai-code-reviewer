package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.dto.CreateReviewRequest;
import AI.PR.Reviewer.entity.Review;
import AI.PR.Reviewer.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(
            @Valid @RequestBody CreateReviewRequest request) {

        return service.saveReview(request);
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return service.getAllReviews();
    }

    @GetMapping("/pull-request/{pullRequestId}")
    public List<Review> getReviewsByPullRequest(
            @PathVariable Long pullRequestId) {

        return service.getReviewsByPullRequest(pullRequestId);
    }
}