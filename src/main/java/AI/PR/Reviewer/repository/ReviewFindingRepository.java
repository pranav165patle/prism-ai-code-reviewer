package AI.PR.Reviewer.repository;

import AI.PR.Reviewer.entity.ReviewFinding;
import java.util.Optional;
import AI.PR.Reviewer.entity.ReviewAgentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewFindingRepository
        extends JpaRepository<ReviewFinding, Long> {

    List<ReviewFinding> findByPullRequestId(Long pullRequestId);
    Optional<ReviewFinding> findByPullRequestIdAndAgentTypeAndFilePathAndLineNumber(
            Long pullRequestId,
            ReviewAgentType agentType,
            String filePath,
            Integer lineNumber
    );
}