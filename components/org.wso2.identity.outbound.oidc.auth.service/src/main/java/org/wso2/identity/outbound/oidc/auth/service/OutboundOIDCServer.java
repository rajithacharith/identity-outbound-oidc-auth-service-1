/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.identity.outbound.oidc.auth.service;

import io.grpc.Grpc;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.TlsServerCredentials;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.wso2.identity.outbound.oidc.auth.service.exception.OutboundOIDCServiceException;
import org.wso2.identity.outbound.oidc.auth.service.interceptor.RequestInterceptor;
import org.wso2.identity.outbound.oidc.auth.service.models.ServerConfig;
import org.wso2.identity.outbound.oidc.auth.service.util.ConfigBuilder;
import org.wso2.identity.outbound.oidc.auth.service.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * This is the main class of the Outbound OIDC Service. This starts the gRPC service with mTLS security.
 */
public class OutboundOIDCServer {

    private static final Log log = LogFactory.getLog(OutboundOIDCServer.class);
    private final ServerConfig serverConfig;
    private Server server;
    private HealthStatusManager healthStatusManager;

    /**
     * Constructor of Outbound OIDC service server.
     *
     * @param serverConfig Outbound OIDC server configuration.
     */
    public OutboundOIDCServer(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    /**
     * Main method of the server class.
     *
     * @param args Main method arguments.
     */
    public static void main(String[] args) {

        log.info("Starting Outbound OIDC Service...");
        try {
            ServerConfig serverConfig = new ConfigBuilder().build();
            setSystemTruststore(serverConfig);
            final OutboundOIDCServer server = new OutboundOIDCServer(serverConfig);
            server.start();
            server.blockUntilShutdown();
        } catch (OutboundOIDCServiceException | InterruptedException | IOException e) {
            log.error("Error starting the Outbound OIDC Service.", e);
        }
    }

    private static KeyManager[] getKeyManagers(String path, String password, String type)
            throws OutboundOIDCServiceException {

        try {
            Path keystoreFilePath = Paths.get(path);
            KeyStore keystore;
            try (InputStream in = Files.newInputStream(keystoreFilePath)) {
                keystore = KeyStore.getInstance(type);
                keystore.load(in, password.toCharArray());
            }
            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (UnrecoverableKeyException | CertificateException |
                IOException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new OutboundOIDCServiceException(Constants.ErrorMessages.ERROR_GETTING_KEY_MANAGER_FILES.getMessage(),
                    Constants.ErrorMessages.ERROR_GETTING_KEY_MANAGER_FILES.getCode(), e);
        }
    }

    private static TrustManager[] getTrustManagers(String path, String password, String type)
            throws OutboundOIDCServiceException {

        try {
            Path truststoreFilePath = Paths.get(path);
            KeyStore truststore;
            try (InputStream in = Files.newInputStream(truststoreFilePath)) {
                truststore = KeyStore.getInstance(type);
                truststore.load(in, password.toCharArray());

            }
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(truststore);
            return trustManagerFactory.getTrustManagers();
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new OutboundOIDCServiceException(Constants.ErrorMessages.ERROR_GETTING_TRUST_MANAGERS.getMessage(),
                    Constants.ErrorMessages.ERROR_GETTING_TRUST_MANAGERS.getCode(), e);
        }
    }

    private static void setSystemTruststore(ServerConfig serverConfig) {

        String sysPropertyTruststorePath = "javax.net.ssl.trustStore";
        String sysPropertyTruststorePassword = "javax.net.ssl.trustStorePassword";
        String sysPropertyTruststoreType = "javax.net.ssl.trustStoreType";
        if (StringUtils.isBlank(System.getProperty(sysPropertyTruststorePath))) {
            System.setProperty(sysPropertyTruststorePath, serverConfig.getTruststorePath());
        }
        if (StringUtils.isBlank(System.getProperty(sysPropertyTruststorePassword))) {
            System.setProperty(sysPropertyTruststorePassword, serverConfig.getTruststorePassword());
        }
        if (StringUtils.isBlank(System.getProperty(sysPropertyTruststoreType))) {
            System.setProperty(sysPropertyTruststoreType, serverConfig.getTruststoreType());
        }
    }

    private void start() throws OutboundOIDCServiceException, IOException {

        healthStatusManager = new HealthStatusManager();
        int port = serverConfig.getServerPort();

        TlsServerCredentials.ClientAuth clientAuth = TlsServerCredentials.ClientAuth.REQUIRE;
        if (!serverConfig.isAuthenticateClient()) {
            log.warn("Disabling client authentication due to the configuration: \"server.auth.client=false\"");
            clientAuth = TlsServerCredentials.ClientAuth.NONE;
        }
        TlsServerCredentials.Builder tlsBuilder = TlsServerCredentials.newBuilder()
                .keyManager(getKeyManagers(serverConfig.getKeystorePath(),
                        serverConfig.getKeystorePassword(),
                        serverConfig.getKeystoreType()))
                .trustManager(getTrustManagers(serverConfig.getTruststorePath(),
                        serverConfig.getTruststorePassword(),
                        serverConfig.getTruststoreType()))
                .clientAuth(clientAuth);

        // TODO: Reflection service support is added temporarily to be removed after testing.
        server = Grpc.newServerBuilderForPort(port, tlsBuilder.build())
                .addService(healthStatusManager.getHealthService())
                .addService(ServerInterceptors.intercept(new OutboundOIDCServiceImpl(),
                        new RequestInterceptor()))
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();
        log.info("Outbound OIDC Service started successfully. Listening on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down Outbound OIDC Service since JVM is shutting down.");
            try {
                OutboundOIDCServer.this.stop();
            } catch (InterruptedException e) {
                log.error("Error while shutting down Outbound OIDC Service.", e);
            }
            log.info("Outbound OIDC Service shutdown complete.");
            // Shutdown logger.
            LogManager.shutdown();
        }));
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {

        if (server != null) {
            server.awaitTermination();
        }
    }

    private void stop() throws InterruptedException {

        if (healthStatusManager != null) {
            healthStatusManager.enterTerminalState();
        }
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
