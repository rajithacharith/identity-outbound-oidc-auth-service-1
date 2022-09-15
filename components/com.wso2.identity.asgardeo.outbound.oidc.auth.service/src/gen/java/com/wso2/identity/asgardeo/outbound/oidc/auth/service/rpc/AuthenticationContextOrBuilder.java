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

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OutboundOIDCService.proto

package com.wso2.identity.asgardeo.outbound.oidc.auth.service.rpc;

public interface AuthenticationContextOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.wso2.identity.asgardeo.outbound.oidc.service.rpc.AuthenticationContext)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string contextIdentifier = 1;</code>
   */
  java.lang.String getContextIdentifier();
  /**
   * <code>string contextIdentifier = 1;</code>
   */
  com.google.protobuf.ByteString
      getContextIdentifierBytes();

  /**
   * <code>map&lt;string, string&gt; authenticatorProperties = 2;</code>
   */
  int getAuthenticatorPropertiesCount();
  /**
   * <code>map&lt;string, string&gt; authenticatorProperties = 2;</code>
   */
  boolean containsAuthenticatorProperties(
      java.lang.String key);
  /**
   * Use {@link #getAuthenticatorPropertiesMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getAuthenticatorProperties();
  /**
   * <code>map&lt;string, string&gt; authenticatorProperties = 2;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getAuthenticatorPropertiesMap();
  /**
   * <code>map&lt;string, string&gt; authenticatorProperties = 2;</code>
   */

  java.lang.String getAuthenticatorPropertiesOrDefault(
      java.lang.String key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; authenticatorProperties = 2;</code>
   */

  java.lang.String getAuthenticatorPropertiesOrThrow(
      java.lang.String key);

  /**
   * <code>map&lt;string, string&gt; authenticatorParams = 3;</code>
   */
  int getAuthenticatorParamsCount();
  /**
   * <code>map&lt;string, string&gt; authenticatorParams = 3;</code>
   */
  boolean containsAuthenticatorParams(
      java.lang.String key);
  /**
   * Use {@link #getAuthenticatorParamsMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getAuthenticatorParams();
  /**
   * <code>map&lt;string, string&gt; authenticatorParams = 3;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getAuthenticatorParamsMap();
  /**
   * <code>map&lt;string, string&gt; authenticatorParams = 3;</code>
   */

  java.lang.String getAuthenticatorParamsOrDefault(
      java.lang.String key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; authenticatorParams = 3;</code>
   */

  java.lang.String getAuthenticatorParamsOrThrow(
      java.lang.String key);

  /**
   * <code>map&lt;string, string&gt; contextProperties = 4;</code>
   */
  int getContextPropertiesCount();
  /**
   * <code>map&lt;string, string&gt; contextProperties = 4;</code>
   */
  boolean containsContextProperties(
      java.lang.String key);
  /**
   * Use {@link #getContextPropertiesMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getContextProperties();
  /**
   * <code>map&lt;string, string&gt; contextProperties = 4;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getContextPropertiesMap();
  /**
   * <code>map&lt;string, string&gt; contextProperties = 4;</code>
   */

  java.lang.String getContextPropertiesOrDefault(
      java.lang.String key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; contextProperties = 4;</code>
   */

  java.lang.String getContextPropertiesOrThrow(
      java.lang.String key);
}
