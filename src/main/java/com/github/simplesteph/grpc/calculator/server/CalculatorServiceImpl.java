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
}
