package AI.PR.Reviewer.worker;

import AI.PR.Reviewer.service.ReviewEngine;
import AI.PR.Reviewer.service.ReviewJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ReviewWorkerImpl implements ReviewWorker {

    private final ReviewJobService reviewJobService;
    private final ReviewEngine reviewEngine;

    public ReviewWorkerImpl(
            ReviewJobService reviewJobService,
            ReviewEngine reviewEngine) {

        this.reviewJobService = reviewJobService;
        this.reviewEngine = reviewEngine;
    }

    @Override
    public void process(Long reviewJobId) {
        log.info("Starting review job processing: {}", reviewJobId);

        try {
            reviewJobService.startJob(reviewJobId);

            Long pullRequestId = reviewJobService
                    .getJobById(reviewJobId)
                    .getPullRequestId();

            log.info("Executing review engine for PR: {}", pullRequestId);
            reviewEngine.execute(pullRequestId);

            reviewJobService.completeJob(reviewJobId);
            log.info("Review job completed successfully: {}", reviewJobId);

        } catch (Exception exception) {
            log.error("Review job failed: {}", reviewJobId, exception);

            reviewJobService.failJob(
                    reviewJobId,
                    exception.getMessage()
            );

            throw exception;
        }
    }
}