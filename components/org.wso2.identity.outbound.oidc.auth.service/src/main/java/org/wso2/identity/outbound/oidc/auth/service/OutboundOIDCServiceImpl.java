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

import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.identity.outbound.oidc.auth.service.authenticator.OIDCAuthenticator;
import org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse;
import org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest;
import org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse;
import org.wso2.identity.outbound.oidc.auth.service.rpc.OutboundOIDCServiceGrpc;
import org.wso2.identity.outbound.oidc.auth.service.rpc.Request;

/**
 * Outbound OIDC authenticator service implementation.
 */
public class OutboundOIDCServiceImpl extends OutboundOIDCServiceGrpc.OutboundOIDCServiceImplBase {

    private static final Log LOG = LogFactory.getLog(OutboundOIDCServiceImpl.class);

    @Override
    public void canHandle(Request request, StreamObserver<CanHandleResponse> responseObserver) {

        boolean canHandle = OIDCAuthenticator.canHandle(request);
        CanHandleResponse canHandleResponse = CanHandleResponse.newBuilder().setCanHandle(canHandle).build();
        responseObserver.onNext(canHandleResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void initiateAuthentication(InitAuthRequest request, StreamObserver<InitAuthResponse> responseObserver) {

        boolean isRedirect = false;
        String redirectURL = OIDCAuthenticator.initiateAuthRequest(request);
        if (StringUtils.isNotBlank(redirectURL)) {
            isRedirect = true;
        }
        InitAuthResponse initAuthResponse = InitAuthResponse.newBuilder().setIsRedirect(isRedirect)
                .setRedirectUrl(redirectURL).build();
        responseObserver.onNext(initAuthResponse);
        responseObserver.onCompleted();
    }
}
