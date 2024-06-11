package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.server.feign.UniversityDomainsListClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organisation")
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganisationController {

    UniversityDomainsListClient universityDomainsListClient;

    @GetMapping
    public ResponseEntity<?> organisations(
            @RequestParam(required = false, defaultValue = "")
            String name,
            Pageable pageable
    ) {
        int limit = pageable.getPageSize();
        JsonNode response = universityDomainsListClient.search(name, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{domain}")
    public ResponseEntity<?> organisations(@PathVariable String domain) {
        JsonNode response = universityDomainsListClient.search(domain);
        JsonNode result = response.get(0);
        return ResponseEntity.ok(result);
    }
}
