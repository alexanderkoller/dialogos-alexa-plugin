/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author koller
 */
public class AlexaExecutionContext {
    private ServerSocket ssocket;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    
    public AlexaExecutionContext(int port) throws IOException {
        ssocket = new ServerSocket(port);        
    }
    
    public void connect() throws IOException {
        socket = ssocket.accept();
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void write(String s) {
        writer.println(s);
        writer.flush();
    }
    
    public String read() throws IOException {
        return reader.readLine();
    }

    void disconnect() throws IOException {
        if( socket != null ) {
            socket.close();
            socket = null;
        }
    }
}
