#! /bin/bash

cat > ${OLBIUS_PATH:-"../.."}/applications/search/config/elasticSearch.properties << EOF
1.4=${ELASTIC_1_4_HOST:-""}
EOF
