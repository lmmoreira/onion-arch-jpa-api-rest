package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeGroup.BANK_ACCOUNT;
import static br.com.company.logistics.project.driver.DriverAttributeGroup.DEFAULT;

import org.apache.commons.lang3.EnumUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum DriverAttributeName {
    FIRST_NAME,
    FULL_NAME,
    BIRTHDATE,
    PHONE,
    EMAIL,
    CPF,
    MOTHERS_NAME,
    FATHERS_NAME,
    DRIVERS_LICENSE,
    IDENTITY_DOCUMENT,
    REFER_NAME,
    WORKER_PHOTO(true),
    WORKER_PHOTO_SMALL(true),
    DRIVERS_LICENSE_PHOTO(true),
    IDENTITY_DOCUMENT_BACK_PHOTO(true),
    IDENTITY_DOCUMENT_FRONT_PHOTO(true),
    TERMS_AND_CONDITIONS_AGREED,
    BACKGROUND_CHECK_FILE(true),
    BACKGROUND_CHECK_ID,
    FIRST_LAST_NAME,
    SECOND_LAST_NAME,
    VEHICLE_DOCUMENT_PHOTO(true),
    FISCAL_DOCUMENT,
    FISCAL_DOCUMENT_PHOTO(true),
    BANK_DOCUMENT_PHOTO(true),
    VEHICLE_LICENSE_PLATE,
    VEHICLE_LICENSE_PLATE_PHOTO(true),
    BANK_ACCOUNT_BANK_CODE(BANK_ACCOUNT),
    BANK_ACCOUNT_BANK_NAME(BANK_ACCOUNT),
    BANK_ACCOUNT_AGENCY(BANK_ACCOUNT),
    BANK_ACCOUNT_NUMBER(BANK_ACCOUNT),
    BANK_ACCOUNT_DIGIT(BANK_ACCOUNT);

    private final boolean isFile;
    private final DriverAttributeGroup group;

    DriverAttributeName() {
        this.isFile = false;
        this.group = DEFAULT;
    }

    DriverAttributeName(final boolean isFile) {
        this.isFile = isFile;
        this.group = DEFAULT;
    }

    DriverAttributeName(final DriverAttributeGroup group) {
        this.isFile = false;
        this.group = group;
    }

    public static Optional<DriverAttributeName> getAttribute(final String attributeKey) {
        return Optional.ofNullable(EnumUtils.getEnum(DriverAttributeName.class, attributeKey));
    }

    public boolean isFile() {
        return isFile;
    }

    public DriverAttributeGroup getGroup() {
        return group;
    }

    public static Set<DriverAttributeName> valuesByBankAccountGroup() {
        return Stream.of(values())
                .filter(e -> e.group.equals(BANK_ACCOUNT))
                .collect(Collectors.toUnmodifiableSet());
    }
}
