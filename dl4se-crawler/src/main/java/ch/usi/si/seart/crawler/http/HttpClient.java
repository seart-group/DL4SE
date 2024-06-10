package ch.usi.si.seart.crawler.http;

import ch.usi.si.seart.crawler.dto.SearchResultDto;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HttpClient {

    Gson gson;
    Pattern headerLinkPattern;
    HttpRequestFactory requestFactory;

    @SneakyThrows(IOException.class)
    public HttpResponse getRequest(GenericUrl url) {
        HttpRequest request = requestFactory.buildGetRequest(url);
        return request.execute();
    }

    @SneakyThrows(IOException.class)
    public List<SearchResultDto> getSearchResults(HttpResponse response) {
        String responseString = response.parseAsString();
        JsonObject searchResult = gson.fromJson(responseString, JsonObject.class);
        JsonArray items = searchResult.get("items").getAsJsonArray();
        return StreamSupport.stream(items.spliterator(), false)
                .map(item -> gson.fromJson(item, SearchResultDto.class))
                .collect(Collectors.toList());
    }

    public Map<String, String> getNavigationLinks(HttpResponse response){
        String xLinkSearch = response.getHeaders().getFirstHeaderStringValue("x-link-search");
        Map<String, String> links = new HashMap<>();
        Matcher matcher = headerLinkPattern.matcher(xLinkSearch);

        while (matcher.find()) {
            String rel = matcher.group(2);
            String link = matcher.group(1);
            links.put(rel, link);
        }

        return links;
    }
}
