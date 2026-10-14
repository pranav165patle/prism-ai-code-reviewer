package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.dto.CreateRepositoryRequest;
import AI.PR.Reviewer.dto.UpdateRepositoryRequest;
import AI.PR.Reviewer.entity.GithubRepository;
import AI.PR.Reviewer.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryControllerTest {

    @Mock
    private GithubRepositoryService service;

    @InjectMocks
    private GithubRepositoryController controller;

    @Test
    void createRepository_shouldReturnSavedRepository() {
        CreateRepositoryRequest request =
                mock(CreateRepositoryRequest.class);
        GithubRepository saved = new GithubRepository();

        when(service.saveRepository(request)).thenReturn(saved);

        assertSame(saved, controller.createRepository(request));
        verify(service).saveRepository(request);
    }

    @Test
    void getAllRepositories_shouldReturnRepositories() {
        List<GithubRepository> repositories =
                List.of(new GithubRepository(), new GithubRepository());

        when(service.getAllRepositories()).thenReturn(repositories);

        assertEquals(repositories, controller.getAllRepositories());
        verify(service).getAllRepositories();
    }

    @Test
    void getRepositoryById_shouldReturnRepository() {
        GithubRepository repository = new GithubRepository();

        when(service.getRepositoryById(1L)).thenReturn(repository);

        assertSame(repository, controller.getRepositoryById(1L));
        verify(service).getRepositoryById(1L);
    }

    @Test
    void updateRepository_shouldReturnUpdatedRepository() {
        UpdateRepositoryRequest request =
                mock(UpdateRepositoryRequest.class);
        GithubRepository updated = new GithubRepository();

        when(service.updateRepository(1L, request)).thenReturn(updated);

        assertSame(updated, controller.updateRepository(1L, request));
        verify(service).updateRepository(1L, request);
    }

    @Test
    void deleteRepository_shouldCallService() {
        assertDoesNotThrow(() -> controller.deleteRepository(1L));

        verify(service).deleteRepository(1L);
    }

    @Test
    void createRepository_shouldHaveCreatedStatus() throws Exception {
        ResponseStatus annotation = GithubRepositoryController.class
                .getMethod("createRepository", CreateRepositoryRequest.class)
                .getAnnotation(ResponseStatus.class);

        assertNotNull(annotation);
        assertEquals(HttpStatus.CREATED, annotation.value());
    }

    @Test
    void deleteRepository_shouldHaveNoContentStatus() throws Exception {
        ResponseStatus annotation = GithubRepositoryController.class
                .getMethod("deleteRepository", Long.class)
                .getAnnotation(ResponseStatus.class);

        assertNotNull(annotation);
        assertEquals(HttpStatus.NO_CONTENT, annotation.value());
    }
}