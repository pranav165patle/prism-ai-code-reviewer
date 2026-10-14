package AI.PR.Reviewer.agent;

public interface SecurityAgent {

    AgentFinding analyze(Long pullRequestId, String diff);
}