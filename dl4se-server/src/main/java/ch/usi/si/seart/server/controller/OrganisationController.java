package ch.usi.si.seart.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/organisation")
public class OrganisationController {

    @GetMapping
    public ResponseEntity<?> getOrganisations(
            @RequestParam(required = false, defaultValue = "")
            String name,
            Pageable pageable
    ) {
        String base = "http://universities.hipolabs.com/search";
        URI uri = UriComponentsBuilder.fromHttpUrl(base)
                .queryParam("name", name)
                .queryParam("limit", pageable.getPageSize())
                .build()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<?> response = restTemplate.exchange(
                uri, HttpMethod.GET, entity, JsonNode.class
        );
        return ResponseEntity.ok(response.getBody());
    }
}
