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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {

    private Path currentCliDir;

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
        logListView.getItems().add("Connecting to server...");
        Network.connect("localhost", 8189);

        try {
            cliNameLabel.setText(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            cliNameLabel.setText("This Computer");
        }

        currentCliDir = Paths.get(System.getProperty("user.home"));
        updateCliListView();
    }

    private void updateCliListView() {
        try {
            cliCurrDirField.setText(currentCliDir.toString());
            cliListView.getItems().clear();
            Files.list(currentCliDir)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .forEach(f -> cliListView.getItems().add(f));
            cliUpBtn.setDisable(currentCliDir.getParent() == null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getSelectedCliItem() {
        if (cliListView.getSelectionModel().getSelectedItem() != null) {
            return currentCliDir.resolve(cliListView.getSelectionModel().getSelectedItem());
        }
        return currentCliDir;
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
        currentCliDir = currentCliDir.getParent().normalize();
        Platform.runLater(this::updateCliListView);
    }

    public void cliListView(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            if (Files.isDirectory(getSelectedCliItem())) {
                uploadBtn.setDisable(true);
            } else {
                uploadBtn.setDisable(false);
            }
        }

        if (mouseEvent.getClickCount() == 2) {
            Path selected = getSelectedCliItem();
            if (Files.isDirectory(selected)) {
                currentCliDir = selected;
                Platform.runLater(this::updateCliListView);
            }
        }
    }
}
