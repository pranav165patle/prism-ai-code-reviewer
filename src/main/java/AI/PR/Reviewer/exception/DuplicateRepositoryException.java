package AI.PR.Reviewer.exception;

public class DuplicateRepositoryException extends RuntimeException {

    public DuplicateRepositoryException(String message) {
        super(message);
    }
}