package project.p2p.exchange.torrent.server.impl;

import project.p2p.exchange.torrent.server.RequestHandler;
import project.p2p.exchange.torrent.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RequestHandlerImpl implements RequestHandler {

    private Socket clientSocket;
    private Server torrentServer;

    public RequestHandlerImpl(Socket clientSocket, Server torreServer) {
        this.clientSocket = clientSocket;
        this.torrentServer = torreServer;
    }

    private String readRequest() {
        String result = null;
        BufferedInputStream inputStream = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            result = reader.readLine();
        } catch (IOException ex) {
            System.out.println("FAiled while reading request.");
            ex.printStackTrace();
        }
        return result;
    }

    private boolean isValidRequest(String rawRequest) {
        return true;
    }

    private String[] parseRequest(String rawRequest) {
        String[] tokens = null;
        if (rawRequest != null) {
            rawRequest = rawRequest.trim();
            if (isValidRequest(rawRequest)) {
                rawRequest = rawRequest.substring("<:|:>".length(), rawRequest.length() - "<:|:>".length()).trim();
                tokens = rawRequest.split("\\s+");
            } else {
                System.out.println(rawRequest + " is not in the valid format.");
            }
        }
        return tokens;
    }

    private String extractAttrValue(String attrName, String line) {
        String value = null;
        int indexKey = line.indexOf(attrName);
        if (indexKey != -1) {
            int indexSeparator = line.indexOf(';', indexKey);
            value = line.substring(indexKey + attrName.length() + 1, indexSeparator);
        }
        return  value;
    }

    private SharedFile extracSharedFile(String info) {
        Map<String, Object> fileInfo = new HashMap<>();

        String name = extractAttrValue(SharedFile.NAME, info);
        fileInfo.put(SharedFile.NAME, name);

        long size =  Long.decode(extractAttrValue(SharedFile.SIZE_BYTES, info));
        fileInfo.put(SharedFile.SIZE_BYTES, size);

        String relativePath = extractAttrValue(SharedFile.RELATIVE_PATH, info);
        fileInfo.put(SharedFile.RELATIVE_PATH, relativePath);

        SharedFile torrent = new SharedFile(fileInfo, null);

        int peerPort = Integer.decode(extractAttrValue(SharedFile.PEER_PORT, info));
        InetAddress clientAddress = clientSocket.getInetAddress();
        InetSocketAddress seeder = new InetSocketAddress(clientAddress, peerPort);
        torrent.addSeeder(seeder);

        return torrent;
    }

    private String handleRegisterCommand(String[] command) {
        String result = "failed";
        SharedFile torrent = extracSharedFile(command[1]);
        String name = torrent.getName();

        ServerStorage storage = torrentServer.getStorage();
        if (storage.torrentExists(name)) {
            InetSocketAddress seeder = torrent.getSeeders().iterator().next();
            storage.addSeederToTorrent(name, seeder);
        } else {
            storage.addTorrent(torrent);
        }
        result = "secceed";
        return result;
    }

    private String handleUnregisterCommand(String[] command) {
        String result = "failed";
        String name = extractAttrValue(SharedFile.NAME, command[1]);
        int port = Integer.decode(extractAttrValue(SharedFile.PEER_PORT, command[1]));
        InetSocketAddress seeder = new InetSocketAddress(clientSocket.getInetAddress(), port);

        ServerStorage storage = torrentServer.getStorage();
        storage.removeSeederFromTorrent(name, seeder);
        result = "secceed";
        return result;
    }

    private String handleListTorrents() {
        String result = null;
        ServerStorage storage = torrentServer.getStorage();
        List<String> torrentsNames = storage.getTorrentsNames();
        StringBuilder sb = new StringBuilder("succeed ");
        Iterator<String> namesIter = torrentsNames.iterator();
        while (namesIter.hasNext()) {
            String name = namesIter.next();
            sb.append(name);
            if (namesIter.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    private String handleGetInfo(String[] command) {
        ServerStorage storage = torrentServer.getStorage();
        String name = extractAttrValue(SharedFile.NAME, command[1]);
        SharedFile torrent = storage.getTorrentByName(name);
        StringBuilder sb = new StringBuilder();
        if (torrent != null) {
            sb.append("succeed ").append(torrent.toString());
        } else {
            sb.append("failed ").append("No such torrent");
        }
        return sb.toString();
    }

    private String runCommand(String[] command) {
        String result = null;
        if ("register".equals(command[0])) {
            System.out.println("egister received");
            result = handleRegisterCommand(command);
        } else if ("unregister".equals(command[0])) {
            System.out.println("unregister received");
            result = handleUnregisterCommand(command);
        } else if ("list".equals(command[0])) {
            System.out.println("list received");
            result = handleListTorrents();
        } else if ("getInfo".equals(command[0])) {
            System.out.println("getInfo received");
            result = handleGetInfo(command);
        } else {
            result = "failed Command name " + command[0] + " not recognized. Request failed" ;
        }
        return result;
    }

    private void writeToSocketOutput(String response) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            writer.write(response + '\n');
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            System.out.println("Failed to send response to .");
        }
    }

    private void sendResponse(String response) {
        writeToSocketOutput(response);
    }

    @Override
    public void handleRequest() {
        String request = readRequest();
        String response = null;
        if (request != null) {
            String[] command = parseRequest(request);
            if (command != null && command.length > 0) {
                response = runCommand(command);
            } else {
                response = "failed Request is not in valid format. Request failed.";
            }
        } else {
            response = "failed No request was received.";
        }
        sendResponse(response);
    }
}
