package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.service.GithubService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/pull-request-diff")
    public String getPullRequestDiff(
            @RequestParam String repositoryFullName,
            @RequestParam Integer pullRequestNumber) {

        return githubService.getPullRequestDiff(
                repositoryFullName,
                pullRequestNumber
        );
    }
}