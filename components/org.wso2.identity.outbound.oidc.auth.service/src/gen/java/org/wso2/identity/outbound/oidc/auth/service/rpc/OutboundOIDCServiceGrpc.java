package org.wso2.identity.outbound.oidc.auth.service.rpc;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: OutboundOIDCService.proto")
public final class OutboundOIDCServiceGrpc {

  private OutboundOIDCServiceGrpc() {}

  public static final String SERVICE_NAME = "org.wso2.identity.outbound.oidc.auth.service.rpc.OutboundOIDCService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.wso2.identity.outbound.oidc.auth.service.rpc.Request,
      org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse> METHOD_CAN_HANDLE =
      io.grpc.MethodDescriptor.<org.wso2.identity.outbound.oidc.auth.service.rpc.Request, org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "org.wso2.identity.outbound.oidc.auth.service.rpc.OutboundOIDCService", "canHandle"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              org.wso2.identity.outbound.oidc.auth.service.rpc.Request.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest,
      org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse> METHOD_INITIATE_AUTHENTICATION =
      io.grpc.MethodDescriptor.<org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest, org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "org.wso2.identity.outbound.oidc.auth.service.rpc.OutboundOIDCService", "initiateAuthentication"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OutboundOIDCServiceStub newStub(io.grpc.Channel channel) {
    return new OutboundOIDCServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OutboundOIDCServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new OutboundOIDCServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OutboundOIDCServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new OutboundOIDCServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class OutboundOIDCServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void canHandle(org.wso2.identity.outbound.oidc.auth.service.rpc.Request request,
        io.grpc.stub.StreamObserver<org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CAN_HANDLE, responseObserver);
    }

    /**
     */
    public void initiateAuthentication(org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest request,
        io.grpc.stub.StreamObserver<org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_INITIATE_AUTHENTICATION, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_CAN_HANDLE,
            asyncUnaryCall(
              new MethodHandlers<
                org.wso2.identity.outbound.oidc.auth.service.rpc.Request,
                org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse>(
                  this, METHODID_CAN_HANDLE)))
          .addMethod(
            METHOD_INITIATE_AUTHENTICATION,
            asyncUnaryCall(
              new MethodHandlers<
                org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest,
                org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse>(
                  this, METHODID_INITIATE_AUTHENTICATION)))
          .build();
    }
  }

  /**
   */
  public static final class OutboundOIDCServiceStub extends io.grpc.stub.AbstractStub<OutboundOIDCServiceStub> {
    private OutboundOIDCServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OutboundOIDCServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OutboundOIDCServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OutboundOIDCServiceStub(channel, callOptions);
    }

    /**
     */
    public void canHandle(org.wso2.identity.outbound.oidc.auth.service.rpc.Request request,
        io.grpc.stub.StreamObserver<org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CAN_HANDLE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void initiateAuthentication(org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest request,
        io.grpc.stub.StreamObserver<org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_INITIATE_AUTHENTICATION, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class OutboundOIDCServiceBlockingStub extends io.grpc.stub.AbstractStub<OutboundOIDCServiceBlockingStub> {
    private OutboundOIDCServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OutboundOIDCServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OutboundOIDCServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OutboundOIDCServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse canHandle(org.wso2.identity.outbound.oidc.auth.service.rpc.Request request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CAN_HANDLE, getCallOptions(), request);
    }

    /**
     */
    public org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse initiateAuthentication(org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_INITIATE_AUTHENTICATION, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class OutboundOIDCServiceFutureStub extends io.grpc.stub.AbstractStub<OutboundOIDCServiceFutureStub> {
    private OutboundOIDCServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OutboundOIDCServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OutboundOIDCServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OutboundOIDCServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse> canHandle(
        org.wso2.identity.outbound.oidc.auth.service.rpc.Request request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CAN_HANDLE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse> initiateAuthentication(
        org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_INITIATE_AUTHENTICATION, getCallOptions()), request);
    }
  }

  private static final int METHODID_CAN_HANDLE = 0;
  private static final int METHODID_INITIATE_AUTHENTICATION = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OutboundOIDCServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(OutboundOIDCServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CAN_HANDLE:
          serviceImpl.canHandle((org.wso2.identity.outbound.oidc.auth.service.rpc.Request) request,
              (io.grpc.stub.StreamObserver<org.wso2.identity.outbound.oidc.auth.service.rpc.CanHandleResponse>) responseObserver);
          break;
        case METHODID_INITIATE_AUTHENTICATION:
          serviceImpl.initiateAuthentication((org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthRequest) request,
              (io.grpc.stub.StreamObserver<org.wso2.identity.outbound.oidc.auth.service.rpc.InitAuthResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class OutboundOIDCServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.wso2.identity.outbound.oidc.auth.service.rpc.OutboundOIDCServiceOuterClass.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (OutboundOIDCServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OutboundOIDCServiceDescriptorSupplier())
              .addMethod(METHOD_CAN_HANDLE)
              .addMethod(METHOD_INITIATE_AUTHENTICATION)
              .build();
        }
      }
    }
    return result;
  }
}
