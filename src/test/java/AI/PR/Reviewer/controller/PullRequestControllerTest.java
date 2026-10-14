package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.dto.CreatePullRequestRequest;
import AI.PR.Reviewer.dto.UpdatePullRequestRequest;
import AI.PR.Reviewer.entity.PullRequest;
import AI.PR.Reviewer.service.PullRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PullRequestControllerTest {

    @Mock
    private PullRequestService service;

    @InjectMocks
    private PullRequestController controller;

    @Test
    void createPullRequest_shouldReturnSavedPullRequest() {
        CreatePullRequestRequest request =
                mock(CreatePullRequestRequest.class);
        PullRequest saved = new PullRequest();

        when(service.savePullRequest(request)).thenReturn(saved);

        PullRequest result = controller.createPullRequest(request);

        assertSame(saved, result);
        verify(service).savePullRequest(request);
    }

    @Test
    void getAllPullRequests_shouldReturnList() {
        List<PullRequest> pullRequests =
                List.of(new PullRequest(), new PullRequest());

        when(service.getAllPullRequests()).thenReturn(pullRequests);

        assertEquals(pullRequests, controller.getAllPullRequests());
        verify(service).getAllPullRequests();
    }

    @Test
    void getPullRequestById_shouldReturnPullRequest() {
        PullRequest pullRequest = new PullRequest();
        when(service.getPullRequestById(1L)).thenReturn(pullRequest);

        assertSame(pullRequest, controller.getPullRequestById(1L));
        verify(service).getPullRequestById(1L);
    }

    @Test
    void updatePullRequest_shouldReturnUpdatedPullRequest() {
        UpdatePullRequestRequest request =
                mock(UpdatePullRequestRequest.class);
        PullRequest updated = new PullRequest();

        when(service.updatePullRequest(1L, request))
                .thenReturn(updated);

        assertSame(updated, controller.updatePullRequest(1L, request));
        verify(service).updatePullRequest(1L, request);
    }

    @Test
    void deletePullRequest_shouldCallService() {
        assertDoesNotThrow(() -> controller.deletePullRequest(1L));

        verify(service).deletePullRequest(1L);
    }

    @Test
    void createPullRequest_shouldReturnCreatedStatusAnnotation() throws Exception {
        HttpStatus status = PullRequestController.class
                .getMethod("createPullRequest",
                        CreatePullRequestRequest.class)
                .getAnnotation(
                        org.springframework.web.bind.annotation.ResponseStatus.class)
                .value();

        assertEquals(HttpStatus.CREATED, status);
    }
}
