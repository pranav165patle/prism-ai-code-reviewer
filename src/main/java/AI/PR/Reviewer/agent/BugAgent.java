package AI.PR.Reviewer.agent;

public interface BugAgent {

    AgentFinding analyze(Long pullRequestId, String diff);
}