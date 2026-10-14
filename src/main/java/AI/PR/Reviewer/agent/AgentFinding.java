package AI.PR.Reviewer.agent;

import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;

public class AgentFinding {

    private final ReviewAgentType agentType;
    private final ReviewSeverity severity;
    private final String message;
    private final String filePath;
    private final Integer lineNumber;

    public AgentFinding(
            ReviewAgentType agentType,
            ReviewSeverity severity,
            String message,
            String filePath,
            Integer lineNumber) {

        this.agentType = agentType;
        this.severity = severity;
        this.message = message;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
    }

    public ReviewAgentType getAgentType() {
        return agentType;
    }

    public ReviewSeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }
}