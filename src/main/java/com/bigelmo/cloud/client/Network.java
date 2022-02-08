package com.bigelmo.cloud.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;

public class Network {

    private static EventLoopGroup worker;

    public static void connect(String server, int port) {
        Thread net = new Thread(new Runnable() {
            @Override
            public void run() {
                worker = new NioEventLoopGroup();
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(worker)
                            .channel(NioSocketChannel.class)
                            .remoteAddress(new InetSocketAddress(server, port))
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) {
                                    channel.pipeline().addLast(
                                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                            new ObjectEncoder(),
                                            new ClientHandler()
                                    );
                                }
                            });
                    ChannelFuture future = bootstrap.connect().sync();
                    System.out.println("connected");
                    future.channel().closeFuture().sync();
                    System.out.println("future closed");
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    worker.shutdownGracefully();
                    System.out.println("disconnected");
                }
            }
        });
        net.setDaemon(true);
        net.start();
    }

    public static void disconnect() {
        worker.shutdownGracefully();
    }
}
