package ch.usi.si.seart.server.feign;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "university-domains-list", url = "http://universities.hipolabs.com")
public interface UniversityDomainsListClient {

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    JsonNode search(@RequestParam("domain") String domain);

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    JsonNode search(@RequestParam("name") String name, @RequestParam("limit") Integer limit);
}
