package com.bigelmo.cloud;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Font;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client implements Initializable {

    public static final String SERVER = "localhost";
    private static final int PORT = 8189;

    public Button srvAddDirBtn;
    public Button srvDelDirBtn;
    public Button downloadBtn;
    public Button cliAddDirBtn;
    public Button cliDelDirBtn;
    public Button uploadBtn;
    public TitledPane srvTitledPane;
    public ListView<String> srvListView;
    public ListView<String> logListView;
    public TitledPane cliTitledPane;
    public ListView<String> cliListView;
    public Label connStatusLabel;
    public Font x3;

    private Socket socket;
    private BufferedInputStream networkIn;
    private BufferedOutputStream networkOut;

    private void readLoop() {
        try {
            while (true) {
                File file = new File("files_on_client/new_file_from_server.txt");
                if (file.createNewFile()) {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] byteArr = new byte[8192];
                    int count;
                    while ((count = networkIn.read(byteArr)) != -1) {
                        bos.write(byteArr, 0, count);
                    }
                    bos.close();
                }
                Platform.runLater(() -> logListView.getItems().add("Got something from server..."));
            }
        } catch (Exception e) {
            logListView.getItems().add("Error in readLoop!");
            logListView.getItems().add(e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connStatusLabel.setText("Disconnected");
        logListView.getItems().add("Hello, #username#!");
        logListView.getItems().add("Connecting to server...");

        try {
            socket = new Socket(SERVER, PORT);
            logListView.getItems().add("Connected");
            connStatusLabel.setText("Connected");
            networkIn = new BufferedInputStream(socket.getInputStream());
            networkOut = new BufferedOutputStream(socket.getOutputStream());
            uploadBtn.setDisable(false);
//            Thread readThread = new Thread(this::readLoop);
//            readThread.setDaemon(true);
//            readThread.start();
        } catch (ConnectException c) {
            logListView.getItems().add("Connection refused");
            c.printStackTrace();
        } catch (Exception e) {
            logListView.getItems().add("Unknown error");
            logListView.getItems().add(e.getClass().getSimpleName());
            e.printStackTrace();
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
        logListView.getItems().add("Uploading file to server...");
        File file = new File("files_on_client/TestFile1.txt");

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] byteArr = new byte[8192];
            int count;
            while ((count = bis.read(byteArr)) != -1) {
                networkOut.write(byteArr, 0, count);
            }
            bis.close();
            logListView.getItems().add("Uploaded");
        } catch (FileNotFoundException f) {
            logListView.getItems().add("File not found!");
            f.printStackTrace();
        } catch (IOException e) {
            logListView.getItems().add("File send error!");
            e.printStackTrace();
        }
    }
}
