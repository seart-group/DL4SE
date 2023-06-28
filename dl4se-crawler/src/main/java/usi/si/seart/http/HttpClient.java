package usi.si.seart.http;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpIOExceptionHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.BackOffUtils;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import usi.si.seart.http.payload.GhsGitRepo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HttpClient {

    private static final class RetryHandler implements HttpIOExceptionHandler {

        BackOff backOff = new ExponentialBackOff();
        Sleeper sleeper = Sleeper.DEFAULT;

        @Override
        public boolean handleIOException(HttpRequest request, boolean supportsRetry) throws IOException {
            if (!supportsRetry) {
                return false;
            }

            try {
                return BackOffUtils.next(sleeper, backOff);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    private static final class RequestInitializer implements HttpRequestInitializer {

        private static final int TIMEOUT = 60_000;

        @Override
        public void initialize(HttpRequest request) {
            request.setReadTimeout(TIMEOUT);
            request.setConnectTimeout(TIMEOUT);
            request.setIOExceptionHandler(new RetryHandler());
        }
    }

    private static final HttpTransport transport = new NetHttpTransport();
    private static final HttpRequestInitializer requestInitializer = new RequestInitializer();
    private static final HttpRequestFactory requestFactory = transport.createRequestFactory(requestInitializer);
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    @SneakyThrows
    public HttpResponse getRequest(GenericUrl url) {
        HttpRequest request = requestFactory.buildGetRequest(url);
        return request.execute();
    }

    @SneakyThrows
    public List<GhsGitRepo> getSearchResults(HttpResponse response) {
        String responseString = response.parseAsString();
        JsonObject searchResult = gson.fromJson(responseString, JsonObject.class);
        JsonArray items = searchResult.get("items").getAsJsonArray();
        return StreamSupport.stream(items.spliterator(), false)
                .map(item -> gson.fromJson(item, GhsGitRepo.class))
                .collect(Collectors.toList());
    }

    /**
     * Pattern for matching Link header values of various REST API responses.
     *
     * @see <a href="https://www.debuggex.com/r/MEmpnWMfSmjwnWUS">Regex definition visualisation</a>
     */
    private static final Pattern HEADER_LINK = Pattern.compile("<([^>]+)>;\\s?rel=\"(\\w+)\"");

    public Map<String, String> getNavigationLinks(HttpResponse response){
        String xLinkSearch = response.getHeaders().getFirstHeaderStringValue("x-link-search");
        Map<String, String> links = new HashMap<>();
        Matcher matcher = HEADER_LINK.matcher(xLinkSearch);

        while (matcher.find()) {
            String rel = matcher.group(2);
            String link = matcher.group(1);
            links.put(rel, link);
        }

        return links;
    }
}
