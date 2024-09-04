package techweek.armeria;

import java.util.concurrent.CompletableFuture;

import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction;
import com.linecorp.armeria.common.prometheus.PrometheusMeterRegistries;
import com.linecorp.armeria.common.util.UnmodifiableFuture;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.auth.AuthService;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.server.prometheus.PrometheusExpositionService;
import com.linecorp.armeria.server.throttling.ThrottlingService;
import com.linecorp.armeria.server.throttling.ThrottlingStrategy;

import io.grpc.stub.StreamObserver;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import techweek.armeria.Greeting.HelloReply;
import techweek.armeria.Greeting.HelloRequest;
import techweek.armeria.GreetingServiceGrpc.GreetingServiceImplBase;

public final class Main {

    public static void main(String[] args) {
        final GrpcService grpcService =
                GrpcService.builder()
                           .addService(new GreetingService())
                           .enableHttpJsonTranscoding(true)
                           .enableUnframedRequests(true)
                           .build();

        final PrometheusMeterRegistry meterRegistry = PrometheusMeterRegistries.newRegistry();
        final Server server =
                Server.builder()
                      .http(8080)
                      .service(grpcService)
                      .serviceUnder("/docs", new DocService())
                      .meterRegistry(meterRegistry)
                      // Expose Prometheus metrics.
                      .service("/internal/metrics",
                               PrometheusExpositionService.of(meterRegistry.getPrometheusRegistry()))
                      // Collect metrics.
                      .decorator(MetricCollectingService.newDecorator(
                              MeterIdPrefixFunction.ofDefault("armeria.server")))
                      // Apply rate limiting.
                      .decorator(ThrottlingService.newDecorator(
                              ThrottlingStrategy.rateLimiting(100)))
                      // Authenticate requests.
                      .decorator(AuthService.newDecorator((ctx, req) -> {
                          final String token = req.headers().get(HttpHeaderNames.AUTHORIZATION, "");
                          return isValidToken(token);
                      }))
                      .decorator(GrpcLoggingService::new)
                      .build();

        server.start().join();
        server.closeOnJvmShutdown();
    }

    private static CompletableFuture<Boolean> isValidToken(String token) {
        return UnmodifiableFuture.completedFuture(true);
    }

    private static final class GreetingService extends GreetingServiceImplBase {
        @Override
        public void hello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            final String name = request.getName();
            final HelloReply reply = HelloReply.newBuilder()
                                               .setMessage("Hello, " + name + '!')
                                               .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
