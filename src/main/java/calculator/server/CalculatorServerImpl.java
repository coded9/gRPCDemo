package calculator.server;

import com.proto.calculator.*;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseStreamObserver){
        responseStreamObserver.onNext(SumResponse.newBuilder().setResult(request.getFirstNum() + request.getSecondNum()).build());
        responseStreamObserver.onCompleted();
    }
    @Override
    public void primeNumberDecomposition(PrimeRequest request, StreamObserver<PrimeResponse> responseStreamObserver){
        int N = request.getNum();
        int k = 2;
        while(N > 1) {
            if (N % k == 0) { // if k evenly divides into N
                // this is a factor
                responseStreamObserver.onNext(PrimeResponse.newBuilder().setResult(k).build());
                N = N / k; // divide N by k so that we have the rest of the number left.
            } else {
                k = k + 1;
            }
        }
    }

    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseStreamObserver){
        return new StreamObserver<AverageRequest>() {
            double sum = 0;
            int count = 0;
            @Override
            public void onNext(AverageRequest request) {
                sum += request.getNum();
                count++;
            }

            @Override
            public void onError(Throwable t) {
                responseStreamObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseStreamObserver.onNext(AverageResponse.newBuilder().setResult(sum/count).build());
                responseStreamObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseStreamObserver){
        return new StreamObserver<MaxRequest>() {
            int max = 0;
            @Override
            public void onNext(MaxRequest request) {
               max = Math.max(max,request.getNum());
                responseStreamObserver.onNext(MaxResponse.newBuilder().setResult(max).build());
            }

            @Override
            public void onError(Throwable t) {
                responseStreamObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseStreamObserver.onCompleted();
            }
        };
    }

    @Override
    public void sqrt(SqrtRequest request, StreamObserver<SqrtResponse> responseObserver) {
        int num = request.getNum();
        if(num < 0){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("number cannot be negative").
                    augmentDescription("Number :"+num).asRuntimeException());
            return;
        }
        responseObserver.onNext(SqrtResponse.newBuilder().setResult(Math.sqrt(num)).build());
        responseObserver.onCompleted();
    }
}
