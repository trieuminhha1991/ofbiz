#! /bin/bash

cat > ${OLBIUS_PATH:-"../.."}/applications/bi-x/config/BiLoader.properties << EOF
dev.mode=false
remote.url=${OLAP_REMOTE_URL}
EOF
