package com.bigelmo.cloud.client;

import com.bigelmo.cloud.model.ExchangeMessage;
import com.bigelmo.cloud.model.FileMessage;
import com.bigelmo.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientHandler extends SimpleChannelInboundHandler<ExchangeMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Connected to server");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ExchangeMessage exchangeMessage) throws Exception {
        System.out.println("Got something from server...");
        switch (exchangeMessage.getType()) {
            case FILE:
                processMessage((FileMessage) exchangeMessage, ctx);
                break;
            case LIST:
                processMessage((ListMessage) exchangeMessage, ctx);
                break;
        }
    }

    private void processMessage(FileMessage file, ChannelHandlerContext ctx) throws IOException {
        System.out.println("processing new file");
//        Path currentDir = mainWindow.getCurrentCliDir();
//        Files.write(currentDir.resolve(file.getFileName()), file.getBytes());
//        mainWindow.updateCliListView();
    }

    private void processMessage(ListMessage list, ChannelHandlerContext ctx) {
        System.out.println("processing new file list");
//        mainWindow.updateSrvListView(list);
    }
}
