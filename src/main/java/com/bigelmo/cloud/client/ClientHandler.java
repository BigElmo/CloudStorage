package com.bigelmo.cloud.client;

import com.bigelmo.cloud.model.ExchangeMessage;
import com.bigelmo.cloud.model.FileMessage;
import com.bigelmo.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<ExchangeMessage> {

    private final MainWindow mainWindow;

    public ClientHandler(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("Connected to server");
        mainWindow.connStatusLabel.setText("Connected");
        mainWindow.logListView.getItems().add("Connected");
        mainWindow.srvNameLabel.setText(ctx.channel().remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ExchangeMessage exchangeMessage) throws Exception {
        log.info("received: {}", exchangeMessage);
        System.out.println("Got something from server...");
        switch (exchangeMessage.getType()) {
            case FILE:
                System.out.println("got file");
                processMessage((FileMessage) exchangeMessage);
                break;
            case LIST:
                System.out.println("got files list");
                processMessage((ListMessage) exchangeMessage);
                break;
        }
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

    private void processMessage(FileMessage file) throws IOException {
        System.out.println("processing new file");
        Path path = mainWindow.getCurrentCliDir();
        Files.write(path.resolve(file.getFileName()), file.getBytes());
        System.out.println("file saved");
        Platform.runLater(mainWindow::updateCliListView);
    }

    private void processMessage(ListMessage list) {
        System.out.println("processing new file list");
        mainWindow.updateSrvListView(list);
    }
}
