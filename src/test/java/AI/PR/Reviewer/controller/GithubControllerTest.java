package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.service.GithubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubControllerTest {

    @Mock
    private GithubService githubService;

    @InjectMocks
    private GithubController controller;

    @Test
    void getPullRequestDiff_shouldReturnDiff() {
        String expectedDiff = "diff --git a/Test.java b/Test.java";

        when(githubService.getPullRequestDiff("owner/repo", 10))
                .thenReturn(expectedDiff);

        String result = controller.getPullRequestDiff("owner/repo", 10);

        assertEquals(expectedDiff, result);
        verify(githubService).getPullRequestDiff("owner/repo", 10);
    }

    @Test
    void getPullRequestDiff_shouldPassCorrectParameters() {
        when(githubService.getPullRequestDiff("openai/project", 5))
                .thenReturn("sample diff");

        controller.getPullRequestDiff("openai/project", 5);

        verify(githubService).getPullRequestDiff("openai/project", 5);
    }

    @Test
    void getPullRequestDiff_shouldReturnEmptyDiff() {
        when(githubService.getPullRequestDiff("owner/repo", 1))
                .thenReturn("");

        String result = controller.getPullRequestDiff("owner/repo", 1);

        assertEquals("", result);
    }
}
