package AI.PR.Reviewer.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GithubRepositoryTest {

    @Test
    void defaultConstructor_shouldCreateRepository() {
        GithubRepository repository = new GithubRepository();

        assertNotNull(repository);
        assertNull(repository.getId());
        assertNull(repository.getOwner());
        assertNull(repository.getName());
        assertNull(repository.getFullName());
        assertNull(repository.getDefaultBranch());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        GithubRepository repository = new GithubRepository();

        repository.setOwner("openai");
        repository.setName("ai-pr-reviewer");
        repository.setFullName("openai/ai-pr-reviewer");
        repository.setDefaultBranch("main");

        assertEquals("openai", repository.getOwner());
        assertEquals("ai-pr-reviewer", repository.getName());
        assertEquals("openai/ai-pr-reviewer", repository.getFullName());
        assertEquals("main", repository.getDefaultBranch());
    }

    @Test
    void setters_shouldAcceptNullValues() {
        GithubRepository repository = new GithubRepository();

        repository.setOwner(null);
        repository.setName(null);
        repository.setFullName(null);
        repository.setDefaultBranch(null);

        assertNull(repository.getOwner());
        assertNull(repository.getName());
        assertNull(repository.getFullName());
        assertNull(repository.getDefaultBranch());
    }

    @Test
    void setters_shouldUpdateExistingValues() {
        GithubRepository repository = new GithubRepository();

        repository.setOwner("old-owner");
        repository.setName("old-repo");
        repository.setFullName("old-owner/old-repo");
        repository.setDefaultBranch("develop");

        repository.setOwner("new-owner");
        repository.setName("new-repo");
        repository.setFullName("new-owner/new-repo");
        repository.setDefaultBranch("main");

        assertEquals("new-owner", repository.getOwner());
        assertEquals("new-repo", repository.getName());
        assertEquals("new-owner/new-repo", repository.getFullName());
        assertEquals("main", repository.getDefaultBranch());
    }
}
