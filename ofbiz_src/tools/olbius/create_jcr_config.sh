#! /bin/bash

cat > ${OLBIUS_PATH:-"../.."}/applications/jcr_client/config/jcr_client.properties << EOF
jcr.remote.url=${JCR_REMOTE_URL:-""}
jcr.dev=false
jcr.port=${JCR_PORT:-8888}
jcr.user=${JCR_USER:-"admin"}
jcr.password=${JCR_PASSWORD:-"admin"}
EOF
