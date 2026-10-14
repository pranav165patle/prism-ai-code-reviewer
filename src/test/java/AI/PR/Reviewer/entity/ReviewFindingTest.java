package AI.PR.Reviewer.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewFindingTest {

    @Test
    void defaultConstructor_shouldCreateFinding() {
        ReviewFinding finding = new ReviewFinding();

        assertNotNull(finding);
        assertNull(finding.getId());
        assertNull(finding.getPullRequestId());
        assertNull(finding.getAgentType());
        assertNull(finding.getSeverity());
        assertNull(finding.getMessage());
        assertNull(finding.getFilePath());
        assertNull(finding.getLineNumber());
        assertNull(finding.getStatus());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        ReviewFinding finding = new ReviewFinding();

        finding.setPullRequestId(10L);
        finding.setAgentType(ReviewAgentType.QUALITY);
        finding.setSeverity(ReviewSeverity.LOW);
        finding.setMessage("Potential code quality issue");
        finding.setFilePath("src/main/java/App.java");
        finding.setLineNumber(25);
        finding.setStatus(ReviewStatus.OPEN);

        assertEquals(10L, finding.getPullRequestId());
        assertEquals(ReviewAgentType.QUALITY, finding.getAgentType());
        assertEquals(ReviewSeverity.LOW, finding.getSeverity());
        assertEquals("Potential code quality issue", finding.getMessage());
        assertEquals("src/main/java/App.java", finding.getFilePath());
        assertEquals(25, finding.getLineNumber());
        assertEquals(ReviewStatus.OPEN, finding.getStatus());
    }

    @Test
    void setters_shouldAcceptNullValues() {
        ReviewFinding finding = new ReviewFinding();

        finding.setPullRequestId(null);
        finding.setAgentType(null);
        finding.setSeverity(null);
        finding.setMessage(null);
        finding.setFilePath(null);
        finding.setLineNumber(null);
        finding.setStatus(null);

        assertNull(finding.getPullRequestId());
        assertNull(finding.getAgentType());
        assertNull(finding.getSeverity());
        assertNull(finding.getMessage());
        assertNull(finding.getFilePath());
        assertNull(finding.getLineNumber());
        assertNull(finding.getStatus());
    }
}
