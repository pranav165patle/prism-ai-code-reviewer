package AI.PR.Reviewer.repository;

import AI.PR.Reviewer.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestRepository
        extends JpaRepository<PullRequest, Long> {

    boolean existsByRepositoryFullNameAndPullRequestNumber(
            String repositoryFullName,
            Integer pullRequestNumber
    );
}