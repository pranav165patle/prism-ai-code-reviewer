package AI.PR.Reviewer.service;

import AI.PR.Reviewer.dto.CreatePullRequestRequest;
import AI.PR.Reviewer.dto.UpdatePullRequestRequest;
import AI.PR.Reviewer.entity.PullRequest;
import AI.PR.Reviewer.exception.DuplicatePullRequestException;
import AI.PR.Reviewer.exception.PullRequestNotFoundException;
import AI.PR.Reviewer.repository.PullRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PullRequestServiceTest {

    @Mock
    private PullRequestRepository repository;

    @InjectMocks
    private PullRequestService service;

    @Test
    void savePullRequest_shouldThrowWhenDuplicateExists() {
        CreatePullRequestRequest request =
                mock(CreatePullRequestRequest.class);

        when(request.getRepositoryFullName()).thenReturn("owner/repo");
        when(request.getPullRequestNumber()).thenReturn(10);
        when(repository.existsByRepositoryFullNameAndPullRequestNumber(
                "owner/repo", 10)).thenReturn(true);

        assertThrows(DuplicatePullRequestException.class,
                () -> service.savePullRequest(request));

        verify(repository, never()).save(any(PullRequest.class));
    }

    @Test
    void savePullRequest_shouldSaveNewPullRequest() {
        CreatePullRequestRequest request =
                mock(CreatePullRequestRequest.class);
        PullRequest saved = new PullRequest();

        when(request.getRepositoryFullName()).thenReturn("owner/repo");
        when(request.getPullRequestNumber()).thenReturn(10);
        when(repository.existsByRepositoryFullNameAndPullRequestNumber(
                "owner/repo", 10)).thenReturn(false);
        when(repository.save(any(PullRequest.class))).thenReturn(saved);

        assertSame(saved, service.savePullRequest(request));
        verify(repository).save(any(PullRequest.class));
    }

    @Test
    void getAllPullRequests_shouldReturnAll() {
        List<PullRequest> expected =
                List.of(new PullRequest(), new PullRequest());

        when(repository.findAll()).thenReturn(expected);

        assertEquals(expected, service.getAllPullRequests());
    }

    @Test
    void getPullRequestById_shouldReturnExistingPullRequest() {
        PullRequest expected = new PullRequest();

        when(repository.findById(1L))
                .thenReturn(Optional.of(expected));

        assertSame(expected, service.getPullRequestById(1L));
    }

    @Test
    void getPullRequestById_shouldThrowWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PullRequestNotFoundException.class,
                () -> service.getPullRequestById(99L));
    }

    @Test
    void updatePullRequest_shouldUpdateExistingPullRequest() {
        UpdatePullRequestRequest request =
                mock(UpdatePullRequestRequest.class);
        PullRequest existing = new PullRequest();

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.updatePullRequest(1L, request));
        verify(repository).save(existing);
    }

    @Test
    void updatePullRequest_shouldThrowWhenMissing() {
        UpdatePullRequestRequest request =
                mock(UpdatePullRequestRequest.class);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PullRequestNotFoundException.class,
                () -> service.updatePullRequest(99L, request));

        verify(repository, never()).save(any(PullRequest.class));
    }

    @Test
    void deletePullRequest_shouldThrowWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PullRequestNotFoundException.class,
                () -> service.deletePullRequest(99L));

        verify(repository, never()).delete(any(PullRequest.class));
    }
    @Test
    void deletePullRequest_shouldDeleteExistingPullRequest() {
        PullRequest existing = new PullRequest();

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        service.deletePullRequest(1L);

        verify(repository).delete(existing);
    }
}