package techweek.armeria;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;

public class Main {
    public static void main(String[] args) {
        final Server server =
                Server.builder()
                      .http(8080)
                      .service("/greeting/:name", (ctx, req) -> {
                          final String name = ctx.pathParam("name");
                          return HttpResponse.of("Hello, " + name + '!');
                      })
                      .build();

        server.start().join();
        server.closeOnJvmShutdown();
    }
}
