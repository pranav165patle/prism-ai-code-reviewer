package AI.PR.Reviewer.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PullRequestTest {

    @Test
    void defaultConstructorShouldCreatePullRequest() {
        PullRequest pullRequest = new PullRequest();

        assertNotNull(pullRequest);
        assertNull(pullRequest.getId());
        assertNull(pullRequest.getRepositoryFullName());
        assertNull(pullRequest.getPullRequestNumber());
        assertNull(pullRequest.getTitle());
        assertNull(pullRequest.getAuthor());
        assertNull(pullRequest.getSourceBranch());
        assertNull(pullRequest.getTargetBranch());
    }

    @Test
    void gettersAndSettersShouldWorkCorrectly() {
        PullRequest pullRequest = new PullRequest();

        pullRequest.setRepositoryFullName("owner/library-system");
        pullRequest.setPullRequestNumber(42);
        pullRequest.setTitle("Improve library search");
        pullRequest.setAuthor("developer");
        pullRequest.setSourceBranch("feature/search");
        pullRequest.setTargetBranch("main");

        assertEquals("owner/library-system",
                pullRequest.getRepositoryFullName());
        assertEquals(42, pullRequest.getPullRequestNumber());
        assertEquals("Improve library search", pullRequest.getTitle());
        assertEquals("developer", pullRequest.getAuthor());
        assertEquals("feature/search", pullRequest.getSourceBranch());
        assertEquals("main", pullRequest.getTargetBranch());
    }

    @Test
    void settersShouldAllowNullValues() {
        PullRequest pullRequest = new PullRequest();

        pullRequest.setRepositoryFullName(null);
        pullRequest.setPullRequestNumber(null);
        pullRequest.setTitle(null);
        pullRequest.setAuthor(null);
        pullRequest.setSourceBranch(null);
        pullRequest.setTargetBranch(null);

        assertNull(pullRequest.getRepositoryFullName());
        assertNull(pullRequest.getPullRequestNumber());
        assertNull(pullRequest.getTitle());
        assertNull(pullRequest.getAuthor());
        assertNull(pullRequest.getSourceBranch());
        assertNull(pullRequest.getTargetBranch());
    }
}