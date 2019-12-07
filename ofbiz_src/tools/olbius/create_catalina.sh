#/bin/bash

cat > ${OLBIUS_PATH:-"../.."}/framework/catalina/ofbiz-component.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>

<ofbiz-component name="catalina"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:noNamespaceSchemaLocation="http://ofbiz.apache.org/dtds/ofbiz-component.xsd">
    <resource-loader name="main" type="component"/>
    <classpath type="jar" location="lib/*"/>
    <classpath type="jar" location="build/lib/*"/>
    <classpath type="dir" location="config"/>
    <entity-resource type="model" reader-name="main" loader="main" location="entitydef/entitymodel.xml"/>

    <container name="catalina-container" loaders="main" class="org.ofbiz.catalina.container.CatalinaContainer">
        <!-- static configuration for tomcat -->
        <property name="delegator-name" value="default"/>
        <property name="use-naming" value="false"/>
        <property name="debug" value="0"/>
        <property name="catalina-runtime-home" value="runtime/catalina"/>
        <property name="apps-context-reloadable" value="false"/>
        <property name="apps-cross-context" value="false"/>
        <property name="apps-distributable" value="false"/>
        <!-- one or more tomcat engines (servers); map to this + host -->
        <property name="default-server" value="engine">
            <property name="default-host" value="0.0.0.0"/>
            <!-- <property name="jvm-route" value="jvm1"/> -->
            <!-- <property name="ssl-accelerator-port" value="8443"/> -->
            <property name="enable-cross-subdomain-sessions" value="false"/>
        </property>
        <!-- all connectors support type, host, port, enable-lookups -->
        <property name="http-connector" value="connector">
            <!-- see http://jakarta.apache.org/tomcat/tomcat-5.5-doc/config/http.html for reference -->
            <property name="allowTrace" value="false"/>
            <property name="emptySessionPath" value="false"/>
            <property name="enableLookups" value="false"/>
            <property name="maxPostSize" value="2097152"/>
            <property name="protocol" value="HTTP/1.1"/>
            <property name="proxyName" value=""/>
            <property name="proxyPort" value=""/>
            <property name="redirectPort" value=""/>
            <property name="scheme" value="http"/>
            <property name="secure" value="false"/>
            <property name="URIEncoding" value="UTF-8"/>
            <property name="useBodyEncodingForURI" value="false"/>
            <property name="xpoweredBy" value="true"/>
            <!-- HTTP connector attributes -->
            <property name="acceptCount" value="10"/>
            <property name="address" value="0.0.0.0"/>
            <property name="bufferSize" value="2048"/>
            <property name="compression" value="off"/>
            <property name="compressableMimeType" value="text/html,text/xml,text/plain,text/javascript,text/css"/>
            <property name="noCompressionUserAgents" value=""/>
            <property name="connectionLinger" value="-1"/>
            <property name="connectionTimeout" value="120000"/>
            <property name="disableUploadTimeout" value="false"/>
            <property name="maxHttpHeaderSize" value="4096"/>
            <property name="maxKeepAliveRequests" value="400"/>
            <property name="maxSpareThreads" value="50"/>
            <property name="maxThreads" value="400"/>
            <property name="minSpareThreads" value="4"/>
            <property name="port" value="8080"/>
            <property name="restrictedUserAgents" value=""/>
            <property name="server" value=""/>
            <property name="socketBuffer" value="9000"/>
            <property name="strategy" value="lf"/>
            <property name="tcpNoDelay" value="true"/>
            <property name="threadPriority" value="java.lang.Thread#NORM_PRIORITY"/>
        </property>
        <property name="https-connector" value="connector">
            <!-- see http://jakarta.apache.org/tomcat/tomcat-5.5-doc/config/http.html for reference -->
            <property name="allowTrace" value="false"/>
            <property name="emptySessionPath" value="false"/>
            <property name="enableLookups" value="false"/>
            <property name="maxPostSize" value="20971520"/>
            <property name="protocol" value="HTTP/1.1"/>
            <property name="proxyName" value=""/>
            <property name="proxyPort" value=""/>
            <property name="redirectPort" value=""/>
            <property name="scheme" value="https"/>
            <property name="secure" value="true"/>
            <property name="URIEncoding" value="UTF-8"/>
            <property name="useBodyEncodingForURI" value="false"/>
            <property name="xpoweredBy" value="true"/>
            <!-- HTTP connector attributes -->
            <property name="acceptCount" value="10"/>
            <property name="address" value="0.0.0.0"/>
            <property name="bufferSize" value="2048"/>
            <property name="compression" value="off"/>
            <property name="compressableMimeType" value="text/html,text/xml,text/plain,text/javascript,text/css"/>
            <property name="noCompressionUserAgents" value=""/>
            <property name="connectionLinger" value="-1"/>
            <property name="connectionTimeout" value="120000"/>
            <property name="disableUploadTimeout" value="false"/>
            <property name="maxHttpHeaderSize" value="4096"/>
            <property name="maxKeepAliveRequests" value="400"/>
            <property name="maxSpareThreads" value="50"/>
            <property name="maxThreads" value="400"/>
            <property name="minSpareThreads" value="4"/>
            <property name="port" value="8443"/>
            <property name="restrictedUserAgents" value=""/>
            <property name="server" value=""/>
            <property name="socketBuffer" value="9000"/>
            <property name="strategy" value="lf"/>
            <property name="tcpNoDelay" value="true"/>
            <property name="threadPriority" value="java.lang.Thread#NORM_PRIORITY"/>
            <!-- SSL connector attributes -->
            <property name="sSLImplementation" value="org.ofbiz.catalina.container.SSLImpl"/>
            <property name="algorithm" value="SunX509"/>
            <property name="clientAuth" value="false"/>
            <property name="keystoreFile" value="framework/base/config/olbiusnew.jks"/>
            <property name="keystorePass" value="olbius"/>
            <property name="keystoreType" value="JKS"/>
            <property name="sslProtocol" value="TLS"/>
            <property name="ciphers" value=""/>
        </property>
    </container>
</ofbiz-component>

EOF
