package project.p2p.exchange.torrent.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;


import project.p2p.exchange.torrent.server.RequestHandler;
import project.p2p.exchange.torrent.server.RequestListener;
import project.p2p.exchange.torrent.server.Server;

public class RequestListenerImpl implements RequestListener {

	private Server torrentServer;

	private ServerSocket serverSocket;
	private int port;
	
	private boolean isListening;
	
	public RequestListenerImpl(Server torrentServer) {
		this.torrentServer = torrentServer;
	}

	@Override
	public void initListener() throws Exception {
		serverSocket = new ServerSocket(torrentServer.getPort());
	}

    @Override
    public void startListener() {
		if (serverSocket != null) {
			isListening = true;
			acceptRequests();
		}
	}

	private void handleSocketRequest(Socket socket) {
		RequestHandler requestHandler = new RequestHandlerImpl(socket, torrentServer);
		torrentServer.startRequestHandler(() -> requestHandler.handleRequest());
	}

	private void acceptRequests() {
		try {
			while (isListening) {
				Socket socket = serverSocket.accept();
				handleSocketRequest(socket);
			}
		} catch (SocketException ex) {  // TODO improve logging
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void stopListener() {
		isListening = false;
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void restartListener() {
		// TODO Auto-generated method stub

	}
}