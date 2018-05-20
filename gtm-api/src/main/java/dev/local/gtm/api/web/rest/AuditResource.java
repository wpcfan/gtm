package dev.local.gtm.api.web.rest;

import org.elasticsearch.ResourceNotFoundException;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.local.gtm.api.service.AuditEventService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/management/audits")
public class AuditResource {

  private final AuditEventService auditEventService;

  /**
   * GET /audits : get a page of AuditEvents.
   *
   * @param pageable the pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents
   *         in body
   */
  @GetMapping
  public ResponseEntity<List<AuditEvent>> getAll(Pageable pageable) {
    Page<AuditEvent> page = auditEventService.findAll(pageable);
    return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
  }

  /**
   * GET /audits : get a page of AuditEvents between the fromDate and toDate.
   *
   * @param fromDate the start of the time period of AuditEvents to get
   * @param toDate   the end of the time period of AuditEvents to get
   * @param pageable the pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents
   *         in body
   */
  @GetMapping("/datesbetween")
  public ResponseEntity<List<AuditEvent>> getByDates(@RequestParam("fromDate") LocalDate fromDate,
      @RequestParam("toDate") LocalDate toDate, Pageable pageable) {
    Page<AuditEvent> page = auditEventService.findByDates(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
        toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant(), pageable);
    return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
  }

  /**
   * GET /audits/:id : get an AuditEvent by id.
   *
   * @param id the id of the entity to get
   * @return the ResponseEntity with status 200 (OK) and the AuditEvent in body,
   *         or status 404 (Not Found)
   */
  @GetMapping("/{id:.+}")
  public AuditEvent get(@PathVariable String id) {
    return auditEventService.find(id).orElseThrow(() -> new ResourceNotFoundException("没有找到 id 为 " + id + " 的审核事件"));
  }
}
