package com.bigelmo.cloud.client;

import com.bigelmo.cloud.model.ExchangeMessage;
import com.bigelmo.cloud.model.FileMessage;
import com.bigelmo.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ExchangeMessageHandler extends SimpleChannelInboundHandler<ExchangeMessage> {

    public MainWindowHandler mainWindow;

    public ExchangeMessageHandler(MainWindowHandler mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("Connected to server");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ExchangeMessage exchangeMessage) throws Exception {
        System.out.println("Got something from server...");
        mainWindow.process(exchangeMessage);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Error in down stream");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("channel inactive");
    }
}
