package com.github.simplesteph.grpc.calculator.server;

import com.proto.calculator.proto.Calculator;
import com.proto.calculator.proto.CalculatorServiceGrpc;
import com.proto.calculator.proto.Calculator.ComputeAverageResponse;
import com.proto.calculator.proto.Calculator.ComputeAverageRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(Calculator.SumRequest request, StreamObserver<Calculator.SumResponse> responseObserver) {
        Calculator.SumResponse sumResponse = Calculator.SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber()+request.getSecondNumber())
                .build();

        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();

    }

    @Override
    public void primeNumberDecomposition(Calculator.PrimeNumberDecompositionRequest request, StreamObserver<Calculator.PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer divisor =2;
        while (number > 1){
            if (number % divisor ==0){
                number = number/divisor;
                responseObserver.onNext(Calculator.PrimeNumberDecompositionResponse.newBuilder()
                        .setPrimeFactor(divisor)
                        .build());
            }else{
                  divisor = divisor+1;
                }
            }
        responseObserver.onCompleted();

    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {

        return new StreamObserver<ComputeAverageRequest>() {
            int sumTotal =0;
            int size=0;
            double average =0;
            @Override
            public void onNext(ComputeAverageRequest value) {
                sumTotal+= value.getNumber();
                size+=1;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                average = (double) sumTotal/size;
                ComputeAverageResponse computeAverageResponse = ComputeAverageResponse.newBuilder()
                        .setAverage(average)
                        .build();
                responseObserver.onNext(computeAverageResponse);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Calculator.FindMaximumRequest> findMaximum(StreamObserver<Calculator.FindMaximumResponse> responseObserver) {
        return new StreamObserver<Calculator.FindMaximumRequest>() {
            int currentMaximum=0;
            @Override
            public void onNext(Calculator.FindMaximumRequest value) {
                if(value.getNumber() > currentMaximum){
                    currentMaximum = value.getNumber();
                    Calculator.FindMaximumResponse findMaximumResponse = Calculator.FindMaximumResponse.newBuilder()
                            .setMaximum(currentMaximum)
                            .build();
                    responseObserver.onNext(findMaximumResponse);

                }
            }

            @Override
            public void onError(Throwable t) { }

            @Override
            public void onCompleted() {
                Calculator.FindMaximumResponse findMaximumResponse = Calculator.FindMaximumResponse.newBuilder()
                        .setMaximum(currentMaximum)
                        .build();
                responseObserver.onNext(findMaximumResponse);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void squareRoot(Calculator.SquareRootRequest request, StreamObserver<Calculator.SquareRootResponse> responseObserver) {
        Integer number =request.getNumber();

        if(number >0){
            double numberRoot = Math.sqrt(number);
            Calculator.SquareRootResponse squareRootResponse= Calculator.SquareRootResponse.newBuilder()
                    .setNumberRoot(numberRoot)
                    .build();

            responseObserver.onNext(squareRootResponse);


        }else
        {
            //We construct the exception
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                    .withDescription("The number being sent is not positive")
                    .asRuntimeException());
        }
    }
}
