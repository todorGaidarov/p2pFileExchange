package project.p2p.exchange.torrent.p2pClient;

public interface DownloadAgent {

    void downloadFile(SharedFile file) throws Exception;
}
