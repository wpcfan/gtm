package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.security.AuthoritiesConstants;
import dev.local.gtm.api.security.SecurityUtils;
import dev.local.gtm.api.service.ElasticsearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ElasticsearchIndexResource {

    private final ElasticsearchIndexService elasticsearchIndexService;

    /**
     * POST  /elasticsearch/index -> Reindex all Elasticsearch documents
     */
    @PostMapping("/elasticsearch/index")
//    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> reindexAll() {
        log.info("REST request to reindex Elasticsearch by user : {}", SecurityUtils.getCurrentUserLogin());
        elasticsearchIndexService.reindexAll();
        return ResponseEntity.accepted().build();
    }
}

