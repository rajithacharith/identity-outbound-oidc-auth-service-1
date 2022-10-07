package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net"
	"strings"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"

	"wso2-enterprise/identity-outbound-oidc-auth-service/data"
	pb "wso2-enterprise/identity-outbound-oidc-auth-service/outboundserver"
)

var (
	tls        = flag.Bool("tls", false, "Connection uses TLS if true, else plain TCP")
	certFile   = flag.String("cert_file", "", "The TLS cert file")
	keyFile    = flag.String("key_file", "", "The TLS key file")
	jsonDBFile = flag.String("json_db_file", "", "A json file containing a list of features")
	port       = flag.Int("port", 50051, "The server port")
)

const (
	LOGIN_TYPE string = "OIDC"
	OAUTH2_PARAM_STATE string = "state"
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

func main() {

	flag.Parse()
	lis, err := net.Listen("tcp", fmt.Sprintf("localhost:%d", *port))
	if (err != nil){
		log.Fatalf("Failed to listen : %v", err)
	}
	var opts []grpc.ServerOption
	if *tls {
		if *certFile == "" {
			*certFile = data.Path("x509/server_cert.pem")
		}
		if *keyFile == "" {
			*keyFile = data.Path("x509/server_key.pem")
		}
		creds, err := credentials.NewServerTLSFromFile(*certFile, *keyFile)
		if err != nil {
			log.Fatalf("Failed to generate credentials %v", err)
		}
		opts = []grpc.ServerOption{grpc.Creds(creds)}
	}
	grpcServer := grpc.NewServer(opts...)
	pb.RegisterOutboundOIDCServiceServer(grpcServer, &OutboundOIDCService{})
	fmt.Println("GRPC server started!")
	grpcServer.Serve(lis)
}