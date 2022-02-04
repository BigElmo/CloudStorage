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

    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // init client dir
        Files.createDirectories(Paths.get("server/clientData"));
        currentDir = Paths.get("server/clientData");
//        sendListMessage(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ExchangeMessage exchangeMessage) throws Exception {
        switch (exchangeMessage.getType()) {
            case FILE_REQUEST:
                processFileRequest((FileRequestMessage) exchangeMessage, ctx);
                break;
            case FILE:
                processFile((FileMessage) exchangeMessage, ctx);
                break;
        }
    }

    private void sendListMessage(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(currentDir));
    }

    private void processFile(FileMessage fileMessage, ChannelHandlerContext ctx) throws IOException {
        Files.write(currentDir.resolve(fileMessage.getFileName()), fileMessage.getBytes());
        sendListMessage(ctx);
    }

    private void processFileRequest(FileRequestMessage fileRequestMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = currentDir.resolve(fileRequestMessage.getFileName());
        ctx.writeAndFlush(new FileMessage(path));
    }
}
