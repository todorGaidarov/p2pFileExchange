package project.p2p.exchange.torrent.server.impl;

import project.p2p.exchange.torrent.server.RequestHandler;

import java.io.*;
import java.net.Socket;

public class RequestHandlerImpl implements RequestHandler {

    private Socket clientSocket;

    public RequestHandlerImpl(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private String readRequest() {
        String result = null;
        byte[] buffer = new byte[1024];
        int bytesRead;
        BufferedInputStream inputStream = null;

        try (ByteArrayOutputStream resultStream = new ByteArrayOutputStream()){
            inputStream = new BufferedInputStream(clientSocket.getInputStream());
            while ((bytesRead = inputStream.read()) != -1) {
                resultStream.write(buffer, 0, bytesRead);
            }
            result = resultStream.toString("UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace(); // TODO improve error handling
        }
        return result;
    }

    private boolean isValidRequest(String rawRequest) {
        boolean result = false;
        if (rawRequest.length() > 0) {
            result = rawRequest.startsWith("<:|:>") && rawRequest.endsWith("<:|:>");
        }
        return result;
    }

    private String[] parseRequest(String rawRequest) {
        String[] tokens = null;
        if (rawRequest != null) {
            rawRequest = rawRequest.trim();
            if (isValidRequest(rawRequest)) {
                rawRequest = rawRequest.substring(5, rawRequest.length() - 5);
                tokens = rawRequest.split("\\s+");
            } else {
                System.out.println(rawRequest + " is not in the valid format.");
            }
        }
        return tokens;
    }

    private String runCommand(String[] command) {
        String result = null;
        if ("add".equals(command[0])) {
            
        } else if ("remove".equals(command[0])) {

        } else if ("list".equals(command[0])) {

        } else {
            result = "Command name " + command[0] + " not recognized. Request failed" ;
        }
        return result;
    }

    private String getFormatedResponse(String response) {
        return "<:|:>" + response.trim() + "<:|:>";
    }

    private void writeToSocketOutput(String response) {
        try (OutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream())) {
            outputStream.write(response.getBytes("UTF-8"));
        } catch (IOException ex) {
            System.out.println("Failed to send response to .");
        }
    }

    private void sendResponse(String response) {
        String formattedResponse = getFormatedResponse(response);
        writeToSocketOutput(formattedResponse);
    }

    @Override
    public void handleRequest() {
        String request = readRequest();
        String response = null;
        if (request != null) {
            String[] command = parseRequest(request);
            response = runCommand(command);
        } else {
            response = "";
        }
        sendResponse(response);
    }
}
