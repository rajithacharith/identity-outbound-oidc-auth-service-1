package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net"
	"regexp"
	"strconv"
	"strings"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
	"google.golang.org/grpc/peer"

	"golang.org/x/oauth2"

	"wso2-enterprise/identity-outbound-oidc-auth-service/data"
	pb "wso2-enterprise/identity-outbound-oidc-auth-service/outboundserver"
)

var (
	app_prop_file = flag.String("app_prop_file", "", "The application properties file")
)

const (
	// Application property names.
	PORT                 = "server.port"
	SERVER_CERT_PATH     = "server.crt.path"
	SERVER_KEY_PATH      = "server.key.path"
	SERVER_CA_CERTS_PATH = "server.ca.crts.path"
	CLIENT_AUTH_ENABLED  = "server.client.auth.enabled"

	// OIDC Authenticator constants.
	CLIENT_ID                        = "ClientId"
	OAUTH2_AUTHZ_URL                 = "OAuth2AuthzEPUrl"
	CALLBACK_URL                     = "callbackUrl"
	LOGIN_TYPE                       = "OIDC"
	OAUTH2_PARAM_STATE               = "state"
	COMMON_AUTH_QUERY_PARAMS         = "commonAuthQueryParams"
	OAUTH2_PARAM_SCOPE               = "scope"
	AUTH_PARAM                       = "$authparam"
	DYNAMIC_AUTH_PARAMS_LOOKUP_REGEX = "\\$authparam\\{(\\w+)\\}"
)

type OutboundOIDCService struct {
	pb.UnimplementedOutboundOIDCServiceServer
}

// Implementation of canHandle method of the authenticator.
// This returns true if authenticator can handle the authentication request.
func (s *OutboundOIDCService) CanHandle(ctx context.Context, canHandleReq *pb.Request) (*pb.CanHandleResponse, error) {

	requestParams := getRequestParamsMap(canHandleReq.GetRequestParams())

	if LOGIN_TYPE == getLoginType(requestParams[OAUTH2_PARAM_STATE][0]) {
		return &pb.CanHandleResponse{CanHandle: true}, nil
	}
	return &pb.CanHandleResponse{CanHandle: false}, nil
}

// Implementation of InitiateAuthentication function of the authenticator.
// This function returns OAuth2 authorize endpoint with required params to initiate federated login.
func (s *OutboundOIDCService) InitiateAuthentication(ctx context.Context, initAuthReq *pb.InitAuthRequest) (*pb.InitAuthResponse, error) {

	var redirectUrl string
	isRedirect := false

	authenticatorProperties := initAuthReq.GetAuthenticationContext().GetAuthenticatorProperties()
	if len(authenticatorProperties) > 0 {
		clientId := authenticatorProperties[CLIENT_ID]
		authEndpoint := authenticatorProperties[OAUTH2_AUTHZ_URL]
		callbackURL := authenticatorProperties[CALLBACK_URL]
		state := authenticatorProperties[OAUTH2_PARAM_STATE]
		authParamQueryString := authenticatorProperties[COMMON_AUTH_QUERY_PARAMS]
		requestParams := getRequestParamsMap(initAuthReq.GetRequest().GetRequestParams())
		queryString := interpretQueryString(initAuthReq.GetAuthenticationContext(), authParamQueryString, requestParams)
		paramValueMap := make(map[string]string)
		if queryString != "" {
			params := strings.Split(queryString, "&")
			for _, param := range params {
				paramNameValueArray := strings.Split(param, "=")
				if len(paramNameValueArray) == 2 {
					paramValueMap[paramNameValueArray[0]] = paramNameValueArray[1]
				}
			}
		}
		scope := paramValueMap[OAUTH2_PARAM_SCOPE]
		scopes := strings.Split(scope, " ")
		oauthConfig := &oauth2.Config{
			ClientID:    clientId,
			RedirectURL: callbackURL,
			Scopes:      scopes,
			Endpoint: oauth2.Endpoint{
				AuthURL: authEndpoint,
			},
		}
		redirectUrl = oauthConfig.AuthCodeURL(state)
		isRedirect = true
	}
	return &pb.InitAuthResponse{IsRedirect: isRedirect, RedirectUrl: redirectUrl}, nil
}

// func (s *OutboundOIDCService) ProcessAuthenticationResponse(ctx context.Context, processAuthRequest *pb.ProcessAuthRequest) (*pb.ProcessAuthResponse, error) {

