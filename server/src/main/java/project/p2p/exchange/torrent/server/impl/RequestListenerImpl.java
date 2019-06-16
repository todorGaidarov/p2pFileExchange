package project.p2p.exchange.torrent.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import project.p2p.exchange.torrent.server.RequestListener;
import project.p2p.exchange.torrent.server.Server;

public class RequestListenerImpl implements RequestListener {

	private Server torrentServer;

	private ServerSocket serverSocket;
	private Executor fixedThreadPool;
	private int port;
	
	private boolean isRunning;
	
	public RequestListenerImpl(Server torrentServer) {
		this.torrentServer = torrentServer;
	}

	@Override
	public void initListener(int port, int numberOfWorkerThreads) throws Exception {
		serverSocket = new ServerSocket(port);
		int numberThreads = numberOfWorkerThreads > 0 ? numberOfWorkerThreads : 1;
		fixedThreadPool = Executors.newFixedThreadPool(numberThreads);
	}

    @Override
    public void startListener() {
		if (serverSocket != null) {
			isRunning = true;
			acceptRequests();
		}
	}

	private void acceptRequests() {
		try {
			while (isRunning) {
				Socket socket = serverSocket.accept();

			}
		} catch (SocketException ex) {  // TODO improve logging
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void stopListener() {
		isRunning = false;
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException ex) {

			}
		}
	}

	@Override
	public void restartListener() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		RequestListenerImpl handler = new RequestListenerImpl(null);
//		handler.initListener(1234, 10);
		handler.startListener();
	}
}
