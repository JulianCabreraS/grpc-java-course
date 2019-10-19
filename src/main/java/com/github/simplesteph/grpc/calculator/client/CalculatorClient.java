package com.github.simplesteph.grpc.calculator.client;

import com.proto.calculator.proto.Calculator;
import com.proto.calculator.proto.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.proto.calculator.proto.CalculatorServiceGrpc;
import com.proto.calculator.proto.Calculator.SumRequest;
import com.proto.calculator.proto.Calculator.SumResponse;



import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static com.proto.calculator.proto.CalculatorServiceGrpc.newBlockingStub;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

       unaryService(channel);
       streamServerService(channel);

        //Do something
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
    private static  void unaryService(ManagedChannel channel){
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
    private static  void streamServerService(ManagedChannel channel) {
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
}
