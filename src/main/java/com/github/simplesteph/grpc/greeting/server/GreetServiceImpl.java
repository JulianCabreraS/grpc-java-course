package com.github.simplesteph.grpc.greeting.server;

import com.proto.greet.Greet.GreetRequest;
import com.proto.greet.Greet.GreetResponse;
import com.proto.greet.Greet.Greeting;
import com.proto.greet.Greet.GreetManyTimesRequest;
import com.proto.greet.Greet.GreetManyTimesResponse;
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


}
