package project.p2p.exchange.torrent.p2pClient;

import java.net.InetSocketAddress;
import java.util.*;

public class SharedFile {

    public static final String SIZE_BYTES = "sizeBytes";
    public static final String SEEDERS = "seeders";
    public static final String RELATIVE_PATH = "relativePath";
    public static final String NAME = "name";
    public static final String NUMBER_SEEDERS = "numberSeeders";

    public static final String PEER_PORT = "peerPort";

    private Map<String, Object> info = new HashMap<>();
    private Set<InetSocketAddress> seeders = new HashSet<>();

    public SharedFile(Map<String, Object> info, Set<InetSocketAddress> seeders) {
        if (info != null ) {
            this.info = new HashMap<>(info);
            if (seeders != null) {
                this.seeders = seeders;
            }
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

    public Set<InetSocketAddress> getSeeders() {
        return seeders;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append(NAME + ":").append(info.get(NAME) + ";").
                      append(SIZE_BYTES + ":").append(info.get(SIZE_BYTES) + ";");

        String relativePath = (String) info.get(RELATIVE_PATH);
        if (relativePath != null) {
            stringBuilder.append(RELATIVE_PATH + ":").append(relativePath + ";");
        }
        stringBuilder.append(SEEDERS).append(':');
        for (InetSocketAddress seeder : seeders) {
            stringBuilder.append(seeder.getHostString() + ':' + seeder.getPort()).append(',');
        }
        stringBuilder.append(';');
        stringBuilder.append(NUMBER_SEEDERS).append(':').append(seeders.size()).append(';');

        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (this == obj) {
            result = true;
        } else {
            if (obj instanceof SharedFile) {
                result = this.info.equals(((SharedFile) obj).info);
            }
        }
        return result;
    }
}
