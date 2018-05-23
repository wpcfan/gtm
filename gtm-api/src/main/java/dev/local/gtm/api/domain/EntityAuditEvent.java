package dev.local.gtm.api.domain;

import org.javers.core.metamodel.object.CdoSnapshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@ToString
@NoArgsConstructor
public class EntityAuditEvent implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String entityId;

  private String entityType;

  private String action;

  private String entityValue;

  private Integer commitVersion;

  private String modifiedBy;

  private Instant modifiedDate;

  public static EntityAuditEvent fromJaversSnapshot(CdoSnapshot snapshot) {
    EntityAuditEvent entityAuditEvent = new EntityAuditEvent();

    switch (snapshot.getType()) {
    case INITIAL:
      entityAuditEvent.setAction("CREATE");
      break;
    case UPDATE:
      entityAuditEvent.setAction("UPDATE");
      break;
    case TERMINAL:
      entityAuditEvent.setAction("DELETE");
      break;
    }

    entityAuditEvent.setId(snapshot.getCommitId().toString());
    entityAuditEvent.setCommitVersion(Math.round(snapshot.getVersion()));
    entityAuditEvent.setEntityType(snapshot.getManagedType().getName());
    entityAuditEvent.setEntityId(snapshot.getGlobalId().value().split("/")[1]);
    entityAuditEvent.setModifiedBy(snapshot.getCommitMetadata().getAuthor());

    if (snapshot.getState().getPropertyNames().size() > 0) {
      int count = 0;
      StringBuilder sb = new StringBuilder("{");

      for (String s : snapshot.getState().getPropertyNames()) {
        count++;
        Object propertyValue = snapshot.getPropertyValue(s);
        sb.append("\"" + s + "\": \"" + propertyValue + "\"");
        if (count < snapshot.getState().getPropertyNames().size()) {
          sb.append(",");
        }
      }

      sb.append("}");
      entityAuditEvent.setEntityValue(sb.toString());
    }
    LocalDateTime localTime = snapshot.getCommitMetadata().getCommitDate();

    Instant modifyDate = localTime.toInstant(ZoneOffset.UTC);

    entityAuditEvent.setModifiedDate(modifyDate);

    return entityAuditEvent;

  }

}
