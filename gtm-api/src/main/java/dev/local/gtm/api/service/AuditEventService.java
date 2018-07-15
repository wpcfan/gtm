package dev.local.gtm.api.service;

import dev.local.gtm.api.config.Constants;
import dev.local.gtm.api.domain.EntityAuditEvent;
import dev.local.gtm.api.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuditEventService {

  private static final String basePackageName = Constants.BASE_PACKAGE_NAME + ".domain.";
  private final Javers javers;

  @Secured(AuthoritiesConstants.ADMIN)
  public Page<EntityAuditEvent> getChanges(String entityType, Pageable pageable) throws ClassNotFoundException {
    log.debug("获得一页指定实体对象类型的审计事件");
    val entityTypeToFetch = Class.forName(basePackageName + entityType);
    val jqlQuery = QueryBuilder.byClass(entityTypeToFetch)
      .limit(pageable.getPageSize())
      .skip(pageable.getPageNumber() * pageable.getPageSize())
      .withNewObjectChanges(true);

    val auditEvents = javers.findSnapshots(jqlQuery.build()).stream()
      .map(snapshot -> {
        EntityAuditEvent event = EntityAuditEvent.fromJaversSnapshot(snapshot);
        event.setEntityType(entityType);
        return event;
      })
      .collect(Collectors.toList());

    return new PageImpl<EntityAuditEvent>(auditEvents);
  }

  @Secured(AuthoritiesConstants.ADMIN)
  public EntityAuditEvent getPrevVersion(String entityType, String entityId, Long commitVersion)
      throws ClassNotFoundException {
    val entityTypeToFetch = Class.forName(basePackageName + entityType);

    val jqlQuery = QueryBuilder.byInstanceId(entityId, entityTypeToFetch).limit(1)
        .withVersion(commitVersion - 1).withNewObjectChanges(true);

    return EntityAuditEvent.fromJaversSnapshot(javers.findSnapshots(jqlQuery.build()).get(0));
  }
}
