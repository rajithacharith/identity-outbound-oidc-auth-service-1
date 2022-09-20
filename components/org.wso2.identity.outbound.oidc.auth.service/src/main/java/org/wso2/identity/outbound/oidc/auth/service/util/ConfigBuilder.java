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

package org.wso2.identity.outbound.oidc.auth.service.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.identity.outbound.oidc.auth.service.exception.OutboundOIDCServiceException;
import org.wso2.identity.outbound.oidc.auth.service.models.ServerConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Configurations builder class for the Outbound OIDC service.
 */
public class ConfigBuilder {

    private static final Log log = LogFactory.getLog(ConfigBuilder.class);

    /**
     * Build the service configuration.
     *
     * @return Service configuration instance.
     * @throws OutboundOIDCServiceException If an error occurred while reading the configuration.
     */
    public ServerConfig build() throws OutboundOIDCServiceException {

        Parameters params = new Parameters();
        // Read data from this file.
        File propertiesFile = new File(Constants.CONF_FILE);
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.fileBased().setFile(propertiesFile));
        Configuration fileBasedConfig;
        String keystorePassword;
        String truststorePassword;
        try {
            fileBasedConfig = builder.getConfiguration();

            String keystoreCredentialFilePath =
                    Paths.get(fileBasedConfig.getString(Constants.CONF_KEYSTORE_CREDENTIAL_PATH)).toString();
            File keystoreCredentialFile = new File(keystoreCredentialFilePath);
            keystorePassword = FileUtils.readFileToString(keystoreCredentialFile, StandardCharsets.UTF_8).trim();

            String truststoreCredentialFilePath =
                    Paths.get(fileBasedConfig.getString(Constants.CONF_TRUSTSTORE_CREDENTIAL_PATH)).toString();
            File truststoreCredentialFile = new File(truststoreCredentialFilePath);
            truststorePassword = FileUtils.readFileToString(truststoreCredentialFile, StandardCharsets.UTF_8).trim();
        } catch (ConfigurationException | IOException e) {
            throw new OutboundOIDCServiceException(Constants.ErrorMessages.ERROR_READING_CONFIGURATION_FILES
                    .getMessage(), Constants.ErrorMessages.ERROR_READING_CONFIGURATION_FILES.getCode(), e);
        }

        ServerConfig serverConfig = new ServerConfig.Builder()
                .setServerPort(fileBasedConfig.getInt(Constants.CONF_SERVER_PORT))
                .setKeystorePath(Paths.get(fileBasedConfig.getString(Constants.CONF_KEYSTORE_PATH)).toString())
                .setKeystorePassword(keystorePassword)
                .setKeystoreType(fileBasedConfig.getString(Constants.CONF_KEYSTORE_TYPE))
                .setTruststorePath(Paths.get(fileBasedConfig.getString(Constants.CONF_TRUSTSTORE_PATH)).toString())
                .setTruststorePassword(truststorePassword)
                .setTruststoreType(fileBasedConfig.getString(Constants.CONF_TRUSTSTORE_TYPE))
                .setAuthenticateClient(fileBasedConfig.getBoolean(Constants.CONF_AUTH_CLIENT, true)).build();
        if (log.isDebugEnabled()) {
            log.debug("Server configuration: " + serverConfig.toString());
        }
        return serverConfig;
    }
}
