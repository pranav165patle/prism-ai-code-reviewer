package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.entity.ReviewJob;
import AI.PR.Reviewer.service.ReviewJobService;
import org.springframework.web.bind.annotation.*;
import AI.PR.Reviewer.worker.ReviewWorker;

import java.util.List;

@RestController
@RequestMapping("/api/review-jobs")
public class ReviewJobController {
    private final ReviewWorker reviewWorker;

    private final ReviewJobService service;

    public ReviewJobController(
            ReviewJobService service,
            ReviewWorker reviewWorker) {

        this.service = service;
        this.reviewWorker = reviewWorker;
    }

    @PostMapping("/{pullRequestId}")
    public ReviewJob createJob(
            @PathVariable Long pullRequestId) {

        return service.createJob(pullRequestId);
    }

    @GetMapping
    public List<ReviewJob> getAllJobs() {
        return service.getAllJobs();
    }

    @GetMapping("/{id}")
    public ReviewJob getJobById(
            @PathVariable Long id) {

        return service.getJobById(id);
    }

    @GetMapping("/pull-request/{pullRequestId}")
    public List<ReviewJob> getJobsByPullRequest(
            @PathVariable Long pullRequestId) {

        return service.getJobsByPullRequest(pullRequestId);
    }

    @PutMapping("/{id}/start")
    public ReviewJob startJob(
            @PathVariable Long id) {

        return service.startJob(id);
    }
    @PutMapping("/{id}/complete")
    public ReviewJob completeJob(
            @PathVariable Long id) {

        return service.completeJob(id);
    }
    @PutMapping("/{id}/fail")
    public ReviewJob failJob(
            @PathVariable Long id,
            @RequestParam String errorMessage) {

        return service.failJob(id, errorMessage);
    }
    @PostMapping("/{id}/process")
    public void processJob(
            @PathVariable Long id) {

        reviewWorker.process(id);
    }

}