// }

func interpretQueryString(authContext *pb.AuthenticationContext, authParamQueryString string, requestParams map[string][]string) string {

	if authParamQueryString == "" || !strings.Contains(authParamQueryString, AUTH_PARAM) {
		return authParamQueryString
	}
	re, _ := regexp.Compile(DYNAMIC_AUTH_PARAMS_LOOKUP_REGEX)
	authParamsNameMap := re.FindAllStringSubmatch(authParamQueryString, -1)
	authParamQueryString = getQueryStringWithAuthParams(authContext, authParamQueryString, authParamsNameMap)
	for _, authParamsList := range authParamsNameMap {
		if requestParams[authParamsList[1]] != nil && requestParams[authParamsList[1]][0] != "" {
			authParamQueryString = strings.ReplaceAll(authParamQueryString, authParamsList[0], requestParams[authParamsList[1]][0])
		}
	}
	return authParamQueryString
}

// ToDo: Need to implement this functionality.
// Populate query string with values for $authParam{param_name}.
func getQueryStringWithAuthParams(authContext *pb.AuthenticationContext, queryString string, authParamsNameMap [][]string) string {

	authenticatorParams := authContext.GetAuthenticatorParams()
	if queryString == "" || len(authenticatorParams) == 0 {
		return queryString
	}
	for _, authParamsList := range authParamsNameMap {
		if authenticatorParams[authParamsList[1]] != "" {
			queryString = strings.ReplaceAll(queryString, authParamsList[0], authenticatorParams[authParamsList[1]])
		}
	}
	return queryString
}

func getLoginType(stateParam string) string {

	loginType := ""
	if len(stateParam) != 0 {
		loginType = strings.Split(stateParam, ",")[1]
	}
	return loginType
}

func getRequestParamsMap(requestParams []*pb.Request_RequestParam) map[string][]string {

	requestParamsMap := make(map[string][]string)
	if len(requestParams) != 0 {
		for _, requestParam := range requestParams {
			for _, paramValue := range requestParam.GetParamValue() {
				requestParamsMap[requestParam.GetParamName()] = append(requestParamsMap[requestParam.GetParamName()], paramValue)
			}
		}
	}
	return requestParamsMap
}

func logClientInfo(ctx context.Context, req interface{}, info *grpc.UnaryServerInfo, handler grpc.UnaryHandler) (resp interface{}, err error) {
	// Log client tls info
	if p, ok := peer.FromContext(ctx); ok {
		if mtls, ok := p.AuthInfo.(credentials.TLSInfo); ok {
			for _, item := range mtls.State.PeerCertificates {
				log.Println("Request certificate subject : ", item.Subject)
			}
		}
	}
	return handler(ctx, req)
}

func main() {

	flag.Parse()
	if *app_prop_file == "" {
		*app_prop_file = "application.properties"
	}
	serverProperties, err := data.ReadApplicationPropertiesFile(*app_prop_file)
	port, err := strconv.Atoi(serverProperties[PORT])
	serverCrtPath := serverProperties[SERVER_CERT_PATH]
	serverKeyPath := serverProperties[SERVER_KEY_PATH]
	clientCrtsPath := serverProperties[SERVER_CA_CERTS_PATH]
	log.Printf("Client authentication is set to : %s", serverProperties[CLIENT_AUTH_ENABLED])
	isClientAuthEnabled, err := strconv.ParseBool(serverProperties[CLIENT_AUTH_ENABLED])
	credentials := data.GetServerTLSConfig(isClientAuthEnabled, serverCrtPath, serverKeyPath, clientCrtsPath)
	opts := []grpc.ServerOption{grpc.Creds(credentials)}
	if isClientAuthEnabled {
		opts = []grpc.ServerOption{
			grpc.Creds(credentials),
			grpc.UnaryInterceptor(logClientInfo),
		}
	}
	lis, err := net.Listen("tcp", fmt.Sprintf("localhost:%d", port))
	if err != nil {
		log.Fatalf("Failed to listen : %v", err)
	}
	grpcServer := grpc.NewServer(opts...)
	pb.RegisterOutboundOIDCServiceServer(grpcServer, &OutboundOIDCService{})
	log.Println("GRPC server started!")
	grpcServer.Serve(lis)
}
