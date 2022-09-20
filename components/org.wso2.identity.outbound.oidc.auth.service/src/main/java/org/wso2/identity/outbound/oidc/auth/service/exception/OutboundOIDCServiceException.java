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

package org.wso2.identity.outbound.oidc.auth.service.exception;

/**
 * Outbound OIDC service base exception class.
 */
public class OutboundOIDCServiceException extends Exception {
    private static final long serialVersionUID = -3973370923387458257L;
    private String errorCode = null;

    /**
     * Plain constructor for OutboundOIDCServiceException.
     */
    public OutboundOIDCServiceException() {

        super();
    }

    /**
     * Constructor for OutboundOIDCServiceException with error message and error code.
     *
     * @param message   Error message.
     * @param errorCode Error code.
     */
    public OutboundOIDCServiceException(String message, String errorCode) {

        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor for OutboundOIDCServiceException with error message, error code and cause.
     *
     * @param message   Error message.
     * @param errorCode Error code.
     * @param cause     Throwable exception.
     */
    public OutboundOIDCServiceException(String message, String errorCode, Throwable cause) {

        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor for OutboundOIDCServiceException with error message and cause.
     *
     * @param message Error message.
     * @param cause   Throwable exception.
     */
    public OutboundOIDCServiceException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Constructor for OutboundOIDCServiceException with cause.
     *
     * @param cause Throwable exception.
     */
    public OutboundOIDCServiceException(Throwable cause) {

        super(cause);
    }

    public String getErrorCode() {

        return errorCode;
    }

    @Override
    public String toString() {

        return errorCode + " - " + super.toString() + "\n";
    }
}
