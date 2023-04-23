/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package webserver;

import java.io.* ;
import java.net.* ;
import java.util.* ;

/**
 *
 * @author João
 */
public class WebServer {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        //CODE
        int port = 1998;
        
        // Establish the listen socket.
        //CODE (SOCKET DE ESCUTA TCP)
        ServerSocket server = new ServerSocket(port);
        
       // Process HTTP service requests in an infinite loop.
       
       int cont = 0;
        while (true) {
            // Listen for a TCP connection request.
            Socket client = server.accept();
            cont ++;
            System.out.println("Numero de requisições TCP: " + cont);
            client.close();
        }
    }
}

final class HttpRequest implements Runnable{

    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception{
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.    
    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequest() throws Exception{
       
    }

    
}
