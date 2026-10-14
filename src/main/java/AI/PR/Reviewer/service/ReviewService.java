package AI.PR.Reviewer.service;

import AI.PR.Reviewer.dto.CreateReviewRequest;
import AI.PR.Reviewer.entity.Review;
import AI.PR.Reviewer.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public Review saveReview(CreateReviewRequest request) {

        Review review = new Review();

        review.setPullRequestId(request.getPullRequestId());
        review.setAgentType(request.getAgentType());
        review.setSeverity(request.getSeverity());
        review.setMessage(request.getMessage());
        review.setFilePath(request.getFilePath());
        review.setLineNumber(request.getLineNumber());
        review.setStatus(request.getStatus());
        review.setAgentType(request.getAgentType());
        review.setSeverity(request.getSeverity());
        review.setStatus(request.getStatus());

        return repository.save(review);
    }

    public List<Review> getAllReviews() {
        return repository.findAll();
    }

    public List<Review> getReviewsByPullRequest(Long pullRequestId) {
        return repository.findByPullRequestId(pullRequestId);
    }

}