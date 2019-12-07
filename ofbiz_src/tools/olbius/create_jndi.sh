#! /bin/bash

if [[ ( "$JMS_ALLOW" == "true" ) && ( ! -z $ACTIVEMQ_HOST ) && ( ! -z $ACTIVEMQ_TOPIC ) ]]; then

cat > ${OLBIUS_PATH:-"../.."}/framework/base/config/jndi.properties << EOF
java.naming.factory.initial=org.apache.activemq.jndi.ActiveMQInitialContextFactory
java.naming.provider.url=tcp://${ACTIVEMQ_HOST}:${ACTIVEMQ_PORT:-61616}
topic.OFBTopic=${ACTIVEMQ_TOPIC}
connectionFactoryNames=connectionFactory, queueConnectionFactory, topicConnectionFactory
EOF

fi
