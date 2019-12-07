VERSION = latest
DOCKER_IMAGE = dev-core-dms
PRIVATE_REGISTRY_URL = docker.olbius.com/
PREFIX = $(PRIVATE_REGISTRY_URL)$(DOCKER_IMAGE)

all: build-ofbiz build-docker push

build-ofbiz:
	@cd ofbiz_src && bash ant clean build

build-docker:
	@bash compressor.sh
	@docker build -t $(PREFIX):$(VERSION) .

push:
	@docker push $(PREFIX):$(VERSION)

