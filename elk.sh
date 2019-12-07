
cat > /etc/filebeat/filebeat.yml << EOF
output:
  logstash:
    enabled: true
    hosts:
      - ${ELK_HOST}:${ELK_PORT:-5044}
    timeout: 15
filebeat:
  prospectors:
    - paths:
        - /opt/olbius/runtime/logs/ofbiz.log
      document_type: ofbiz-log
      multiline:
        pattern: '^\[2'
        negate: true
        match:   after
      fields:
        app: ofbiz
        tenant: ${DOMAIN_NAME}
EOF

/etc/init.d/filebeat start
