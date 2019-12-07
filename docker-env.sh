#!/bin/bash

set -e

#source olbius-db.sh

for SCRIPT in ${OLBIUS_PATH}/tools/olbius/*
	do
		if [ -f $SCRIPT -a -x $SCRIPT ]
		then
			$SCRIPT
		fi
	done

if [[ "${OLBIUS_LOAD_DATA}" == "true" ]]; then
	bash ant ${OLBIUS_LOAD:-"load-base-seed"}
fi

if [[ ! -z ${ELK_HOST} ]]; then
	source elk.sh
fi

if [[ "${OLBIUS_START_MODE}" == '' ]]; then
	bash ant start
fi

bash ant ${OLBIUS_START_MODE}
