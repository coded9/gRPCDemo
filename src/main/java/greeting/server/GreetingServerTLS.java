package greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServerTLS {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50053;

        Server server = ServerBuilder.forPort(port).
                useTransportSecurity(new File("ssl/server.crt"),
                        new File("ssl/server.pem")).
                addService(new GreetingServerImpl()).build();

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
