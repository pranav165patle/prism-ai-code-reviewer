package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.dto.CreateReviewFindingRequest;
import AI.PR.Reviewer.entity.ReviewFinding;
import AI.PR.Reviewer.entity.ReviewStatus;
import AI.PR.Reviewer.service.ReviewFindingService;

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
class ReviewFindingControllerTest {

    @Mock
    private ReviewFindingService service;

    @InjectMocks
    private ReviewFindingController controller;

    @Test
    void createFinding_shouldReturnSavedFinding() {
        CreateReviewFindingRequest request =
                mock(CreateReviewFindingRequest.class);

        ReviewFinding saved = new ReviewFinding();

        when(service.saveFinding(request)).thenReturn(saved);

        assertSame(saved, controller.createFinding(request));
        verify(service).saveFinding(request);
    }

    @Test
    void getAllFindings_shouldReturnFindings() {
        List<ReviewFinding> findings =
                List.of(new ReviewFinding(), new ReviewFinding());

        when(service.getAllFindings()).thenReturn(findings);

        assertEquals(findings, controller.getAllFindings());
        verify(service).getAllFindings();
    }

    @Test
    void getFindingsByPullRequest_shouldReturnFindings() {
        List<ReviewFinding> findings =
                List.of(new ReviewFinding());

        when(service.getFindingsByPullRequest(1L)).thenReturn(findings);

        assertEquals(findings, controller.getFindingsByPullRequest(1L));
        verify(service).getFindingsByPullRequest(1L);
    }

    @Test
    void getFindingById_shouldReturnFinding() {
        ReviewFinding finding = new ReviewFinding();

        when(service.getFindingById(1L)).thenReturn(finding);

        assertSame(finding, controller.getFindingById(1L));
        verify(service).getFindingById(1L);
    }

    @Test
    void updateFindingStatus_shouldReturnUpdatedFinding() {
        ReviewFinding finding = new ReviewFinding();

        when(service.updateFindingStatus(1L, ReviewStatus.RESOLVED))
                .thenReturn(finding);

        assertSame(
                finding,
                controller.updateFindingStatus(1L, ReviewStatus.RESOLVED)
        );

        verify(service).updateFindingStatus(1L, ReviewStatus.RESOLVED);
    }

    @Test
    void deleteFinding_shouldCallService() {
        assertDoesNotThrow(() -> controller.deleteFinding(1L));

        verify(service).deleteFinding(1L);
    }

    @Test
    void createFinding_shouldHaveCreatedStatus() throws Exception {
        ResponseStatus annotation = ReviewFindingController.class
                .getMethod("createFinding", CreateReviewFindingRequest.class)
                .getAnnotation(ResponseStatus.class);

        assertNotNull(annotation);
        assertEquals(HttpStatus.CREATED, annotation.value());
    }

    @Test
    void deleteFinding_shouldHaveNoContentStatus() throws Exception {
        ResponseStatus annotation = ReviewFindingController.class
                .getMethod("deleteFinding", Long.class)
                .getAnnotation(ResponseStatus.class);

        assertNotNull(annotation);
        assertEquals(HttpStatus.NO_CONTENT, annotation.value());
    }
}