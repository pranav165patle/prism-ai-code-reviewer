package AI.PR.Reviewer.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void defaultConstructor_shouldCreateReview() {
        Review review = new Review();

        assertNotNull(review);
        assertNull(review.getId());
        assertNull(review.getPullRequestId());
        assertNull(review.getAgentType());
        assertNull(review.getSeverity());
        assertNull(review.getMessage());
        assertNull(review.getFilePath());
        assertNull(review.getLineNumber());
        assertNull(review.getStatus());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        Review review = new Review();

        review.setPullRequestId(1L);
        review.setAgentType(ReviewAgentType.QUALITY);
        review.setSeverity(ReviewSeverity.LOW);
        review.setMessage("Potential code quality issue");
        review.setFilePath("src/main/java/App.java");
        review.setLineNumber(25);
        review.setStatus(ReviewStatus.OPEN);

        assertEquals(1L, review.getPullRequestId());
        assertEquals(ReviewAgentType.QUALITY, review.getAgentType());
        assertEquals(ReviewSeverity.LOW, review.getSeverity());
        assertEquals("Potential code quality issue", review.getMessage());
        assertEquals("src/main/java/App.java", review.getFilePath());
        assertEquals(25, review.getLineNumber());
        assertEquals(ReviewStatus.OPEN, review.getStatus());
    }

    @Test
    void setters_shouldAcceptNullValues() {
        Review review = new Review();

        review.setPullRequestId(null);
        review.setAgentType(null);
        review.setSeverity(null);
        review.setMessage(null);
        review.setFilePath(null);
        review.setLineNumber(null);
        review.setStatus(null);

        assertNull(review.getPullRequestId());
        assertNull(review.getAgentType());
        assertNull(review.getSeverity());
        assertNull(review.getMessage());
        assertNull(review.getFilePath());
        assertNull(review.getLineNumber());
        assertNull(review.getStatus());
    }

    @Test
    void setters_shouldUpdateExistingValues() {
        Review review = new Review();

        review.setPullRequestId(1L);
        review.setMessage("Old message");
        review.setFilePath("Old.java");
        review.setLineNumber(10);
        review.setStatus(ReviewStatus.OPEN);

        review.setPullRequestId(2L);
        review.setMessage("Updated message");
        review.setFilePath("Updated.java");
        review.setLineNumber(20);
        review.setStatus(ReviewStatus.RESOLVED);

        assertEquals(2L, review.getPullRequestId());
        assertEquals("Updated message", review.getMessage());
        assertEquals("Updated.java", review.getFilePath());
        assertEquals(20, review.getLineNumber());
        assertEquals(ReviewStatus.RESOLVED, review.getStatus());
    }
}