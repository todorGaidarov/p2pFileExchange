package project.p2p.exchange.torrent.p2pClient;

import java.net.Inet4Address;
import java.util.List;
import java.util.Map;

public interface P2PClient {
    void start();

    void stop();

    void listTorrentsFromServer();

    List<String> searchTorrent(String searchString);

    String getTorrentInfo(String torrent);

    void downloadTorrent(String torrentName);

    void registerTorrent(String relativePath);

    void unregisterTorrent(String relativePath);

    void configure(Map<String, Object> configs);

    Inet4Address getTorrentServerAddr();

    int getTorrentServerPort();
}
