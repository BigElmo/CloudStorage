package com.bigelmo.cloud.server;

import com.bigelmo.cloud.model.ExchangeMessage;
import com.bigelmo.cloud.model.FileMessage;
import com.bigelmo.cloud.model.FileRequestMessage;
import com.bigelmo.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends SimpleChannelInboundHandler<ExchangeMessage> {

    private Path rootDir;
    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected!");
        rootDir = Paths.get("server/clientData/clientName");
        if (Files.notExists(rootDir)) {
            Files.createDirectories(rootDir);
        }
        currentDir = rootDir;
        sendListMessage(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client disconnected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ExchangeMessage exchangeMessage) throws Exception {
        System.out.println("Got something from client...");
        switch (exchangeMessage.getType()) {
            case FILE_REQUEST:
                processMessage((FileRequestMessage) exchangeMessage, ctx);
                break;
            case FILE:
                processMessage((FileMessage) exchangeMessage, ctx);
                break;
        }
    }

    private void sendListMessage(ChannelHandlerContext ctx) throws IOException {
        boolean isRootDir = (currentDir == rootDir);
        ctx.writeAndFlush(new ListMessage(currentDir, isRootDir));
        System.out.println("Files list sent!");
    }

    private void processMessage(FileMessage file, ChannelHandlerContext ctx) throws IOException {
        Files.write(currentDir.resolve(file.getFileName()), file.getBytes());
        sendListMessage(ctx);
    }

    private void processMessage(FileRequestMessage fileRequest, ChannelHandlerContext ctx) throws IOException {
        Path path = currentDir.resolve(fileRequest.getFileName());
        ctx.writeAndFlush(new FileMessage(path));
    }
}
