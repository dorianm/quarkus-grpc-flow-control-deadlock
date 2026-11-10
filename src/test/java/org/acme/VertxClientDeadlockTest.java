package org.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import org.acme.demo.LookupReply;
import org.junit.jupiter.api.Test;

@QuarkusTest
class VertxClientDeadlockTest {

    @Inject
    NestedCallPipeline pipeline;

    @Test
    void nestedUnaryCallWhileConsumingServerStream() {
        var replies = pipeline.run().collect().asList().await().atMost(Duration.ofSeconds(30));

        assertEquals(NestedCallPipeline.STREAMED_IDS, replies.stream().mapToInt(LookupReply::getCount).sum());
    }
}
