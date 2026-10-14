package AI.PR.Reviewer.service;

import AI.PR.Reviewer.agent.AgentFinding;
import AI.PR.Reviewer.agent.BugAgent;
import AI.PR.Reviewer.agent.QualityAgent;
import AI.PR.Reviewer.agent.SecurityAgent;
import AI.PR.Reviewer.dto.CreateReviewFindingRequest;
import AI.PR.Reviewer.entity.PullRequest;
import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import AI.PR.Reviewer.repository.PullRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewEngineImplTest {

    @Mock
    private PullRequestRepository pullRequestRepository;

    @Mock
    private GithubService githubService;

    @Mock
    private BugAgent bugAgent;

    @Mock
    private SecurityAgent securityAgent;

    @Mock
    private QualityAgent qualityAgent;

    @Mock
    private ReviewFindingService reviewFindingService;

    @InjectMocks
    private ReviewEngineImpl reviewEngine;

    private PullRequest createPullRequest() {
        PullRequest pr = new PullRequest();
        pr.setRepositoryFullName("owner/repo");
        pr.setPullRequestNumber(10);
        return pr;
    }

    @Test
    void shouldExecuteReviewAndSaveFinding() {
        PullRequest pr = createPullRequest();
        String diff = "+ System.out.println(\"test\");";

        when(pullRequestRepository.findById(1L))
                .thenReturn(Optional.of(pr));
        when(githubService.getPullRequestDiff("owner/repo", 10))
                .thenReturn(diff);

        AgentFinding finding = new AgentFinding(
                ReviewAgentType.QUALITY,
                ReviewSeverity.MEDIUM,
                "Avoid console output",
                "Test.java",
                1
        );

        when(bugAgent.analyze(1L, diff)).thenReturn(null);
        when(securityAgent.analyze(1L, diff)).thenReturn(null);
        when(qualityAgent.analyze(1L, diff)).thenReturn(finding);

        reviewEngine.execute(1L);

        verify(bugAgent, times(1)).analyze(1L, diff);
        verify(securityAgent, times(1)).analyze(1L, diff);
        verify(qualityAgent, times(1)).analyze(1L, diff);
        verify(reviewFindingService, times(1))
                .saveFinding(any(CreateReviewFindingRequest.class));
    }

    @Test
    void shouldThrowWhenPullRequestDoesNotExist() {
        when(pullRequestRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> reviewEngine.execute(99L));

        verifyNoInteractions(githubService, bugAgent,
                securityAgent, qualityAgent, reviewFindingService);
    }

    @Test
    void shouldThrowWhenDiffIsEmpty() {
        PullRequest pr = createPullRequest();

        when(pullRequestRepository.findById(1L))
                .thenReturn(Optional.of(pr));
        when(githubService.getPullRequestDiff("owner/repo", 10))
                .thenReturn(" ");

        assertThrows(IllegalStateException.class,
                () -> reviewEngine.execute(1L));

        verifyNoInteractions(bugAgent, securityAgent,
                qualityAgent, reviewFindingService);
    }

    @Test
    void shouldNotSaveWhenNoAgentFindsIssues() {
        PullRequest pr = createPullRequest();

        when(pullRequestRepository.findById(1L))
                .thenReturn(Optional.of(pr));
        when(githubService.getPullRequestDiff("owner/repo", 10))
                .thenReturn("clean diff");

        when(bugAgent.analyze(1L, "clean diff")).thenReturn(null);
        when(securityAgent.analyze(1L, "clean diff")).thenReturn(null);
        when(qualityAgent.analyze(1L, "clean diff")).thenReturn(null);

        reviewEngine.execute(1L);

        verifyNoInteractions(reviewFindingService);
    }

    @Test
    void shouldSaveAllThreeFindings() {
        PullRequest pr = createPullRequest();

        when(pullRequestRepository.findById(1L))
                .thenReturn(Optional.of(pr));
        when(githubService.getPullRequestDiff("owner/repo", 10))
                .thenReturn("test diff");

        when(bugAgent.analyze(1L, "test diff"))
                .thenReturn(new AgentFinding(
                        ReviewAgentType.BUG, ReviewSeverity.MEDIUM,
                        "Bug detected", "A.java", 5));

        when(securityAgent.analyze(1L, "test diff"))
                .thenReturn(new AgentFinding(
                        ReviewAgentType.SECURITY, ReviewSeverity.HIGH,
                        "Secret detected", "B.java", 8));

        when(qualityAgent.analyze(1L, "test diff"))
                .thenReturn(new AgentFinding(
                        ReviewAgentType.QUALITY, ReviewSeverity.LOW,
                        "Quality issue", "C.java", 12));

        reviewEngine.execute(1L);

        verify(reviewFindingService, times(3))
                .saveFinding(any(CreateReviewFindingRequest.class));
    }

    @Test
    void shouldSaveOnlyNonNullFindings() {
        PullRequest pr = createPullRequest();

        when(pullRequestRepository.findById(1L))
                .thenReturn(Optional.of(pr));
        when(githubService.getPullRequestDiff("owner/repo", 10))
                .thenReturn("test diff");

        when(bugAgent.analyze(1L, "test diff"))
                .thenReturn(new AgentFinding(
                        ReviewAgentType.BUG, ReviewSeverity.MEDIUM,
                        "Bug detected", "A.java", 2));

        when(securityAgent.analyze(1L, "test diff")).thenReturn(null);
        when(qualityAgent.analyze(1L, "test diff")).thenReturn(null);

        reviewEngine.execute(1L);

        verify(reviewFindingService, times(1))
                .saveFinding(any(CreateReviewFindingRequest.class));
    }
    @Test
    void shouldThrowWhenDiffIsNull() {
        PullRequest pr = createPullRequest();

        when(pullRequestRepository.findById(1L))
                .thenReturn(Optional.of(pr));
        when(githubService.getPullRequestDiff("owner/repo", 10))
                .thenReturn(null);

        assertThrows(
                IllegalStateException.class,
                () -> reviewEngine.execute(1L)
        );

        verifyNoInteractions(
                bugAgent,
                securityAgent,
                qualityAgent,
                reviewFindingService
        );
    }
}
