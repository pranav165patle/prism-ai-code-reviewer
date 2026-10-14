package AI.PR.Reviewer.service;

public interface GithubService {
    String getPullRequestDiff(
            String repositoryFullName,
            Integer pullRequestNumber
    );
}
