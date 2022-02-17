package com.bigelmo.cloud.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("App start");
        Parent parent = FXMLLoader.load(getClass().getResource("main_window.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
        System.out.println("MainWindow show");
    }

    public void stop() {
        System.out.println("App stopped");
    }
}
