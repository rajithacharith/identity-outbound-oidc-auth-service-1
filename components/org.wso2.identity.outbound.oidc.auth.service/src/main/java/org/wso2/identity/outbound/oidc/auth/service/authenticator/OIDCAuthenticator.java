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

package org.wso2.identity.outbound.oidc.auth.service.authenticator;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.wso2.identity.outbound.oidc.auth.service.rpc.AuthenticationContext;
import org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest;
import org.wso2.identity.outbound.oidc.auth.service.rpc.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * OIDC Authenticator.
 */
public class OIDCAuthenticator {

    private static final Log log = LogFactory.getLog(OIDCAuthenticator.class);
    private static final String DYNAMIC_PARAMETER_LOOKUP_REGEX = "\\$\\{(\\w+)\\}";
    private static Pattern pattern = Pattern.compile(DYNAMIC_PARAMETER_LOOKUP_REGEX);

    public static boolean canHandle(Request request) {

        Map<String, List<String>> requestParams = getRequestParams(request);

        if (OIDCAuthenticatorConstants.LOGIN_TYPE.equals(getLoginType(requestParams))) {
            return true;
        }
        return false;
    }

    public static String initiateAuthRequest(InitAuthRequest initAuthRequest) {


        String redirectURL = null;
        try {
            Map<String, String> authenticatorProperties = initAuthRequest.getAuthenticationContext()
                    .getAuthenticatorPropertiesMap();
            if (MapUtils.isNotEmpty(authenticatorProperties)) {
                String clientId = authenticatorProperties.get(OIDCAuthenticatorConstants.CLIENT_ID);
                String authorizationEP = authenticatorProperties.get(OIDCAuthenticatorConstants.OAUTH2_AUTHZ_URL);
                String callbackUrl = authenticatorProperties.get(OIDCAuthenticatorConstants.IdPConfParams.CALLBACK_URL);
                String state = initAuthRequest.getAuthenticationContext().getContextIdentifier() + ","
                        + OIDCAuthenticatorConstants.LOGIN_TYPE;
                String queryString = authenticatorProperties.get(OIDCAuthenticatorConstants.QUERY_PARAMS);
                Map<String, List<String>> requestParams = getRequestParams(initAuthRequest.getRequest());
                queryString = interpretQueryString(initAuthRequest.getAuthenticationContext(), queryString,
                        requestParams);

                Map<String, String> paramValueMap = new HashMap<>();

                if (StringUtils.isNotBlank(queryString)) {
                    String[] params = queryString.split("&");
                    for (String param : params) {
                        String[] intParam = param.split("=");
                        if (intParam.length >= 2) {
                            paramValueMap.put(intParam[0], intParam[1]);
                        }
                    }
                }

                String scope = paramValueMap.get(OIDCAuthenticatorConstants.OAuth20Params.SCOPE);

                OAuthClientRequest authzRequest;
                if (StringUtils.isNotBlank(queryString) && queryString.toLowerCase().contains("scope=") && queryString
                        .toLowerCase().contains("redirect_uri=")) {
                    authzRequest = OAuthClientRequest.authorizationLocation(authorizationEP).setClientId(clientId)
                            .setResponseType(OIDCAuthenticatorConstants.OAUTH2_GRANT_TYPE_CODE).setState(state)
                            .buildQueryMessage();
                } else if (StringUtils.isNotBlank(queryString) && queryString.toLowerCase().contains("scope=")) {
                    authzRequest = OAuthClientRequest.authorizationLocation(authorizationEP).setClientId(clientId)
                            .setRedirectURI(callbackUrl)
                            .setResponseType(OIDCAuthenticatorConstants.OAUTH2_GRANT_TYPE_CODE).setState(state)
                            .buildQueryMessage();
                } else if (StringUtils.isNotBlank(queryString) && queryString.toLowerCase().contains("redirect_uri=")) {
                    authzRequest = OAuthClientRequest.authorizationLocation(authorizationEP).setClientId(clientId)
                            .setResponseType(OIDCAuthenticatorConstants.OAUTH2_GRANT_TYPE_CODE)
                            .setScope(OIDCAuthenticatorConstants.OAUTH_OIDC_SCOPE).setState(state).buildQueryMessage();

                } else {
                    authzRequest = OAuthClientRequest.authorizationLocation(authorizationEP).setClientId(clientId)
                            .setRedirectURI(callbackUrl)
                            .setResponseType(OIDCAuthenticatorConstants.OAUTH2_GRANT_TYPE_CODE).setScope(scope)
                            .setState(state).buildQueryMessage();
                }

                redirectURL = authzRequest.getLocationUri();
                String domain = null;
                if (requestParams.get("domain") != null) {
                    domain = requestParams.get("domain").get(0);
                }


                if (StringUtils.isNotBlank(domain)) {
                    redirectURL = redirectURL + "&fidp=" + domain;
                }

                if (StringUtils.isNotBlank(queryString)) {
                    if (!queryString.startsWith("&")) {
                        redirectURL = redirectURL + "&" + queryString;
                    } else {
                        redirectURL = redirectURL + queryString;
                    }
                }
            }
        } catch (OAuthSystemException e) {
            log.error("Error occurred while initiating authentication request.", e);
        }
        return redirectURL;
    }

