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
@TestProfile(GrpcJavaClientTest.GrpcJavaClient.class)
class GrpcJavaClientTest {

    public static class GrpcJavaClient implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.grpc.clients.demo.use-quarkus-grpc-client", "false");
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
