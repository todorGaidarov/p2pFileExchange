package project.p2p.exchange.torrent.server;

import java.util.Map;

public interface Server {

	void start();

	void stop();

	void configure(Map<String, Object> configuration);

}