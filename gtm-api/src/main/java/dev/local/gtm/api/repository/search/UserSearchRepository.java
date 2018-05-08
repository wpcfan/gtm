package dev.local.gtm.api.repository.search;

import dev.local.gtm.api.domain.search.UserSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSearchRepository extends ElasticsearchRepository<UserSearch, String> {
}
