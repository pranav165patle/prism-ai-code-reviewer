package AI.PR.Reviewer.exception;

public class DuplicatePullRequestException extends RuntimeException {

    public DuplicatePullRequestException(String message) {
        super(message);
    }
}