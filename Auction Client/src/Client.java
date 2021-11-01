import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class Client {
    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 43595;

        // Create item list of possible items to sell
        AuctionItems items = new AuctionItems();
        List<String> itemList = items.LoadItemsFromFile("items.txt");

        for (int i = 0; i < 5; i++) {
            System.out.println("Creating client " + i);
            try {
                Socket socket = new Socket(hostname, port);
                // Generate a UUID for each thread and set it as a unique identifier
                // to register with the server
                UUID uid = UUID.randomUUID();
                new ClientThread(socket, uid, itemList).start();
            } catch (IOException e) {
                System.out.println("Client initialization error: " + e.getMessage());
            }
        }
    }
}
