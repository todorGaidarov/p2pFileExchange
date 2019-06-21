package project.p2p.exchange.torrent.p2pClient.impl;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private String serverAddress;
    private int port;

    public SocketClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public String sendRequest(String request) throws IOException {
        String response = null;

        Socket socket = new Socket(serverAddress, port);

        BufferedWriter writer = null;

        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(request);
        writer.newLine();
        writer.flush();
        response = getResponse(socket);
        return response;
    }

    private String getResponse(Socket socket) throws IOException {
        String response = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            response = reader.readLine();
        }
        return response;
    }
}
