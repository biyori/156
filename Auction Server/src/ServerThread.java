import java.io.*;
import java.net.*;

/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        System.out.println(
                "Current Thread Name: "
                        + Thread.currentThread().getName());
        // gets the ID of the current thread
        System.out.println(
                "Current Thread ID: "
                        + Thread.currentThread().getId());

        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);


            String text, reverseText;

            do {
                text = reader.readLine();
                if(text !=null)
                    reverseText = new StringBuilder(text).reverse().toString();
                else
                    reverseText = "";
                writer.println("Server: " + reverseText);

            } while (text != null && !text.equals("bye"));
            System.out.println("Client disconnected");
            socket.close();
            this.join();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}