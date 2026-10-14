package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.dto.CreateReviewFindingRequest;
import AI.PR.Reviewer.entity.ReviewFinding;
import AI.PR.Reviewer.service.ReviewFindingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import AI.PR.Reviewer.entity.ReviewStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review-findings")
public class ReviewFindingController {

    private final ReviewFindingService service;

    public ReviewFindingController(ReviewFindingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewFinding createFinding(
            @Valid @RequestBody CreateReviewFindingRequest request) {

        return service.saveFinding(request);
    }

    @GetMapping
    public List<ReviewFinding> getAllFindings() {
        return service.getAllFindings();
    }

    @GetMapping("/pull-request/{pullRequestId}")
    public List<ReviewFinding> getFindingsByPullRequest(
            @PathVariable Long pullRequestId) {

        return service.getFindingsByPullRequest(pullRequestId);
    }
    @GetMapping("/{id}")
    public ReviewFinding getFindingById(
            @PathVariable Long id) {

        return service.getFindingById(id);
    }
    @PutMapping("/{id}/status")
    public ReviewFinding updateFindingStatus(
            @PathVariable Long id,
            @RequestParam ReviewStatus status) {

        return service.updateFindingStatus(id, status);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFinding(
            @PathVariable Long id) {

        service.deleteFinding(id);
    }
}