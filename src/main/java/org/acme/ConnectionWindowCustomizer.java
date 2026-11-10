package org.acme;

import io.grpc.netty.NettyChannelBuilder;
import io.quarkus.grpc.api.ChannelBuilderCustomizer;
import io.quarkus.grpc.runtime.config.GrpcClientConfiguration;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.grpc.client.GrpcClientOptions;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

// Vert.x client ignores quarkus.grpc.clients.<name>.flow-control-window, so set the HTTP/2 connection window here
@Singleton
public class ConnectionWindowCustomizer implements ChannelBuilderCustomizer<NettyChannelBuilder> {

    @ConfigProperty(name = "reproducer.http2-connection-window-size")
    Optional<Integer> connectionWindowSize;

    @Override
    public void customize(String name, GrpcClientConfiguration config, GrpcClientOptions options) {
        connectionWindowSize.ifPresent(size -> ((HttpClientOptions) options.getTransportOptions())
                .setHttp2ConnectionWindowSize(size));
    }
}
