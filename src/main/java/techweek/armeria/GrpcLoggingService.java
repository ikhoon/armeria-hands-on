package techweek.armeria;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.RpcRequest;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.SimpleDecoratingHttpService;

final class GrpcLoggingService extends SimpleDecoratingHttpService {

    private static final Logger logger = LoggerFactory.getLogger(GrpcLoggingService.class);

    GrpcLoggingService(HttpService delegate) {
        super(delegate);
    }

    /**
     * Logs the gRPC request and response.
     * <pre>{@code
     * <service>:<method>(<parameters>) -> <response> grpc-status=<int> (<elapsed> ms)
     * }</pre>
     */
    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        ctx.log().whenComplete().thenAccept(log -> {
            if(log.requestContent() instanceof RpcRequest) {
                final RpcRequest rpcRequest = (RpcRequest) log.requestContent();
                logger.info("{}.{}({}) -> {} grpc-status={} ({} ms)",
                            rpcRequest.serviceName(),
                            rpcRequest.method(),
                            Joiner.on(",").join(rpcRequest.params()),
                            log.responseContent(), log.responseTrailers().get("grpc-status"),
                            TimeUnit.NANOSECONDS.toMillis(log.totalDurationNanos()));
            }
        });

        return unwrap().serve(ctx, req);
    }
}
