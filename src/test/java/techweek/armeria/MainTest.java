package techweek.armeria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import com.linecorp.armeria.client.BlockingWebClient;
import com.linecorp.armeria.client.RestClient;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.ResponseEntity;

class MainTest {

    @Test
    void webClient() {
        final WebClient client = WebClient.of("http://127.0.0.1:8080");
        final HttpResponse response = client.get("/greeting/Armeria");

        final CompletableFuture<AggregatedHttpResponse> future = response.aggregate();
        final AggregatedHttpResponse aggregated = future.join();
        assertThat(aggregated.status()).isEqualTo(HttpStatus.OK);
        assertThat(aggregated.contentUtf8()).isEqualTo("Hello, Armeria!");
    }

    @Test
    void blockingClient() {
        final BlockingWebClient client = BlockingWebClient.of("http://127.0.0.1:8080");
        final AggregatedHttpResponse response = client.get("/greeting/Armeria");

        assertThat(response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.contentUtf8()).isEqualTo("Hello, Armeria!");
    }

    @Test
    void restClient() {
        final RestClient client = RestClient.of("http://127.0.0.1:8080");
        final CompletableFuture<ResponseEntity<String>> future =
                client.get("/greeting/:name")
                      .pathParam("name", "Armeria")
                      .execute(String.class);
        final ResponseEntity<String> response = future.join();

        assertThat(response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.content()).isEqualTo("Hello, Armeria!");
    }
}
