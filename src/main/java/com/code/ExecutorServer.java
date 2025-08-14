package com.code;

import com.example.grpc.ExecutorServiceGrpc;
import com.example.grpc.Request;
import com.example.grpc.Result;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ExecutorServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
                .addService(new ExecutorServiceImpl())
                .build()
                .start();

        System.out.println("ExecutorServer started on port 50052");
        server.awaitTermination();
    }

    public static class ExecutorServiceImpl extends ExecutorServiceGrpc.ExecutorServiceImplBase {

        @Override
        public void executeStream(Request request, StreamObserver<Result> responseObserver) {
            String jobId = request.getJobId();
            System.out.println("Executor received job: " + jobId);

            // Simulate streaming results
            for (int i = 1; i <= 3; i++) {
                Result result = Result.newBuilder()
                        .setMessage("Result " + i + " for job " + jobId )
                        .build();
                responseObserver.onNext(result);

                try {
                    Thread.sleep(500); // simulate processing delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            responseObserver.onCompleted();
        }
    }
}
