package calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    private static void doSum(ManagedChannel channel){
        System.out.println("Enter doSum ");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
                CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse response = stub.sum(SumRequest.newBuilder().setFirstNum(100).setSecondNum(200).build());
        System.out.println("Sum "+response.getResult());
    }

    private static void doPrimeNumberDecomposition(ManagedChannel channel){
        System.out.println("Enter doPrimeNumberDecomposition ");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
                CalculatorServiceGrpc.newBlockingStub(channel);
        stub.primeNumberDecomposition(PrimeRequest.newBuilder().setNum(120).build()).forEachRemaining(primeResponse -> {
            System.out.println(" Factor "+ primeResponse.getResult());
        });
    }

    private static void doAverage(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doAverage ");
        CalculatorServiceGrpc.CalculatorServiceStub stub =
                CalculatorServiceGrpc.newStub(channel);
        List<Integer> numbers = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        Collections.addAll(numbers,1, 2, 3,4);
        StreamObserver<AverageRequest> stream = stub.average(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse response) {
                System.out.println(response.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for(Integer num: numbers){
            stream.onNext(AverageRequest.newBuilder().setNum(num).build());
        }
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);

    }

    private static void doMax(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doMax ");
        CalculatorServiceGrpc.CalculatorServiceStub stub =
                CalculatorServiceGrpc.newStub(channel);
        List<Integer> numbers = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        Collections.addAll(numbers,1,5,3,6,2,20);
        StreamObserver<MaxRequest> stream = stub.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse response) {
                System.out.println(response.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for(Integer num: numbers){
            stream.onNext(MaxRequest.newBuilder().setNum(num).build());
        }
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);

    }

    private static void doSqrt(ManagedChannel channel){
        System.out.println("Enter doSqrt ");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
                CalculatorServiceGrpc.newBlockingStub(channel);
        SqrtResponse response = stub.sqrt(SqrtRequest.newBuilder().setNum(-100).build());
        System.out.println("Sqrt "+response.getResult());
    }
    public static void main(String[] args) throws InterruptedException {

        if(args.length == 0){
            System.out.println("Need one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50054)
                .usePlaintext().build();

        switch(args[0]){
            case "sum": doSum(channel); break;
            case "prime_number_decomposition": doPrimeNumberDecomposition(channel); break;
            case "average": doAverage(channel); break;
            case "max": doMax(channel); break;
            case "sqrt": doSqrt(channel); break;
            default:
                System.out.println("Keyword invalid "+args[0]);
        }
        System.out.println("Shutting down ");
        channel.shutdown();

    }
}
