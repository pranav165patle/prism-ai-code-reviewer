package AI.PR.Reviewer.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "pull_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_repository_pr_number",
                        columnNames = {
                                "repository_full_name",
                                "pull_request_number"
                        }
                )
        }
)
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String repositoryFullName;

    @Column(nullable = false)
    private Integer pullRequestNumber;

    @Column(nullable = false)
    private String title;

    private String author;

    private String sourceBranch;

    private String targetBranch;

    public PullRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getRepositoryFullName() {
        return repositoryFullName;
    }

    public void setRepositoryFullName(String repositoryFullName) {
        this.repositoryFullName = repositoryFullName;
    }

    public Integer getPullRequestNumber() {
        return pullRequestNumber;
    }

    public void setPullRequestNumber(Integer pullRequestNumber) {
        this.pullRequestNumber = pullRequestNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
    }
}