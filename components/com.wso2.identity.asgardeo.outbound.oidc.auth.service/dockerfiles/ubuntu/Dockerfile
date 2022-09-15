# -------------------------------------------------------------------------------------
# Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 Inc. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# -------------------------------------------------------------------------------------

# remote image tag: docker.io/adoptopenjdk/openjdk11:jdk-11.0.14.1_1-ubuntu image digest: "sha256:3b97327612035c25d3899d7b1e82dd8b6758c7564afbdc5faa89a8b11a905a02"
# ACR image tag: adoptopenjdk/openjdk11:jdk-11.0.14.1_1-ubuntu_2022-06-23_06-19-50
FROM acrasgardeomainshared001.azurecr.io/adoptopenjdk/openjdk11@sha256:acca4c94be391621111a5bb145e30e26801896e8f5955424a2fcbf8f04128430
LABEL maintainer="Asgardeo Team"

# Set Docker image build arguments.
# Build arguments for user/group configurations.
ARG USER=wso2carbon
ARG USER_ID=10001
ARG USER_GROUP=wso2
ARG USER_GROUP_ID=10001
ARG USER_HOME=/home/${USER}

# build argument for MOTD
ARG MOTD="\n\
Welcome to WSO2 Docker resources.\n\
------------------------------------ \n\
This Docker container comprises of a WSO2 product, running with its latest GA release \n\
which is under the Apache License, Version 2.0. \n\
Read more about Apache License, Version 2.0 here @ http://www.apache.org/licenses/LICENSE-2.0.\n"

# Create the non-root user and group and set MOTD login message.
RUN \
    groupadd --system -g ${USER_GROUP_ID} ${USER_GROUP} \
    && useradd --system --create-home --home-dir ${USER_HOME} --no-log-init -g ${USER_GROUP_ID} -u ${USER_ID} ${USER} \
    && echo '[ ! -z "${TERM}" -a -r /etc/motd ] && cat /etc/motd' >> /etc/bash.bashrc; echo "${MOTD}" > /etc/motd

COPY --chown=wso2carbon:wso2 com.wso2.identity.asgardeo.outbound.oidc.service.jar ${USER_HOME}/com.wso2.identity.asgardeo.outbound.oidc.service.jar

RUN GRPC_HEALTH_PROBE_VERSION=v0.4.4 && \
    curl -o /bin/grpc_health_probe https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/${GRPC_HEALTH_PROBE_VERSION}/grpc_health_probe-linux-amd64 -L && \
    chmod +x /bin/grpc_health_probe

# Set the user and work directory.
USER ${USER_ID}
WORKDIR ${USER_HOME}

# Expose ports.
EXPOSE 8443

# Initiate container and start Outbound OIDC service.
ENTRYPOINT java $JVM_MEM_OPTS -Dlog4j.configurationFile=log4j2.properties -jar com.wso2.identity.asgardeo.outbound.oidc.service.jar
