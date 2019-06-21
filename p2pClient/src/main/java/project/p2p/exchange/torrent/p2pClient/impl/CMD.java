package project.p2p.exchange.torrent.p2pClient.impl;

import project.p2p.exchange.torrent.p2pClient.P2PClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class CMD {

    private P2PClient p2PClient;

    public CMD(P2PClient p2pClient) {
        this.p2PClient = p2pClient;
    }

    public void open() {
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in))) {
            String line = null;
            while ((line = inputReader.readLine()) != null) {
                String[] command = parseCommandFromLine(line);
                if (isExitCommand(command)) {
                    p2PClient.stop();
                    break;
                }
                handleCommand(command);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isExitCommand(String[] command) {
        String commandName = command[0].toLowerCase();
        return "exit".equals(commandName) || "quit".equals(commandName) || "q".equals(commandName);
    }

    private String[] parseCommandFromLine(String line) {
        String[] command = null;
        String trimmedLine = line.trim();
        if (trimmedLine != "") {
            String tmpCommand[] = command = trimmedLine.split("\\s+");
            command = Arrays.stream(tmpCommand).map(token -> {return token.trim();}).toArray(String[]::new);
        }
        return command;
    }

    private void handleRegister(String[] command) {
        if (command.length > 1) {
            try {
                p2PClient.registerTorrent(command[1]);
            } catch (Exception ex) {
                System.out.println("Error while registering file " + command[1] +
                        " Error: " + ex.getMessage());
            }
        } else {
            System.out.println("Invalid number of parameters." + System.lineSeparator() +
                    "Usage: register <filename>");
        }
    }

    private void handleListTorrents(String[] command) {
        try {
            List<String> torrents = p2PClient.listTorrentsFromServer();
            printTorrents(torrents);
        } catch (Exception ex) {
            System.out.println("Error while listing torrents. Message: " + ex.getMessage());
        }
    }

    private void handleUnregister(String[] command) {
        if (command.length > 1) {
            try {
                p2PClient.unregisterTorrent(command[1].trim());
            } catch (Exception ex) {
                System.out.println("Error while registering file " + command[1] +
                        " Error: " + ex.getMessage());
            }
        } else {
            System.out.println("Invalid number of parameters." + System.lineSeparator() +
                    "Usage: register <filename>");
        }
    }

    private void handleGetTorrentInfo(String[] command) {
        if (command.length > 1) {
            try {
                String fileInfo = p2PClient.getTorrentInfo(command[1]);
                System.out.println("File info for " + command[1]);
                System.out.println(fileInfo + System.lineSeparator());
            } catch (Exception ex) {
                System.out.println("Error while obtaining info for file " + command[1] +
                        " Error: " + ex.getMessage());
            }
        } else {
            System.out.println("Invalid number of parameters." + System.lineSeparator() +
                    "Usage: getInfo <filename>");
        }
    }

    private void handleCommand(String[] command) {
        if ("lsTorrents".equals(command[0])) {
            handleListTorrents(command);
        } else if ("register".equals(command[0])) {
            handleRegister(command);
        } else if ("unregister".equals(command[0])) {
            handleUnregister(command);
        } else if ("lsLocal".equals(command[0])) {

        } else if ("getInfo".equals(command[0])) {
            handleGetTorrentInfo(command);
        } else {
            System.out.println("No such command  '" + command[0] + '\'');
        }
    }

    private void printTorrents(List<String> torrents) {
        if (torrents != null) {
            System.out.println("Found torrents:");
            torrents.stream().forEach(torrentName -> System.out.println(torrentName));
        } else {
            System.out.println("No torrents found.");
        }
    }
}
