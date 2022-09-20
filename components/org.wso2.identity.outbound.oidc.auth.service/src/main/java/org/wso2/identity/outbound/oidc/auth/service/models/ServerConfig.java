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

package org.wso2.identity.outbound.oidc.auth.service.models;

import org.apache.commons.lang3.StringUtils;

/**
 * This is the server configuration model class which holds all the configurations related to the server.
 */
public class ServerConfig {

    private final int serverPort;
    private final String keystorePath;
    private final String keystorePassword;
    private final String keystoreType;
    private final String truststorePath;
    private final String truststorePassword;
    private final String truststoreType;
    private final boolean authenticateClient;

    /**
     * Constructor for server configurations with builder attribute.
     *
     * @param builder Builder instance.
     */
    public ServerConfig(Builder builder) {

        this.serverPort = builder.serverPort;
        this.keystorePath = builder.keystorePath;
        this.keystorePassword = builder.keystorePassword;
        this.keystoreType = builder.keystoreType;
        this.truststorePath = builder.truststorePath;
        this.truststorePassword = builder.truststorePassword;
        this.truststoreType = builder.truststoreType;
        this.authenticateClient = builder.authenticateClient;
    }

    public int getServerPort() {

        return serverPort;
    }

    public String getKeystorePath() {

        return keystorePath;
    }

    public String getKeystorePassword() {

        return keystorePassword;
    }

    public String getKeystoreType() {

        return keystoreType;
    }

    public String getTruststorePath() {

        return truststorePath;
    }

    public String getTruststorePassword() {

        return truststorePassword;
    }

    public String getTruststoreType() {

        return truststoreType;
    }

    public boolean isAuthenticateClient() {

        return authenticateClient;
    }

    @Override
    public String toString() {

        return "ServerConfig{" +
                "serverPort=" + serverPort +
                ", keystorePath='" + keystorePath + '\'' +
                ", keystorePassword='" + (StringUtils.isNotBlank(keystorePassword) ? "****" : "undefined") + '\'' +
                ", keystoreType='" + keystoreType + '\'' +
                ", truststorePath='" + truststorePath + '\'' +
                ", truststorePassword='" + (StringUtils.isNotBlank(truststorePassword) ? "****" : "undefined") + '\'' +
                ", truststoreType='" + truststoreType + '\'' +
                ", authenticateClient=" + authenticateClient +
                '}';
    }

    /**
     * Builder class for the server configurations.
     */
    public static class Builder {

        private int serverPort;
        private String keystorePath;
        private String keystorePassword;
        private String keystoreType;
        private String truststorePath;
        private String truststorePassword;
        private String truststoreType;
        private boolean authenticateClient;

        /**
         * Set the server port attribute in the builder instance.
         *
         * @param serverPort Server port.
         * @return Builder instance.
         */
        public Builder setServerPort(int serverPort) {

            this.serverPort = serverPort;
            return this;
        }

        /**
         * Set the keystore path attribute in the builder instance.
         *
         * @param keystorePath Keystore path.
         * @return Builder instance.
         */
        public Builder setKeystorePath(String keystorePath) {

            this.keystorePath = keystorePath;
            return this;
        }

        /**
         * Set the keystore password attribute in the builder instance.
         *
         * @param keystorePassword Keystore password.
         * @return Builder instance.
         */
        public Builder setKeystorePassword(String keystorePassword) {

            this.keystorePassword = keystorePassword;
            return this;
        }

        /**
         * Set the keystore type attribute in the builder instance.
         *
         * @param keystoreType Keystore type.
         * @return Builder instance.
         */
        public Builder setKeystoreType(String keystoreType) {

            this.keystoreType = keystoreType;
            return this;
        }

        /**
         * Set the truststore path attribute in the builder instance.
         *
         * @param truststorePath Truststore path.
         * @return Builder instance.
         */
        public Builder setTruststorePath(String truststorePath) {

            this.truststorePath = truststorePath;
            return this;
        }

        /**
         * Set the truststore password in the builder instance.
         *
         * @param truststorePassword Truststore password.
         * @return Builder instance.
         */
        public Builder setTruststorePassword(String truststorePassword) {

            this.truststorePassword = truststorePassword;
            return this;
        }

        /**
         * Set the truststore type attribute in the builder instance.
         *
         * @param truststoreType Truststore type.
         * @return Builder instance.
         */
        public Builder setTruststoreType(String truststoreType) {

            this.truststoreType = truststoreType;
            return this;
        }

        /**
         * Set the authenticate client attribute in the builder instance.
         *
         * @param authenticateClient Whether client is authenticated or not.
         * @return Builder instance.
         */
        public Builder setAuthenticateClient(boolean authenticateClient) {

            this.authenticateClient = authenticateClient;
            return this;
        }

        /**
         * Build the server configurations instance.
         *
         * @return Server configurations instance.
         */
        public ServerConfig build() {

            validate();
            return new ServerConfig(this);
        }

        private void validate() {

            if (serverPort < 1 || serverPort > 65535) {
                throw new IllegalStateException("Given value is not a valid port number.");
            }
            if (StringUtils.isBlank(keystorePath)) {
                throw new IllegalStateException("Keystore path cannot be null.");
            }
            if (StringUtils.isBlank(keystorePassword)) {
                throw new IllegalStateException("Keystore password cannot be null.");
            }
            if (StringUtils.isBlank(keystoreType)) {
                throw new IllegalStateException("Keystore type cannot be null.");
            }
            if (StringUtils.isBlank(truststorePath)) {
                throw new IllegalStateException("Truststore path cannot be null.");
            }
            if (StringUtils.isBlank(truststorePassword)) {
                throw new IllegalStateException("Truststore password cannot be null.");
            }
            if (StringUtils.isBlank(truststoreType)) {
                throw new IllegalStateException("Truststore type cannot be null.");
            }
        }
    }
}
