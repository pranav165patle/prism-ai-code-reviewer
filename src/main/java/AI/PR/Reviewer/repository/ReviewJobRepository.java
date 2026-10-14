package AI.PR.Reviewer.repository;

import AI.PR.Reviewer.entity.ReviewJob;
import java.util.List;
import AI.PR.Reviewer.entity.ReviewJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ReviewJobRepository
        extends JpaRepository<ReviewJob, Long> {

    List<ReviewJob> findByPullRequestId(Long pullRequestId);

    boolean existsByPullRequestIdAndStatusIn(
            Long pullRequestId,
            List<ReviewJobStatus> statuses
    );
}