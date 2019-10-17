package com.github.simplesteph.grpc.greeting.client;
import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.Greet;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("hello I am a gRPC Client");

        ManagedChannel  channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        System.out.println("Creating stub");

        //Created a greet service client (blocking synchronous
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //Created a protocol buffer greeting message
        Greet.Greeting greeting = Greet.Greeting.newBuilder()
                .setFirstName("Julian")
                .setLastName("Cabrera")
                .build();
        //Create a Greet Request
        Greet.GreetRequest greetRequest = Greet.GreetRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();
        //Call the RPC and get back a GreetResponse
        Greet.GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());

        //Do something
        System.out.println("Shutting down channel");
        channel.shutdown();

    }
}
