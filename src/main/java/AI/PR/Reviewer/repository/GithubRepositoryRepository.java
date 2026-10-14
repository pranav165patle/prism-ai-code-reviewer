package AI.PR.Reviewer.repository;

import AI.PR.Reviewer.entity.GithubRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepositoryRepository
        extends JpaRepository<GithubRepository, Long> {
    boolean existsByFullName(String fullName);
}