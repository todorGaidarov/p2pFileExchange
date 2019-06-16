package project.p2p.exchange.torrent.p2pClient;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedFile {

    public static final String SIZE_BYTES = "sizeBytes";
    public static final String SEEDERS = "seeders";
    public static final String RELATIVE_PATH = "relativePath";
    public static final String NAME = "name";

    Map<String, Object> info = new HashMap<>();

    public SharedFile(Map<String, Object> info) {
        if (info != null ) {
            this.info = new HashMap<>(info);
        }
    }

    public String getName() {
        return (String) info.get(NAME);
    }

    public Long getSizeBytes() {
        return (Long) info.get(SIZE_BYTES);
    }

    public String getRelativePath() {
        return (String) info.get(RELATIVE_PATH);
    }

    public List<InetSocketAddress> getSeeders() {
        return (List<InetSocketAddress>) info.get(SEEDERS);
    }
}
