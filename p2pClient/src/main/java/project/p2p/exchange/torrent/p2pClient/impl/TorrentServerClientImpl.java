package project.p2p.exchange.torrent.p2pClient.impl;

import project.p2p.exchange.torrent.p2pClient.P2PClient;
import project.p2p.exchange.torrent.p2pClient.SharedFile;
import project.p2p.exchange.torrent.p2pClient.TorrentServerClient;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class TorrentServerClientImpl implements TorrentServerClient {

    private SocketClient socketClient;
    private int peerPort;

    public TorrentServerClientImpl(SocketClient socketClient, int peerPort) {
        this.socketClient = socketClient;
        this.peerPort = peerPort;
    }

    private List<String> parseStrings(String response) {
        int index = response.indexOf(RESPONSE_PREF_SUCCEED);
        String[] elements = response.substring(index + RESPONSE_PREF_SUCCEED.length()).trim().split(",");
        return Arrays.stream(elements).map(element -> {return element.trim();}).
                collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<String> listTorrents() throws Exception {
        List<String> torrents = null;
        StringBuilder sb = new StringBuilder("<:|:>");
        sb.append("list").append("<:|:>");

        String response = null;
        try {
            response = socketClient.sendRequest(sb.toString());
        } catch (IOException ex) {
            throw new Exception("Connection error while obtaining torrents", ex);
        }

        checkIfRequestFailed(response);
        torrents = parseStrings(response);
        return torrents;
    }

    @Override
    public SharedFile getTorrentInfo(String filename) throws Exception {
        SharedFile info = null;
        StringBuilder sb = new StringBuilder("<:|:>");
        sb.append("getInfo ").append(SharedFile.NAME).append(':').append(filename).append(";<:|:>");

        String response = null;
        try {
            response = socketClient.sendRequest(sb.toString());
        } catch (IOException ex) {
            throw new Exception("Connection error while obtaining info for " + filename , ex);
        }

        checkIfRequestFailed(response);
        info = parseFileInfo(response);
        return info;
    }

    private void checkIfRequestFailed(String response) throws Exception {
        if (response != null && response.trim() != "") {
            if (response.trim().startsWith(RESPONSE_PREF_FAILED)) {
                int index = response.indexOf(RESPONSE_FAIL_CAUSE);
                String failCause = response.substring(index +  RESPONSE_FAIL_CAUSE.length()).trim();
                throw new Exception(failCause);
            }
        } else {
            throw new Exception("No or invalid response from the server: " + response);
        }
    }

    // file info from the request: {succeed/failed [<attrName>:<value>;...]}
    private SharedFile parseFileInfo(String response) {
        SharedFile sharedFile = null;
        String fileInfo = response.substring(RESPONSE_PREF_SUCCEED.length()).trim();

        Map<String, Object> sharedFileAttr = new HashMap<>();

        String name = extractAttrValue(SharedFile.NAME, fileInfo);
        sharedFileAttr.put(SharedFile.NAME, name);

        String relativePath = extractAttrValue(SharedFile.RELATIVE_PATH, fileInfo);
        sharedFileAttr.put(SharedFile.RELATIVE_PATH, relativePath);

        Long fileSize = Long.decode(extractAttrValue(SharedFile.SIZE_BYTES, fileInfo));
        sharedFileAttr.put(SharedFile.SIZE_BYTES, fileSize);

        Integer numberOfSeeders = Integer.decode(extractAttrValue(SharedFile.NUMBER_SEEDERS, fileInfo));
        sharedFileAttr.put(SharedFile.NUMBER_SEEDERS, numberOfSeeders);

        Set<InetSocketAddress> seeders = null;
        if (numberOfSeeders > 0) {
            String seedersInfo = extractAttrValue(SharedFile.SEEDERS, fileInfo);
            seeders = new HashSet<>(parseSocketAddresses(seedersInfo));
        }

        sharedFile = new SharedFile(sharedFileAttr, seeders);
        return sharedFile;
    }

    private List<InetSocketAddress> parseSocketAddresses(String addresses) {
        String[] entries = addresses.trim().split(",");
        List<InetSocketAddress> socketAddresses = Arrays.stream(entries).map(entry -> {
            String[] hostAndPort = entry.split(":");
            return new InetSocketAddress(hostAndPort[0], Integer.valueOf(hostAndPort[1]));
        }).collect(Collectors.toCollection(ArrayList::new));
        return socketAddresses;
    }

    private String extractAttrValue(String attrName, String line) {
        int indexKey = line.indexOf(attrName);
        int indexSeparator = line.indexOf(';', indexKey);
        String value = line.substring(indexKey + attrName.length() + 1, indexSeparator);
        return  value;
    }

    @Override
    public void registerTorrent(SharedFile sharedFile) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<:|:>register ").append(sharedFile.toString()).
                append(P2PClient.PROP_PEER_PORT).append(':').append(peerPort).append(';').
                append("<:|:>");

        String response = null;
        try {
            response = socketClient.sendRequest(sb.toString());
        } catch (IOException ex) {
            throw new Exception("Connection error while registering file " + sharedFile.getName(), ex);
        }
        checkIfRequestFailed(response);
    }


    @Override
    public void unregisterTorrent(String filename) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<:|:>unregister ").append(SharedFile.NAME).append(':').append(filename).append(';').
                append(P2PClient.PROP_PEER_PORT).append(':').append(peerPort).append(';').append("<:|:>");

        String response = null;
        try {
            response = socketClient.sendRequest(sb.toString());
        } catch (IOException ex) {
            throw new Exception("Connection error while unregistering file " + filename, ex);
        }
        checkIfRequestFailed(response);
    }
}
