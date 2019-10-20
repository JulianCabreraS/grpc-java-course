package com.github.simplesteph.grpc.calculator.client;

import com.proto.calculator.proto.Calculator;
import com.proto.calculator.proto.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.proto.calculator.proto.CalculatorServiceGrpc;
import com.proto.calculator.proto.Calculator.SumRequest;
import com.proto.calculator.proto.Calculator.SumResponse;
import com.proto.calculator.proto.Calculator.ComputeAverageRequest;
import com.proto.calculator.proto.Calculator.ComputeAverageResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.proto.calculator.proto.CalculatorServiceGrpc.newBlockingStub;


class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

       unaryService(channel);
       streamServerService(channel);
       streamClientService(channel);

        //Do something
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
    private static void unaryService(ManagedChannel channel){
        //Created a greet service client (blocking synchronous
        CalculatorServiceBlockingStub calculatorClient = newBlockingStub(channel);

        //Create a Greet Request
        SumRequest sumRequest = SumRequest
                .newBuilder()
                .setFirstNumber(1)
                .setSecondNumber(2)
                .build();

        //Call the RPC and get back a GreetResponse
        SumResponse sumResponse = calculatorClient.sum(sumRequest);
        System.out.println(sumRequest.getFirstNumber() +"+" + sumRequest.getSecondNumber() +"="+ sumResponse.getSumResult());
    }
    private static void streamServerService(ManagedChannel channel) {
        //Created a greet service client (blocking synchronous
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = newBlockingStub(channel);

        //Create a Greet Request
        Calculator.PrimeNumberDecompositionRequest primedDecompositionRequest = Calculator.PrimeNumberDecompositionRequest
                .newBuilder()
                .setNumber(567890   )
                .build();

        //Call the RPC and get back a GreetResponse
        calculatorClient.primeNumberDecomposition(primedDecompositionRequest)
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });

    }
    private static void streamClientService(ManagedChannel channel){

        Calculator.ComputeAverageRequest computeAverageRequest;
        //Create a aysnchronous client
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestStreamObserver = asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                //we get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getAverage());
                //onNext will be called only once
            }

            @Override
            public void onError(Throwable t) { }

            @Override
            public void onCompleted() { }
        });

        //Streaming message #1
        System.out.println("sending message 1");

        computeAverageRequest=ComputeAverageRequest.newBuilder().setNumber(1).build();
        requestStreamObserver.onNext(computeAverageRequest);

        computeAverageRequest= ComputeAverageRequest.newBuilder().setNumber(2).build();
        requestStreamObserver.onNext(computeAverageRequest);

        computeAverageRequest= ComputeAverageRequest.newBuilder().setNumber(3).build();
        requestStreamObserver.onNext(computeAverageRequest);

        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
