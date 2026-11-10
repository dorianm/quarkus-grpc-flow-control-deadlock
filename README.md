# gRPC Vert.x client flow-control deadlock reproducer

A server-streaming call consumed with bounded demand (`group().intoLists().of(500).concatMap(...)`) deadlocks when the pipeline issues a unary call to the same service, with the Vert.x based gRPC client (`use-quarkus-grpc-client=true`).

The unread backlog of the paused stream fills the 65535-byte HTTP/2 connection window, so the unary response body is never received, so the stream is never drained.

Client and server live in the same application, the client targets `localhost` (real HTTP/2 connection, no Stork, no compression).

- `DemoService`: `ListIds` streams 50,000 small messages, `Lookup` is unary.
- `NestedCallPipeline`: the consuming pipeline.
- `VertxClientDeadlockTest`: `use-quarkus-grpc-client=true`, fails after 30 s with a `TimeoutException` since 3.37.
- `GrpcJavaClientTest`: same pipeline with `use-quarkus-grpc-client=false`, passes.
- `VertxClientLargerConnectionWindowTest`: Vert.x client with the HTTP/2 connection window raised to 1 MiB through `ConnectionWindowCustomizer` (a `ChannelBuilderCustomizer`), passes. `quarkus.grpc.clients.<name>.flow-control-window` is not applied to the Vert.x client.

## Run

```shell
# Quarkus 3.40.1.1: VertxClientDeadlockTest times out, the two other tests pass
./mvnw clean test

# Quarkus 3.33.4: all pass
./mvnw clean test -Dquarkus.platform.version=3.33.4
```

Requires JDK 25+.
