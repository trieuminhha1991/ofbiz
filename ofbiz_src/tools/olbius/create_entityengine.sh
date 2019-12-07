#! /bin/bash

mkdir -p ${OLBIUS_PATH:-"../.."}/framework/entity/config

cat > ${OLBIUS_PATH:-"../.."}/framework/entity/config/entityengine.xml << EOF
<?xml version="1.0" encoding="UTF-8" ?>
<entity-config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://ofbiz.apache.org/dtds/entity-config.xsd">
	<resource-loader name="fieldfile" class="org.ofbiz.base.config.FileLoader" prepend-env="ofbiz.home" prefix="/framework/entity/fieldtype/" />

	<transaction-factory class="org.ofbiz.geronimo.GeronimoTransactionFactory" />

	<connection-factory class="org.ofbiz.entity.connection.DBCPConnectionFactory" />

	<debug-xa-resources value="false" />

	<delegator name="default" entity-model-reader="main" entity-group-reader="main" entity-eca-reader="main" distributed-cache-clear-enabled="${JMS_ALLOW:-"false"}">
		<group-map group-name="org.ofbiz" datasource-name="localmysql" />
		<group-map group-name="org.ofbiz.olap" datasource-name="localmysqlolap" />
		<group-map group-name="org.ofbiz.tenant" datasource-name="localmysqltenant" />
	</delegator>

	<delegator name="backuppass" entity-model-reader="main" entity-group-reader="main" entity-eca-reader="main" distributed-cache-clear-enabled="false">
		<group-map group-name="org.ofbiz" datasource-name="localmysqlbackuppass" />
	
		<group-map group-name="org.ofbiz.olap" datasource-name="localmysqlolap" />
		<group-map group-name="org.ofbiz.tenant" datasource-name="localmysqltenant" />
	
	</delegator>
	
	<entity-model-reader name="main" />
	<entity-group-reader name="main" />
	<entity-eca-reader name="main" />
	<entity-data-reader name="tenant" />
	<entity-data-reader name="seed" />
	<entity-data-reader name="seed-initial" />
	<entity-data-reader name="demo" />
	<entity-data-reader name="ext" />
	<entity-data-reader name="ext-test" />
	<entity-data-reader name="ext-demo" />

	<field-type name="mysql" loader="fieldfile" location="fieldtypemysql.xml" />

	<datasource name="localmysqlbackuppass" helper-class="org.ofbiz.entity.datasource.GenericHelperDAO" field-type-name="mysql"
		check-on-start="true" add-missing-on-start="true" alias-view-columns="false" use-binary-type-for-blob="true"
		check-pks-on-start="false" join-style="ansi-no-parenthesis" drop-fk-use-foreign-key-keyword="true" table-type="InnoDB"
		character-set="utf8" collate="utf8_bin" offset-style="limit">

		<read-data reader-name="tenant" />
		<read-data reader-name="seed" />
		<read-data reader-name="seed-initial" />
		<read-data reader-name="demo" />
		<read-data reader-name="ext" />
		<inline-jdbc jdbc-driver="com.mysql.jdbc.Driver" jdbc-uri="${OLBIUS_URI_BKUPASS:-"jdbc:mysql://127.0.0.1:3306/olbius"}"
			jdbc-username="${OLBIUS_USER_BKUPASS:-"olbius"}" jdbc-password="${OLBIUS_PASSWORD_BKUPASS:-"olbius"}" isolation-level="ReadCommitted" pool-minsize="${OLBIUS_MIN_POOL_SIZE:-1}" pool-maxsize="${OLBIUS_MAX_POOL_SIZE:-30}" time-between-eviction-runs-millis="600000" />

	</datasource>

	<datasource name="localmysql" helper-class="org.ofbiz.entity.datasource.GenericHelperDAO" field-type-name="mysql"
		check-on-start="true" add-missing-on-start="true" alias-view-columns="false" use-binary-type-for-blob="true"
		check-pks-on-start="false" join-style="ansi-no-parenthesis" drop-fk-use-foreign-key-keyword="true" table-type="InnoDB"
		character-set="utf8" collate="utf8_bin" offset-style="limit">

		<read-data reader-name="tenant" />
		<read-data reader-name="seed" />
		<read-data reader-name="seed-initial" />
		<read-data reader-name="demo" />
		<read-data reader-name="ext" />
		<inline-jdbc jdbc-driver="com.mysql.jdbc.Driver" jdbc-uri="${OLBIUS_URI:-"jdbc:mysql://127.0.0.1:3306/olbius"}"
			jdbc-username="${OLBIUS_USER:-"olbius"}" jdbc-password="${OLBIUS_PASSWORD:-"olbius"}" isolation-level="ReadCommitted" pool-minsize="${OLBIUS_MIN_POOL_SIZE:-1}" pool-maxsize="${OLBIUS_MAX_POOL_SIZE:-30}" time-between-eviction-runs-millis="600000" />

	</datasource>
	
	
	<datasource name="localmysqlolap" helper-class="org.ofbiz.entity.datasource.GenericHelperDAO" field-type-name="mysql"
		check-on-start="true" add-missing-on-start="true" alias-view-columns="false" use-binary-type-for-blob="true"
		check-pks-on-start="false" join-style="ansi-no-parenthesis" drop-fk-use-foreign-key-keyword="true" table-type="InnoDB"
		character-set="utf8" collate="utf8_bin">
		<read-data reader-name="tenant" />
		<read-data reader-name="seed" />
		<read-data reader-name="seed-initial" />
		<read-data reader-name="demo" />
		<read-data reader-name="ext" />
		<inline-jdbc jdbc-driver="com.mysql.jdbc.Driver" jdbc-uri="${OLBIUS_OLAP_URI:-"jdbc:mysql://127.0.0.1:3306/olap"}"
			jdbc-username="${OLBIUS_OLAP_USER:-"olbius"}" jdbc-password="${OLBIUS_OLAP_PASSWORD:-"olbius"}" isolation-level="ReadCommitted" pool-minsize="${OLBIUS_OLAP_MIN_POOL_SIZE:-1}" pool-maxsize="${OLBIUS_OLAP_MAX_POOL_SIZE:-30}" time-between-eviction-runs-millis="600000" />

	</datasource>
	<datasource name="localmysqltenant" helper-class="org.ofbiz.entity.datasource.GenericHelperDAO" field-type-name="mysql"
		check-on-start="true" add-missing-on-start="true" alias-view-columns="false" use-binary-type-for-blob="true"
		check-pks-on-start="false" join-style="ansi-no-parenthesis" drop-fk-use-foreign-key-keyword="true" table-type="InnoDB"
		character-set="utf8" collate="utf8_bin">
		<read-data reader-name="tenant" />
		<read-data reader-name="seed" />
		<read-data reader-name="seed-initial" />
		<read-data reader-name="demo" />
		<read-data reader-name="ext" />
		<inline-jdbc jdbc-driver="com.mysql.jdbc.Driver" jdbc-uri="${OLBIUS_TENANT_URI:-"jdbc:mysql://127.0.0.1:3306/tenant"}"
			jdbc-username="${OLBIUS_TENANT_USER:-"olbius"}" jdbc-password="${OLBIUS_TENANT_PASSWORD:-"olbius"}" isolation-level="ReadCommitted" pool-minsize="${OLBIUS_TENANT_MIN_POOL_SIZE:-1}" pool-maxsize="${OLBIUS_TENANT_MAX_POOL_SIZE:-30}" time-between-eviction-runs-millis="600000" />

	</datasource>


</entity-config>
EOF
