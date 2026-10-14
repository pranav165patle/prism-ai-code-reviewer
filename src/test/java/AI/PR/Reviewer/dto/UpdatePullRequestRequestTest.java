package AI.PR.Reviewer.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePullRequestRequestTest {

    @Test
    void shouldSetAndGetAllFields() {
        UpdatePullRequestRequest request = new UpdatePullRequestRequest();

        request.setRepositoryFullName("owner/repository");
        request.setPullRequestNumber(42);
        request.setTitle("Update authentication");
        request.setAuthor("developer");
        request.setSourceBranch("feature/update-auth");
        request.setTargetBranch("main");

        assertEquals("owner/repository", request.getRepositoryFullName());
        assertEquals(42, request.getPullRequestNumber());
        assertEquals("Update authentication", request.getTitle());
        assertEquals("developer", request.getAuthor());
        assertEquals("feature/update-auth", request.getSourceBranch());
        assertEquals("main", request.getTargetBranch());
    }
}