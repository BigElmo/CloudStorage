package com.bigelmo.cloud.client;

import com.bigelmo.cloud.model.*;
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
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainWindow implements Initializable, MainWindowHandler {

    private Path currentCliDir;
    private Network network;
    private String cliSelected;
    private String srvSelected;

    public Button srvAddDirBtn;
    public Button srvDelDirBtn;
    public Button downloadBtn;
    public Button cliAddDirBtn;
    public Button cliDelDirBtn;
    public Button uploadBtn;
    public ListView<String> srvListView;
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
        connStatusLabel.setText("Connecting to server......");
        try {
            cliNameLabel.setText(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            cliNameLabel.setText("This Computer");
        }
        currentCliDir = Paths.get(System.getProperty("user.home"));
        Platform.runLater(this::updateCliListView);

        network = new Network("localhost", 8189, this);
        network.start();
        System.out.println("MainWindow initialized");
    }

    public void process(ExchangeMessage message) {
        switch (message.getType()) {
            case FILE:
                System.out.println("got file");
                processMessage((FileMessage) message);
                break;
            case LIST:
                System.out.println("got files list");
                processMessage((ListMessage) message);
                break;
            case INFO:
                System.out.println("got path info");
                processMessage((InfoMessage) message);
                break;
        }
    }

    @Override
    public void setConnectionStatus(String status) {
        connStatusLabel.setText(status);
    }

    private void processMessage(FileMessage file) {
        System.out.println("processing new file");
        try {
            Files.write(getCurrentCliDir().resolve(file.getFileName()), file.getBytes());
            System.out.println("file saved");
        } catch (IOException e) {
            System.out.println("Error saving file in current dir!");
            e.printStackTrace();
        }
        Platform.runLater(this::updateCliListView);
    }

    private void processMessage(ListMessage list) {
        System.out.println("processing new file list");
        Platform.runLater(() -> {
            downloadBtn.setDisable(true);
            srvUpBtn.setDisable(list.isRootDir());
            srvCurrDirField.clear();
            srvCurrDirField.setText(list.getPathName());
            srvListView.getItems().clear();
            srvListView.getItems().addAll(list.getFileNames());
        });
        System.out.println("server file list updated");
    }

    private void processMessage(InfoMessage info) {
        System.out.println("processing new path info");
        downloadBtn.setDisable(info.isDir());
    }

    public Path getCurrentCliDir() {
        return currentCliDir;
    }

    public void updateCliListView() {
        try {
            uploadBtn.setDisable(true);
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

    public void srvAddDir(ActionEvent actionEvent) {
    }

    public void srvDelDir(ActionEvent actionEvent) {
    }

    public void download(ActionEvent actionEvent) {
        network.getChannel().writeAndFlush(new FileRequestMessage(srvSelected));
    }

    public void cliAddDir(ActionEvent actionEvent) {
    }

    public void cliDelDir(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        network.getChannel().writeAndFlush(new FileMessage(currentCliDir.resolve(cliSelected)));
    }

    public void srvUp(ActionEvent actionEvent) {
        network.getChannel().writeAndFlush(new DirOutMessage());
    }

    public void cliUp(ActionEvent actionEvent) {
        currentCliDir = currentCliDir.getParent().normalize();
        Platform.runLater(this::updateCliListView);
    }

    public void cliListView(MouseEvent mouseEvent) {
        cliSelected = cliListView.getSelectionModel().getSelectedItem();
        if (cliSelected != null) {
            Path path = currentCliDir.resolve(cliSelected);
            if (mouseEvent.getClickCount() == 1) {
                uploadBtn.setDisable(Files.isDirectory(path));
            }

            if (mouseEvent.getClickCount() == 2) {
                if (Files.isDirectory(path)) {
                    currentCliDir = path;
                    Platform.runLater(this::updateCliListView);
                }
            }
        }
    }

    public void srvListView(MouseEvent mouseEvent) {
        srvSelected = srvListView.getSelectionModel().getSelectedItem();
        if (srvSelected != null) {
            if (mouseEvent.getClickCount() == 1) {
                network.getChannel().writeAndFlush(new RequestInfoMessage(srvSelected));
            }

            if (mouseEvent.getClickCount() == 2) {
                network.getChannel().writeAndFlush(new DirInMessage(srvSelected));
            }
        }
    }
}
