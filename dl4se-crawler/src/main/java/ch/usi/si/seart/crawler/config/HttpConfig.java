package ch.usi.si.seart.crawler.config;

import com.google.api.client.http.HttpIOExceptionHandler;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.BackOffUtils;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class HttpConfig {

    /**
     * @return pattern for matching Link header values of various REST API responses.
     *
     * @see <a href="https://www.debuggex.com/r/MEmpnWMfSmjwnWUS">Regex definition visualisation</a>
     */
    @Bean
    public Pattern headerLinkPattern() {
        return Pattern.compile("<([^>]+)>;\\s?rel=\"(\\w+)\"");
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
    }

    @Bean
    public BackOff backOff() {
        return new ExponentialBackOff();
    }

    @Bean
    public Sleeper sleeper() {
        return Sleeper.DEFAULT;
    }

    @Bean
    public HttpIOExceptionHandler ioExceptionHandler(Sleeper sleeper, BackOff backOff) {
        return (request, supportsRetry) -> {
            if (!supportsRetry) {
                return false;
            }

            try {
                return BackOffUtils.next(sleeper, backOff);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return false;
            }
        };
    }

    @Bean
    public HttpRequestInitializer requestInitializer(HttpIOExceptionHandler ioExceptionHandler) {
        return request -> {
            int timeout = 60_000;
            request.setReadTimeout(timeout);
            request.setConnectTimeout(timeout);
            request.setIOExceptionHandler(ioExceptionHandler);
        };
    }

    @Bean
    public NetHttpTransport netHttpTransport() {
        return new NetHttpTransport();
    }

    @Bean
    public HttpRequestFactory requestFactory(
            NetHttpTransport httpTransport, HttpRequestInitializer httpRequestInitializer
    ) {
        return httpTransport.createRequestFactory(httpRequestInitializer);
    }
}
