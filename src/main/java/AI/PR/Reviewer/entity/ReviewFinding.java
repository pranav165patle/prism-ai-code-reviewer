package AI.PR.Reviewer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "review_findings")
public class ReviewFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pullRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewAgentType agentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewSeverity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private String filePath;

    private Integer lineNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    public ReviewFinding() {
    }

    public Long getId() {
        return id;
    }

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