package AI.PR.Reviewer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import AI.PR.Reviewer.exception.ReviewJobNotFoundException;

import java.util.Map;
import AI.PR.Reviewer.exception.ReviewFindingNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateRepositoryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateRepository(
            DuplicateRepositoryException exception) {

        return Map.of(
                "error", "DUPLICATE_REPOSITORY",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(RepositoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRepositoryNotFound(
            RepositoryNotFoundException exception) {

        return Map.of(
                "error", "REPOSITORY_NOT_FOUND",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicatePullRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicatePullRequest(
            DuplicatePullRequestException exception) {

        return Map.of(
                "error", "DUPLICATE_PULL_REQUEST",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(PullRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePullRequestNotFound(
            PullRequestNotFoundException exception) {

        return Map.of(
                "error", "PULL_REQUEST_NOT_FOUND",
                "message", exception.getMessage()
        );
    }
    @ExceptionHandler(ReviewFindingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleReviewFindingNotFound(
            ReviewFindingNotFoundException exception) {

        return Map.of(
                "error", "REVIEW_FINDING_NOT_FOUND",
                "message", exception.getMessage()
        );

    }
    @ExceptionHandler(ReviewJobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleReviewJobNotFound(
            ReviewJobNotFoundException exception) {

        return Map.of(
                "error", "REVIEW_JOB_NOT_FOUND",
                "message", exception.getMessage()
        );
    }
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleIllegalState(
            IllegalStateException exception) {

        return Map.of(
                "error", "INVALID_JOB_STATE",
                "message", exception.getMessage()
        );
    }
}