package AI.PR.Reviewer.agent;

import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class QualityAgentImpl implements QualityAgent {

    private static final Pattern FILE_PATTERN = Pattern.compile(
            "^diff --git a/(.*?) b/(.*?)$"
    );

    private static final Pattern HUNK_PATTERN = Pattern.compile(
            "^@@ -(\\d+)(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@.*$"
    );

    @Override
    public AgentFinding analyze(Long pullRequestId, String diff) {

        log.info(
                "Quality Agent started | pullRequestId={}",
                pullRequestId
        );

        if (diff == null || diff.isBlank()) {
            log.warn(
                    "Quality Agent received empty diff | pullRequestId={}",
                    pullRequestId
            );
            return null;
        }

        String[] lines = diff.split("\\R");

        String filePath = null;
        Integer currentLine = null;

        for (String line : lines) {

            if (line.startsWith("diff --git ")) {
                Matcher fileMatcher = FILE_PATTERN.matcher(line);

                if (fileMatcher.matches()) {
                    filePath = fileMatcher.group(2);
                }

                currentLine = null;
                continue;
            }

            Matcher hunkMatcher = HUNK_PATTERN.matcher(line);

            if (hunkMatcher.matches()) {
                currentLine = Integer.parseInt(hunkMatcher.group(2));
                continue;
            }

            if (line.startsWith("+++ ") || line.startsWith("--- ")
                    || line.startsWith("\\")) {
                continue;
            }

            if (line.startsWith("+")) {

                String addedCode = line.substring(1);
                String issueMessage = detectQualityIssue(addedCode);

                if (issueMessage != null) {

                    log.warn(
                            "Quality issue detected | pullRequestId={} | filePath={} | lineNumber={} | issue={}",
                            pullRequestId,
                            filePath,
                            currentLine,
                            issueMessage
                    );

                    return new AgentFinding(
                            ReviewAgentType.QUALITY,
                            ReviewSeverity.MEDIUM,
                            issueMessage,
                            filePath,
                            currentLine
                    );
                }

                if (currentLine != null) {
                    currentLine++;
                }

            } else if (line.startsWith("-")) {

                // Deleted lines do not advance the new-file line number.
                continue;

            } else {

                // Context lines advance the new-file line number.
                if (currentLine != null) {
                    currentLine++;
                }
            }
        }

        log.info(
                "Quality Agent analysis completed | pullRequestId={} | no matching pattern found",
                pullRequestId
        );

        return null;
    }

    private String detectQualityIssue(String code) {

        String trimmed = code.trim();

        if (trimmed.isEmpty()
                || trimmed.startsWith("//")
                || trimmed.startsWith("*")) {
            return null;
        }

        if (trimmed.contains("System.out.println")
                || trimmed.contains("System.err.println")) {
            return "Avoid direct console output; use the project logger.";
        }

        if (trimmed.contains("TODO") || trimmed.contains("FIXME")) {
            return "Unresolved TODO/FIXME comment found; complete or track the task.";
        }

        if (trimmed.contains("Thread.sleep")) {
            return "Thread.sleep may block execution; consider a non-blocking alternative.";
        }

        if (trimmed.contains("printStackTrace()")) {
            return "Avoid printStackTrace(); use structured logging for exceptions.";
        }

        return null;
    }
}