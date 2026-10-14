package AI.PR.Reviewer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GithubServiceImplTest {

    private WebClient.Builder webClientBuilder;
    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    private GithubServiceImpl githubService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        webClientBuilder = mock(WebClient.Builder.class);
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(any(String.class)))
                .thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        githubService = new GithubServiceImpl(
                webClientBuilder, "https://api.github.com");
    }

    @Test
    void shouldFetchPullRequestDiff() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                any(String.class), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(String.class), any(String[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("diff --git a/Test.java b/Test.java"));

        String result = githubService.getPullRequestDiff("owner/repo", 10);

        assertNotNull(result);
        assertTrue(result.contains("diff --git"));
    }

    @Test
    void shouldReturnEmptyDiffWhenResponseIsEmpty() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                any(String.class), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(String.class), any(String[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.empty());

        String result = githubService.getPullRequestDiff("owner/repo", 10);

        assertNull(result);
    }

    @Test
    void shouldThrowWhenGitHubApiFails() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                any(String.class), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(String.class), any(String[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(
                        new RuntimeException("GitHub API failed")));

        assertThrows(RuntimeException.class,
                () -> githubService.getPullRequestDiff("owner/repo", 10));
    }
}