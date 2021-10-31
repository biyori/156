import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 43595;

        for (int i = 0; i < 5; i++) {
            System.out.println("Creating client " + i);
            try {
                Socket socket = new Socket(hostname, port);
                new ClientThread(socket).start();
            } catch (IOException e) {
                System.out.println("Client initialization error: " + e.getMessage());
            }
        }
    }
}
