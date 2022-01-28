package com.bigelmo.cloud;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static int SERVER_PORT = 8189;

    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(SERVER_PORT);
        System.out.println("Server started at port " + SERVER_PORT);
        while (true) {
            Socket socket = server.accept();
            System.out.println("New client connected...");
            new Thread(new Handler(socket)).start();
        }

    }

}
