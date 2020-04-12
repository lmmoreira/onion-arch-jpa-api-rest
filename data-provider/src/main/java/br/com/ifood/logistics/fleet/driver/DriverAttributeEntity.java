package br.com.company.logistics.project.driver;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "uuid")
@Entity
@DynamicUpdate
@Table(name = "DRIVER_ATTRIBUTE")
@EntityListeners(AuditingEntityListener.class)
class DriverAttributeEntity {

    @Id
    @NonNull
    @Column(name = "UUID", nullable = false, length = 36)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private DriverEntity driver;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(name = "ATTRIBUTE", nullable = false, updatable = false)
    private DriverAttributeName name;

    @Column(name = "VALUE", nullable = false)
    @Setter
    private String value;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    private ZonedDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    private ZonedDateTime updatedAt;

    @Version
    private long version;

    public static DriverAttributeEntity of(final UUID uuid, final DriverAttributeName name, final String value) {
        final DriverAttributeEntity entity = of(uuid, name);
        entity.value = value;
        return entity;
    }

}
