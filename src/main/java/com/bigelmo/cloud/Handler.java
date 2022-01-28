package com.bigelmo.cloud;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable {

    private BufferedInputStream networkIn;
    private BufferedOutputStream networkOut;

    public Handler(Socket socket) throws IOException {
        networkIn = new BufferedInputStream(socket.getInputStream());
        networkOut = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                File file = new File("files_on_server/new_file_from_client.txt");
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                byte[] byteArr = new byte[8192];
                int count;
                while ((count = networkIn.read(byteArr)) != -1) {
                    bos.write(byteArr, 0, count);
                }
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
