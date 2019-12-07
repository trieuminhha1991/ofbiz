#!/bin/bash

gen_key () {
	gen=$(uuidgen)
	tmp=""
	echo "$@${gen//-/$tmp}"
}

assert () {
	if [[ -z $2 ]]; then 
		echo $1
		exit 1
	fi
}

trim () {
	echo ${1//[[:blank:]]/}
}

pg_check_env () {
	echo "Check $1"
	assert "Host not found" $2
	assert "Db name not found" $3
	assert "User name not found" $4
	assert "Password not found" $5
}
