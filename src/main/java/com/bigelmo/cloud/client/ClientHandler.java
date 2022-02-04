package com.bigelmo.cloud.client;

import com.bigelmo.cloud.model.ExchangeMessage;
import com.bigelmo.cloud.model.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler extends SimpleChannelInboundHandler<ExchangeMessage> {

    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // init client dir
        Files.createDirectories(Paths.get("client/clientData"));
        currentDir = Paths.get("client/clientData");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ExchangeMessage exchangeMessage) throws Exception {
        switch (exchangeMessage.getType()) {
            case FILE:
                processFile((FileMessage) exchangeMessage, ctx);
                break;
        }
    }

    private void processFile(FileMessage fileMessage, ChannelHandlerContext ctx) throws IOException {
        Files.write(currentDir.resolve(fileMessage.getFileName()), fileMessage.getBytes());
    }
}
