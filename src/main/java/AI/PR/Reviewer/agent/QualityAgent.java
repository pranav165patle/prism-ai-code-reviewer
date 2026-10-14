package AI.PR.Reviewer.agent;

public interface QualityAgent {

    AgentFinding analyze(Long pullRequestId, String diff);
}