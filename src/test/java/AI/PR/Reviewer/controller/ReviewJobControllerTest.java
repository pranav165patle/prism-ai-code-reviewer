package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.entity.ReviewJob;
import AI.PR.Reviewer.service.ReviewJobService;
import AI.PR.Reviewer.worker.ReviewWorker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewJobControllerTest {

    @Mock
    private ReviewJobService service;

    @Mock
    private ReviewWorker reviewWorker;

    @InjectMocks
    private ReviewJobController controller;

    @Test
    void createJob_shouldReturnCreatedJob() {
        ReviewJob job = new ReviewJob();
        when(service.createJob(1L)).thenReturn(job);

        assertSame(job, controller.createJob(1L));
        verify(service).createJob(1L);
    }

    @Test
    void getAllJobs_shouldReturnJobs() {
        List<ReviewJob> jobs = List.of(new ReviewJob(), new ReviewJob());
        when(service.getAllJobs()).thenReturn(jobs);

        assertEquals(jobs, controller.getAllJobs());
        verify(service).getAllJobs();
    }

    @Test
    void getJobById_shouldReturnJob() {
        ReviewJob job = new ReviewJob();
        when(service.getJobById(1L)).thenReturn(job);

        assertSame(job, controller.getJobById(1L));
        verify(service).getJobById(1L);
    }

    @Test
    void getJobsByPullRequest_shouldReturnJobs() {
        List<ReviewJob> jobs = List.of(new ReviewJob());
        when(service.getJobsByPullRequest(1L)).thenReturn(jobs);

        assertEquals(jobs, controller.getJobsByPullRequest(1L));
        verify(service).getJobsByPullRequest(1L);
    }

    @Test
    void startJob_shouldReturnStartedJob() {
        ReviewJob job = new ReviewJob();
        when(service.startJob(1L)).thenReturn(job);

        assertSame(job, controller.startJob(1L));
        verify(service).startJob(1L);
    }

    @Test
    void completeJob_shouldReturnCompletedJob() {
        ReviewJob job = new ReviewJob();
        when(service.completeJob(1L)).thenReturn(job);

        assertSame(job, controller.completeJob(1L));
        verify(service).completeJob(1L);
    }

    @Test
    void failJob_shouldPassErrorMessageToService() {
        ReviewJob job = new ReviewJob();
        when(service.failJob(1L, "Test error")).thenReturn(job);

        assertSame(job, controller.failJob(1L, "Test error"));
        verify(service).failJob(1L, "Test error");
    }

    @Test
    void processJob_shouldCallWorker() {
        assertDoesNotThrow(() -> controller.processJob(1L));

        verify(reviewWorker).process(1L);
        verifyNoInteractions(service);
    }
}
