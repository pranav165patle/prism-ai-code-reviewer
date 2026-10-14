package AI.PR.Reviewer.service;

import AI.PR.Reviewer.dto.CreateRepositoryRequest;
import AI.PR.Reviewer.dto.UpdateRepositoryRequest;
import AI.PR.Reviewer.entity.GithubRepository;
import AI.PR.Reviewer.exception.DuplicateRepositoryException;
import AI.PR.Reviewer.exception.RepositoryNotFoundException;
import AI.PR.Reviewer.repository.GithubRepositoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {

    @Mock
    private GithubRepositoryRepository repository;

    @InjectMocks
    private GithubRepositoryService service;

    private GithubRepository githubRepository;
    private CreateRepositoryRequest createRequest;
    private UpdateRepositoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        githubRepository = new GithubRepository();
        githubRepository.setOwner("openai");
        githubRepository.setName("demo-project");
        githubRepository.setFullName("openai/demo-project");
        githubRepository.setDefaultBranch("main");

        createRequest = new CreateRepositoryRequest();
        createRequest.setOwner("openai");
        createRequest.setName("demo-project");
        createRequest.setFullName("openai/demo-project");
        createRequest.setDefaultBranch("main");

        updateRequest = new UpdateRepositoryRequest();
        updateRequest.setOwner("openai");
        updateRequest.setName("updated-project");
        updateRequest.setFullName("openai/updated-project");
        updateRequest.setDefaultBranch("develop");
    }

    @Test
    void saveRepositoryShouldSaveNewRepository() {
        when(repository.existsByFullName("openai/demo-project"))
                .thenReturn(false);
        when(repository.save(any(GithubRepository.class)))
                .thenReturn(githubRepository);

        GithubRepository result = service.saveRepository(createRequest);

        assertNotNull(result);
        assertEquals("openai/demo-project", result.getFullName());
        verify(repository).save(any(GithubRepository.class));
    }

    @Test
    void saveRepositoryShouldThrowWhenRepositoryAlreadyExists() {
        when(repository.existsByFullName("openai/demo-project"))
                .thenReturn(true);

        assertThrows(
                DuplicateRepositoryException.class,
                () -> service.saveRepository(createRequest)
        );

        verify(repository, never()).save(any(GithubRepository.class));
    }

    @Test
    void getAllRepositoriesShouldReturnRepositories() {
        when(repository.findAll()).thenReturn(List.of(githubRepository));

        List<GithubRepository> result = service.getAllRepositories();

        assertEquals(1, result.size());
        assertEquals("openai/demo-project", result.get(0).getFullName());
    }

    @Test
    void getRepositoryByIdShouldReturnRepository() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(githubRepository));

        GithubRepository result = service.getRepositoryById(1L);

        assertSame(githubRepository, result);
    }

    @Test
    void getRepositoryByIdShouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RepositoryNotFoundException.class,
                () -> service.getRepositoryById(99L)
        );
    }

    @Test
    void updateRepositoryShouldUpdateExistingRepository() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(githubRepository));
        when(repository.existsByFullName("openai/updated-project"))
                .thenReturn(false);
        when(repository.save(githubRepository)).thenReturn(githubRepository);

        GithubRepository result =
                service.updateRepository(1L, updateRequest);

        assertSame(githubRepository, result);
        assertEquals("openai/updated-project", result.getFullName());
        assertEquals("develop", result.getDefaultBranch());
        verify(repository).save(githubRepository);
    }

    @Test
    void updateRepositoryShouldThrowWhenNewNameAlreadyExists() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(githubRepository));
        when(repository.existsByFullName("openai/updated-project"))
                .thenReturn(true);

        assertThrows(
                DuplicateRepositoryException.class,
                () -> service.updateRepository(1L, updateRequest)
        );

        verify(repository, never()).save(any(GithubRepository.class));
    }

    @Test
    void deleteRepositoryShouldDeleteExistingRepository() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(githubRepository));

        service.deleteRepository(1L);

        verify(repository).delete(githubRepository);
    }
    @Test
    void updateRepositoryShouldSkipDuplicateCheckWhenFullNameIsUnchanged() {
        updateRequest.setFullName("openai/demo-project");

        when(repository.findById(1L))
                .thenReturn(Optional.of(githubRepository));
        when(repository.save(githubRepository))
                .thenReturn(githubRepository);

        GithubRepository result =
                service.updateRepository(1L, updateRequest);

        assertSame(githubRepository, result);
        assertEquals("openai/demo-project", result.getFullName());

        verify(repository, never()).existsByFullName(anyString());
        verify(repository).save(githubRepository);
    }

    @Test
    void updateRepositoryShouldThrowWhenRepositoryIsNotFound() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RepositoryNotFoundException.class,
                () -> service.updateRepository(99L, updateRequest)
        );

        verify(repository, never()).save(any(GithubRepository.class));
    }

    @Test
    void deleteRepositoryShouldThrowWhenRepositoryIsNotFound() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RepositoryNotFoundException.class,
                () -> service.deleteRepository(99L)
        );

        verify(repository, never()).delete(any(GithubRepository.class));
    }
}