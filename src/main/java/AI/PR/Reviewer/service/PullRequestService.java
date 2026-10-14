package AI.PR.Reviewer.service;

import AI.PR.Reviewer.dto.CreatePullRequestRequest;
import AI.PR.Reviewer.entity.PullRequest;
import AI.PR.Reviewer.exception.DuplicatePullRequestException;
import AI.PR.Reviewer.exception.PullRequestNotFoundException;
import AI.PR.Reviewer.repository.PullRequestRepository;
import org.springframework.stereotype.Service;
import AI.PR.Reviewer.dto.UpdatePullRequestRequest;

import java.util.List;

@Service
public class PullRequestService {

    private final PullRequestRepository repository;

    public PullRequestService(PullRequestRepository repository) {
        this.repository = repository;
    }

    public PullRequest savePullRequest(CreatePullRequestRequest request) {

        if (repository.existsByRepositoryFullNameAndPullRequestNumber(
                request.getRepositoryFullName(),
                request.getPullRequestNumber())) {

            throw new DuplicatePullRequestException(
                    "Pull request already exists: "
                            + request.getRepositoryFullName()
                            + " #" + request.getPullRequestNumber()
            );
        }

        PullRequest pullRequest = new PullRequest();

        pullRequest.setRepositoryFullName(request.getRepositoryFullName());
        pullRequest.setPullRequestNumber(request.getPullRequestNumber());
        pullRequest.setTitle(request.getTitle());
        pullRequest.setAuthor(request.getAuthor());
        pullRequest.setSourceBranch(request.getSourceBranch());
        pullRequest.setTargetBranch(request.getTargetBranch());

        return repository.save(pullRequest);
    }

    public List<PullRequest> getAllPullRequests() {
        return repository.findAll();
    }

    public PullRequest getPullRequestById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new PullRequestNotFoundException(
                                "Pull request not found with id: " + id
                        )
                );
    }
    public PullRequest updatePullRequest(
            Long id,
            UpdatePullRequestRequest request) {

        PullRequest pullRequest = repository.findById(id)
                .orElseThrow(() ->
                        new PullRequestNotFoundException(
                                "Pull request not found with id: " + id
                        )
                );

        pullRequest.setRepositoryFullName(request.getRepositoryFullName());
        pullRequest.setPullRequestNumber(request.getPullRequestNumber());
        pullRequest.setTitle(request.getTitle());
        pullRequest.setAuthor(request.getAuthor());
        pullRequest.setSourceBranch(request.getSourceBranch());
        pullRequest.setTargetBranch(request.getTargetBranch());

        return repository.save(pullRequest);
    }

    public void deletePullRequest(Long id) {

        PullRequest pullRequest = repository.findById(id)
                .orElseThrow(() ->
                        new PullRequestNotFoundException(
                                "Pull request not found with id: " + id
                        )
                );

        repository.delete(pullRequest);
    }
}