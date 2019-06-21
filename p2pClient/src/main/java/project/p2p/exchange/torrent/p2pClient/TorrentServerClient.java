package project.p2p.exchange.torrent.p2pClient;

import java.util.List;

public interface TorrentServerClient {

    static final String REQUESST_PREF_LIST_TORRENTS = "getTorrents";
    static final String REQUESST_PREF_GET_TORRENT_INFO = "getInfo";
    static final String REQUESST_PREF_REGISTER_TORRENT = "register";
    static final String REQUESST_PREF_UNREGISTER_TORRENT = "register";

    public static final String RESPONSE_PREF_SUCCEED = "succeed";
    public static final String RESPONSE_PREF_FAILED = "failed";
    public static final String RESPONSE_FAIL_CAUSE = "cause:";

    List<String> listTorrents() throws Exception;

    SharedFile getTorrentInfo(String torrent) throws Exception;

    void registerTorrent(SharedFile sharedFile) throws Exception;

    void unregisterTorrent(String fileName) throws Exception;

}
