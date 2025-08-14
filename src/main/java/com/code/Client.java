package com.code;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.grpc.Request;
import com.example.grpc.FinalResponse;
import com.example.grpc.ServiceGrpc;

public class Client {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ServiceGrpc.ServiceBlockingStub stub = ServiceGrpc.newBlockingStub(channel);

        Request request = Request.newBuilder().setJobId("123").build();
        FinalResponse response = stub.processJob(request);

        System.out.println("Client received: " + response.getSummary());
        channel.shutdown();
    }
}
