package com.bigelmo.cloud.server;

import com.bigelmo.cloud.model.ExchangeMessage;
import com.bigelmo.cloud.model.FileMessage;
import com.bigelmo.cloud.model.FileRequestMessage;
import com.bigelmo.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
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
                System.out.println("got file request");
                processMessage((FileRequestMessage) exchangeMessage, ctx);
                break;
            case FILE:
                System.out.println("got file");
                processMessage((FileMessage) exchangeMessage, ctx);
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        System.out.println("Error in down stream");
        cause.printStackTrace();
        ctx.close();
    }

    private void sendListMessage(ChannelHandlerContext ctx) throws IOException {
        boolean isRootDir = (currentDir == rootDir);
        ListMessage message = new ListMessage(currentDir, isRootDir);
        log.info("sent: {}", message);
        ctx.writeAndFlush(message);
        System.out.println("Files list sent!");
    }

    private void processMessage(FileMessage file, ChannelHandlerContext ctx) throws IOException {
        Files.write(currentDir.resolve(file.getFileName()), file.getBytes());
        System.out.println("file saved in current dir");

        ctx.writeAndFlush(new FileMessage(Paths.get("server/clientData/clientName/Test.txt")));
        System.out.println("file sent");
        sendListMessage(ctx);
    }

    private void processMessage(FileRequestMessage fileRequest, ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getFileName())));
        System.out.println("file sent");
    }
}
