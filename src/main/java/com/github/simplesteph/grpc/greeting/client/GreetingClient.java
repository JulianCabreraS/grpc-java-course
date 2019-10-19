package com.github.simplesteph.grpc.greeting.client;

import com.proto.greet.Greet.GreetRequest;
import com.proto.greet.Greet.GreetResponse;
import com.proto.greet.Greet.GreetManyTimesRequest;

import com.proto.greet.Greet.Greeting;
import com.proto.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static com.proto.greet.GreetServiceGrpc.newBlockingStub;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("hello I am a gRPC Client");

        ManagedChannel  channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        System.out.println("Creating stub");

       unaryService(channel);
       streamServerService(channel);

        //Do something
        System.out.println("Shutting down channel");
        channel.shutdown();

    }
    public static void unaryService(ManagedChannel channel){
        //Created a greet service client (blocking synchronous

        GreetServiceBlockingStub greetClient = newBlockingStub(channel);

        //Created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Julian")
                .setLastName("Cabrera")
                .build();
        //Create a Greet Request
        GreetRequest greetRequest =GreetRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();
        //Call the RPC and get back a GreetResponse
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());
    }

    private static void streamServerService(ManagedChannel channel) {
        //Created a greet service client (blocking synchronous
        GreetServiceBlockingStub greetClient = newBlockingStub(channel);

        //Create a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Julian")
                .setLastName("Cabrera")
                .build();

        //Call the RPC Service and get back a greet response
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();

        //Call the RPC and get back a GreetResponse
         greetClient.greetManyTimes(greetManyTimesRequest)
                 .forEachRemaining(greetManyTimesResponse ->
                 {
                     System.out.println(greetManyTimesResponse.getResult());
                 });
    }


}
