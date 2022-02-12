package com.bigelmo.cloud.client;

import com.bigelmo.cloud.model.ListMessage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class App extends Application {

    private Network network;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("App start");
        Parent parent = FXMLLoader.load(getClass().getResource("main_window.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
