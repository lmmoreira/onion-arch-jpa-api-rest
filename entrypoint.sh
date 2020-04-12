#!/bin/bash
EXPORT_VARS_FILES=${EXPORT_VARS_FILES:-true}
if [ "${EXPORT_VARS_FILES}" == "true" ]; then
  if [ -d /vault/secrets ]; then for i in $(ls -1 /vault/secrets); do source /vault/secrets/$i; done; fi
fi
sed -i "s#\$NR_LICENSE_KEY#${NR_LICENSE_KEY}#g" /newrelic/newrelic.yml && \
 sed -i "s#\$NR_APP_NAME#${NR_APP_NAME}#g" /newrelic/newrelic.yml && \
 java ${JAVA_OPTS} \
 -javaagent:/newrelic/newrelic.jar -Dnewrelic.config.file=/newrelic/newrelic.yml \
 -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
 -jar "/usr/src/app/project-driver-account-api.jar"
