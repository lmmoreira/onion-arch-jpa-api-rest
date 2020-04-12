package br.com.company.logistics.project.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class DeliveryExternalSystemResolver {

    private static final String DELIVERY_EXTERNAL_SYSTEM_project = "project";
    private static final String TENANT_BR = "br";
    
    public String formatDeliveryExternalSystem(final String tenant, final String deliveryExternalSystem) {
        if (isDeliveryExternalSystemprojectAndNonTenantBR(tenant, deliveryExternalSystem)) {
            return String.format("%s-%s", DELIVERY_EXTERNAL_SYSTEM_project, tenant);
        }
        return deliveryExternalSystem;
    }

    private boolean isDeliveryExternalSystemprojectAndNonTenantBR(final String tenant,
                                                                final String deliveryExternalSystem) {
        return StringUtils.startsWith(deliveryExternalSystem, DELIVERY_EXTERNAL_SYSTEM_project) &&
                !TENANT_BR.equals(tenant);
    }
}
