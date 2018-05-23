package dev.local.gtm.api.web.rest;

import org.elasticsearch.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import dev.local.gtm.api.domain.EntityAuditEvent;
import dev.local.gtm.api.security.AuthoritiesConstants;
import dev.local.gtm.api.service.AuditEventService;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuditResource {

  private final AuditEventService auditEventService;

  @GetMapping("/management/audits/entities")
  @Secured(AuthoritiesConstants.ADMIN)
  public List<String> getAuditedEntities() {
    return Arrays.asList("User", "Authority", "Task");
  }

  @GetMapping("/management/audits/getchanges")
  public String retrieveChanges(@RequestParam("entityType") String entityType) {
    return auditEventService.getChangesByClassName(entityType)
      .orElseThrow(() -> new ResourceNotFoundException("error"));
  }

  @GetMapping("/management/audits/changes")
  public Page<EntityAuditEvent> getChanges(@RequestParam("entityType") String entityType, Pageable pageable) {
    try {
      return auditEventService.getChanges(entityType, pageable);
    } catch(ClassNotFoundException e) {
      throw new ResourceNotFoundException("指定的类型没有找到");
    }
  }

  @GetMapping("/management/audits/previous")
  public EntityAuditEvent getPreviousVersion(
    @RequestParam String entityType,
    @RequestParam String entityId,
    @RequestParam Long commitVersion) {
    try {
      return auditEventService.getPrevVersion(entityType, entityId, commitVersion);
    } catch (ClassNotFoundException e) {
      throw new ResourceNotFoundException("指定的类型没有找到");
    }
  }
}
