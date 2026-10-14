package AI.PR.Reviewer.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewJobTest {

    @Test
    void defaultConstructor_shouldCreateReviewJob() {
        ReviewJob job = new ReviewJob();

        assertNotNull(job);
        assertNull(job.getId());
        assertNull(job.getPullRequestId());
        assertNull(job.getStatus());
        assertNull(job.getCreatedAt());
        assertNull(job.getStartedAt());
        assertNull(job.getCompletedAt());
        assertNull(job.getErrorMessage());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        ReviewJob job = new ReviewJob();

        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 10, 10, 0);
        LocalDateTime startedAt = LocalDateTime.of(2026, 1, 10, 10, 1);
        LocalDateTime completedAt = LocalDateTime.of(2026, 1, 10, 10, 5);

        job.setPullRequestId(1L);
        job.setStatus(ReviewJobStatus.QUEUED);
        job.setCreatedAt(createdAt);
        job.setStartedAt(startedAt);
        job.setCompletedAt(completedAt);
        job.setErrorMessage("Test error");

        assertEquals(1L, job.getPullRequestId());
        assertEquals(ReviewJobStatus.QUEUED, job.getStatus());
        assertEquals(createdAt, job.getCreatedAt());
        assertEquals(startedAt, job.getStartedAt());
        assertEquals(completedAt, job.getCompletedAt());
        assertEquals("Test error", job.getErrorMessage());
    }

    @Test
    void setters_shouldAcceptNullValues() {
        ReviewJob job = new ReviewJob();

        job.setPullRequestId(null);
        job.setStatus(null);
        job.setCreatedAt(null);
        job.setStartedAt(null);
        job.setCompletedAt(null);
        job.setErrorMessage(null);

        assertNull(job.getPullRequestId());
        assertNull(job.getStatus());
        assertNull(job.getCreatedAt());
        assertNull(job.getStartedAt());
        assertNull(job.getCompletedAt());
        assertNull(job.getErrorMessage());
    }

    @Test
    void setters_shouldUpdateExistingValues() {
        ReviewJob job = new ReviewJob();

        LocalDateTime firstTime = LocalDateTime.of(2026, 1, 1, 9, 0);
        LocalDateTime updatedTime = LocalDateTime.of(2026, 1, 2, 10, 0);

        job.setPullRequestId(1L);
        job.setCreatedAt(firstTime);
        job.setStartedAt(firstTime);
        job.setCompletedAt(firstTime);
        job.setErrorMessage("Old error");

        job.setPullRequestId(2L);
        job.setCreatedAt(updatedTime);
        job.setStartedAt(updatedTime);
        job.setCompletedAt(updatedTime);
        job.setErrorMessage("Updated error");

        assertEquals(2L, job.getPullRequestId());
        assertEquals(updatedTime, job.getCreatedAt());
        assertEquals(updatedTime, job.getStartedAt());
        assertEquals(updatedTime, job.getCompletedAt());
        assertEquals("Updated error", job.getErrorMessage());
    }
}