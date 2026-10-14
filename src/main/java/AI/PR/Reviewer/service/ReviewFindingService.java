package AI.PR.Reviewer.service;

import AI.PR.Reviewer.dto.CreateReviewFindingRequest;
import AI.PR.Reviewer.entity.ReviewFinding;
import AI.PR.Reviewer.exception.PullRequestNotFoundException;
import AI.PR.Reviewer.exception.ReviewFindingNotFoundException;
import AI.PR.Reviewer.repository.PullRequestRepository;
import AI.PR.Reviewer.entity.ReviewStatus;
import AI.PR.Reviewer.repository.ReviewFindingRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
@Service
public class ReviewFindingService {

    private final ReviewFindingRepository repository;
    private final PullRequestRepository pullRequestRepository;

    public ReviewFindingService(
            ReviewFindingRepository repository,
            PullRequestRepository pullRequestRepository) {

        this.repository = repository;
        this.pullRequestRepository = pullRequestRepository;
    }

    public ReviewFinding saveFinding(CreateReviewFindingRequest request) {

        if (!pullRequestRepository.existsById(request.getPullRequestId())) {
            throw new PullRequestNotFoundException(
                    "Pull request not found with id: "
                            + request.getPullRequestId()
            );
        }

        log.debug(
                "Checking existing finding | pullRequestId={} | agentType={} | filePath={} | lineNumber={}",
                request.getPullRequestId(),
                request.getAgentType(),
                request.getFilePath(),
                request.getLineNumber()
        );

        return repository
                .findByPullRequestIdAndAgentTypeAndFilePathAndLineNumber(
                        request.getPullRequestId(),
                        request.getAgentType(),
                        request.getFilePath(),
                        request.getLineNumber()
                )
                .orElseGet(() -> {

                    ReviewFinding finding = new ReviewFinding();

                    finding.setPullRequestId(request.getPullRequestId());
                    finding.setAgentType(request.getAgentType());
                    finding.setSeverity(request.getSeverity());
                    finding.setMessage(request.getMessage());
                    finding.setFilePath(request.getFilePath());
                    finding.setLineNumber(request.getLineNumber());
                    finding.setStatus(request.getStatus());

                    return repository.save(finding);
                });
    }

    public List<ReviewFinding> getAllFindings() {
        return repository.findAll();
    }

    public List<ReviewFinding> getFindingsByPullRequest(
            Long pullRequestId) {

        return repository.findByPullRequestId(pullRequestId);
    }

    public ReviewFinding getFindingById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ReviewFindingNotFoundException(
                                "Review finding not found with id: " + id
                        )
                );
    }
    public ReviewFinding updateFindingStatus(Long id, ReviewStatus status) {

        ReviewFinding finding = repository.findById(id)
                .orElseThrow(() ->
                        new ReviewFindingNotFoundException(
                                "Review finding not found with id: " + id
                        )
                );

        finding.setStatus(status);

        return repository.save(finding);
    }

    public void deleteFinding(Long id) {

        ReviewFinding finding = repository.findById(id)
                .orElseThrow(() ->
                        new ReviewFindingNotFoundException(
                                "Review finding not found with id: " + id
                        )
                );

        repository.delete(finding);
    }
}