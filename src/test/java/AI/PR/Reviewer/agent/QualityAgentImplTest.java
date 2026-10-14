package AI.PR.Reviewer.agent;

import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QualityAgentImplTest {

    private final QualityAgentImpl qualityAgent = new QualityAgentImpl();

    @Test
    void shouldDetectSystemOutPrintln() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,1 @@
                +System.out.println("Hello");
                """;

        AgentFinding finding = qualityAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertEquals(ReviewAgentType.QUALITY, finding.getAgentType());
        assertEquals(ReviewSeverity.MEDIUM, finding.getSeverity());
        assertTrue(finding.getMessage().contains("console output"));
        assertEquals("Example.java", finding.getFilePath());
        assertEquals(1, finding.getLineNumber());
    }

    @Test
    void shouldDetectSystemErrPrintln() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,1 @@
                +System.err.println("Error message");
                """;

        AgentFinding finding = qualityAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertTrue(finding.getMessage().contains("console output"));
    }

    @Test
    void shouldDetectThreadSleep() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,1 @@
                +Thread.sleep(1000);
                """;

        AgentFinding finding = qualityAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertTrue(finding.getMessage().contains("Thread.sleep"));
    }

    @Test
    void shouldDetectTodoInAddedCode() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,1 @@
                +String task = "TODO";
                """;

        AgentFinding finding = qualityAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertTrue(finding.getMessage().contains("TODO/FIXME"));
    }

    @Test
    void shouldDetectPrintStackTrace() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,1 @@
                +exception.printStackTrace();
                """;

        AgentFinding finding = qualityAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertTrue(finding.getMessage().contains("printStackTrace"));
    }

    @Test
    void shouldIgnoreCleanCode() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,2 @@
                +int value = 10;
                +String name = "Developer";
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldIgnoreEmptyDiff() {
        assertNull(qualityAgent.analyze(1L, ""));
    }

    @Test
    void shouldReturnNullForNullOrBlankDiff() {
        assertNull(qualityAgent.analyze(1L, null));
        assertNull(qualityAgent.analyze(1L, "   "));
    }

    @Test
    void shouldIgnoreTodoAndFixmeComments() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,2 @@
                +// TODO: finish later
                +// FIXME: fix this
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldIgnoreEmptyAddedLinesAndBlockComments() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,2 @@
                +
                +   // regular comment
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldHandleDeletedAndContextLinesBeforeAddedCode() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,2 +1,2 @@
                 int oldValue = 1;
                -int removed = 2;
                +System.out.println("new");
                """;

        AgentFinding finding = qualityAgent.analyze(1L, diff);

        assertNotNull(finding);
    }

    @Test
    void shouldContinueWhenFileHeaderDoesNotMatchPattern() {
        String diff = """
                diff --git malformed header
                @@ -1,0 +1,1 @@
                +int value = 10;
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldIgnoreDiffWithOnlyDeletedLines() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,1 +1,0 @@
                -System.out.println("old code");
                """;

        assertNull(qualityAgent.analyze(1L, diff));
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

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldIgnoreBlockCommentLine() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,1 @@
                +* this is a block comment line
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldIgnoreFixmeInsideLineComment() {
        String diff = """
                diff --git a/Example.java b/Example.java
                --- a/Example.java
                +++ b/Example.java
                @@ -1,0 +1,1 @@
                +// FIXME: handle this later
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldHandleAddedLinesBeforeHunkHeader() {
        String diff = """
                diff --git a/Example.java b/Example.java
                +int value = 10;
                @@ -1,0 +1,1 @@
                +int count = 20;
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }

    @Test
    void shouldHandleContextLinesBeforeHunkHeader() {
        String diff = """
                diff --git a/Example.java b/Example.java
                 int existingValue = 5;
                @@ -1,1 +1,2 @@
                 int value = 10;
                +int count = 20;
                """;

        assertNull(qualityAgent.analyze(1L, diff));
    }
    @Test
    void shouldDetectFixmeInAddedCodeOutsideComment() {
        String diff = """
            diff --git a/Example.java b/Example.java
            --- a/Example.java
            +++ b/Example.java
            @@ -1,0 +1,1 @@
            +String task = "FIXME";
            """;

        AgentFinding finding = qualityAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertTrue(finding.getMessage().contains("TODO/FIXME"));
    }
}