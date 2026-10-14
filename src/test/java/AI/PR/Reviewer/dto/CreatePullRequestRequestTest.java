package AI.PR.Reviewer.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePullRequestRequestTest {

    @Test
    void shouldSetAndGetAllFields() {
        CreatePullRequestRequest request = new CreatePullRequestRequest();

        request.setRepositoryFullName("owner/repository");
        request.setPullRequestNumber(42);
        request.setTitle("Add authentication");
        request.setAuthor("developer");
        request.setSourceBranch("feature/auth");
        request.setTargetBranch("main");

        assertEquals("owner/repository", request.getRepositoryFullName());
        assertEquals(42, request.getPullRequestNumber());
        assertEquals("Add authentication", request.getTitle());
        assertEquals("developer", request.getAuthor());
        assertEquals("feature/auth", request.getSourceBranch());
        assertEquals("main", request.getTargetBranch());
    }
}