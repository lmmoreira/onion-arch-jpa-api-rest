package br.com.company.logistics.project.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@EnableConfigurationProperties
@ConfigurationProperties("tenant-identifier")
public class TenantIdentifierHolder {

    private Map<String, String> tenantExceptionallyMap;

    public String getValue(final String tenant) {
        final String tenantLower = StringUtils.lowerCase(tenant);
        return tenantExceptionallyMap.getOrDefault(tenantLower, tenantLower);
    }
}
