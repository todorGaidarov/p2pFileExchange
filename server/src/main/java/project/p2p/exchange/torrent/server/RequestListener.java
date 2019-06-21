package project.p2p.exchange.torrent.server;

public interface RequestListener {

	void initListener() throws Exception;

	void startListener();

	void stopListener();

	void restartListener();
}
