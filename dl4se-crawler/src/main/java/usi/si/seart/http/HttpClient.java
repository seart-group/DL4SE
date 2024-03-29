package usi.si.seart.http;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import usi.si.seart.http.payload.GhsGitRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HttpClient {

    static HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
    static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

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
        HttpHeaders responseHeaders = response.getHeaders();
        String linkHeaderValue = responseHeaders.getFirstHeaderStringValue("links");
        Map<String, String> links = new HashMap<>();
        Matcher matcher = HEADER_LINK.matcher(linkHeaderValue);

        while (matcher.find()) {
            String rel = matcher.group(2);
            String link = matcher.group(1);
            links.put(rel, link);
        }

        return links;
    }
}
