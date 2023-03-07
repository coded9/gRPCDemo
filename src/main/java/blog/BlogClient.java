package blog.client;

import com.google.protobuf.Empty;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BlogClient {

    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub){
        try{
            BlogId createResponse = stub.createBlog(Blog.newBuilder()
                    .setAuthor("Ashok").setTitle("New Blog").setContent("Hello world, this is a new blog!")
                    .build());
            System.out.println("blog create "+ createResponse.getId());
            return createResponse;
        }catch (StatusRuntimeException ex){
            System.out.println("Couldn't create the blog");
            ex.printStackTrace();
            return null;
        }
    }

    private static void readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId){
        try{
            Blog readResponse = stub.readBlog(blogId);
            System.out.println("Blog read "+readResponse);
        }catch (StatusRuntimeException ex){
            System.out.println("couldn't read the blog");
            ex.printStackTrace();
        }

    }

    private static void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId){
        try{
            Blog newBlog = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setAuthor("Ashok")
                    .setTitle("New Blog(changed)")
                    .setContent("Hello world, this is my new blog(changed)")
                    .build();
            stub.updateBlog(newBlog);
            System.out.println("Blog updated "+newBlog);
        }catch (StatusRuntimeException ex){
            System.out.println("couldn't updated the blog");
            ex.printStackTrace();
        }
    }

    private static void listBlogs(BlogServiceGrpc.BlogServiceBlockingStub stub){
        System.out.println("Received List Blog Request");
        stub.listBlogs(Empty.getDefaultInstance()).forEachRemaining(e ->{
            System.out.println(e);
        });
    }

    private static  void deleteBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId){
        try{
            stub.deleteBlog(blogId);
            System.out.println("Blog  deleted: "+blogId.getId());
        }catch (StatusRuntimeException ex){
            System.out.println("Blog couldn't be deleted");
            ex.printStackTrace();
        }
    }
    private static void run(ManagedChannel channel){
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);
        BlogId blogId = createBlog(stub);
        if(blogId == null){
            return;
        }
        readBlog(stub,blogId);
        updateBlog(stub,blogId);
        //listBlogs(stub);
        deleteBlog(stub,blogId);
    }
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50055)
                .usePlaintext().build();

        run(channel);
        System.out.println("Shutting down ");
        channel.shutdown();

    }
}
