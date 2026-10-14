package AI.PR.Reviewer.service;

import AI.PR.Reviewer.dto.CreateReviewFindingRequest;
import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewFinding;
import AI.PR.Reviewer.entity.ReviewSeverity;
import AI.PR.Reviewer.entity.ReviewStatus;
import AI.PR.Reviewer.exception.PullRequestNotFoundException;
import AI.PR.Reviewer.exception.ReviewFindingNotFoundException;
import AI.PR.Reviewer.repository.PullRequestRepository;
import AI.PR.Reviewer.repository.ReviewFindingRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewFindingServiceTest {

    @Mock
    private ReviewFindingRepository repository;

    @Mock
    private PullRequestRepository pullRequestRepository;

    @InjectMocks
    private ReviewFindingService service;

    private CreateReviewFindingRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateReviewFindingRequest();
        request.setPullRequestId(1L);
        request.setAgentType(ReviewAgentType.QUALITY);
        request.setSeverity(ReviewSeverity.LOW);
        request.setMessage("Test finding");
        request.setFilePath("src/Test.java");
        request.setLineNumber(10);
        request.setStatus(ReviewStatus.OPEN);
    }

    @Test
    void shouldCreateFindingWhenPullRequestExists() {
        when(pullRequestRepository.existsById(1L)).thenReturn(true);

        when(repository
                .findByPullRequestIdAndAgentTypeAndFilePathAndLineNumber(
                        1L, ReviewAgentType.QUALITY, "src/Test.java", 10
                )).thenReturn(Optional.empty());

        ReviewFinding savedFinding = new ReviewFinding();
        savedFinding.setPullRequestId(1L);
        savedFinding.setAgentType(ReviewAgentType.QUALITY);
        savedFinding.setSeverity(ReviewSeverity.LOW);
        savedFinding.setMessage("Test finding");
        savedFinding.setFilePath("src/Test.java");
        savedFinding.setLineNumber(10);
        savedFinding.setStatus(ReviewStatus.OPEN);

        when(repository.save(any(ReviewFinding.class)))
                .thenReturn(savedFinding);

        ReviewFinding result = service.saveFinding(request);

        assertNotNull(result);
        assertEquals(1L, result.getPullRequestId());
        assertEquals(ReviewAgentType.QUALITY, result.getAgentType());
        assertEquals("Test finding", result.getMessage());

        verify(repository).save(any(ReviewFinding.class));
    }

    @Test
    void shouldThrowWhenPullRequestDoesNotExist() {
        when(pullRequestRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                PullRequestNotFoundException.class,
                () -> service.saveFinding(request)
        );

        verify(repository, never()).save(any(ReviewFinding.class));
    }

    @Test
    void shouldReturnFindingWhenIdExists() {
        ReviewFinding finding = new ReviewFinding();
        finding.setMessage("Existing finding");

        when(repository.findById(5L)).thenReturn(Optional.of(finding));

        ReviewFinding result = service.getFindingById(5L);

        assertEquals("Existing finding", result.getMessage());
    }

    @Test
    void shouldThrowWhenFindingDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ReviewFindingNotFoundException.class,
                () -> service.getFindingById(99L)
        );
    }

    @Test
    void shouldUpdateFindingStatus() {
        ReviewFinding finding = new ReviewFinding();
        finding.setStatus(ReviewStatus.OPEN);

        when(repository.findById(5L)).thenReturn(Optional.of(finding));
        when(repository.save(finding)).thenReturn(finding);

        ReviewFinding result =
                service.updateFindingStatus(5L, ReviewStatus.IGNORED);

        assertEquals(ReviewStatus.IGNORED, result.getStatus());
        verify(repository).save(finding);
    }

    @Test
    void shouldDeleteExistingFinding() {
        ReviewFinding finding = new ReviewFinding();

        when(repository.findById(5L)).thenReturn(Optional.of(finding));

        service.deleteFinding(5L);

        verify(repository).delete(finding);
    }

    @Test
    void saveFinding_shouldReturnExistingFindingWithoutSavingDuplicate() {
        CreateReviewFindingRequest request =
                mock(CreateReviewFindingRequest.class);

        ReviewFinding existing = new ReviewFinding();

        when(request.getPullRequestId()).thenReturn(1L);
        when(request.getAgentType()).thenReturn(null);
        when(request.getFilePath()).thenReturn("src/Main.java");
        when(request.getLineNumber()).thenReturn(10);
        when(pullRequestRepository.existsById(1L)).thenReturn(true);

        when(repository.findByPullRequestIdAndAgentTypeAndFilePathAndLineNumber(
                1L, null, "src/Main.java", 10
        )).thenReturn(Optional.of(existing));

        ReviewFinding result = service.saveFinding(request);

        assertSame(existing, result);
        verify(repository, never()).save(any(ReviewFinding.class));
    }

    @Test
    void saveFinding_shouldThrowWhenPullRequestDoesNotExist() {
        CreateReviewFindingRequest request =
                mock(CreateReviewFindingRequest.class);

        when(request.getPullRequestId()).thenReturn(999L);
        when(pullRequestRepository.existsById(999L)).thenReturn(false);

        assertThrows(
                PullRequestNotFoundException.class,
                () -> service.saveFinding(request)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void getFindingById_shouldThrowWhenFindingDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ReviewFindingNotFoundException.class,
                () -> service.getFindingById(999L)
        );
    }

    @Test
    void deleteFinding_shouldThrowWhenFindingDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ReviewFindingNotFoundException.class,
                () -> service.deleteFinding(999L)
        );

        verify(repository, never()).delete(any(ReviewFinding.class));
    }
    @Test
    void getAllFindings_shouldReturnAllFindings() {
        List<ReviewFinding> findings = List.of(
                new ReviewFinding(),
                new ReviewFinding()
        );

        when(repository.findAll()).thenReturn(findings);

        List<ReviewFinding> result = service.getAllFindings();

        assertEquals(findings, result);
        verify(repository).findAll();
    }

    @Test
    void getFindingsByPullRequest_shouldReturnMatchingFindings() {
        List<ReviewFinding> findings = List.of(new ReviewFinding());

        when(repository.findByPullRequestId(1L)).thenReturn(findings);

        List<ReviewFinding> result = service.getFindingsByPullRequest(1L);

        assertEquals(findings, result);
        verify(repository).findByPullRequestId(1L);
    }
    @Test
    void updateFindingStatus_shouldThrowWhenFindingDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ReviewFindingNotFoundException.class,
                () -> service.updateFindingStatus(999L, ReviewStatus.IGNORED)
        );

        verify(repository, never()).save(any(ReviewFinding.class));
    }
}