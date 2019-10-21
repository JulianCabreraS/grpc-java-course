package com.github.simplesteph.grpc.greeting.server;

import com.proto.greet.Greet;
import com.proto.greet.Greet.Greeting;
import com.proto.greet.Greet.GreetRequest;
import com.proto.greet.Greet.GreetResponse;

import com.proto.greet.Greet.GreetManyTimesRequest;
import com.proto.greet.Greet.GreetManyTimesResponse;
import com.proto.greet.Greet.LongGreetRequest;
import com.proto.greet.Greet.LongGreetResponse;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();

        //Create the response
        String result = "Hello " + firstName + " " + lastName;
        GreetResponse response = GreetResponse
                .newBuilder()
                .setResult(result)
                .build();
        //send the response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();
        try {
            for (int i = 0; i < 10; i++)
            {
                String result = "Hello " + firstName + ", response number: " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(response);

                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Greet.LongGreetRequest> longGreet(StreamObserver<Greet.LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> streamObserverOfRequest = new StreamObserver<LongGreetRequest>() {
            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                //client sends a message
                result += "Hello " + value.getGreeting().getFirstName()+"!";
            }

            @Override
            public void onError(Throwable t) {
                //client sends an error
            }

            @Override
            public void onCompleted() {
                LongGreetResponse longGreetResponse = LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build();

                //client is done
                responseObserver.onNext(longGreetResponse);

                //this is when we want to retun a response
                responseObserver.onCompleted();
            }
        };
        return streamObserverOfRequest;
    }

    @Override
    public StreamObserver<Greet.GreetEveryoneRequest> greetEveryone(StreamObserver<Greet.GreetEveryoneResponse> responseObserver) {
        return new StreamObserver<Greet.GreetEveryoneRequest>() {
            @Override
            public void onNext(Greet.GreetEveryoneRequest value) {
                String response = "Hello " + value.getGreeting().getFirstName();
                Greet.GreetEveryoneResponse greetEveryoneResponse= Greet.GreetEveryoneResponse.newBuilder()
                        .setResult(response)
                        .build();
                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                //this is when we want to retun a response
                responseObserver.onCompleted();
            }
        };
    }
}
