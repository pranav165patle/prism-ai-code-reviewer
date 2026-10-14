package AI.PR.Reviewer.worker;

import AI.PR.Reviewer.entity.ReviewJob;
import AI.PR.Reviewer.entity.ReviewJobStatus;
import AI.PR.Reviewer.service.ReviewEngine;
import AI.PR.Reviewer.service.ReviewJobService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewWorkerImplTest {

    @Mock
    private ReviewJobService reviewJobService;

    @Mock
    private ReviewEngine reviewEngine;

    @InjectMocks
    private ReviewWorkerImpl reviewWorker;

    @Test
    void shouldProcessJobSuccessfully() {
        ReviewJob job = new ReviewJob();
        job.setPullRequestId(1L);

        when(reviewJobService.getJobById(10L)).thenReturn(job);

        reviewWorker.process(10L);

        verify(reviewJobService).startJob(10L);
        verify(reviewJobService).getJobById(10L);
        verify(reviewEngine).execute(1L);
        verify(reviewJobService).completeJob(10L);
        verify(reviewJobService, never()).failJob(anyLong(), anyString());
    }

    @Test
    void shouldMarkJobFailedWhenEngineThrows() {
        ReviewJob job = new ReviewJob();
        job.setPullRequestId(1L);

        when(reviewJobService.getJobById(10L)).thenReturn(job);
        doThrow(new RuntimeException("Engine failed"))
                .when(reviewEngine).execute(1L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewWorker.process(10L)
        );

        assertEquals("Engine failed", exception.getMessage());
        verify(reviewJobService).failJob(10L, "Engine failed");
        verify(reviewJobService, never()).completeJob(10L);
    }

    @Test
    void shouldMarkJobFailedWhenStartingJobThrows() {
        doThrow(new IllegalStateException("Cannot start job"))
                .when(reviewJobService).startJob(10L);

        assertThrows(IllegalStateException.class,
                () -> reviewWorker.process(10L));

        verify(reviewJobService).failJob(10L, "Cannot start job");
        verifyNoInteractions(reviewEngine);
    }

    @Test
    void shouldRethrowExceptionWhenReviewEngineFails() {
        ReviewJob job = new ReviewJob();
        job.setPullRequestId(5L);

        when(reviewJobService.getJobById(10L)).thenReturn(job);
        doThrow(new RuntimeException("Review error"))
                .when(reviewEngine).execute(5L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewWorker.process(10L)
        );

        assertEquals("Review error", exception.getMessage());
    }
}
