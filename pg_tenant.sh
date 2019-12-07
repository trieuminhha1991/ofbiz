#!/bin/bash

TENANT_SCRIPT=${TENANT_SCRIPT:-"tenants"}

$TENANT_SCRIPT info --group org.ofbiz --tenant $DOMAIN_NAME --type host

DB_NAME=$($TENANT_SCRIPT info --group org.ofbiz --tenant $DOMAIN_NAME --type dbname)

if [[ -z $DB_NAME ]]; then
	export OLBIUS_LOAD_DATA=true
fi

$TENANT_SCRIPT create --tenant $DOMAIN_NAME

if [[ -z $OLBIUS_LOAD_DATA ]]; then
	export OLBIUS_LOAD_DATA=false
fi

export OLBIUS_URI=$($TENANT_SCRIPT info --group org.ofbiz --tenant $DOMAIN_NAME --type uri)
export OLBIUS_USER=$($TENANT_SCRIPT info --group org.ofbiz --tenant $DOMAIN_NAME --type username)
export OLBIUS_PASSWORD=$($TENANT_SCRIPT info --group org.ofbiz --tenant $DOMAIN_NAME --type password)

export OLBIUS_OLAP_URI=$($TENANT_SCRIPT info --group org.ofbiz.olap --tenant $DOMAIN_NAME --type uri)
export OLBIUS_OLAP_USER=$($TENANT_SCRIPT info --group org.ofbiz.olap --tenant $DOMAIN_NAME --type username)
export OLBIUS_OLAP_PASSWORD=$($TENANT_SCRIPT info --group org.ofbiz.olap --tenant $DOMAIN_NAME --type password)

export OLBIUS_TENANT_URI=$OLBIUS_URI
export OLBIUS_TENANT_USER=$OLBIUS_USER
export OLBIUS_TENANT_PASSWORD=$OLBIUS_PASSWORD
