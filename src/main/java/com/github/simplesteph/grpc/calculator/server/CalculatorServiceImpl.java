package com.github.simplesteph.grpc.calculator.server;

import com.proto.calculator.proto.Calculator;
import com.proto.calculator.proto.CalculatorServiceGrpc;
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
}
