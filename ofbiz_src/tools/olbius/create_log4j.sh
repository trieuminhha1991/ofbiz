#/bin/bash

cat > ${OLBIUS_PATH:-"../.."}/framework/base/config/log4j.xml << EOF
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/" debug="false">
    <!-- async appender -->
    <appender name="async" class="org.apache.log4j.AsyncAppender">
        <param name="locationInfo" value="true"/>
        <param name="blocking" value="false"/>
        <param name="bufferSize" value="256"/>
        <appender-ref ref="stdout"/>
        <appender-ref ref="ofbiz-file"/>
        <appender-ref ref="debug"/>
        <appender-ref ref="error"/>
        <!-- <appender-ref ref="socket"/> -->
        <!-- <appender-ref ref="email"/> -->
    </appender>

    <!-- ofbiz file appender -->
    <appender name="ofbiz-file" class="org.apache.log4j.RollingFileAppender">
        <param name="maxFileSize" value="10000KB"/>
        <param name="maxBackupIndex" value="10"/>
        <param name="File" value="\${ofbiz.home}/runtime/logs/ofbiz.log"/>
        <param name="threshold" value="info"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="[%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}] (%t) [%24F:%-3L:%-5p]%x %m%n"/>
        </layout>
    </appender>

    <!-- debug log -->
    <appender name="debug" class="org.apache.log4j.RollingFileAppender">
        <param name="maxFileSize" value="10000KB"/>
        <param name="maxBackupIndex" value="10"/>
        <param name="File" value="\${ofbiz.home}/runtime/logs/debug.log"/>
        <param name="Append" value="false"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} (%t) [%24F:%-3L:%-5p]%x %m%n"/>
        </layout>
        <filter class="org.apache.log4j.varia.LevelRangeFilter">
            <param name="LevelMax" value="info"/>
            <param name="LevelMin" value="trace"/>
            <param name="AcceptOnMatch" value="true"/>
        </filter>
    </appender>

    <!-- error log -->
    <appender name="error" class="org.apache.log4j.RollingFileAppender">
        <param name="maxFileSize" value="10000KB"/>
        <param name="maxBackupIndex" value="10"/>
        <param name="File" value="\${ofbiz.home}/runtime/logs/error.log"/>
        <param name="Append" value="false"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} (%t) [%24F:%-3L:%-5p]%x %m%n"/>
        </layout>
        <filter class="org.apache.log4j.varia.LevelRangeFilter">
            <param name="LevelMax" value="fatal"/>
            <param name="LevelMin" value="error"/>
            <param name="AcceptOnMatch" value="true"/>
        </filter>
    </appender>

    <!-- special appenders for subcomponents -->
    <appender name="fop-log" class="org.apache.log4j.RollingFileAppender">
        <param name="maxFileSize" value="5000KB"/>
        <param name="maxBackupIndex" value="10"/>
        <param name="File" value="\${ofbiz.home}/runtime/logs/fop.log"/>
        <param name="Append" value="false"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} (%t) [%24F:%-3L:%-5p]%x %m%n"/>
        </layout>
    </appender>

    <appender name="freemarker-log" class="org.apache.log4j.RollingFileAppender">
        <param name="maxFileSize" value="5000KB"/>
        <param name="maxBackupIndex" value="10"/>
        <param name="File" value="\${ofbiz.home}/runtime/logs/freemarker.log"/>
        <param name="Append" value="false"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} (%t) [%24F:%-3L:%-5p]%x %m%n"/>
        </layout>
    </appender>

    <appender name="tomcat-log" class="org.apache.log4j.RollingFileAppender">
        <param name="maxFileSize" value="5000KB"/>
        <param name="maxBackupIndex" value="10"/>
        <param name="File" value="\${ofbiz.home}/runtime/logs/tomcat.log"/>
        <param name="Append" value="false"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} (%t) [%24F:%-3L:%-5p]%x %m%n"/>
        </layout>
    </appender>

    <category name="org.ofbiz.base.converter.Converters">
        <priority value="warn"/>
    </category>

    <category name="org.apache.axis">
        <priority value="warn"/>
    </category>

    <category name="org.apache.commons.digester">
        <priority value="warn"/>
    </category>

    <category name="org.apache.coyote">
        <priority value="warn"/>
    </category>

    <category name="org.apache.jasper">
        <priority value="warn"/>
    </category>

    <category name="org.apache.jk">
        <priority value="warn"/>
    </category>

    <category name="org.apache.geronimo.transaction">
        <priority value="warn"/>
    </category>

    <category name="org.apache.log4j">
        <priority value="warn"/>
    </category>

    <!-- special category/loggers -->
    <category name="org.apache.catalina">
        <priority value="warn"/>
        <appender-ref ref="tomcat-log"/>
    </category>

    <category name="org.apache.fop">
        <priority value="warn"/>
        <appender-ref ref="fop-log"/>
    </category>

    <category name="freemarker">
        <priority value="warn"/>
        <appender-ref ref="freemarker-log"/>
    </category>

    <!-- root logger -->
    <root>
        <priority value="all"></priority>
        <appender-ref ref="async"/>
    </root>
</log4j:configuration>
EOF
