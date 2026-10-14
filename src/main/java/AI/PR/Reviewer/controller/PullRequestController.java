package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.dto.CreatePullRequestRequest;
import AI.PR.Reviewer.entity.PullRequest;
import AI.PR.Reviewer.service.PullRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import AI.PR.Reviewer.dto.UpdatePullRequestRequest;

import java.util.List;

@RestController
@RequestMapping("/api/pull-requests")
public class PullRequestController {

    private final PullRequestService service;

    public PullRequestController(PullRequestService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PullRequest createPullRequest(
            @Valid @RequestBody CreatePullRequestRequest request) {

        return service.savePullRequest(request);
    }

    @GetMapping
    public List<PullRequest> getAllPullRequests() {
        return service.getAllPullRequests();
    }

    @GetMapping("/{id}")
    public PullRequest getPullRequestById(
            @PathVariable Long id) {

        return service.getPullRequestById(id);
    }
    @PutMapping("/{id}")
    public PullRequest updatePullRequest(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePullRequestRequest request) {

        return service.updatePullRequest(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePullRequest(
            @PathVariable Long id) {

        service.deletePullRequest(id);
    }
}