    private static Map<String, List<String>> getRequestParams(Request request) {

        if (request.getRequestParamsList().isEmpty()) {
            return new HashMap<>();
        }
        return request.getRequestParamsList().stream().collect(
                Collectors.toMap(Request.RequestParam::getParamName, Request.RequestParam::getParamValueList));
    }

    private static String getLoginType(Map<String, List<String>> requestParams) {

        if (requestParams.get(OIDCAuthenticatorConstants.OAUTH2_PARAM_STATE) != null) {
            String state = requestParams.get(OIDCAuthenticatorConstants.OAUTH2_PARAM_STATE).get(0);
            if (state != null) {
                String[] stateElements = state.split(",");
                if (stateElements.length > 1) {
                    return stateElements[1];
                }
            }
        }
        return null;
    }

    private static String interpretQueryString(AuthenticationContext context, String queryString,
                                               Map<String, List<String>> parameters) {

        if (StringUtils.isBlank(queryString)) {
            return null;
        }
        if (queryString.contains(OIDCAuthenticatorConstants.AUTH_PARAM)) {
            queryString = getQueryStringWithAuthenticatorParam(context, queryString);
        }
        Matcher matcher = pattern.matcher(queryString);
        while (matcher.find()) {
            String name = matcher.group(1);
            List<String> values = parameters.get(name);
            String value = "";
            if (values != null && values.size() > 0) {
                value = values.get(0);
            }
            if (log.isDebugEnabled()) {
                log.debug("InterpretQueryString name: " + name + ", value: " + value);
            }
            queryString = queryString.replaceAll("\\$\\{" + name + "}", Matcher.quoteReplacement(value));
        }
        if (log.isDebugEnabled()) {
            log.debug("Output QueryString: " + queryString);
        }
        return queryString;
    }

    /**
     * To capture the additional authenticator params from the adaptive script and interpret the query string with
     * additional params.
     *
     * @param context     Authentication context
     * @param queryString the query string with additional param
     * @return interpreted query string
     */
    private static String getQueryStringWithAuthenticatorParam(AuthenticationContext context, String queryString) {

        Matcher matcher = Pattern.compile(OIDCAuthenticatorConstants.DYNAMIC_AUTH_PARAMS_LOOKUP_REGEX)
                .matcher(queryString);
        Map<String, String> authenticatorParams = context.getAuthenticatorParamsMap();
        String value = "";
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (StringUtils.isNotEmpty(authenticatorParams.get(paramName))) {
                value = authenticatorParams.get(paramName);
            }
            try {
                value = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                if (log.isDebugEnabled()) {
                    log.debug("InterpretQueryString with authenticator param: " + paramName + "," +
                            " value: " + value);
                }
            } catch (UnsupportedEncodingException e) {
                log.error("Error while encoding the authenticator param: " + paramName +
                        " with value: " + value, e);
            }
            queryString = queryString.replaceAll("\\$authparam\\{" + paramName + "}",
                    Matcher.quoteReplacement(value));
        }
        if (log.isDebugEnabled()) {
            log.debug("Output QueryString with Authenticator Params : " + queryString);
        }
        return queryString;
    }
}
