#!/bin/bash

findjs () {
	find $1 -type f -name "*.js"
}

findcss () {
	find $1 -type f -name "*.css"
}

compressor () {
	JAVA=
	if [ -f "$JAVA_HOME/bin/java" ]; then
		JAVA="$JAVA_HOME/bin/java"
	else
		JAVA=java
	fi
	"$JAVA" -jar yuicompressor.jar --type $2 -o "$1" "$1"
}

files=$(cat compress-files)

echo "Compressing..."

for file in $files
do
	IFS=':' read -ra ADDR <<< "$file"
	if [[ ! -z "${ADDR[1]}" ]]; then
		case ${ADDR[0]} in
			folder)
				js=$(findjs ${ADDR[1]})
				for j in $js
				do
					compressor $j js
				done
				css=$(findcss ${ADDR[1]})
				for c in $css
				do
					compressor $j css
				done
			;;
			js)
				compressor ${ADDR[1]} js
			;;
			css)
				compressor ${ADDR[1]} css
			;;
		esac
	fi
done

echo "Compressed"
