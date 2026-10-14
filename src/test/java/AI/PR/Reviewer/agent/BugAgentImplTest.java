package AI.PR.Reviewer.agent;

import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BugAgentImplTest {

    private final BugAgentImpl bugAgent = new BugAgentImpl();

    @Test
    void shouldDetectNullPointerRiskPattern() {
        String diff = """
                diff --git a/src/main/java/example/Service.java b/src/main/java/example/Service.java
                --- a/src/main/java/example/Service.java
                +++ b/src/main/java/example/Service.java
                @@ -10,0 +11,1 @@
                +throw new NullPointerException("test");
                """;

        AgentFinding finding = bugAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertEquals(ReviewAgentType.BUG, finding.getAgentType());
        assertEquals(ReviewSeverity.MEDIUM, finding.getSeverity());
        assertEquals(11, finding.getLineNumber());
    }

    @Test
    void shouldIgnoreCleanCode() {
        String diff = """
                diff --git a/src/main/java/example/Service.java b/src/main/java/example/Service.java
                --- a/src/main/java/example/Service.java
                +++ b/src/main/java/example/Service.java
                @@ -10,0 +11,1 @@
                +int total = first + second;
                """;

        AgentFinding finding = bugAgent.analyze(1L, diff);

        assertNull(finding);
    }

    @Test
    void shouldIgnoreEmptyDiff() {
        AgentFinding finding = bugAgent.analyze(1L, "");

        assertNull(finding);
    }

    @Test
    void shouldDetectGetMethod() {
        String diff = """
            diff --git a/Service.java b/Service.java
            --- a/Service.java
            +++ b/Service.java
            @@ -1 +1 @@
            -Object value = oldValue;
            +Object value = items.get();
            """;

        AgentFinding finding = bugAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertEquals("Service.java", finding.getFilePath());
        assertEquals(1, finding.getLineNumber());
    }

    @Test
    void shouldDetectLengthMethod() {
        String diff = """
                diff --git a/Service.java b/Service.java
                @@ -1,0 +1,1 @@
                +int size = name.length();
                """;

        assertNotNull(bugAgent.analyze(1L, diff));
    }

    @Test
    void shouldDetectSubstringMethod() {
        String diff = """
                diff --git a/Service.java b/Service.java
                @@ -1,0 +1,1 @@
                +String part = name.substring(2);
                """;

        assertNotNull(bugAgent.analyze(1L, diff));
    }

    @Test
    void shouldDetectArrayIndexing() {
        String diff = """
                diff --git a/Service.java b/Service.java
                @@ -1,0 +1,1 @@
                +int value = numbers[index];
                """;

        assertNotNull(bugAgent.analyze(1L, diff));
    }

    @Test
    void shouldDetectDivisionByZero() {
        String diff = """
                diff --git a/Service.java b/Service.java
                @@ -1,0 +1,1 @@
                +int result = number / 0;
                """;

        assertNotNull(bugAgent.analyze(1L, diff));
    }

    @Test
    void shouldDetectIntegerParseInt() {
        String diff = """
                diff --git a/Service.java b/Service.java
                @@ -1,0 +1,1 @@
                +int value = Integer.parseInt(input);
                """;

        assertNotNull(bugAgent.analyze(1L, diff));
    }

    @Test
    void shouldDetectLongParseLong() {
        String diff = """
                diff --git a/Service.java b/Service.java
                @@ -1,0 +1,1 @@
                +long value = Long.parseLong(input);
                """;

        assertNotNull(bugAgent.analyze(1L, diff));
    }

    @Test
    void shouldIgnoreNullAndWhitespaceDiff() {
        assertNull(bugAgent.analyze(1L, null));
        assertNull(bugAgent.analyze(1L, "   \n   "));
    }

    @Test
    void shouldIgnoreDeletedBugPattern() {
        String diff = """
                diff --git a/Service.java b/Service.java
                @@ -1,1 +1,0 @@
                -Object value = items.get(0);
                """;

        assertNull(bugAgent.analyze(1L, diff));
    }

    @Test
    void shouldTrackLineNumbersAcrossContextAndDeletedLines() {
        String diff = """
                diff --git a/Service.java b/Service.java
                --- a/Service.java
                +++ b/Service.java
                @@ -1,2 +5,3 @@
                 int first = 1;
                -int oldValue = 2;
                +int second = 2;
                +Object value = items.get();
                """;

        AgentFinding finding = bugAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertEquals("Service.java", finding.getFilePath());
        assertEquals(7, finding.getLineNumber());


    }
    @Test
    void shouldIgnoreAddedCodeWithSimilarButNonMatchingPattern() {
        String diff = """
            diff --git a/Service.java b/Service.java
            --- a/Service.java
            +++ b/Service.java
            @@ -1,0 +1,2 @@
            +Object value = items.get(0);
            +int result = number / 10;
            """;

        assertNull(bugAgent.analyze(1L, diff));
    }
    @Test
    void shouldHandleAddedCodeBeforeHunkHeader() {
        String diff = """
            diff --git a/Service.java b/Service.java
            --- a/Service.java
            +++ b/Service.java
            +String value = "safe";
            @@ -1,0 +1,1 @@
            +String greeting = "hello";
            """;

        assertNull(bugAgent.analyze(1L, diff));
    }
    @Test
    void shouldHandleMalformedFileHeaderAndContinueParsing() {
        String diff = """
            diff --git malformed header
            @@ -1,0 +1,1 @@
            +String greeting = "hello";
            """;

        assertNull(bugAgent.analyze(1L, diff));
    }
    @Test
    void shouldIgnoreNoNewlineMetadataLine() {
        String diff = """
            diff --git a/Example.java b/Example.java
            --- a/Example.java
            +++ b/Example.java
            @@ -1,1 +1,1 @@
            -int value = 1;
            +int value = 2;
            \\ No newline at end of file
            """;

        assertNull(bugAgent.analyze(1L, diff));
    }
}