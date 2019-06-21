package project.p2p.exchange.torrent.server.impl;

import project.p2p.exchange.torrent.server.RequestListener;
import project.p2p.exchange.torrent.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TorrentServer implements Server {

    static final String PORT = "port";
    static final String NUMBER_THREADS = "threads";
    static final int EXECUTOR_DEFAULT_AWAIT_TIME_S = 1;

    private Map<String, Object> configs;
    private ServerStorage serverStorage;
    private RequestListener requestListener;
    private ExecutorService fixedThreadPool;

    @Override
    public void configure(Map<String, Object> configuration) {
        if (configs == null) {
            configs = new HashMap<>();
        }
        Set<String> keys = configuration.keySet();
        keys.stream().forEach(key -> configs.put(key, configuration.get(key)));
    }

    private void initServerStorage() {
        serverStorage = new ServerStorage();
    }

    private void initThreadPool() {
        fixedThreadPool = Executors.newFixedThreadPool((Integer) configs.get(NUMBER_THREADS));
    }

    private void startRequestListener() {
        requestListener = new RequestListenerImpl(this);
        try {
            requestListener.initListener();
            fixedThreadPool.execute(() -> requestListener.startListener());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start() {
        initServerStorage();
        initThreadPool();
        startRequestListener();
    }

    private void deactivateThreadPool() {
        if (fixedThreadPool != null) {
            fixedThreadPool.shutdown();
            try {
                fixedThreadPool.awaitTermination(EXECUTOR_DEFAULT_AWAIT_TIME_S, TimeUnit.SECONDS);
                if (!fixedThreadPool.isTerminated()) {
                    fixedThreadPool.shutdownNow();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                if (fixedThreadPool.isTerminated()) {
                    fixedThreadPool.shutdownNow();
                }
            }
            fixedThreadPool = null;
        }
    }

    @Override
    public void stop() {
        if (requestListener != null) {
            requestListener.stopListener();
            deactivateThreadPool();
        }
        // 1. stop request listener and wait for server workers to finish
        // 2. deal with the storage - persist maybe
    }

    @Override
    public void startRequestHandler(Runnable handler) {
        if (fixedThreadPool != null) {
            fixedThreadPool.execute(handler);
        }
    }

    @Override
    public ServerStorage getStorage() {
        return serverStorage;
    }

    @Override
    public Integer getPort() {
        return (Integer) configs.get(PORT);
    }

    public static void main(String[] args) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(TorrentServer.PORT, 1234);
        configs.put(TorrentServer.NUMBER_THREADS, 10);

        TorrentServer server = new TorrentServer();
        server.configure(configs);
        server.start();

        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in))) {
            String line = null;
            while ((line = inputReader.readLine()) != null) {
                if ("exit".equals(line) || "quit".equals(line)) {
                    server.stop();
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Error while reading input for the server");
        }
    }
}
