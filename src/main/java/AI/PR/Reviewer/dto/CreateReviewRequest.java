package AI.PR.Reviewer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import AI.PR.Reviewer.entity.ReviewStatus;

public class CreateReviewRequest {

    @NotNull
    @Positive
    private Long pullRequestId;

    @NotNull
    private ReviewAgentType agentType;

    @NotNull
    private ReviewSeverity severity;

    @NotBlank
    private String message;

    private String filePath;

    @Positive
    private Integer lineNumber;

    @NotNull
    private ReviewStatus status;

    public Long getPullRequestId() {
        return pullRequestId;
    }

    public void setPullRequestId(Long pullRequestId) {
        this.pullRequestId = pullRequestId;
    }

    public ReviewAgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(ReviewAgentType agentType) {
        this.agentType = agentType;
    }

    public ReviewSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ReviewSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }
}