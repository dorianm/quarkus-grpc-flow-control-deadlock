package org.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Map;
import org.acme.demo.LookupReply;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(VertxClientLargerConnectionWindowTest.LargerConnectionWindow.class)
class VertxClientLargerConnectionWindowTest {

    public static class LargerConnectionWindow implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            // 1 MiB, well above the 65535-byte stream window
            return Map.of("reproducer.http2-connection-window-size", "1048576");
        }
    }

    @Inject
    NestedCallPipeline pipeline;

    @Test
    void nestedUnaryCallWhileConsumingServerStream() {
        var replies = pipeline.run().collect().asList().await().atMost(Duration.ofSeconds(30));

        assertEquals(NestedCallPipeline.STREAMED_IDS, replies.stream().mapToInt(LookupReply::getCount).sum());
    }
}
