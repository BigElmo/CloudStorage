package com.bigelmo.cloud.server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Server started");
        Network network = new Network(8189);
        network.start();
    }
}
