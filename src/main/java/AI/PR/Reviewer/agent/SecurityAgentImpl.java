package AI.PR.Reviewer.agent;

import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SecurityAgentImpl implements SecurityAgent {

    private static final Pattern FILE_PATTERN = Pattern.compile(
            "^diff --git a/(.*?) b/(.*?)$"
    );

    private static final Pattern HUNK_PATTERN = Pattern.compile(
            "^@@ -(\\d+)(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@.*$"
    );

    private static final Pattern SECRET_PATTERN = Pattern.compile(
            "(?i)(password|passwd|secret|api[_-]?key|access[_-]?token|"
                    + "auth[_-]?token|private[_-]?key|client[_-]?secret)"
                    + "\\s*[:=]\\s*[\"'][^\"']{4,}[\"']"
                    + "|"
                    + "(?i)(password|passwd|secret|api[_-]?key|access[_-]?token|"
                    + "auth[_-]?token|private[_-]?key|client[_-]?secret)"
                    + "\\s*[:=]\\s*\\S{8,}"
    );

    @Override
    public AgentFinding analyze(Long pullRequestId, String diff) {

        log.info("Security Agent started | pullRequestId={}", pullRequestId);

        if (diff == null || diff.isBlank()) {
            log.warn(
                    "Security Agent received empty diff | pullRequestId={}",
                    pullRequestId
            );
            return null;
        }

        String[] lines = diff.split("\\R");

        String filePath = "GitHub PR diff";
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

            if (line.startsWith("+++ ") || line.startsWith("--- ")) {
                continue;
            }

            if (line.startsWith("\\")) {
                continue;
            }

            if (line.startsWith("+")) {

                String addedCode = line.substring(1);

                if (SECRET_PATTERN.matcher(addedCode).find()) {

                    log.warn(
                            "Potential secret detected | pullRequestId={} | filePath={} | lineNumber={}",
                            pullRequestId,
                            filePath,
                            currentLine
                    );

                    return new AgentFinding(
                            ReviewAgentType.SECURITY,
                            ReviewSeverity.HIGH,
                            "Potential hardcoded secret detected in added code.",
                            filePath,
                            currentLine
                    );
                }

                if (currentLine != null) {
                    currentLine++;
                }

            } else if (line.startsWith("-")) {

                // Removed lines are not part of the new version.
                continue;

            } else {

                // Context lines advance the new-file line number.
                if (currentLine != null) {
                    currentLine++;
                }
            }
        }

        log.info(
                "Security Agent analysis completed | pullRequestId={} | no matching secret pattern found",
                pullRequestId
        );

        return null;
    }
}
