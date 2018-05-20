package dev.local.gtm.api.service;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import dev.local.gtm.api.config.audit.AuditEventConverter;
import dev.local.gtm.api.repository.mongo.PersistenceAuditEventRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuditEventService {

  private final PersistenceAuditEventRepository persistenceAuditEventRepository;

  private final AuditEventConverter auditEventConverter;

  public Page<AuditEvent> findAll(Pageable pageable) {
    return persistenceAuditEventRepository.findAll(pageable).map(auditEventConverter::convertToAuditEvent);
  }

  public Page<AuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable) {
    return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable)
        .map(auditEventConverter::convertToAuditEvent);
  }

  public Optional<AuditEvent> find(String id) {
    return persistenceAuditEventRepository.findById(id).map(auditEventConverter::convertToAuditEvent);
  }
}
