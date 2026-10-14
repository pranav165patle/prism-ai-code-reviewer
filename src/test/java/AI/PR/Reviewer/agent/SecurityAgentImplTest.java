package AI.PR.Reviewer.agent;

import AI.PR.Reviewer.entity.ReviewAgentType;
import AI.PR.Reviewer.entity.ReviewSeverity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityAgentImplTest {

    private final SecurityAgentImpl securityAgent = new SecurityAgentImpl();

    @Test
    void shouldDetectHardcodedPassword() {
        String diff = """
                diff --git a/src/main/java/example/Config.java b/src/main/java/example/Config.java
                --- a/src/main/java/example/Config.java
                +++ b/src/main/java/example/Config.java
                @@ -10,0 +11,1 @@
                +String password = "SuperSecret123";
                """;

        AgentFinding finding = securityAgent.analyze(1L, diff);

        assertNotNull(finding);
        assertEquals(ReviewAgentType.SECURITY, finding.getAgentType());
        assertEquals(ReviewSeverity.HIGH, finding.getSeverity());
        assertEquals(11, finding.getLineNumber());
        assertTrue(finding.getMessage().toLowerCase().contains("secret"));
    }

    @Test
    void shouldIgnoreCodeWithoutSecrets() {
        String diff = """
                diff --git a/src/main/java/example/Service.java b/src/main/java/example/Service.java
                --- a/src/main/java/example/Service.java
                +++ b/src/main/java/example/Service.java
                @@ -10,0 +11,1 @@
                +String greeting = "Hello world";
                """;

        AgentFinding finding = securityAgent.analyze(1L, diff);

        assertNull(finding);
    }

    @Test
    void shouldIgnoreEmptyDiff() {
        AgentFinding finding = securityAgent.analyze(1L, "");

        assertNull(finding);
    }

    @Test
    void shouldDetectApiKeyWithQuotedValue() {
        String diff = """
                diff --git a/Config.java b/Config.java
                --- a/Config.java
                +++ b/Config.java
                @@ -1,0 +1,1 @@
                +String api_key = "abcdefgh123456";
                """;

        AgentFinding finding = securityAgent.analyze(2L, diff);

        assertNotNull(finding);
        assertEquals(ReviewAgentType.SECURITY, finding.getAgentType());
        assertEquals(ReviewSeverity.HIGH, finding.getSeverity());
        assertEquals("Config.java", finding.getFilePath());
        assertEquals(1, finding.getLineNumber());
    }

    @Test
    void shouldDetectUnquotedAccessToken() {
        String diff = """
                diff --git a/Config.java b/Config.java
                --- a/Config.java
                +++ b/Config.java
                @@ -1,0 +1,1 @@
                +access_token=abcdefgh123456
                """;

        AgentFinding finding = securityAgent.analyze(2L, diff);

        assertNotNull(finding);
        assertTrue(finding.getMessage().contains("secret"));
    }

    @Test
    void shouldIgnoreShortSecretValues() {
        String diff = """
                diff --git a/Config.java b/Config.java
                --- a/Config.java
                +++ b/Config.java
                @@ -1,0 +1,1 @@
                +String password = "abc";
                """;

        assertNull(securityAgent.analyze(2L, diff));
    }

    @Test
    void shouldIgnoreDeletedSecretsAndDiffMetadata() {
        String diff = """
                diff --git a/Config.java b/Config.java
                --- a/Config.java
                +++ b/Config.java
                @@ -1,2 +1,1 @@
                -String password = "OldSecret123";
                +String greeting = "hello";
                 String name = "developer";
                \\ No newline at end of file
                """;

        assertNull(securityAgent.analyze(2L, diff));
    }

    @Test
    void shouldHandleMalformedFileHeaderAndNullDiff() {
        assertNull(securityAgent.analyze(2L, null));

        String diff = """
                diff --git malformed header
                @@ -1,0 +1,1 @@
                +String greeting = "hello";
                """;

        AgentFinding finding = securityAgent.analyze(2L, diff);

        assertNull(finding);
    }
    @Test
    void shouldDetectSecretWithSingleQuotedValue() {
        String diff = """
            diff --git a/Config.java b/Config.java
            --- a/Config.java
            +++ b/Config.java
            @@ -1,0 +1,1 @@
            +String client_secret = 'mySecretValue123';
            """;

        AgentFinding finding = securityAgent.analyze(3L, diff);

        assertNotNull(finding);
        assertEquals(ReviewAgentType.SECURITY, finding.getAgentType());
        assertEquals(ReviewSeverity.HIGH, finding.getSeverity());
        assertEquals("Config.java", finding.getFilePath());
        assertEquals(1, finding.getLineNumber());
    }
    @Test
    void shouldHandleAddedCodeBeforeHunkHeader() {
        String diff = """
            diff --git malformed header
            +String greeting = "hello";
            @@ -1,0 +1,1 @@
            +String name = "developer";
            """;

        AgentFinding finding = securityAgent.analyze(3L, diff);

        assertNull(finding);
    }
    @Test
    void shouldHandleSecretAfterHunkLineNumberIsSet() {
        String diff = """
            diff --git a/Config.java b/Config.java
            --- a/Config.java
            +++ b/Config.java
            @@ -1,0 +1,2 @@
            +String greeting = "hello";
            +String password = "SuperSecret123";
            """;

        AgentFinding finding = securityAgent.analyze(4L, diff);

        assertNotNull(finding);
        assertEquals(2, finding.getLineNumber());
    }
    @Test
    void shouldHandleAddedNonSecretLineBeforeSecret() {
        String diff = """
            diff --git a/Config.java b/Config.java
            --- a/Config.java
            +++ b/Config.java
            @@ -1,0 +1,2 @@
            +String greeting = "hello";
            +String password = "SuperSecret123";
            """;

        AgentFinding finding = securityAgent.analyze(4L, diff);

        assertNotNull(finding);
        assertEquals(2, finding.getLineNumber());
    }
    @Test
    void shouldDetectSecretWhenLineNumberIsNull() {
        String diff = """
            diff --git a/Config.java b/Config.java
            +String greeting = "hello";
            +String password = "SuperSecret123";
            """;

        AgentFinding finding = securityAgent.analyze(4L, diff);

        assertNotNull(finding);
        assertEquals(ReviewAgentType.SECURITY, finding.getAgentType());
        assertEquals(ReviewSeverity.HIGH, finding.getSeverity());
        assertEquals("Config.java", finding.getFilePath());
        assertNull(finding.getLineNumber());
    }
    @Test
    void shouldProcessAddedLineWhenLineNumberIsNull() {
        String diff = """
            diff --git a/Config.java b/Config.java
            +String greeting = "hello";
            @@ -1,0 +1,1 @@
            +String name = "developer";
            """;

        assertNull(securityAgent.analyze(4L, diff));
    }
    @Test
    void shouldIncrementLineNumberForContextLine() {
        String diff = """
            diff --git a/Config.java b/Config.java
            --- a/Config.java
            +++ b/Config.java
            @@ -1,1 +1,2 @@
             String greeting = "hello";
            +String name = "developer";
            """;

        AgentFinding finding = securityAgent.analyze(4L, diff);

        assertNull(finding);
    }
    @Test
    void shouldProcessContextLineAfterHunkHeader() {
        String diff = """
            diff --git a/Config.java b/Config.java
            --- a/Config.java
            +++ b/Config.java
            @@ -1,1 +1,1 @@
             String greeting = "hello";
            """;

        assertNull(securityAgent.analyze(4L, diff));
    }
    @Test
    void shouldProcessContextLineWhenCurrentLineIsNull() {
        String diff = """
        diff --git a/Config.java b/Config.java
        +String greeting = "hello";
         String name = "developer";
        """;

        AgentFinding finding = securityAgent.analyze(5L, diff);

        assertNull(finding);
    }
}