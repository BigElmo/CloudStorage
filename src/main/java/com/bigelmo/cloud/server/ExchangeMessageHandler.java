package com.bigelmo.cloud.server;

import com.bigelmo.cloud.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ExchangeMessageHandler extends SimpleChannelInboundHandler<ExchangeMessage> {

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
            case DIR_OUT:
                System.out.println("got dir up request");
                processMessage((DirOutMessage) exchangeMessage, ctx);
                break;
            case DIR_IN:
                System.out.println("got dir in request");
                processMessage((DirInMessage) exchangeMessage, ctx);
                break;
            case INFO_REQUEST:
                System.out.println("got info request");
                processMessage((RequestInfoMessage) exchangeMessage, ctx);
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
        boolean isRootDir = (currentDir.equals(rootDir));
        ctx.writeAndFlush(new ListMessage(currentDir, isRootDir));
        System.out.println("Files list sent!");
    }

    private void processMessage(FileMessage file, ChannelHandlerContext ctx) throws IOException {
        Files.write(currentDir.resolve(file.getFileName()), file.getBytes());
        System.out.println("file saved in current dir");
        sendListMessage(ctx);
    }

    private void processMessage(FileRequestMessage fileRequest, ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getFileName())));
        System.out.println("file sent");
    }

    private void processMessage(DirOutMessage dirOut, ChannelHandlerContext ctx) throws IOException {
        if (!currentDir.equals(rootDir)) {
            currentDir = currentDir.getParent();
        }
        sendListMessage(ctx);
    }

    private void processMessage(DirInMessage dirIn, ChannelHandlerContext ctx) throws IOException {
        System.out.println("processing dir in request");
        Path newPath = currentDir.resolve(dirIn.getNewDirName());
        if (Files.isDirectory(newPath)) {
            currentDir = newPath;
        }
        sendListMessage(ctx);
    }

    private void processMessage(RequestInfoMessage requestInfo, ChannelHandlerContext ctx) {
        Path path = currentDir.resolve(requestInfo.getFileName());
        ctx.writeAndFlush(new InfoMessage(Files.isDirectory(path)));
    }
}
