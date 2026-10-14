package AI.PR.Reviewer.agent;

import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class BugAgentImpl implements BugAgent {

    private static final Pattern FILE_PATTERN = Pattern.compile(
            "^diff --git a/(.*?) b/(.*?)$"
    );

    private static final Pattern HUNK_PATTERN = Pattern.compile(
            "^@@ -(\\d+)(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@.*$"
    );

    @Override
    public AgentFinding analyze(Long pullRequestId, String diff) {

        log.info("Bug Agent started | pullRequestId={}", pullRequestId);

        if (diff == null || diff.isBlank()) {
            log.warn(
                    "Bug Agent received empty diff | pullRequestId={}",
                    pullRequestId
            );
            return null;
        }

        String[] lines = diff.split("\\R");

        String filePath = "GitHub PR diff";
        Integer lineNumber = null;
        boolean bugDetected = false;

        int currentLine = 0;

        for (String line : lines) {

            // Identify each file in the GitHub diff.
            if (line.startsWith("diff --git ")) {

                Matcher fileMatcher = FILE_PATTERN.matcher(line);

                if (fileMatcher.matches()) {
                    filePath = fileMatcher.group(2);
                }

                currentLine = 0;
                continue;
            }

            // Read the starting line number for the new file.
            Matcher hunkMatcher = HUNK_PATTERN.matcher(line);

            if (hunkMatcher.matches()) {
                currentLine = Integer.parseInt(hunkMatcher.group(2));
                continue;
            }

            // Ignore diff metadata.
            if (line.startsWith("+++ ") || line.startsWith("--- ")) {
                continue;
            }

            if (line.startsWith("\\")) {
                continue;
            }

            // Inspect only added lines.
            if (line.startsWith("+")) {

                String addedCode = line.substring(1);

                if (containsBugPattern(addedCode)) {

                    bugDetected = true;
                    lineNumber = currentLine;

                    log.warn(
                            "Potential bug pattern detected | pullRequestId={} | filePath={} | lineNumber={}",
                            pullRequestId,
                            filePath,
                            lineNumber
                    );

                    break;
                }

                currentLine++;

            } else if (line.startsWith("-")) {

                // Deleted lines do not advance the new-file line number.
                continue;

            } else {

                // Context lines exist in both old and new versions.
                currentLine++;
            }
        }

        log.info(
                "Bug Agent analysis completed | pullRequestId={} | filePath={} | lineNumber={} | bugDetected={}",
                pullRequestId,
                filePath,
                lineNumber,
                bugDetected
        );

        if (bugDetected) {
            return new AgentFinding(
                    ReviewAgentType.BUG,
                    ReviewSeverity.MEDIUM,
                    "Potential null pointer risk detected in changed code.",
                    filePath,
                    lineNumber
            );
        }

        return null;
    }

    private boolean containsBugPattern(String code) {
        String normalized = code.trim();

        return normalized.contains("NullPointerException")
                || normalized.matches(".*\\.get\\(\\).*")
                || normalized.matches(".*\\.length\\(\\).*")
                || normalized.matches(".*\\.substring\\(.*")
                || normalized.matches(".*\\[[^]]*\\].*")
                || normalized.matches(".* / 0([;,) ]|$).*")
                || normalized.contains("Integer.parseInt(")
                || normalized.contains("Long.parseLong(");
    }
}
