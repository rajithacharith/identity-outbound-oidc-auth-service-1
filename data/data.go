package data

import (
	"bufio"
	"crypto/tls"
	"crypto/x509"
	"io/ioutil"
	"log"
	"os"
	"path/filepath"
	"runtime"
	"strings"

	"google.golang.org/grpc/credentials"
)

// basepath is the root directory of this package.
var basepath string

func init() {
	_, currentFile, _, _ := runtime.Caller(0)
	basepath = filepath.Dir(currentFile)
}

// Path returns the absolute path the given relative file or directory path,
// relative to the 'data' directory in the user's GOPATH.
// If rel is already absolute, it is returned unmodified.
func Path(rel string) string {
	if filepath.IsAbs(rel) {
		return rel
	}

	return filepath.Join(basepath, rel)
}

// Read application properties file from the given path.
// Returns a map of property name value pairs.
func ReadApplicationPropertiesFile(app_prop_file string) (map[string]string, error) {

	appPropFile := Path(app_prop_file)
	appProps := make(map[string]string)
	file, err := os.Open(appPropFile)
	if err != nil {
		log.Fatal(err)
		return nil, err
	}
	// Close file once file reading is completed.
	defer file.Close()
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()
		if equalInd := strings.Index(line, "="); equalInd > 0 {
			if key := strings.TrimSpace(line[:equalInd]); len(key) > 0 {
				if len(line) > 0 {
					appProps[key] = strings.TrimSpace(line[equalInd+1:])
				}
			}
		}
	}
	if err := scanner.Err(); err != nil {
		log.Fatal(err)
		return nil, err
	}
	return appProps, nil
}

// Creates and returns the TLS config for the server.
// Uses the .crt and .key files provided as the server key, server cert and client certs.
// Returns MTLS config if `isClientAuthEnabled` is set to true.
func GetServerTLSConfig(isClientAuthEnabled bool, serverCrtPath string, serverKeyPath string, clientCertsPath string) credentials.TransportCredentials {

	certificate, err := tls.LoadX509KeyPair(Path(serverCrtPath), Path(serverKeyPath))
	if err != nil {
		panic("Error while loading server credentials: " + err.Error())
	}

	tlsConfig := &tls.Config{
		Certificates: []tls.Certificate{certificate},
	}

	if !isClientAuthEnabled {
		return credentials.NewTLS(tlsConfig)
	}

	data, err := ioutil.ReadFile(Path(clientCertsPath))
	if err != nil {
		panic("Error while loading client certificates: " + err.Error())
	}

	capool := x509.NewCertPool()
	if !capool.AppendCertsFromPEM(data) {
		panic("Error no client certs in ca.crt file")
	}

	tlsConfig = &tls.Config{
		ClientAuth:   tls.RequireAndVerifyClientCert,
		Certificates: []tls.Certificate{certificate},
		ClientCAs:    capool,
	}

	return credentials.NewTLS(tlsConfig)
}
