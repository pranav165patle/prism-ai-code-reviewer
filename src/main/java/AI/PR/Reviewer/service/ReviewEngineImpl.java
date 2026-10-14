package AI.PR.Reviewer.service;

import AI.PR.Reviewer.agent.BugAgent;
import AI.PR.Reviewer.agent.QualityAgent;
import AI.PR.Reviewer.agent.SecurityAgent;
import AI.PR.Reviewer.dto.CreateReviewFindingRequest;
import AI.PR.Reviewer.entity.PullRequest;
import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import AI.PR.Reviewer.entity.ReviewStatus;
import AI.PR.Reviewer.repository.PullRequestRepository;
import AI.PR.Reviewer.agent.AgentFinding;
import AI.PR.Reviewer.service.ReviewFindingService;
import AI.PR.Reviewer.service.GithubService;
import AI.PR.Reviewer.service.ReviewEngine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ReviewEngineImpl implements ReviewEngine {

    private final PullRequestRepository pullRequestRepository;
    private final GithubService githubService;
    private final BugAgent bugAgent;
    private final SecurityAgent securityAgent;
    private final QualityAgent qualityAgent;
    private final ReviewFindingService reviewFindingService;

    public ReviewEngineImpl(
            PullRequestRepository pullRequestRepository,
            GithubService githubService,
            BugAgent bugAgent,
            SecurityAgent securityAgent,
            QualityAgent qualityAgent,
            ReviewFindingService reviewFindingService
    ) {
        this.pullRequestRepository = pullRequestRepository;
        this.githubService = githubService;
        this.bugAgent = bugAgent;
        this.securityAgent = securityAgent;
        this.qualityAgent = qualityAgent;
        this.reviewFindingService = reviewFindingService;
    }

    public void execute(Long pullRequestId) {

        log.info(
                "Starting review engine | pullRequestId={}",
                pullRequestId
        );

        PullRequest pullRequest = pullRequestRepository
                .findById(pullRequestId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Pull request not found: " + pullRequestId
                        )
                );

        String diff = githubService.getPullRequestDiff(
                pullRequest.getRepositoryFullName(),
                pullRequest.getPullRequestNumber()
        );

        if (diff == null || diff.isBlank()) {
            throw new IllegalStateException(
                    "GitHub returned an empty diff for pull request: "
                            + pullRequestId
            );
        }

        log.info(
                "GitHub diff fetched | pullRequestId={} | diffLength={}",
                pullRequestId,
                diff.length()
        );

        // Run each agent exactly once.
        AgentFinding bugFinding =
                bugAgent.analyze(pullRequestId, diff);

        AgentFinding securityFinding =
                securityAgent.analyze(pullRequestId, diff);

        AgentFinding qualityFinding =
                qualityAgent.analyze(pullRequestId, diff);

        // Log which agents actually detected an issue.
        log.info(
                "Agent results | BUG={} | SECURITY={} | QUALITY={}",
                bugFinding != null,
                securityFinding != null,
                qualityFinding != null
        );

        List<AgentFinding> findings = new ArrayList<>();

        findings.add(bugFinding);
        findings.add(securityFinding);
        findings.add(qualityFinding);

        long actualFindingCount = findings.stream()
                .filter(finding -> finding != null)
                .count();

        log.info(
                "Actual findings generated: {}",
                actualFindingCount
        );

        // Save only actual findings; ignore null results.
        for (AgentFinding finding : findings) {

            if (finding == null) {
                continue;
            }

            saveFinding(pullRequestId, finding);
        }

        log.info(
                "Review engine completed | pullRequestId={} | findings={}",
                pullRequestId,
                actualFindingCount
        );
    }

    private void saveFinding(
            Long pullRequestId,
            AgentFinding finding
    ) {

        CreateReviewFindingRequest request =
                new CreateReviewFindingRequest();

        request.setPullRequestId(pullRequestId);
        request.setAgentType(finding.getAgentType());
        request.setSeverity(finding.getSeverity());
        request.setMessage(finding.getMessage());
        request.setFilePath(finding.getFilePath());
        request.setLineNumber(finding.getLineNumber());
        request.setStatus(ReviewStatus.OPEN);

        reviewFindingService.saveFinding(request);
    }
}