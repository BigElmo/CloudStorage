package com.bigelmo.cloud.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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

    private final String host;
    private final int port;
    private Channel channel;
    public MainWindowHandler mainWindow;

    public Network(String host, int port, MainWindowHandler mainWindow) {
        this.host = host;
        this.port = port;
        this.mainWindow = mainWindow;
    }

    public Channel getChannel() {
        return channel;
    }

    public void start() {
        Thread t1 = new Thread(() -> {
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(worker)
                        .channel(NioSocketChannel.class)
                        .remoteAddress(new InetSocketAddress(host, port))
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) {
                                channel.pipeline().addLast(
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(),
                                        new ExchangeMessageHandler(mainWindow)
                                );
                            }
                        });
                ChannelFuture future = bootstrap.connect().sync();
                System.out.println("Network started");
                channel = future.channel();
                channel.closeFuture().sync();
                System.out.println("Channel closed");
            } catch (InterruptedException e) {
                System.out.println("Network interrupted!");
                e.printStackTrace();
            } finally {
                worker.shutdownGracefully();
                System.out.println("network stopped");
            }
        });
        t1.setDaemon(true);
        t1.start();
    }
}
