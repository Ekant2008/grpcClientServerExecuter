package com.code;

import com.example.grpc.ExecutorServiceGrpc;
import com.example.grpc.FinalResponse;
import com.example.grpc.Request;
import com.example.grpc.ServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Server {
  private final static Logger logger = Logger.getLogger(Server.class.getName());
    public static void main(String[] args) throws Exception {
        io.grpc.Server server = ServerBuilder.forPort(50051)
                .addService(new ServiceImpl())
                .build()
                .start();
        logger.info("JobServer started on port 50051");
        server.awaitTermination();
    }
    public static class ServiceImpl extends ServiceGrpc.ServiceImplBase {

        @Override
        public void processJob(Request request, StreamObserver<FinalResponse> responseObserver) {
            // Connect to Executor
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                    .usePlaintext()
                    .build();
            ExecutorServiceGrpc.ExecutorServiceBlockingStub executorStub = ExecutorServiceGrpc.newBlockingStub(channel);

            // Collect streaming responses
            List<String> messages = new ArrayList<>();
            executorStub.executeStream(request).forEachRemaining(result -> {
                logger.info("Executor response: " + result.getMessage());
                messages.add(result.getMessage());
            });

            channel.shutdown();

            // Aggregate into final response
            String summary = String.join(", ", messages);
            FinalResponse finalResponse = FinalResponse.newBuilder()
                    .setSummary("Job completed with results: " + summary)
                    .build();

            // Send back to Client
            responseObserver.onNext(finalResponse);
            responseObserver.onCompleted();
        }
    }


    }

