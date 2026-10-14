package AI.PR.Reviewer.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleDuplicateRepositoryShouldReturnConflictError() {
        DuplicateRepositoryException exception =
                new DuplicateRepositoryException("Repository already exists");

        Map<String, String> result =
                handler.handleDuplicateRepository(exception);

        assertEquals("DUPLICATE_REPOSITORY", result.get("error"));
        assertEquals("Repository already exists", result.get("message"));
        try {
            assertEquals(
                    HttpStatus.CONFLICT,
                    GlobalExceptionHandler.class
                            .getMethod("handleDuplicateRepository",
                                    DuplicateRepositoryException.class)
                            .getAnnotation(ResponseStatus.class)
                            .value()
            );
        } catch (NoSuchMethodException e) {
            fail("Handler method not found", e);
        }
    }

    @Test
    void handleRepositoryNotFoundShouldReturnNotFoundError() {
        RepositoryNotFoundException exception =
                new RepositoryNotFoundException("Repository not found");

        Map<String, String> result =
                handler.handleRepositoryNotFound(exception);

        assertEquals("REPOSITORY_NOT_FOUND", result.get("error"));
        assertEquals("Repository not found", result.get("message"));
        try {
            assertEquals(
                    HttpStatus.NOT_FOUND,
                    GlobalExceptionHandler.class
                            .getMethod("handleRepositoryNotFound",
                                    RepositoryNotFoundException.class)
                            .getAnnotation(ResponseStatus.class)
                            .value()
            );
        } catch (NoSuchMethodException e) {
            fail("Handler method not found", e);
        }
    }

    @Test
    void handleDuplicatePullRequestShouldReturnConflictError() {
        DuplicatePullRequestException exception =
                new DuplicatePullRequestException("Pull request already exists");

        Map<String, String> result =
                handler.handleDuplicatePullRequest(exception);

        assertEquals("DUPLICATE_PULL_REQUEST", result.get("error"));
        assertEquals("Pull request already exists", result.get("message"));
    }

    @Test
    void handlePullRequestNotFoundShouldReturnNotFoundError() {
        PullRequestNotFoundException exception =
                new PullRequestNotFoundException("Pull request not found");

        Map<String, String> result =
                handler.handlePullRequestNotFound(exception);

        assertEquals("PULL_REQUEST_NOT_FOUND", result.get("error"));
        assertEquals("Pull request not found", result.get("message"));
    }

    @Test
    void handleReviewFindingNotFoundShouldReturnNotFoundError() {
        ReviewFindingNotFoundException exception =
                new ReviewFindingNotFoundException("Finding not found");

        Map<String, String> result =
                handler.handleReviewFindingNotFound(exception);

        assertEquals("REVIEW_FINDING_NOT_FOUND", result.get("error"));
        assertEquals("Finding not found", result.get("message"));
    }

    @Test
    void handleReviewJobNotFoundShouldReturnNotFoundError() {
        ReviewJobNotFoundException exception =
                new ReviewJobNotFoundException("Review job not found");

        Map<String, String> result =
                handler.handleReviewJobNotFound(exception);

        assertEquals("REVIEW_JOB_NOT_FOUND", result.get("error"));
        assertEquals("Review job not found", result.get("message"));
    }

    @Test
    void handleIllegalStateShouldReturnConflictError() {
        IllegalStateException exception =
                new IllegalStateException("Invalid job state");

        Map<String, String> result =
                handler.handleIllegalState(exception);

        assertEquals("INVALID_JOB_STATE", result.get("error"));
        assertEquals("Invalid job state", result.get("message"));
    }
}