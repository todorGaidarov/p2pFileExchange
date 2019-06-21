package project.p2p.exchange.torrent.server;

import project.p2p.exchange.torrent.server.impl.ServerStorage;

import java.util.Map;

public interface Server {

	void start();

	void stop();

	void configure(Map<String, Object> configuration);

	void startRequestHandler(Runnable handler);

	ServerStorage getStorage();

	Integer getPort();
}