package AI.PR.Reviewer.exception;

public class ReviewJobNotFoundException extends RuntimeException {

    public ReviewJobNotFoundException(String message) {
        super(message);
    }
}