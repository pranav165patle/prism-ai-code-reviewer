package AI.PR.Reviewer.service;

import AI.PR.Reviewer.entity.ReviewJob;
import AI.PR.Reviewer.entity.ReviewJobStatus;
import AI.PR.Reviewer.exception.PullRequestNotFoundException;
import AI.PR.Reviewer.exception.ReviewJobNotFoundException;
import AI.PR.Reviewer.repository.PullRequestRepository;
import AI.PR.Reviewer.repository.ReviewJobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewJobService {


    private final ReviewJobRepository repository;
    private final PullRequestRepository pullRequestRepository;

    public ReviewJobService(
            ReviewJobRepository repository,
            PullRequestRepository pullRequestRepository) {

        this.repository = repository;
        this.pullRequestRepository = pullRequestRepository;
    }

    public ReviewJob createJob(Long pullRequestId) {

        if (repository.existsByPullRequestIdAndStatusIn(
                pullRequestId,
                List.of(
                        ReviewJobStatus.QUEUED,
                        ReviewJobStatus.RUNNING
                ))) {

            throw new IllegalStateException(
                    "An active review job already exists for this pull request."
            );
        }

        ReviewJob job = new ReviewJob();

        job.setPullRequestId(pullRequestId);
        job.setStatus(ReviewJobStatus.QUEUED);
        job.setCreatedAt(LocalDateTime.now());

        return repository.save(job);
    }

    public List<ReviewJob> getAllJobs() {
        return repository.findAll();
    }

    public List<ReviewJob> getJobsByPullRequest(Long pullRequestId) {
        return repository.findByPullRequestId(pullRequestId);
    }

    public ReviewJob getJobById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ReviewJobNotFoundException(
                                "Review job not found with id: " + id
                        )
                );
    }

    public ReviewJob startJob(Long id) {

        ReviewJob job = getJobOrThrow(id);

        if (job.getStatus() != ReviewJobStatus.QUEUED) {
            throw new IllegalStateException(
                    "Only QUEUED jobs can be started."
            );
        }

        job.setStatus(ReviewJobStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());

        return repository.save(job);
    }

    public ReviewJob completeJob(Long id) {

        ReviewJob job = getJobOrThrow(id);
        if (job.getStatus() != ReviewJobStatus.RUNNING) {
            throw new IllegalStateException(
                    "Only RUNNING jobs can be completed."
            );
        }
        job.setStatus(ReviewJobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());

        return repository.save(job);
    }

    public ReviewJob failJob(Long id, String errorMessage) {

        ReviewJob job = getJobOrThrow(id);
        if (job.getStatus() != ReviewJobStatus.RUNNING) {
            throw new IllegalStateException(
                    "Only RUNNING jobs can be failed."
            );
        }

        job.setStatus(ReviewJobStatus.FAILED);
        job.setCompletedAt(LocalDateTime.now());
        job.setErrorMessage(errorMessage);

        return repository.save(job);
    }
    private ReviewJob getJobOrThrow(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ReviewJobNotFoundException(
                                "Review job not found with id: " + id
                        )
                );
    }

}