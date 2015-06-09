/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umd.cs.hcil.twitterreplay.senders;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author cbuntain
 */
public class SocketSender implements Sender {
    
    private final ServerSocket mServer;
    private Socket mSocket;
    private PrintWriter mOutput;
    
    public SocketSender(String host, int port) throws IOException {
        mServer = new ServerSocket(port, 3, InetAddress.getAllByName(host)[0]);
    }
    
    public void accept() throws IOException {
        mSocket = mServer.accept();
        
        mOutput = new PrintWriter(mSocket.getOutputStream(), true);
    }

    public void send(List<String> messages) {
        for ( String s : messages ) {
            mOutput.println(s);
        }
    }
    
}
