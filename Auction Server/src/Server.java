import java.io.*;
import java.net.*;

public class Server {
    public static void main(String args[]) {
        int port = 43595;

        try {
            String line, newLine;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port "+ port);
            // Communication Endpoint for the client and server.
            while (true) {
                // Waiting for socket connection
                Socket socket = serverSocket.accept();
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String text;
                do {
                    System.out.println("Press 'q' if you want to exit server");
                    text = reader.readLine();
                    // Writes in output stream as bytes
                    //out.writeBytes(line +'\n');
                    System.out.println("Received from client: " + text);

                    writer.println("Server: " + text);
                } while (!text.equals("q"));

                socket.close();
            }
        } catch (IOException ex) {
            System.out.println("Server error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
