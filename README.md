# identity-outbound-oidc-auth-service

## Setting up development environment.

### Prerequisite
* Install latest version of [GoLang](https://go.dev/doc/install)
* Install [Protocol Buffer Compiler](https://grpc.io/docs/protoc-installation/)
* Install Go plugins for protocol compiler
    ```
    $ go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.28
    $ go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.2
    ```
    ```
    # Update path for the protoc compiler to find plugins
    $ export PATH="$PATH:$(go env GOPATH)/bin"
    ```
* Get a clone of https://github.com/wso2-enterprise/identity-outbound-oidc-auth-service.


### Running the server
1. Go to cloned project root and checkout to `go_impl` branch.
    ```
    $ cd identity-outbound-oidc-auth-service && git checkout go_impl
    ```
2. To compile and run the server.
    ```
    $ go run server/server.go
    ```