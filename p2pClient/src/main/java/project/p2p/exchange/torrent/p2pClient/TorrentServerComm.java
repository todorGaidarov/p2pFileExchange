package project.p2p.exchange.torrent.p2pClient;

import java.util.List;

public interface TorrentServerComm {

    List<String> listTorrents();

    String getTorrentInfo(String torrent);

    void registerTorrent(String relativePath) throws Exception;

    void unregisterTorrent(String relativePath) throws Exception;

}
