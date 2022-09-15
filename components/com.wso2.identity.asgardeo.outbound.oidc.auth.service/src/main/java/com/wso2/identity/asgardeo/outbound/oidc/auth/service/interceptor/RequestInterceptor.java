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

package com.wso2.identity.asgardeo.outbound.oidc.auth.service.interceptor;

import com.wso2.identity.asgardeo.outbound.oidc.auth.service.models.RequestMetadata;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.ThreadContext;

import java.util.StringJoiner;
import java.util.UUID;

import static com.wso2.identity.asgardeo.outbound.oidc.auth.service.util.Constants.ACCESS_LOGGER;
import static com.wso2.identity.asgardeo.outbound.oidc.auth.service.util.Constants.CORRELATION_ID_KEY;

/**
 * Interceptor for outbound oidc service.
 */
public class RequestInterceptor implements ServerInterceptor {

    private static final Log LOG = LogFactory.getLog(RequestInterceptor.class);
    private static final Log ACCESS_LOG = LogFactory.getLog(ACCESS_LOGGER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        RequestMetadata requestMetadata = new RequestMetadata(serverCall, metadata);
        String correlationId = requestMetadata.getCorrelationId();
        ThreadContext.put(CORRELATION_ID_KEY, correlationId);
        try {
            final ServerCall.Listener<ReqT> original = serverCallHandler.startCall(serverCall, metadata);
            return new ServiceServerCallListener<>(original, requestMetadata);
        } finally {
            ThreadContext.remove(CORRELATION_ID_KEY);
        }
    }

    private String processCorrelationId(String correlationId) {

        if (StringUtils.isNotBlank(correlationId)) {
            return correlationId;
        }
        correlationId = UUID.randomUUID().toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(CORRELATION_ID_KEY + " not available in request. Setting correlation id to: " + correlationId);
        }
        return correlationId;
    }

    private static class ServiceServerCallListener<ReqT> extends
            ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

        private final String correlationId;
        private final RequestMetadata requestMetadata;

        protected ServiceServerCallListener(ServerCall.Listener<ReqT> delegate, RequestMetadata requestMetadata) {

            super(delegate);
            this.requestMetadata = requestMetadata;
            this.correlationId = requestMetadata.getCorrelationId();
        }

        @Override
        public void onMessage(final ReqT message) {

            try {
                ThreadContext.put(CORRELATION_ID_KEY, correlationId);
                super.onMessage(message);
            } finally {
                ThreadContext.remove(CORRELATION_ID_KEY);
            }
        }

        @Override
        public void onHalfClose() {

            try {
                ThreadContext.put(CORRELATION_ID_KEY, correlationId);
                super.onHalfClose();
            } finally {
                ThreadContext.remove(CORRELATION_ID_KEY);
            }
        }

        @Override
        public void onCancel() {

            try {
                ThreadContext.put(CORRELATION_ID_KEY, correlationId);
                super.onCancel();
            } finally {
                publishAccessLog(false);
                ThreadContext.remove(CORRELATION_ID_KEY);
            }
        }

        @Override
        public void onComplete() {

            try {
                ThreadContext.put(CORRELATION_ID_KEY, correlationId);
                super.onComplete();
            } finally {
                publishAccessLog(true);
                ThreadContext.remove(CORRELATION_ID_KEY);
            }
        }

        @Override
        public void onReady() {

            try {
                ThreadContext.put(CORRELATION_ID_KEY, correlationId);
                super.onReady();
            } finally {
                ThreadContext.remove(CORRELATION_ID_KEY);
            }
        }

        private void publishAccessLog(boolean isComplete) {

            long executionDurationInMillis = requestMetadata.getDurationInMillis();
            StringJoiner log = new StringJoiner(" ");
            log.add(requestMetadata.getRemoteIP())
                    .add("[" + requestMetadata.getReceivedDateTime() + "]")
                    .add(requestMetadata.getMethodType())
                    .add(requestMetadata.getCallMethod());
            if (isComplete) {
                log.add("COMPLETE");
            } else {
                log.add("CANCEL");
            }
            log.add("\"" + requestMetadata.getUserAgent() + "\"")
                    .add(String.valueOf(executionDurationInMillis));
            ACCESS_LOG.info(log.toString());
        }
    }
}
