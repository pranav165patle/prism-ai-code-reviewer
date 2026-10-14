package AI.PR.Reviewer.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GithubServiceImpl implements GithubService {

    private final WebClient webClient;
    public GithubServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${github.api-url}") String githubApiUrl) {

        this.webClient = webClientBuilder
                .baseUrl(githubApiUrl)
                .build();
    }

    @Override
    public String getPullRequestDiff(
            String repositoryFullName,
            Integer pullRequestNumber) {

        return webClient
                .get()
                .uri("/repos/{owner}/{repo}/pulls/{pullRequestNumber}",
                        repositoryFullName.split("/")[0],
                        repositoryFullName.split("/")[1],
                        pullRequestNumber)
                .header("Accept", "application/vnd.github.v3.diff")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}