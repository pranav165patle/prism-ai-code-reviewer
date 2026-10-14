package AI.PR.Reviewer.service;

import AI.PR.Reviewer.entity.ReviewJob;
import AI.PR.Reviewer.entity.ReviewJobStatus;
import AI.PR.Reviewer.exception.ReviewJobNotFoundException;
import AI.PR.Reviewer.repository.PullRequestRepository;
import AI.PR.Reviewer.repository.ReviewJobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewJobServiceTest {

    @Mock
    private ReviewJobRepository repository;

    @Mock
    private PullRequestRepository pullRequestRepository;

    @InjectMocks
    private ReviewJobService service;

    @Test
    void shouldCreateQueuedJob() {
        when(repository.existsByPullRequestIdAndStatusIn(
                eq(1L), any()))
                .thenReturn(false);

        when(repository.save(any(ReviewJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewJob job = service.createJob(1L);

        assertEquals(1L, job.getPullRequestId());
        assertEquals(ReviewJobStatus.QUEUED, job.getStatus());
        assertNotNull(job.getCreatedAt());

        verify(repository).save(any(ReviewJob.class));
    }

    @Test
    void shouldRejectDuplicateActiveJob() {
        when(repository.existsByPullRequestIdAndStatusIn(
                eq(1L), any()))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> service.createJob(1L));

        verify(repository, never()).save(any(ReviewJob.class));
    }

    @Test
    void shouldReturnAllJobs() {
        when(repository.findAll()).thenReturn(List.of(new ReviewJob()));

        assertEquals(1, service.getAllJobs().size());
    }

    @Test
    void shouldReturnJobsByPullRequest() {
        when(repository.findByPullRequestId(1L))
                .thenReturn(List.of(new ReviewJob()));

        assertEquals(1, service.getJobsByPullRequest(1L).size());
    }

    @Test
    void shouldReturnJobById() {
        ReviewJob job = new ReviewJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        assertSame(job, service.getJobById(1L));
    }

    @Test
    void shouldThrowWhenJobDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReviewJobNotFoundException.class,
                () -> service.getJobById(99L));
    }

    @Test
    void shouldStartQueuedJob() {
        ReviewJob job = new ReviewJob();
        job.setStatus(ReviewJobStatus.QUEUED);

        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(repository.save(any(ReviewJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewJob result = service.startJob(1L);

        assertEquals(ReviewJobStatus.RUNNING, result.getStatus());
        assertNotNull(result.getStartedAt());
    }

    @Test
    void shouldRejectStartingNonQueuedJob() {
        ReviewJob job = new ReviewJob();
        job.setStatus(ReviewJobStatus.RUNNING);

        when(repository.findById(1L)).thenReturn(Optional.of(job));

        assertThrows(IllegalStateException.class,
                () -> service.startJob(1L));

        verify(repository, never()).save(any(ReviewJob.class));
    }

    @Test
    void shouldCompleteRunningJob() {
        ReviewJob job = new ReviewJob();
        job.setStatus(ReviewJobStatus.RUNNING);

        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(repository.save(any(ReviewJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewJob result = service.completeJob(1L);

        assertEquals(ReviewJobStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
    }

    @Test
    void shouldFailRunningJob() {
        ReviewJob job = new ReviewJob();
        job.setStatus(ReviewJobStatus.RUNNING);

        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(repository.save(any(ReviewJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewJob result = service.failJob(1L, "Test failure");

        assertEquals(ReviewJobStatus.FAILED, result.getStatus());
        assertEquals("Test failure", result.getErrorMessage());
        assertNotNull(result.getCompletedAt());
    }
    @Test
    void createJob_shouldRejectWhenQueuedJobAlreadyExists() {
        when(repository.existsByPullRequestIdAndStatusIn(
                eq(1L), anyList()))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.createJob(1L)
        );

        verify(repository, never()).save(any(ReviewJob.class));
    }

    @Test
    void startJob_shouldRejectWhenJobIsNotQueued() {
        ReviewJob job = new ReviewJob();
        job.setStatus(ReviewJobStatus.RUNNING);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(job));

        assertThrows(
                IllegalStateException.class,
                () -> service.startJob(1L)
        );

        verify(repository, never()).save(any(ReviewJob.class));
    }

    @Test
    void completeJob_shouldRejectWhenJobIsNotRunning() {
        ReviewJob job = new ReviewJob();
        job.setStatus(ReviewJobStatus.QUEUED);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(job));

        assertThrows(
                IllegalStateException.class,
                () -> service.completeJob(1L)
        );

        verify(repository, never()).save(any(ReviewJob.class));
    }

    @Test
    void failJob_shouldRejectWhenJobIsNotRunning() {
        ReviewJob job = new ReviewJob();
        job.setStatus(ReviewJobStatus.QUEUED);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(job));

        assertThrows(
                IllegalStateException.class,
                () -> service.failJob(1L, "Test error")
        );

        verify(repository, never()).save(any(ReviewJob.class));
    }

    @Test
    void getJobsByPullRequest_shouldReturnRepositoryResults() {
        List<ReviewJob> jobs = List.of(new ReviewJob());

        when(repository.findByPullRequestId(1L)).thenReturn(jobs);

        assertEquals(jobs, service.getJobsByPullRequest(1L));

        verify(repository).findByPullRequestId(1L);
    }

    @Test
    void getAllJobs_shouldReturnRepositoryResults() {
        List<ReviewJob> jobs = List.of(new ReviewJob(), new ReviewJob());

        when(repository.findAll()).thenReturn(jobs);

        assertEquals(jobs, service.getAllJobs());

        verify(repository).findAll();
    }
    @Test
    void startJob_shouldThrowWhenJobDoesNotExist() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ReviewJobNotFoundException.class,
                () -> service.startJob(99L)
        );

        verify(repository, never()).save(any(ReviewJob.class));
    }
}