package project.p2p.exchange.torrent.server.impl;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerStorage {

    private Object storageLock = new Object();
    private Map<String, SharedFile> torrents;
    private Map<String, Set<String>> seederToFiles;

    public ServerStorage() {
        torrents = new ConcurrentHashMap<>();
        seederToFiles = new HashMap<>();
    }

    public SharedFile getTorrentByName(String name) {
        return torrents.get(name);
    }

    public void addTorrent(SharedFile torrent) {
        if (torrent != null) {
            String name = torrent.getName();
            if (name != null && name != "") {
                torrents.put(name, torrent);
            }
        }
    }

    public void addSeederToTorrent(String name, InetSocketAddress seeder) {
        SharedFile torrent = torrents.get(name);
        if (torrent != null) {
            torrent.addSeeder(seeder);
        }
    }

    public void removeSeederFromTorrent(String name, InetSocketAddress seeder) {
        SharedFile torrent = torrents.get(name);
        if (torrent != null) {
            System.out.println("Torrent " + name + " not null");
            torrent.removeSeeder(seeder);
        }
    }

    public void removeSeeder() {

    }
    public List<String> getTorrentsNames() {
        List<String> torrentsNames = null;
        if (torrents != null && torrents.size() > 0) {
            torrentsNames = new ArrayList<>(torrents.keySet());
        } else {
            torrentsNames = new ArrayList();
        }
        return torrentsNames;
    }

    public boolean torrentExists(String name) {
        return torrents.containsKey(name);
    }
}
