package org.acme;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.demo.IdReply;
import org.acme.demo.ListRequest;
import org.acme.demo.LookupReply;
import org.acme.demo.LookupRequest;
import org.acme.demo.MutinyDemoGrpc;

@ApplicationScoped
public class NestedCallPipeline {

    public static final int STREAMED_IDS = 50_000;

    @GrpcClient("demo")
    MutinyDemoGrpc.MutinyDemoStub demo;

    // Unary call to the same service for each batch while the stream is still open
    public Multi<LookupReply> run() {
        return demo.listIds(ListRequest.newBuilder().setCount(STREAMED_IDS).build())
                .map(IdReply::getId)
                .group().intoLists().of(500)
                .concatMap(ids -> demo.lookup(LookupRequest.newBuilder().addAllIds(ids).build()).toMulti());
    }
}
