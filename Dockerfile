FROM docker-registry.olbius.com/psql:9.5
MAINTAINER Hieuvv  "hieuvu0804@gmail.com"

EXPOSE 8080 8443
WORKDIR /opt/olbius
CMD "./docker-env.sh"

ENV OLBIUS_PATH /opt/olbius

COPY util.sh pg_tenant.sh olbius-db.sh docker-env.sh elk.sh /opt/olbius/

COPY ofbiz_src /opt/olbius


