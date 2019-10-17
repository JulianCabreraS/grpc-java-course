package com.github.simplesteph.grpc.calculator.client;

import com.proto.calculator.proto.Calculator;
import com.proto.calculator.proto.CalculatorServiceGrpc;
import com.proto.greet.Greet;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        //Created a greet service client (blocking synchronous
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        //Create a Greet Request
        Calculator.SumRequest sumRequest = Calculator.SumRequest
                .newBuilder()
                .setFirstNumber(1)
                .setSecondNumber(2)
                .build();

        //Call the RPC and get back a GreetResponse
        Calculator.SumResponse sumResponse = calculatorClient.sum(sumRequest);
        System.out.println(sumResponse.getSumResult());

        //Do something
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
