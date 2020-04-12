package br.com.company.logistics.project.driver;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "uuid")
@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicUpdate
@Table(name = "DRIVER")
@NamedEntityGraphs({
    @NamedEntityGraph(name = "driverEntityGraph", attributeNodes = {@NamedAttributeNode(value = "attributes")})})
@EntityListeners(AuditingEntityListener.class)
class DriverEntity {

    @Id
    @NonNull
    @Column(name = "UUID", nullable = false, length = 36)
    private UUID uuid;

    @Column(name = "EXTERNAL_ID", updatable = false)
    @Setter
    private String externalId;

    @Column(name = "TENANT", nullable = false, updatable = false)
    @NonNull
    private String tenant;

    @Column(name = "DELIVERY_EXTERNAL_SYSTEM", updatable = false)
    @Setter
    private String deliveryExternalSystem;

    @Column(name = "EXTERNAL_UPDATED_AT", nullable = false)
    @NonNull
    @Setter
    private ZonedDateTime externalUpdatedAt;

    @Column(name = "USER_UUID", length = 36)
    @Setter
    private UUID userUuid;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "driver", orphanRemoval = true)
    private Set<DriverAttributeEntity> attributes = new HashSet<>();

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    private ZonedDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    private ZonedDateTime updatedAt;

    @Column(name = "ANONYMIZED_AT")
    @Setter
    private ZonedDateTime anonymizedAt;

    @Version
    private long version;

    public void addDriverAttributeEntity(final DriverAttributeEntity driverAttributeEntity) {
        driverAttributeEntity.setDriver(this);
        getAttributes().add(driverAttributeEntity);
    }

    public void addDriverAttributeEntities(final Set<DriverAttributeEntity> driverAttributeEntities) {
        driverAttributeEntities.forEach(driverAttributeEntity -> driverAttributeEntity.setDriver(this));
        getAttributes().addAll(driverAttributeEntities);
    }

}
