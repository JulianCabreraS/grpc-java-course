package com.github.simplesteph.grpc.greeting.server;

import com.proto.greet.Greet;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {


    @Override
    public void greet(Greet.GreetRequest request, StreamObserver<Greet.GreetResponse> responseObserver) {
        Greet.Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        //Create the response
        String result = "Hello " + firstName;
        Greet.GreetResponse response = Greet.GreetResponse
                .newBuilder()
                .setResult(result)
                .build();
        //send the response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }
}
