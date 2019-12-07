#!/bin/bash

cat >  ${OLBIUS_PATH:-"../.."}/framework/base/config/debug.properties << EOF
pack.exception=${PACK_EXCEPTION:-"true"}
print.verbose=${PRINT_VERBOSE:-"false"}
print.timing=${PRINT_TIMING:-"true"}
print.info=${PRINT_INFO:-"true"}
print.important=${PRINT_IMPORTANT:-"true"}
print.warning=${PRINT_WARNING:-"true"}
print.error=${PRINT_ERROR:-"true"}
print.fatal=${PRINT_FATAL:-"true"}
print.debug=${PRINT_DEBUG:-"true"}

EOF