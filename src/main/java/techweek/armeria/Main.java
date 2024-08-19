package techweek.armeria;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;

import io.grpc.stub.StreamObserver;
import techweek.armeria.Greeting.HelloReply;
import techweek.armeria.Greeting.HelloRequest;
import techweek.armeria.GreetingServiceGrpc.GreetingServiceImplBase;

public class Main {

    public static void main(String[] args) {
        final GrpcService grpcService =
                GrpcService.builder()
                           .addService(new GreetingService())
                           .enableHttpJsonTranscoding(true)
                           .enableUnframedRequests(true)
                           .build();
        final Server server =
                Server.builder()
                      .http(8080)
                      .service(grpcService)
                      .serviceUnder("/docs", new DocService())
                      .build();

        server.start().join();
        server.closeOnJvmShutdown();
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
