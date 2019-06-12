package project.p2p.exchange.torrent.server.impl;

import project.p2p.exchange.torrent.server.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TorrentServer implements Server {

    static String PORT = "port";
    static String NUMBER_THREADS = "threads";

    private Map<String, Object> configs;
    private ServerStorage serverStorage;

    @Override
    public void configure(Map<String, Object> configuration) {
        if (configs == null) {
            configs = new HashMap<>();
        }
        Set<String> keys = configuration.keySet();
        keys.stream().forEach(key -> configs.put(key, configuration.get(key)));
    }

    private void initServerStorage() {

    }

    private void startRequestListener() {

    }

    @Override
    public void start() {
        initServerStorage();
        startRequestListener();
    }

    @Override
    public void stop() {
        // 1. stop request listener and wait for server workers to finish
        // 2. deal with the storage - persist maybe
    }
}
