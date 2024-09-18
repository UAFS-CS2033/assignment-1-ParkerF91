import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;




public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int portNo;
    

    public Server(int portNo){
        this.portNo=portNo;
    }

    private void processConnection() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
        final String DOCROOT = "docroot/";

        //*** Application Protocol *****
       
        String buffer;
        String path = " ";
        //  reading the request line
        while((buffer = in.readLine()) != null && buffer.startsWith("GET")){
            System.out.println(buffer);
            //assinging path to /home.html
            String[] requests = buffer.split(" ");
            path = requests[1];
            
            //creating full path
            if(path.equals("/")){
                path = "/index.html";
            }
            String absPath = DOCROOT+path;
            
            File file = new File(absPath);
            // sending the response
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + Files.probeContentType(Paths.get(absPath)));
            out.println("Content-Length: " + file.length());
            out.println();
            
            // sending the file contents but not the picture cause i cant figure
            // that out and this is already late.
            Files.copy(file.toPath(),clientSocket.getOutputStream());
        }
        
        in.close();
        out.close();
    }

    public void run() throws IOException{
        boolean running = true;
       
        serverSocket = new ServerSocket(portNo);
        System.out.printf("Listen on Port: %d\n",portNo);
        
        while(running){
            clientSocket = serverSocket.accept();
            //** Application Protocol
            processConnection();
            clientSocket.close();
        }
        serverSocket.close();
    }
    public static void main(String[] args0) throws IOException{
        Server server = new Server(8080);
        server.run();
    }
}
