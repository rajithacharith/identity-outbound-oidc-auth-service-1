package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net"
	"strconv"
	"strings"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
	"google.golang.org/grpc/peer"

	"wso2-enterprise/identity-outbound-oidc-auth-service/data"
	pb "wso2-enterprise/identity-outbound-oidc-auth-service/outboundserver"
)

var (
	app_prop_file   = flag.String("app_prop_file", "", "The application properties file")
)

const (
	LOGIN_TYPE string = "OIDC"
	OAUTH2_PARAM_STATE string = "state"

	// Application property names.
	PORT = "server.port"
	SERVER_CERT_PATH = "server.crt.path"
	SERVER_KEY_PATH = "server.key.path"
	SERVER_CA_CERTS_PATH = "server.ca.crts.path"
	CLIENT_AUTH_ENABLED = "server.client.auth.enabled"
)

type OutboundOIDCService struct {
	pb.UnimplementedOutboundOIDCServiceServer
}

func (s *OutboundOIDCService) CanHandle(ctx context.Context, canHandleReq *pb.Request) (*pb.CanHandleResponse, error) {

	requestParams := getRequestParamsMap(canHandleReq.GetRequestParams())
	
	if (LOGIN_TYPE == getLoginType(requestParams[OAUTH2_PARAM_STATE][0])) {
		return &pb.CanHandleResponse{CanHandle: true}, nil
	}
	return &pb.CanHandleResponse{CanHandle: false}, nil
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

// func (s *OutboundOIDCService) initiateAuthentication(ctx context.Context, initAuthReq *pb.InitAuthRequest) (*pb.InitAuthResponse, error) {

// }

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
	credentials := data.LoadKeyPair(isClientAuthEnabled, serverCrtPath, serverKeyPath, clientCrtsPath)
	opts := []grpc.ServerOption{grpc.Creds(credentials)}
	if isClientAuthEnabled {
		opts = []grpc.ServerOption{
			grpc.Creds(credentials),
			grpc.UnaryInterceptor(logClientInfo),
		}	
	}
	lis, err := net.Listen("tcp", fmt.Sprintf("localhost:%d", port))
	if (err != nil){
		log.Fatalf("Failed to listen : %v", err)
	}
	grpcServer := grpc.NewServer(opts...)
	pb.RegisterOutboundOIDCServiceServer(grpcServer, &OutboundOIDCService{})
	log.Println("GRPC server started!")
	grpcServer.Serve(lis)
}
