package com.bigelmo.cloud.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {

    public static final String SERVER = "localhost";
    private static final int PORT = 8189;

    public Button srvAddDirBtn;
    public Button srvDelDirBtn;
    public Button downloadBtn;
    public Button cliAddDirBtn;
    public Button cliDelDirBtn;
    public Button uploadBtn;
    public ListView<String> srvListView;
    public ListView<String> logListView;
    public ListView<String> cliListView;
    public Label connStatusLabel;
    public Font x3;
    public Button srvUpBtn;
    public Button cliUpBtn;
    public Label srvNameLabel;
    public TextField srvCurrDirField;
    public Label cliNameLabel;
    public TextField cliCurrDirField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connStatusLabel.setText("Disconnected");
        logListView.getItems().add("Hello, #username#!");
        logListView.getItems().add("Connecting to server...");

        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(SERVER, PORT))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            channel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new ClientHandler()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();
            System.out.println("connected");
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
//            logListView.getItems().add(ex.getMessage());
        } finally {
            worker.shutdownGracefully();
            logListView.getItems().add("Disconnected");
            connStatusLabel.setText("Disconnected");
        }
    }

    public void srvAddDir(ActionEvent actionEvent) {
    }

    public void srvDelDir(ActionEvent actionEvent) {
    }

    public void download(ActionEvent actionEvent) {
    }

    public void cliAddDir(ActionEvent actionEvent) {
    }

    public void cliDelDir(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) {
    }

    public void srvUp(ActionEvent actionEvent) {
    }

    public void cliUp(ActionEvent actionEvent) {
    }
}
