package org.acme;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.acme.demo.IdReply;
import org.acme.demo.ListRequest;
import org.acme.demo.LookupReply;
import org.acme.demo.LookupRequest;
import org.acme.demo.MutinyDemoGrpc;

@GrpcService
public class DemoService extends MutinyDemoGrpc.DemoImplBase {

    @Override
    public Multi<IdReply> listIds(ListRequest request) {
        return Multi.createFrom().range(0, request.getCount())
                .map(i -> IdReply.newBuilder().setId("ID-%012d".formatted(i)).build());
    }

    @Override
    public Uni<LookupReply> lookup(LookupRequest request) {
        return Uni.createFrom().item(LookupReply.newBuilder().setCount(request.getIdsCount()).build());
    }
}
