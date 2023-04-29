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
        int port = 1998;
        
        // Establish the listen socket.
        ServerSocket server = new ServerSocket(port);
        
       // Process HTTP service requests in an infinite loop.     
        while (true) {
            // Listen for a TCP connection request.
            Socket client = server.accept();
            System.out.println();   //skip a line between requests
            
            /*Handling the request:*/
            HttpRequest request = new HttpRequest(client);
            // Create a new thread to process the request.
            Thread thread = new Thread(request);
            thread.start();
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
        // Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());   
        // Set up input stream filters.
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        // reading the request line.
        String requestLine = br.readLine();
        System.out.println("===REQUEST===");
        System.out.println(requestLine);
        
        // reading the header lines
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }
         
        /*Handling the request*/
        /*Extract the filename from the request line.*/
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();           
        // Prepend a "." so that file request is within the current directory.
        fileName = "./resources" + fileName;
        
        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }
        
        /*Construct the response message.*/
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-type: " +
            contentType( fileName ) + CRLF;
        } else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: " +
            "text/html" + CRLF; 
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                    "<BODY>Not Found</BODY></HTML>";
        }

        /*Sending data*/
        // Send the status line.
        os.writeBytes(statusLine);
        // Send the content type line.
        os.writeBytes(contentTypeLine);
        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);
        
        System.out.println();
        System.out.println("===RESPONSE===");
        System.out.println(statusLine);
        System.out.println(contentTypeLine);
        System.out.println(entityBody);
        System.out.println();

        // Send the entity body.
        if (fileExists) {
            sendBytes(fis, os);               // Send file to client
            fis.close();
        } else {
            os.writeBytes(entityBody);      // 404 Not Found
            System.out.println("A");
        }
        
        // Close streams and socket.
        os.close();
        is.close();
        br.close();
        socket.close();               
    }
    
    private static String contentType(String fileName){
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if(fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if(fileName.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }
    
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // Copy requested file into the socket's output stream.
        while((bytes = fis.read(buffer)) != -1 ) {
            os.write(buffer, 0, bytes);
        }
    }
}
