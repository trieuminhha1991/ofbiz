#! /bin/bash

cat > ${OLBIUS_PATH:-"../.."}/framework/service/config/serviceengine.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>

<service-config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:noNamespaceSchemaLocation="http://ofbiz.apache.org/dtds/service-config.xsd">

    <service-engine name="default">
        <!-- Name of the service to use for authorization -->
        <authorization service-name="userLogin"/>

        <!-- Job poller configuration. Many of these attributes are set to the job poller defaults, but they are included here for convenience. -->
        <thread-pool send-to-pool="pool"
                     purge-job-days="4"
                     failed-retry-min="3"
                     ttl="120000"
                     jobs="${THREAD_POOL_JOBS:-100}"
                     min-threads="${THREAD_POOL_MIN:-2}"
                     max-threads="${THREAD_POOL_MAX:-5}"
                     poll-enabled="true"
                     poll-db-millis="30000">
            <run-from-pool name="pool"/>
        </thread-pool>

        <!-- Service Engine Configuration -->
        <engine name="entity-auto" class="org.ofbiz.service.engine.EntityAutoEngine"/>
        <engine name="group" class="org.ofbiz.service.group.ServiceGroupEngine"/>
        <engine name="interface" class="org.ofbiz.service.engine.InterfaceEngine"/>
        <engine name="java" class="org.ofbiz.service.engine.StandardJavaEngine"/>
        <engine name="simple" class="org.ofbiz.minilang.SimpleServiceEngine"/>
        <engine name="script" class="org.ofbiz.service.engine.ScriptEngine"/>
        <engine name="olbius" class="com.olbius.service.engine.StandardJavaEngine"/>
	<engine name="olbius-java" class="com.olbius.service.engine.OlbiusJavaEngine"/>
        <!-- Engines that can be replaced by the generic script engine -->
        <engine name="bsh" class="org.ofbiz.service.engine.BeanShellEngine"/>
        <engine name="groovy" class="org.ofbiz.service.engine.GroovyEngine">
            <parameter name="scriptBaseClass" value="org.ofbiz.service.engine.GroovyBaseScript"/>
        </engine>
        <engine name="jacl" class="org.ofbiz.service.engine.BSFEngine"/>
        <engine name="javascript" class="org.ofbiz.service.engine.ScriptEngine"/>
        <engine name="jpython" class="org.ofbiz.service.engine.BSFEngine"/>
        <!--  -->
        <engine name="route" class="org.ofbiz.service.engine.RouteEngine"/>
        <engine name="http" class="org.ofbiz.service.engine.HttpEngine"/>
        <engine name="jms" class="org.ofbiz.service.jms.JmsServiceEngine"/>
        <engine name="rmi" class="org.ofbiz.service.rmi.RmiServiceEngine"/>
        <engine name="soap" class="org.ofbiz.service.engine.SOAPClientEngine"/>
        <!-- The engine xml-rpc-local is only used by a test service and for
             this reason it is configured to run on port 8080 (see rmi-dispatcher in service/ofbiz-component.xml);
             in order to use this in OFBiz change the port accordingly (for demo the default value is 8080)
        -->
        <engine name="xml-rpc-local" class="org.ofbiz.service.engine.XMLRPCClientEngine">
            <parameter name="url" value="http://localhost:8080/webtools/control/xmlrpc"/>
            <parameter name="login" value="admin"/>
            <parameter name="password" value="ofbiz"/>
        </engine>

        <service-location name="main-rmi" location="rmi://localhost:1099/RMIDispatcher"/>
        <service-location name="main-http" location="http://localhost:8080/webtools/control/httpService"/>

        <service-location name="entity-sync-rmi" location="rmi://localhost:1099/RMIDispatcher"/>
        <service-location name="entity-sync-http" location="http://localhost:8080/webtools/control/httpService"/>

        <service-location name="rita-rmi" location="rmi://localhost:1099/RMIDispatcher"/>
        <service-location name="eedcc-test" location="http://localhost:8080/webtools/control/httpService"/>
EOF

if [[ "${JMS_ALLOW}" == "true" ]]; then
	echo "
	<jms-service name=\"serviceMessenger\" send-mode=\"all\">
            <server jndi-server-name=\"default\"
                    jndi-name=\"topicConnectionFactory\"
                    topic-queue=\"OFBTopic\"
                    type=\"topic\"
                    username=\"${ACTIVEMQ_USER}\"
                    password=\"${ACTIVEMQ_PASSWORD}\"
                    listen=\"true\"/>
        </jms-service>
" >> ${OLBIUS_PATH:-"../.."}/framework/service/config/serviceengine.xml
fi

echo "
    </service-engine>
</service-config>" >> ${OLBIUS_PATH:-"../.."}/framework/service/config/serviceengine.xml

