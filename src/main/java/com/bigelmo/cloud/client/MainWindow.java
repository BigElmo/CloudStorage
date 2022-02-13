package com.bigelmo.cloud.client;

import com.bigelmo.cloud.model.FileMessage;
import com.bigelmo.cloud.model.ListMessage;
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

public class MainWindow implements Initializable {

    private Path currentCliDir;
    private Network network;

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
        network = new Network("localhost", 8189, this);
        Thread thread = new Thread(network);
        thread.setDaemon(true);
        thread.start();

        try {
            cliNameLabel.setText(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            cliNameLabel.setText("This Computer");
        }
        currentCliDir = Paths.get(System.getProperty("user.home"));
        Platform.runLater(this::updateCliListView);
    }

    public Path getCurrentCliDir() {
        return currentCliDir;
    }

    public void updateCliListView() {
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

    public void updateSrvListView(ListMessage list) {
        Platform.runLater(() -> {
            srvCurrDirField.setText(list.getPath().toString());
            srvListView.getItems().clear();
            srvListView.getItems().addAll(list.getFileNames());
            srvUpBtn.setDisable(list.isRootDir() || !list.isHasParent());
        });
    }

    private Path getSelectedCliItem() {
        if (cliListView.getSelectionModel().getSelectedItem() != null) {
            return currentCliDir.resolve(cliListView.getSelectionModel().getSelectedItem());
        }
        return currentCliDir;
    }

    public void srvAddDir(ActionEvent actionEvent) throws IOException {
        network.getChannel().writeAndFlush(new FileMessage(Paths.get("server/ttt.txt")));

    }

    public void srvDelDir(ActionEvent actionEvent) {
    }

    public void download(ActionEvent actionEvent) {
    }

    public void cliAddDir(ActionEvent actionEvent) {
    }

    public void cliDelDir(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        network.getChannel().writeAndFlush(new FileMessage(getSelectedCliItem()));
    }

    public void srvUp(ActionEvent actionEvent) {
    }

    public void cliUp(ActionEvent actionEvent) {
        currentCliDir = currentCliDir.getParent().normalize();
        Platform.runLater(this::updateCliListView);
    }

    public void cliListView(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            uploadBtn.setDisable(Files.isDirectory(getSelectedCliItem()));
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
