package com.github.simplesteph.grpc.greeting.client;

import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import com.proto.greet.Greet;
import com.proto.greet.Greet.Greeting;
import com.proto.greet.Greet.GreetRequest;
import com.proto.greet.Greet.GreetResponse;
import com.proto.greet.Greet.GreetManyTimesRequest;
import com.proto.greet.Greet.LongGreetRequest;
import com.proto.greet.Greet.LongGreetResponse;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.proto.greet.GreetServiceGrpc.newBlockingStub;


public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("hello I am a gRPC Client");

        ManagedChannel  channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        System.out.println("Creating stub");

       unaryService(channel);
       streamGreetServerService(channel);
       streamGreetClientService(channel);
       bidiStreamService(channel);

        //Do something
        System.out.println("Shutting down channel");
        channel.shutdown();

    }
    private static void unaryService(ManagedChannel channel){

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

    private static void streamGreetServerService(ManagedChannel channel) {

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

    private static void streamGreetClientService(ManagedChannel channel){

        Greeting greeting;
        LongGreetRequest longGreetRequest;

        //Create a aysnchronous client
        GreetServiceGrpc.GreetServiceStub streamClientService = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver = streamClientService.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(Greet.LongGreetResponse value) {
                //we get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) { }

            @Override
            public void onCompleted() {
                //the server is done sending us data
                //onCompleted will be called right after onNext
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        //Streaming message #1
        System.out.println("sending message 1");
        greeting = Greeting.newBuilder().setFirstName("Stephane").build();
        longGreetRequest = LongGreetRequest.newBuilder().setGreeting(greeting).build();
        requestStreamObserver.onNext(longGreetRequest);
        //Streaming message #2
        System.out.println("sending message 2");
        greeting = Greeting.newBuilder().setFirstName("John").build();
        longGreetRequest = LongGreetRequest.newBuilder().setGreeting(greeting).build();
        requestStreamObserver.onNext(longGreetRequest);
        //Streaming message #3
        System.out.println("sending message 3");
        greeting = Greeting.newBuilder().setFirstName("Marc").build();
        longGreetRequest = LongGreetRequest.newBuilder().setGreeting(greeting).build();
        requestStreamObserver.onNext(longGreetRequest);

        //we tell the server that the client is done sending data
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static void bidiStreamService(ManagedChannel channel){
        //Create a aysnchronous client
        GreetServiceGrpc.GreetServiceStub asynClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Greet.GreetEveryoneRequest> requestStreamObserver=  asynClient.greetEveryone(new StreamObserver<Greet.GreetEveryoneResponse>() {
            @Override
            public void onNext(Greet.GreetEveryoneResponse value) {
                System.out.println("Response from server: "+ value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();

            }
        });

        Arrays.asList("Stephane", "John", "Marc", "Patricia").forEach(
                name -> {
                    System.out.println("Sending "+ name);
                    requestStreamObserver.onNext(Greet.GreetEveryoneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(name))
                            .build());
                }
        );

        requestStreamObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
}
