package br.com.company.logistics.project.driver;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
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
@Table(name = "DRIVER_ANONYMIZATION")
@EntityListeners(AuditingEntityListener.class)
class DriverAnonymizationEntity {

    @Id
    @NonNull
    @Column(name = "UUID", nullable = false, length = 36, updatable = false)
    private UUID uuid;

    @NonNull
    @Column(name = "DRIVER_UUID", nullable = false, length = 36, updatable = false)
    private UUID driverUuid;

    @NonNull
    @Column(name = "USER_UUID", nullable = false, length = 36, updatable = false)
    private String userUuid;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    private ZonedDateTime createdAt;

    @Column(name = "ANONYMIZED_AT")
    @Setter
    private ZonedDateTime anonymizedAt;

    @Version
    private long version;

}
