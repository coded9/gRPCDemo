package calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50054;

        Server server = ServerBuilder.forPort(port).
                addService(new CalculatorServerImpl()).build();

        server.start();
        System.out.println(" Server started ");
        System.out.println(" Listening on port "+ port);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            System.out.println("Received shutdown request ");
            server.shutdown();
            System.out.println("Server stopped ");
        }));

        server.awaitTermination();
    }
}
