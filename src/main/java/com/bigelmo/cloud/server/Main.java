package com.bigelmo.cloud.server;

import com.bigelmo.cloud.model.ListMessage;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Server started");
        Network network = new Network(8189);
        network.start();
    }
}
