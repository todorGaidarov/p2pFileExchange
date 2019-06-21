package project.p2p.exchange.torrent.p2pClient;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public interface P2PClient {

    static final String PROP_TORRENT_SERVER_ADDR = "serverAddr";
    static final String PROP_TORRENT_SERVER_PORT = "serverPort";
    static final String PROP_DOWNLOAD_DIR_PATH = "downloadDir";
    static final String PROP_PEER_PORT = "peerPort";

    void start();

    void stop();

    List<String> listTorrentsFromServer() throws Exception;

    List<String> searchTorrent(String searchString) throws Exception;

    String getTorrentInfo(String torrent) throws Exception;

    void downloadTorrent(String torrentName) throws Exception;

    void registerTorrent(String filename) throws Exception;

    void unregisterTorrent(String filename) throws Exception;

    List<String> listLocalSharedFiles() throws Exception;

    void configure(Map<String, Object> configs);

    InetAddress getTorrentServerAddr();

    int getTorrentServerPort();
}
