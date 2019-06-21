package project.p2p.exchange.torrent.p2pClient.impl;

import project.p2p.exchange.torrent.p2pClient.*;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class P2PClientImpl implements P2PClient {

    private Map<String, Object> configs;
    private DownloadAgent downloadAgent;
    private UploadServer uploadServer;
    private TorrentServerClient torrentServerClient;
    private CMD cmd;
    private SocketClient socketClient;

    @Override
    public void configure(Map<String, Object> configs) {
        if (this.configs == null) {
            this.configs = new HashMap<>(configs);
        } else {
            this.configs.putAll(configs);
        }
    }

    @Override
    public void start() {
        String serverAddress = (String) configs.get(PROP_TORRENT_SERVER_ADDR);
        Integer serverPort = (Integer) configs.get(PROP_TORRENT_SERVER_PORT);
        socketClient = new SocketClient(serverAddress, serverPort);
        Integer peerPort = (Integer) configs.get(PROP_PEER_PORT);
        torrentServerClient = new TorrentServerClientImpl(socketClient, peerPort);

        cmd = new CMD(this);
        cmd.open();
    }

    @Override
    public void stop() {
        // will be used when Downloading/Uploading of file is implemented
    }

    @Override
    public List<String> listTorrentsFromServer() throws Exception {
        return torrentServerClient.listTorrents();
    }

    @Override
    public List<String> searchTorrent(String searchString) throws Exception {
        throw new Exception("Non-implemented operation");
    }

    @Override
    public String getTorrentInfo(String filename) throws Exception {
        String torrentInfo = null;
        if (filename != null && !filename.equals("")) {
            try {
                SharedFile torrent = torrentServerClient.getTorrentInfo(filename);
                torrentInfo = torrent.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new Exception("Error while obtaining torrent info  for '" + filename + '\'' + " Cause: " +  ex.getMessage());
            }
        }
        return torrentInfo;
    }

    @Override
    public void downloadTorrent(String name) {

    }

    private SharedFile parseFile(File file) {
        SharedFile sharedFile = null;
        if (file.isFile()) {
            Map<String, Object> fileProps = new HashMap<>();
            fileProps.put(SharedFile.NAME, file.getName());
            fileProps.put(SharedFile.SIZE_BYTES, file.getTotalSpace());
;           sharedFile = new SharedFile(fileProps, null);
        } else {
            // TODO handle the case of a directory with nested files
        }
        return sharedFile;
    }

    private SharedFile getSharedFile(String name) {
        SharedFile sharedFile = null;
        String downloadDirPath = (String) configs.get(PROP_DOWNLOAD_DIR_PATH);
        File file = new File(downloadDirPath, name);
        if (file.exists()) {
            sharedFile = parseFile(file);
        }
        return sharedFile;
    }

    @Override
    public void registerTorrent(String filename) throws Exception {
        SharedFile sharedFile = getSharedFile(filename);
        if (sharedFile != null) { // TODO what if there is an error
            try {
                torrentServerClient.registerTorrent(sharedFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new Exception("Error while registering file '" + filename + '\'' + " Cause: " +  ex.getMessage());
            }
        } else {
            throw new Exception("File '" + filename + "' can does not exist in directory " +
                    configs.get(P2PClient.PROP_DOWNLOAD_DIR_PATH));
        }
    }

    @Override
    public void unregisterTorrent(String filename) throws Exception {
        if (filename != null && !filename.equals("")) {
            try {
                torrentServerClient.unregisterTorrent(filename);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new Exception("Error while unregistering file '" + filename + '\'' + " Cause: " +  ex.getMessage());
            }
        }
    }

    @Override
    public List<String> listLocalSharedFiles() throws Exception {
        List<String> localSharedFiles = new ArrayList<>();
        String downloadDirPath = (String) configs.get(PROP_DOWNLOAD_DIR_PATH);

        Path dirPath = Paths.get(downloadDirPath);
        Files.newDirectoryStream(dirPath, dirEntry -> {return Files.isRegularFile(dirEntry);}).
                forEach(path -> localSharedFiles.add(path.getFileName().toString()));
        return localSharedFiles;
    }

    @Override
    public InetAddress getTorrentServerAddr() {
        return (InetAddress) configs.get(PROP_TORRENT_SERVER_ADDR);
    }

    @Override
    public int getTorrentServerPort() {
        return (Integer) configs.get(PROP_TORRENT_SERVER_PORT);
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(P2PClient.PROP_DOWNLOAD_DIR_PATH, "/home/todorgg/Downloads");
        configs.put(P2PClient.PROP_TORRENT_SERVER_PORT, 1234);
        configs.put(P2PClient.PROP_PEER_PORT, 1212);
        configs.put(P2PClient.PROP_TORRENT_SERVER_ADDR, "localhost");

        P2PClient client = new P2PClientImpl();
        client.configure(configs);
        client.start();
    }
}
