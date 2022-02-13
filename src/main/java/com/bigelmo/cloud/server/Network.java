package com.bigelmo.cloud.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Network {

    private final int port;
    private Channel channel;

    public Network(int port) {
        this.port = port;
    }

    public Channel getChannel() {
        return channel;
    }

    public void start() {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new ServerHandler()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.printf("Bind port: %d%n", port);
            channel = future.channel();
            channel.closeFuture().sync();
            System.out.println("Channel closed");
        } catch (InterruptedException e) {
            System.out.println("Network interrupted!");
            e.printStackTrace();
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
            System.out.println("network stopped");
        }
    }
}
